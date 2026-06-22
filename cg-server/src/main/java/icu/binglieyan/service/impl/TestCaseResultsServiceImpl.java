package icu.binglieyan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import icu.binglieyan.constant.MessageConstant;
import icu.binglieyan.entity.*;
import icu.binglieyan.exception.QuestionSubmissionsException;
import icu.binglieyan.exception.TestCasesResultException;
import icu.binglieyan.mapper.TestCaseResultsMapper;
import icu.binglieyan.mapper.TestCasesMapper;
import icu.binglieyan.mapper.QuestionSubmissionsMapper;
import icu.binglieyan.service.TestCaseResultsService;
import icu.binglieyan.vo.HiddenTestCaseResultsVO;
import icu.binglieyan.vo.TestCaseResultsVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 测试用例结果服务实现类
 * 仅负责查询已保存的测试结果（判题逻辑已迁移至 judge 项目）
 *
 * @author binglieyan
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class TestCaseResultsServiceImpl extends ServiceImpl<TestCaseResultsMapper, TestCaseResults> implements TestCaseResultsService {

    private final QuestionSubmissionsMapper questionSubmissionsMapper;
    private final TestCasesMapper testCasesMapper;

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
}
