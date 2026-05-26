package icu.binglieyan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import icu.binglieyan.constant.MessageConstant;
import icu.binglieyan.context.BaseContext;
import icu.binglieyan.dto.QuestionSubmissionsUpdateDTO;
import icu.binglieyan.entity.*;
import icu.binglieyan.exception.QuestionSubmissionsException;
import icu.binglieyan.exception.QuestionsException;
import icu.binglieyan.exception.UserScopeException;
import icu.binglieyan.exception.UsersException;
import icu.binglieyan.mapper.*;
import icu.binglieyan.service.QuestionSubmissionsService;
import icu.binglieyan.vo.QuestionSubmissionsVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author binglieyan
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class QuestionSubmissionsServiceImpl extends ServiceImpl<QuestionSubmissionsMapper, QuestionSubmissions> implements QuestionSubmissionsService {

    private static final String ALLOWED_EXTENSIONS = "java";
    private static final String FILE_EXTENSION_SEPARATOR = ".";
    private static final String ASSIGNMENT_STATUS_CODE = "PUBLISHED";
    private final UsersMapper usersMapper;
    private final QuestionsMapper questionsMapper;
    private final AssignmentsMapper assignmentsMapper;
    private final ClassesMapper classesMapper;
    private final SubmissionsMapper submissionsMapper;
    @Value("${cg.uploadFile.uploadDir}")
    private String uploadDir;

    /**
     * 读取文件内容
     *
     * @param filePath 文件路径
     * @return 文件内容
     */
    private String readFileContent(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return null;
        }
        try {
            return Files.readString(Paths.get(filePath));
        } catch (IOException e) {
            log.error("读取文件失败：{}", filePath, e);
            return null;
        }
    }


    /**
     * 上传答案
     *
     * @param questionId 题目ID
     * @param file       答案文件
     */
    @Override
    public void uploadWithFile(Long questionId, MultipartFile file) {
        try {
            //1. 校验提交是否在作业提交的有限时间段内
            LocalDateTime now = LocalDateTime.now();
            Questions questions = questionsMapper.selectOne(
                    new LambdaQueryWrapper<Questions>()
                            .eq(Questions::getId, questionId)
                            .select(Questions::getAssignmentId));
            Assignments assignments = assignmentsMapper.selectOne(
                    new LambdaQueryWrapper<Assignments>()
                            .eq(Assignments::getId, questions.getAssignmentId())
                            .select(
                                    Assignments::getStartTime,
                                    Assignments::getDeadline,
                                    Assignments::getAssignmentStatusCode
                            ));
            if (now.isBefore(assignments.getStartTime())) {
                throw new QuestionSubmissionsException(MessageConstant.ASSIGNMENT_SUBMISSIONS_NOT_START);
            }
            if (now.isAfter(assignments.getDeadline())) {
                throw new QuestionSubmissionsException(MessageConstant.ASSIGNMENT_SUBMISSIONS_NOT_END);
            }
            //2. 校验当前作业是否处于已发布状态
            if (!ASSIGNMENT_STATUS_CODE.equals(assignments.getAssignmentStatusCode())) {
                throw new QuestionSubmissionsException(MessageConstant.ASSIGNMENT_NOT_PUBLISHED);
            }
            //3. 校验当前用户的当前题目下的题目提交记录是否存在
            Optional<Long> studentIdOpt = BaseContext.getCurrentId();
            if (studentIdOpt.isEmpty()) {
                throw new UserScopeException(MessageConstant.USER_SCOPE_ERROR);
            }
            Long studentId = studentIdOpt.get();
            if (this.exists(new LambdaQueryWrapper<QuestionSubmissions>()
                    .eq(QuestionSubmissions::getQuestionId, questionId)
                    .eq(QuestionSubmissions::getStudentId, studentId))) {
                throw new QuestionSubmissionsException(MessageConstant.QUESTION_SUBMISSIONS_EXISTS);
            }
            //4. 判断文件是否为空
            if (file == null || file.isEmpty()) {
                throw new QuestionSubmissionsException(MessageConstant.UPLOAD_FILE_EMPTY);
            }
            //5. 提取扩展名
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || !originalFilename.contains(FILE_EXTENSION_SEPARATOR)) {
                throw new QuestionSubmissionsException(MessageConstant.UPLOAD_FILE_INVALID);
            }

            //6. 校验扩展名
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf(FILE_EXTENSION_SEPARATOR) + 1).toLowerCase();
            if (!ALLOWED_EXTENSIONS.contains(fileExtension)) {
                throw new QuestionSubmissionsException(MessageConstant.UPLOAD_FILE_INVALID);
            }

            //7. 创建上传目录
            Users users = usersMapper.selectById(studentId);
            String newUploadDir = uploadDir + "/" + questions.getAssignmentId() + "/" + users.getUserNumber() + users.getRealName();
            File dir = new File(newUploadDir);
            if (!dir.exists() && !dir.mkdirs()) {
                throw new QuestionSubmissionsException(MessageConstant.UPLOAD_DIR_CREATE_FAILED);
            }

            //8. 生成文件名并保存 - 使用题目 ID 避免文件名冲突
            String newFileName = questionId + "_" + "Solution" + FILE_EXTENSION_SEPARATOR + fileExtension;
            String filePath = newUploadDir + "/" + newFileName;
            File destFile = new File(filePath);
            file.transferTo(destFile);

            //9. 数据库插入提交记录
            QuestionSubmissions questionSubmissions = QuestionSubmissions.builder()
                    .questionId(questionId)
                    .studentId(studentId)
                    .studentAnswer(filePath)
                    .build();
            this.save(questionSubmissions);
        } catch (IOException ioException) {
            log.error(MessageConstant.UPLOAD_FILE_FAILED, ioException);
            throw new QuestionSubmissionsException(MessageConstant.UPLOAD_FILE_FAILED);
        }
    }

    /**
     * 查询题目提交详情
     *
     * @param questionId 题目ID
     * @return 提交详情
     */
    @Override
    public List<QuestionSubmissionsVO> queryById(Long questionId) {
        if (questionId == null) {
            throw new QuestionSubmissionsException(MessageConstant.ID_NOT_NULL);
        }
        Questions questions = questionsMapper.selectOne(
                new LambdaQueryWrapper<Questions>()
                        .eq(Questions::getId, questionId)
                        .select(Questions::getAssignmentId));
        if (questions == null) {
            throw new QuestionsException(MessageConstant.QUESTIONS_NOT_FOUND);
        }
        Optional<Long> studentIdOpt = BaseContext.getCurrentId();
        if (studentIdOpt.isEmpty()) {
            throw new UserScopeException(MessageConstant.USER_SCOPE_ERROR);
        }
        Optional<String> studentNumberOpt = BaseContext.getCurrentUserNumber();
        if (studentNumberOpt.isEmpty()) {
            throw new UserScopeException(MessageConstant.USER_SCOPE_ERROR);
        }
        if (!submissionsMapper.exists(
                new LambdaQueryWrapper<Submissions>()
                        .eq(Submissions::getAssignmentId, questions.getAssignmentId())
                        .eq(Submissions::getStudentId, studentIdOpt.get()))) {
            throw new QuestionSubmissionsException(MessageConstant.SUBMISSIONS_NOT_FOUND);
        }
        List<QuestionSubmissions> questionSubmissionsList = this.list(
                new LambdaQueryWrapper<QuestionSubmissions>()
                        .eq(QuestionSubmissions::getQuestionId, questionId)
                        .eq(QuestionSubmissions::getStudentId, studentIdOpt.get())
                        .select(
                                QuestionSubmissions::getId,
                                QuestionSubmissions::getQuestionId,
                                QuestionSubmissions::getStudentId,
                                QuestionSubmissions::getStudentAnswer,
                                QuestionSubmissions::getScore,
                                QuestionSubmissions::getGradedById,
                                QuestionSubmissions::getGradingCompletedAt,
                                QuestionSubmissions::getTeacherFeedback
                        ));
        
        if (questionSubmissionsList.isEmpty()) {
            return List.of();
        }
        
        // 批量查询批改人信息（避免N+1）
        List<Long> gradedByIds = questionSubmissionsList.stream()
                .map(QuestionSubmissions::getGradedById)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        
        Map<Long, String> gradedByNameMap = Map.of();
        if (!gradedByIds.isEmpty()) {
            List<Users> gradedByUsers = usersMapper.selectList(
                    new LambdaQueryWrapper<Users>()
                            .in(Users::getId, gradedByIds)
                            .select(Users::getId, Users::getRealName));
            gradedByNameMap = gradedByUsers.stream()
                    .collect(Collectors.toMap(Users::getId, Users::getRealName));
        }
        
        String studentNumber = studentNumberOpt.get();
        Map<Long, String> finalGradedByNameMap = gradedByNameMap;
        
        return questionSubmissionsList.stream()
                .map(questionSubmissions -> {
                    String gradedByName = "系统自动批改";
                    if (questionSubmissions.getGradedById() != null) {
                        gradedByName = finalGradedByNameMap.get(questionSubmissions.getGradedById());
                    }

                    return QuestionSubmissionsVO.builder()
                            .id(questionSubmissions.getId())
                            .questionId(questionSubmissions.getQuestionId())
                            .studentNumber(studentNumber)
                            .studentAnswerCode(readFileContent(questionSubmissions.getStudentAnswer()))
                            .score(questionSubmissions.getScore())
                            .gradedByName(gradedByName)
                            .gradingCompletedAt(questionSubmissions.getGradingCompletedAt())
                            .teacherFeedback(questionSubmissions.getTeacherFeedback())
                            .build();
                })
                .toList();
    }

    /**
     * 教师查询题目提交详情
     *
     * @param questionId    题目ID
     * @param studentNumber 学生学号
     * @return 提交详情
     */
    @Override
    public List<QuestionSubmissionsVO> teacherQueryById(Long questionId, String studentNumber) {
        if (questionId == null) {
            throw new QuestionSubmissionsException(MessageConstant.ID_NOT_NULL);
        }
        if (StringUtils.isBlank(studentNumber)) {
            throw new QuestionSubmissionsException(MessageConstant.CODE_NOT_NULL);
        }
        //studentNumber -> studentId
        Users users = usersMapper.selectOne(
                new LambdaQueryWrapper<Users>()
                        .eq(Users::getUserNumber, studentNumber)
                        .select(Users::getId)
        );
        if (users == null) {
            throw new UsersException(MessageConstant.USER_NOT_FOUND);
        }
        
        List<QuestionSubmissions> questionSubmissionsList = this.list(
                new LambdaQueryWrapper<QuestionSubmissions>()
                        .eq(QuestionSubmissions::getQuestionId, questionId)
                        .eq(QuestionSubmissions::getStudentId, users.getId())
                        .select(
                                QuestionSubmissions::getId,
                                QuestionSubmissions::getQuestionId,
                                QuestionSubmissions::getStudentId,
                                QuestionSubmissions::getStudentAnswer,
                                QuestionSubmissions::getScore,
                                QuestionSubmissions::getGradedById,
                                QuestionSubmissions::getGradingCompletedAt,
                                QuestionSubmissions::getTeacherFeedback
                        ));
        
        if (questionSubmissionsList.isEmpty()) {
            return List.of();
        }
        
        // 批量查询批改人信息（避免N+1）
        List<Long> gradedByIds = questionSubmissionsList.stream()
                .map(QuestionSubmissions::getGradedById)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        
        Map<Long, String> gradedByNameMap = Map.of();
        if (!gradedByIds.isEmpty()) {
            List<Users> gradedByUsers = usersMapper.selectList(
                    new LambdaQueryWrapper<Users>()
                            .in(Users::getId, gradedByIds)
                            .select(Users::getId, Users::getRealName));
            gradedByNameMap = gradedByUsers.stream()
                    .collect(Collectors.toMap(Users::getId, Users::getRealName));
        }
        
        String studentNum = users.getUserNumber();
        Map<Long, String> finalGradedByNameMap = gradedByNameMap;
        
        return questionSubmissionsList.stream()
                .map(questionSubmissions -> {
                    String gradedByName = "系统自动批改";
                    if (questionSubmissions.getGradedById() != null) {
                        gradedByName = finalGradedByNameMap.get(questionSubmissions.getGradedById());
                    }

                    return QuestionSubmissionsVO.builder()
                            .id(questionSubmissions.getId())
                            .questionId(questionSubmissions.getQuestionId())
                            .studentNumber(studentNum)
                            .studentAnswerCode(readFileContent(questionSubmissions.getStudentAnswer()))
                            .score(questionSubmissions.getScore())
                            .gradedByName(gradedByName)
                            .gradingCompletedAt(questionSubmissions.getGradingCompletedAt())
                            .teacherFeedback(questionSubmissions.getTeacherFeedback())
                            .build();
                })
                .toList();
    }

    /**
     * 手动评分
     *
     * @param questionSubmissionsUpdateDTO 评分信息
     */
    @Override
    public void manualScore(QuestionSubmissionsUpdateDTO questionSubmissionsUpdateDTO) {
        LambdaUpdateWrapper<QuestionSubmissions> updateWrapper = new LambdaUpdateWrapper<>();
        Optional<Long> teacherIdOpt = BaseContext.getCurrentId();
        BigDecimal originalScore = null;
        BigDecimal newScore = null;
        BigDecimal scoreDifference = null;
        Long assignmentId = null;
        if (teacherIdOpt.isEmpty()) {
            throw new UserScopeException(MessageConstant.USER_SCOPE_ERROR);
        }
        //1. 先查询是否存在
        QuestionSubmissions questionSubmissions = this.getOne(
                new LambdaQueryWrapper<QuestionSubmissions>()
                        .eq(QuestionSubmissions::getId, questionSubmissionsUpdateDTO.getId())
                        .select(
                                QuestionSubmissions::getQuestionId,
                                QuestionSubmissions::getStudentId,
                                QuestionSubmissions::getScore
                        ));
        if (questionSubmissions == null) {
            throw new QuestionSubmissionsException(MessageConstant.QUESTION_SUBMISSIONS_NOT_EXIST);
        }
        //2. 先判断这个QuestionSubmissions记录是否是自己班级下的作业所产生的
        Questions questions = questionsMapper.selectOne(
                new LambdaQueryWrapper<Questions>()
                        .eq(Questions::getId, questionSubmissions.getQuestionId())
                        .select(
                                Questions::getAssignmentId,
                                Questions::getMaxScore
                        ));
        Assignments assignments = assignmentsMapper.selectOne(
                new LambdaQueryWrapper<Assignments>()
                        .eq(Assignments::getId, questions.getAssignmentId())
                        .select(Assignments::getClassId));
        Classes classes = classesMapper.selectOne(
                new LambdaQueryWrapper<Classes>()
                        .eq(Classes::getId, assignments.getClassId())
                        .select(Classes::getTeacherId));
        if (!teacherIdOpt.get().equals(classes.getTeacherId())) {
            throw new QuestionSubmissionsException(MessageConstant.QUESTION_SUBMISSIONS_NOT_MATCH);
        }
        updateWrapper.eq(QuestionSubmissions::getId, questionSubmissionsUpdateDTO.getId());

        if (questionSubmissionsUpdateDTO.getScore() != null) {
            originalScore = questionSubmissions.getScore();
            assignmentId = questions.getAssignmentId();
            BigDecimal score = questionSubmissionsUpdateDTO.getScore();
            BigDecimal maxScore = questions.getMaxScore();
            // 验证分数范围：必须在 0 到最大分值之间
            if (score.compareTo(BigDecimal.ZERO) < 0 || score.compareTo(maxScore) > 0) {
                throw new QuestionSubmissionsException("分数必须在 0 到该题最大分值 (" + maxScore + ") 之间");
            }
            newScore = questionSubmissionsUpdateDTO.getScore();
            updateWrapper.set(QuestionSubmissions::getScore, questionSubmissionsUpdateDTO.getScore());
        }
        if (StringUtils.isNotBlank(questionSubmissionsUpdateDTO.getTeacherFeedback())) {
            updateWrapper.set(QuestionSubmissions::getTeacherFeedback, questionSubmissionsUpdateDTO.getTeacherFeedback());
        }
        updateWrapper.set(QuestionSubmissions::getGradedById, teacherIdOpt.get());
        updateWrapper.set(QuestionSubmissions::getGradingCompletedAt, LocalDateTime.now());
        //3. 更新
        this.update(updateWrapper);
        //4. 更新作业总分
        if (newScore != null) {
            scoreDifference = newScore.subtract(originalScore);
        }
        Submissions submissions = submissionsMapper.selectOne(
                new LambdaQueryWrapper<Submissions>()
                        .eq(Submissions::getAssignmentId, assignmentId)
                        .eq(Submissions::getStudentId, questionSubmissions.getStudentId())
                        .select(Submissions::getTotalScore));
        BigDecimal newTotalScore = submissions.getTotalScore().add(scoreDifference);
        LambdaUpdateWrapper<Submissions> submissionsUpdateWrapper = new LambdaUpdateWrapper<>();
        submissionsUpdateWrapper.eq(Submissions::getAssignmentId, assignmentId);
        submissionsUpdateWrapper.eq(Submissions::getStudentId, questionSubmissions.getStudentId());
        submissionsUpdateWrapper.set(Submissions::getTotalScore, newTotalScore);
        submissionsMapper.update(submissionsUpdateWrapper);
    }
}
