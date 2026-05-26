package icu.binglieyan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;

import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import icu.binglieyan.constant.MessageConstant;
import icu.binglieyan.entity.*;
import icu.binglieyan.exception.DictDataException;
import icu.binglieyan.exception.QuestionSubmissionsException;
import icu.binglieyan.exception.TestCasesResultException;
import icu.binglieyan.mapper.*;
import icu.binglieyan.service.TestCaseResultsService;
import icu.binglieyan.vo.HiddenTestCaseResultsVO;
import icu.binglieyan.vo.TestCaseResultsVO;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value ;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 测试用例结果服务实现类
 * 使用Docker容器安全地执行代码判题
 *
 * @author binglieyan
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class TestCaseResultsServiceImpl extends ServiceImpl<TestCaseResultsMapper, TestCaseResults> implements TestCaseResultsService {

    private final QuestionSubmissionsMapper questionSubmissionsMapper;
    private final TestCasesMapper testCasesMapper;
    private final QuestionsMapper questionsMapper;
    private final SubmissionsMapper submissionsMapper;
    private final DictDataMapper dictDataMapper;

    @Value("${cg.docker.image}")
    private String dockerImage;

    @Value("${cg.docker.timeout}")
    private Long timeout;

    @Value("${cg.docker.workspace}")
    private String workspacePath;

    @Value("${cg.docker.hostWorkspace}")
    private String hostWorkspacePath;

    @Value("${cg.docker.host}")
    private String host;

    @Value("${cg.docker.tlsVerify}")
    private String tlsVerify;

    @Value("${cg.docker.containerWorkspace}")
    private String containerWorkspace;




    /**
     * 错误信息标记
     */
    private static final String ERROR_MARKER = "error:";

    /**
     * 异常信息标记
     */
    private static final String EXCEPTION_MARKER = "Exception";

    private DockerClient dockerClient;

    /**
     * 获取Docker客户端（懒加载单例模式）
     */
    private DockerClient getDockerClient() {

        DefaultDockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost(host)
                .withDockerTlsVerify(tlsVerify)
                .build();
        DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .connectionTimeout(Duration.ofSeconds(5))
                .responseTimeout(Duration.ofMillis(timeout))
                .maxConnections(50)
                .build();
        if (dockerClient == null) {
            try {
                dockerClient = DockerClientImpl.getInstance(config, httpClient);
                log.info("Docker客户端初始化成功");
            } catch (Exception e) {
                log.error("Docker客户端初始化失败", e);
                throw new RuntimeException("无法连接到Docker守护进程", e);
            }
        }
        return dockerClient;
    }

    /**
     * 自动判题主方法
     *
     * @param assignmentId 作业ID
     * @param studentId    学生ID
     * @param submissionId 作业提交ID
     */
    @Override
    public void autoJudge(Long assignmentId, Long studentId, Long submissionId) {
        log.info("开始自动判题 - 作业 ID: {}, 学生 ID: {}", assignmentId, studentId);

        try {
            // 更新提交状态为"批改中"
            updateSubmission(submissionId,"GRADING",  BigDecimal.ZERO);

            // 【优化】获取该学生的所有提交记录及对应的题目信息（包含 maxScore）
            List<QuestionSubmissionWithQuestion> submissionWithQuestions = getQuestionSubmissionsWithQuestionInfo(assignmentId, studentId);


            if (submissionWithQuestions == null || submissionWithQuestions.isEmpty()) {
                log.warn("未找到需要判题的记录 - 作业 ID: {}, 学生 ID: {}", assignmentId, studentId);
                return;
            }

            // 遍历每个提交进行判题
            for (QuestionSubmissionWithQuestion questionSubmissionWithQuestion : submissionWithQuestions) {
                judgeSingleQuestionOptimized(questionSubmissionWithQuestion, submissionId);
            }
            // 计算该学生在该作业下的总分
            BigDecimal totalScore = calculateTotalScore(assignmentId, studentId);

            // 全部判题完成，更新状态为"已批改"并保存总分
            updateSubmission(submissionId, "GRADED", totalScore);

            log.info("自动判题完成 - 作业 ID: {}, 学生 ID: {}", assignmentId, studentId);
        } catch (Exception exception) {
            log.error("自动判题失败 - 作业 ID: {}, 学生 ID: {}", assignmentId, studentId, exception);
            // 判题失败，更新状态为"自动判题失败"
            updateSubmission(submissionId,"AUTO_JUDGE_FAILED", BigDecimal.ZERO);
            throw new TestCasesResultException(MessageConstant.AUTO_JUDGE_FAILED);
        }
    }

    /**
     * 获取测试用例结果
     * @param questionSubmissionId 题目提交记录ID
     * @return 测试用例结果列表
     */
    @Override
    public List<TestCaseResultsVO> getTestCaseResults(Long questionSubmissionId) {
        if (questionSubmissionId == null) {
            throw new TestCasesResultException(MessageConstant.ID_NOT_NULL);
        }
        List<TestCaseResults> testCaseResultsList = list(
                new LambdaQueryWrapper<TestCaseResults>()
                        .eq(TestCaseResults::getQuestionSubmissionId, questionSubmissionId)
                        .select(
                                TestCaseResults::getId,
                                TestCaseResults::getQuestionSubmissionId,
                                TestCaseResults::getTestCaseId,
                                TestCaseResults::getSubmissionId,
                                TestCaseResults::getActualOutput,
                                TestCaseResults::getPassed,
                                TestCaseResults::getExecutionTime,
                                TestCaseResults::getErrorMessage
                        ));
        if (testCaseResultsList == null || testCaseResultsList.isEmpty()) {
            throw new TestCasesResultException(MessageConstant.TEST_CASES_RESULTS_NOT_FOUND);
        }
        return testCaseResultsList.stream()
                .map(testCaseResults -> TestCaseResultsVO.builder()
                        .id(testCaseResults.getId())
                        .questionSubmissionId(testCaseResults.getQuestionSubmissionId())
                        .testCaseId(testCaseResults.getTestCaseId())
                        .submissionId(testCaseResults.getSubmissionId())
                        .actualOutput(testCaseResults.getActualOutput())
                        .passed(testCaseResults.getPassed())
                        .executionTime(testCaseResults.getExecutionTime())
                        .errorMessage(testCaseResults.getErrorMessage())
                        .build())
                .toList();
    }

    /**
     * 学生获取可见测试用例结果
     * @param questionSubmissionId 题目提交记录ID
     * @return 测试用例结果
     */
    @Override
    public List<TestCaseResultsVO> studentGetTestCaseResults(Long questionSubmissionId) {
        if (questionSubmissionId == null) {
            throw new TestCasesResultException(MessageConstant.ID_NOT_NULL);
        }
        QuestionSubmissions questionSubmissions = questionSubmissionsMapper.selectOne(
                new LambdaQueryWrapper<QuestionSubmissions>()
                        .eq(QuestionSubmissions::getId, questionSubmissionId)
                        .select(QuestionSubmissions::getQuestionId));
        if (questionSubmissions == null) {
            throw new QuestionSubmissionsException(MessageConstant.QUESTION_SUBMISSIONS_NOT_EXIST);
        }
        List<TestCases> testCasesList = testCasesMapper.selectList(
                new LambdaQueryWrapper<TestCases>()
                        .eq(TestCases::getQuestionId, questionSubmissions.getQuestionId())
                        .eq(TestCases::getHidden, false)
                        .select(TestCases::getId));
        List<Long> testCaseIds = testCasesList.stream()
                .map(TestCases::getId)
                .toList();
        List<TestCaseResults> testCaseResultsList = this.list(
                new LambdaQueryWrapper<TestCaseResults>()
                        .in(TestCaseResults::getTestCaseId, testCaseIds)
                        .eq(TestCaseResults::getQuestionSubmissionId, questionSubmissionId)
                        .select(
                                TestCaseResults::getId,
                                TestCaseResults::getQuestionSubmissionId,
                                TestCaseResults::getTestCaseId,
                                TestCaseResults::getSubmissionId,
                                TestCaseResults::getActualOutput,
                                TestCaseResults::getPassed,
                                TestCaseResults::getExecutionTime,
                                TestCaseResults::getErrorMessage
                        ));
        return testCaseResultsList.stream()
                .map(testCaseResults -> TestCaseResultsVO.builder()
                        .id(testCaseResults.getId())
                        .questionSubmissionId(testCaseResults.getQuestionSubmissionId())
                        .testCaseId(testCaseResults.getTestCaseId())
                        .submissionId(testCaseResults.getSubmissionId())
                        .actualOutput(testCaseResults.getActualOutput())
                        .passed(testCaseResults.getPassed())
                        .executionTime(testCaseResults.getExecutionTime())
                        .errorMessage(testCaseResults.getErrorMessage())
                        .build())
                .toList();
    }

    /**
     * 学生获取隐藏测试用例结果通过统计
     * @param questionSubmissionId 题目提交记录ID
     * @return 隐藏测试用例结果通过统计
     */
    @Override
    public HiddenTestCaseResultsVO studentGetHiddenTestCaseResults(Long questionSubmissionId) {
        if (questionSubmissionId == null) {
            throw new TestCasesResultException(MessageConstant.ID_NOT_NULL);
        }
        QuestionSubmissions questionSubmissions = questionSubmissionsMapper.selectOne(
                new LambdaQueryWrapper<QuestionSubmissions>()
                        .eq(QuestionSubmissions::getId, questionSubmissionId)
                        .select(QuestionSubmissions::getQuestionId));
        if (questionSubmissions == null) {
            throw new QuestionSubmissionsException(MessageConstant.QUESTION_SUBMISSIONS_NOT_EXIST);
        }
        // 查询该题目下所有隐藏测试用例的总数
        long totalCount = testCasesMapper.selectCount(
                new LambdaQueryWrapper<TestCases>()
                        .eq(TestCases::getQuestionId, questionSubmissions.getQuestionId())
                        .eq(TestCases::getHidden, true));

        // 获取所有隐藏测试用例 ID
        List<Long> hiddenTestCaseIds = testCasesMapper.selectList(
                new LambdaQueryWrapper<TestCases>()
                        .eq(TestCases::getQuestionId, questionSubmissions.getQuestionId())
                        .eq(TestCases::getHidden, true))
                .stream()
                .map(TestCases::getId)
                .toList();

        // 如果没有任何隐藏测试用例，直接返回
        if (hiddenTestCaseIds.isEmpty()) {
            return HiddenTestCaseResultsVO.builder()
                    .passedCount(0)
                    .totalCount(0)
                    .build();
        }

        // 查询已通过的隐藏测试用例数量
        long passedCount = this.count(
                new LambdaQueryWrapper<TestCaseResults>()
                        .in(TestCaseResults::getTestCaseId, hiddenTestCaseIds)
                        .eq(TestCaseResults::getQuestionSubmissionId, questionSubmissionId)
                        .eq(TestCaseResults::getPassed, true));
        return HiddenTestCaseResultsVO.builder()
                .passedCount((int) passedCount)
                .totalCount((int) totalCount)
                .build();
    }

    /**
     * 更新提交状态并设置总分
     * @param submissionId 提交ID
     * @param statusCode 状态码
     * @param totalScore 总分
     */
    private void updateSubmission(Long submissionId, String statusCode, BigDecimal totalScore) {
        LambdaUpdateWrapper<Submissions> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Submissions::getId, submissionId);
        if (StringUtils.isNotBlank(statusCode)) {
            if (!dictDataMapper.exists(new LambdaQueryWrapper<DictData>()
                    .eq(DictData::getDataCode, statusCode)
                    .eq(DictData::getActive, true))) {
                throw new DictDataException(MessageConstant.DICT_DATA_NOT_FOUND);
            }
            updateWrapper.set(Submissions::getSubmissionStatusCode, statusCode);
        }
        if (totalScore != null) {
            updateWrapper.set(Submissions::getTotalScore, totalScore);
        }

        updateWrapper.set(Submissions::getGradingCompletedAt, LocalDateTime.now());
        submissionsMapper.update(updateWrapper);
        log.debug("更新提交状态及总分 - 状态：{}, 总分：{}", statusCode, totalScore);
    }

    /**
     * 计算学生在该作业下的总分（所有题目得分之和）
     * @param assignmentId 作业 ID
     * @param studentId 学生 ID
     * @return 总分
     */
    private BigDecimal calculateTotalScore(Long assignmentId, Long studentId) {
        // 1. 获取该作业下所有题目的 ID
        LambdaQueryWrapper<Questions> questionWrapper = new LambdaQueryWrapper<>();
        questionWrapper.eq(Questions::getAssignmentId, assignmentId)
                .select(Questions::getId);
        List<Long> questionIds = questionsMapper.selectList(questionWrapper)
                .stream()
                .map(Questions::getId)
                .toList();

        if (questionIds.isEmpty()) {
            log.warn("作业 ID: {} 没有题目", assignmentId);
            return BigDecimal.ZERO;
        }

        // 2. 查询该学生在这些题目上的得分
        List<QuestionSubmissions> submissions = questionSubmissionsMapper.selectList(
                new LambdaQueryWrapper<QuestionSubmissions>()
                        .in(QuestionSubmissions::getQuestionId, questionIds)
                        .eq(QuestionSubmissions::getStudentId, studentId)
                        .select(QuestionSubmissions::getScore));

        // 3. 累加所有题目的得分
        BigDecimal totalScore = submissions.stream()
                .map(QuestionSubmissions::getScore)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        log.debug("计算总分 - 作业 ID: {}, 学生 ID: {}, 题目数：{}, 总分：{}",
                assignmentId, studentId, questionIds.size(), totalScore);

        return totalScore;
    }

    /**
     * 判题单个题目（优化版，使用预加载的题目信息）
     * @param questionSubmissionWithQuestion 提交记录与题目信息的封装对象
     * @param submissionId 提交 ID
     * @throws Exception 测试用例结果异常
     */
    private void judgeSingleQuestionOptimized(QuestionSubmissionWithQuestion questionSubmissionWithQuestion, Long submissionId) throws Exception {
        Long questionId = questionSubmissionWithQuestion.getQuestionId();
        Long questionSubmissionsId = questionSubmissionWithQuestion.getQuestionSubmissionId();
        String filePath = questionSubmissionWithQuestion.getStudentAnswer();
        BigDecimal maxScore = questionSubmissionWithQuestion.getMaxScore();

        log.info("开始判题 - 题目 ID: {}, 提交 ID: {}, 文件路径：{}", questionId, questionSubmissionsId, filePath);

        // 验证文件路径
        if (filePath == null || filePath.trim().isEmpty()) {
            log.warn("学生答案文件路径为空 - 提交 ID: {}", questionSubmissionsId);
            return;
        }

        Path studentFile = Path.of(filePath);
        if (!Files.exists(studentFile)) {
            log.error("学生答案文件不存在 - 路径：{}", filePath);
            throw new FileNotFoundException(MessageConstant.STUDENT_ANSWER_FILE_NOT_EXIST);
        }

        // 获取该题目的所有测试用例
        List<TestCases> testCases = getTestCasesByQuestionId(questionId);

        if (testCases == null || testCases.isEmpty()) {
            log.warn("题目 ID: {} 没有测试用例", questionId);
            return;
        }

        // 通过的测试用例数
        int passedCount = 0;
        // 标记是否发生系统级错误
        boolean systemErrorOccurred = false;
        // 系统错误信息
        String systemErrorMessage = null;

        // 遍历每个测试用例
        for (TestCases testCases1 : testCases) {
            TestCaseResults testCaseResults;
            try {
                // 在 Docker 容器中执行单个测试用例
                testCaseResults = runTestCaseInDocker(studentFile, testCases1);
            } catch (TestCasesResultException e) {
                // Docker 环境异常等系统级错误
                log.error("测试用例执行发生系统错误 - 题目 ID: {}, 测试用例 ID: {}",
                        questionId, testCases1.getId(), e);
                systemErrorOccurred = true;
                systemErrorMessage = e.getMessage();
                // 创建系统错误的测试结果记录
                testCaseResults = TestCaseResults.builder()
                        .passed(false)
                        .actualOutput("")
                        .executionTime(0)
                        .errorMessage("系统错误：" + e.getMessage())
                        .build();
            }

            // 创建测试用例结果记录
            TestCaseResults testCaseResult = TestCaseResults.builder()
                    .questionSubmissionId(questionSubmissionsId)
                    .testCaseId(testCases1.getId())
                    .submissionId(submissionId)
                    .actualOutput(testCaseResults.getActualOutput())
                    .passed(testCaseResults.getPassed())
                    .executionTime(testCaseResults.getExecutionTime())
                    .errorMessage(testCaseResults.getErrorMessage())
                    .build();
            // 保存结果
            this.save(testCaseResult);

            // 只有非系统错误且通过时才计数
            if (!systemErrorOccurred && testCaseResults.getPassed()) {
                passedCount++;
            }

            // 如果发生系统错误，停止后续测试用例的执行
            if (systemErrorOccurred) {
                break;
            }
        }

        // 如果发生系统错误，抛出异常让外层处理状态
        if (systemErrorOccurred) {
            throw new TestCasesResultException("题目 ID: " + questionId + " 判题时发生系统错误：" + systemErrorMessage);
        }

        // 更新提交记录的判题结果（直接使用传入的 questionSubmissions 对象）
        QuestionSubmissions questionSubmissions = QuestionSubmissions.builder()
                .id(questionSubmissionsId)
                .questionId(questionId)
                .studentAnswer(filePath)
                .build();
        updateQuestionSubmissionResult(questionSubmissions, passedCount, testCases.size(), maxScore);

        log.info("判题完成 - 题目 ID: {}, 通过数：{}/{}, 题目得分：{}/{}",
                questionId, passedCount, testCases.size(),
                calculateScore(passedCount, testCases.size(), maxScore), maxScore);
    }

    /**
     * 一次性获取学生的提交记录及对应的题目信息（包含 maxScore）
     * @param assignmentId 作业 ID
     * @param studentId 学生 ID
     * @return 提交记录与题目信息的封装列表
     */
    private List<QuestionSubmissionWithQuestion> getQuestionSubmissionsWithQuestionInfo(Long assignmentId, Long studentId) {
        // 1. 获取该作业下所有题目的 ID 和 maxScore
        List<Questions> questionsList = questionsMapper.selectList(
                new LambdaQueryWrapper<Questions>()
                        .eq(Questions::getAssignmentId, assignmentId)
                        .select(
                                Questions::getId,
                                Questions::getMaxScore
                        ));

        if (questionsList == null || questionsList.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. 提取所有题目 ID
        List<Long> questionIds = questionsList.stream()
                .map(Questions::getId)
                .toList();

        // 3. 批量查询学生的提交记录
        List<QuestionSubmissions> submissions = questionSubmissionsMapper.selectList(
                new LambdaQueryWrapper<QuestionSubmissions>()
                        .in(QuestionSubmissions::getQuestionId, questionIds)
                        .eq(QuestionSubmissions::getStudentId, studentId)
                        .select(
                                QuestionSubmissions::getId,
                                QuestionSubmissions::getQuestionId,
                                QuestionSubmissions::getStudentAnswer
                        ));

        // 4. 构建题目 ID 到 maxScore 的映射
        Map<Long, BigDecimal> questionMaxScoreMap = questionsList.stream()
                .collect(Collectors.toMap(
                        Questions::getId,
                        Questions::getMaxScore
                ));

        // 5. 组装数据
        return submissions.stream()
                .map(submission -> {
                    Long questionId = submission.getQuestionId();
                    return QuestionSubmissionWithQuestion.builder()
                            .questionSubmissionId(submission.getId())
                            .questionId(questionId)
                            .studentAnswer(submission.getStudentAnswer())
                            .maxScore(questionMaxScoreMap.get(questionId))
                            .build();
                })
                .toList();
    }

    /**
     * 在Docker容器中运行测试用例
     * @return 测试用例结果
     */
    private TestCaseResults runTestCaseInDocker(Path studentFile, TestCases testCases) {
        // 临时目录
        Path tempDir = null;
        // 容器 ID
        String containerId = null;
        DockerClient client = null;

        try {
            // 创建临时目录存放代码文件
            if (workspacePath != null && !workspacePath.trim().isEmpty()) {
                Path workspace = Path.of(workspacePath);
                if (!Files.exists(workspace)) {
                    Files.createDirectories(workspace);
                }
                tempDir = Files.createTempDirectory(workspace, "code_grader_");
            } else {
                tempDir = Files.createTempDirectory("code_grader_");
            }
            log.debug("创建临时目录：{}", tempDir);

            String hostTempDir = tempDir.toString().replace(workspacePath, hostWorkspacePath);

            // 生成测试包装类代码（包含测试逻辑）
            String wrappedCode = generateTestWrapper(studentFile, testCases.getInputData());
            Path wrapperFile = tempDir.resolve("GraderTest.java");
            Files.writeString(wrapperFile, wrappedCode, StandardCharsets.UTF_8);

            // 配置Docker容器卷挂载，将临时目录映射到容器的workspace
            Volume containerVolume = new Volume(containerWorkspace);
            HostConfig hostConfig = HostConfig.newHostConfig()
                    .withBinds(new Bind(hostTempDir, containerVolume))
                    // 限制内存为256MB（适合基础算法题）
                    .withMemory(256 * 1024 * 1024L)
                    .withMemorySwap(256 * 1024 * 1024L)
                    // 限制CPU核心数为1
                    .withCpuCount(1L)
                    // 禁用网络访问
                    .withNetworkMode("none")
                    .withReadonlyRootfs(true)
                    .withPidsLimit(64L);


            client = getDockerClient();

            // 创建Docker容器
            CreateContainerResponse container = client.createContainerCmd(dockerImage)
                    .withHostConfig(hostConfig)
                    .withCmd("bash", "-c",
                            "cd " + containerWorkspace + " && " +
                                    "javac -encoding UTF-8 GraderTest.java && " +
                                    "timeout " + (timeout / 2000) + "s java GraderTest")
                    .withWorkingDir(containerWorkspace)
                    .withTty(false)
                    .withAttachStdout(true)
                    .withAttachStderr(true)
                    .exec();

            containerId = container.getId();
            log.debug("创建Docker容器: {}", containerId);

            long startTime = System.currentTimeMillis();

            // 启动容器
            client.startContainerCmd(containerId).exec();
            log.debug("启动Docker容器");

            // 同步读取容器日志
            String output;
            try (LogContainerResultCallback callback = new LogContainerResultCallback()) {
                boolean completed = client.logContainerCmd(containerId)
                        .withStdOut(true)
                        .withStdErr(true)
                        .withFollowStream(true)
                        .exec(callback)
                        .awaitCompletion(timeout, TimeUnit.MILLISECONDS);
                
                output = callback.toString();
                
                // 如果未在规定时间内完成，视为超时
                if (!completed) {
                    log.error("Docker容器执行超时，强制停止容器");
                    client.stopContainerCmd(containerId).exec();
                    return TestCaseResults.builder()
                            .passed(false)
                            .actualOutput(output)
                            .executionTime(timeout.intValue())
                            .errorMessage("执行超时（超过" + timeout + "ms）")
                            .build();
                }
            } catch (IOException e) {
                log.error("读取容器日志失败", e);
                throw new TestCasesResultException("读取容器日志失败：" + e.getMessage());
            }

            // 获取容器退出码
            Long exitCode = client.inspectContainerCmd(containerId)
                    .exec()
                    .getState()
                    .getExitCodeLong();

            long executionTime = System.currentTimeMillis() - startTime;

            if (exitCode == null || exitCode != 0) {
                String errorMsg = extractErrorMessage(output);
                return TestCaseResults.builder()
                        .passed(false)
                        .actualOutput("")
                        .executionTime((int) executionTime)
                        .errorMessage("编译或运行错误：" + errorMsg)
                        .build();
            }
            log.debug("容器执行完成，退出码: {}", exitCode);

            // 比较实际输出和预期输出
            boolean passed = improvedCompareOutputs(testCases.getExpectedOutput(), output);

            return TestCaseResults.builder()
                    .passed(passed)
                    .actualOutput(output)
                    .executionTime((int) executionTime)
                    .errorMessage(passed ? null : "输出不匹配\n预期输出:\n" + testCases.getExpectedOutput() + "\n实际输出:\n" + output)
                    .build();

        } catch (TestCasesResultException e) {
            throw e;
        } catch (IOException e) {
            // IO 相关错误（文件操作、Docker 连接等）视为系统错误
            log.error("Docker 判题发生 IO 异常", e);
            throw new TestCasesResultException("Docker 环境异常：" + e.getMessage());
        } catch (RuntimeException e) {
            // 运行时异常（如 Docker 镜像拉取失败、容器创建失败等）视为系统错误
            log.error("Docker 判题发生运行时异常", e);
            throw new TestCasesResultException("Docker 环境异常：" + e.getMessage());
        } catch (Exception e) {
            // 其他未知异常
            log.error("Docker 判题失败", e);
            throw new TestCasesResultException("Docker 环境异常：" + e.getMessage());
        } finally {
            // 清理 Docker 容器和临时文件
            cleanupResources(containerId, tempDir, client);
        }
    }

    /**
     * 从错误输出中提取有用的错误信息
     * @param output 错误输出
     * @return 错误信息
     */
    private String extractErrorMessage(String output) {
        if (output == null || output.isEmpty()) {
            return "未知错误";
        }

        // 提取编译错误
        if (output.contains(ERROR_MARKER)) {
            String[] lines = output.split("\n");
            StringBuilder sb = new StringBuilder();
            for (String line : lines) {
                if (line.contains("error:")) {
                    sb.append(line).append("\n");
                }
            }
            return sb.toString().trim();
        }

        // 提取异常信息
        if (output.contains(EXCEPTION_MARKER)) {
            String[] lines = output.split("\n");
            StringBuilder sb = new StringBuilder();
            for (String line : lines) {
                if (line.contains("Exception") || line.contains("at ")) {
                    sb.append(line).append("\n");
                }
            }
            return sb.toString().trim();
        }

        // 返回前200个字符
        return output.length() > 200 ? output.substring(0, 200) + "..." : output;
    }

    /**
     * 生成测试包装类代码
     * @return 测试包装类代码
     */
    private String generateTestWrapper(Path studentFile, List<String> inputData) throws IOException {

        MethodInfo methodInfo = methodExtractor(studentFile);
        String variableNames = extractVariableNames(inputData);


        // 使用文本块生成完整测试类
        return
                """
                import java.util.*;
                public class GraderTest {
                    public void printResult(Object result) {
                        switch (result) {
                            case null -> System.out.println("null");
                            case int[] ints -> System.out.println(Arrays.toString(ints));
                            case long[] longs -> System.out.println(Arrays.toString(longs));
                            case double[] doubles -> System.out.println(Arrays.toString(doubles));
                            case boolean[] booleans -> System.out.println(Arrays.toString(booleans));
                            case char[] chars -> System.out.println(Arrays.toString(chars));
                            case float[] floats -> System.out.println(Arrays.toString(floats));
                            case short[] shorts -> System.out.println(Arrays.toString(shorts));
                            case byte[] bytes -> System.out.println(Arrays.toString(bytes));
                            case Object[] objects -> System.out.println(Arrays.deepToString(objects));
                            default -> System.out.println(result);
                        }
                    }
                    %s
                    void main() {
                        try {
                            %s
                            GraderTest graderTest = new GraderTest();
                            Object result = graderTest.%s
                            printResult(result);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                """.formatted(
                        methodInfo.getMethod(),
                        variableNames,
                        methodInfo.getMethodNameParams()
                );
    }

    /**
     * 提取变量名
     * @param path 文件路径
     * @return 变量名
     * @throws IOException 读取文件异常
     */
    private MethodInfo methodExtractor(Path path) throws IOException {
        StaticJavaParser.getParserConfiguration().setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_25);
        CompilationUnit cu = StaticJavaParser.parse(path);
        ClassOrInterfaceDeclaration clazz = cu.findFirst(ClassOrInterfaceDeclaration.class)
                .orElseThrow(() -> new TestCasesResultException(MessageConstant.CLASS_NOT_FOUND));
        MethodDeclaration method = clazz.getMethods().getFirst();
        // 获取方法名
        String methodName = method.getNameAsString();

        // 获取参数列表（只要参数名）
        String params = method.getParameters().stream()
                .map(Parameter::getNameAsString)
                .collect(Collectors.joining(", "));
        String methodNameParams = methodName +"(" + params + ")" + ";";
        MethodInfo methodInfo = new MethodInfo();
        methodInfo.setMethod(method.toString());
        methodInfo.setMethodNameParams(methodNameParams);
        return methodInfo;
    }


    /**
     * 提取变量名
     * @param declarations 声明
     * @return 变量名
     */
    private String extractVariableNames(List<String> declarations) {
        StringBuilder result = new StringBuilder();
        for (String decl : declarations) {
            result.append(decl).append(";\n");
        }
        return result.toString().trim();
    }



    /**
     * 清理 Docker 容器和临时文件
     * @param containerId Docker 容器 ID
     * @param tempDir 临时目录路径
     * @param client DockerClient
     */
    private void cleanupResources(String containerId, Path tempDir, DockerClient client) {
        if (containerId != null && client != null) {
            try {

                // 尝试停止容器
                try {
                    // 增加检查：只有当容器确实存在且运行时才尝试停止，或者直接尝试停止并忽略特定异常
                    // 这里采用直接尝试停止，但捕获异常后继续执行删除，确保资源释放
                    client.stopContainerCmd(containerId).exec();
                    log.debug("已停止Docker容器: {}", containerId);
                } catch (Exception e) {
                    // 记录日志但不中断流程，因为容器可能未启动成功，直接删除即可
                    log.debug("停止容器失败或容器未运行 (ID: {}), 将直接尝试删除: {}", containerId, e.getMessage());
                }

                // 强制删除容器 (无论是否停止成功)
                try {
                    client.removeContainerCmd(containerId).withForce(true).exec();
                    log.debug("已删除Docker容器: {}", containerId);
                } catch (Exception e) {
                    log.warn("删除Docker容器失败: {}", containerId, e);
                }
            } catch (Exception e) {
                log.warn("清理 Docker 容器资源时发生未知错误: {}", containerId, e);
            }
        }

        if (tempDir != null) {
            cleanupTempDir(tempDir);
        }
    }

    /**
     * 清理临时目录
     * @param tempDir 临时目录
     */
    private void cleanupTempDir(Path tempDir) {
        try (var stream = Files.walk(tempDir)) {
            stream.sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException e) {
                            log.warn("删除临时文件失败：{}", path, e);
                        }
                    });
            log.debug("清理临时目录：{}", tempDir);
        } catch (IOException e) {
            log.error("清理临时目录失败", e);
        }
    }

    /**
     * 更新提交记录的判题结果
     * @param questionSubmissions 提交记录
     * @param passedCount 通过的测试用例数
     * @param totalCount 总的测试用例数
     */
    private void updateQuestionSubmissionResult(QuestionSubmissions questionSubmissions,
                                        int passedCount, int totalCount, BigDecimal maxScore) {
        BigDecimal score = calculateScore(passedCount, totalCount, maxScore);

        LambdaUpdateWrapper<QuestionSubmissions> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(QuestionSubmissions::getId, questionSubmissions.getId());
        updateWrapper.set(QuestionSubmissions::getScore, score);
        questionSubmissionsMapper.update(updateWrapper);
    }

    /**
     * 计算得分
     * @param passedCount 通过的测试用例数
     * @param totalCount 总的测试用例数
     * @param maxScore 题目最高分
     * @return 计算后的得分
     */
    private BigDecimal calculateScore(int passedCount, int totalCount, BigDecimal maxScore) {
        if (totalCount > 0 && maxScore != null) {
            return BigDecimal.valueOf((double) passedCount / totalCount * maxScore.doubleValue());
        }
        return BigDecimal.ZERO;
    }

    /**
     * 获取题目的测试用例
     * @param questionId 题目
     * @return 测试用例列表
     */
    private List<TestCases> getTestCasesByQuestionId(Long questionId) {
        return testCasesMapper.selectList(
                new LambdaQueryWrapper<TestCases>()
                        .eq(TestCases::getQuestionId, questionId)
                        .orderByAsc(TestCases::getCaseOrder));
    }

    /**
     * 比较输出结果
     * @param expected 预期输出
     * @param actual 实际输出
     * @return 是否一致
     */
    public static boolean improvedCompareOutputs(String expected, String actual) {
        if (expected == null && actual == null) {
            return true;
        }
        if (expected == null || actual == null) {
            return false;
        }

        String expectedTrimmed = expected.trim();
        String actualTrimmed = actual.trim();

        // 统一换行符
        expectedTrimmed = expectedTrimmed.replace("\r\n", "\n");
        actualTrimmed = actualTrimmed.replace("\r\n", "\n");

        // 精确匹配
        return expectedTrimmed.equals(actualTrimmed);
    }


    /**
     * Docker容器日志回调
     */
    private static class LogContainerResultCallback extends ResultCallback.Adapter<Frame> {
        private final StringBuilder log = new StringBuilder();

        @Override
        public void onNext(Frame item) {
            log.append(new String(item.getPayload(), StandardCharsets.UTF_8));
            super.onNext(item);
        }

        @Override
        public String toString() {
            return log.toString().trim();
        }
    }

    /**
     *内部类：用于存储方法信息
     */
    @Data
    private static class MethodInfo {
        private String method;
        private String methodNameParams;
    }

    @Data
    @Builder
    private static class QuestionSubmissionWithQuestion {
        /**
         * 提交记录 ID
         */
        private Long questionSubmissionId;

        /**
         * 题目 ID
         */
        private Long questionId;

        /**
         * 学生答案
         */
        private String studentAnswer;

        /**
         * 题目最高分
         */
        private BigDecimal maxScore;
    }
}