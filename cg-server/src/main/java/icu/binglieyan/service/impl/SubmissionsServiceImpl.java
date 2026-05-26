package icu.binglieyan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import icu.binglieyan.constant.MessageConstant;
import icu.binglieyan.context.BaseContext;
import icu.binglieyan.entity.*;
import icu.binglieyan.exception.DictDataException;
import icu.binglieyan.exception.SubmissionsException;
import icu.binglieyan.exception.UserScopeException;
import icu.binglieyan.mapper.*;
import icu.binglieyan.service.SubmissionsService;
import icu.binglieyan.service.TestCaseResultsService;
import icu.binglieyan.utils.IpUtil;
import icu.binglieyan.utils.UserAgentUtil;
import icu.binglieyan.vo.SubmissionsVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * @author binglieyan
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class SubmissionsServiceImpl extends ServiceImpl<SubmissionsMapper, Submissions> implements SubmissionsService {

    private final TestCaseResultsService testCaseResultsService;
    private final AssignmentsMapper assignmentsMapper;
    private final UsersMapper usersMapper;
    private final DictDataMapper dictDataMapper;
    private final QuestionsMapper questionsMapper;
    private final QuestionSubmissionsMapper questionSubmissionsMapper;

    /**
     * 添加作业提交详情
     * @param assignmentId 作业ID
     */
    @Override
    public void addSubmissions(Long assignmentId) {
        if (assignmentId == null){
            throw new SubmissionsException(MessageConstant.ID_NOT_NULL);
        }
        //1. 校验当前作业是否在作业提交有限时间段内
        LocalDateTime now = LocalDateTime.now();
        Assignments assignments = assignmentsMapper.selectOne(
                new LambdaQueryWrapper<Assignments>()
                        .eq(Assignments::getId,assignmentId)
                        .select(Assignments::getStartTime, Assignments::getDeadline));
        if (now.isBefore(assignments.getStartTime())){
            throw new SubmissionsException(MessageConstant.ASSIGNMENT_SUBMISSIONS_NOT_START);
        }
        if (now.isAfter(assignments.getDeadline())){
            throw new SubmissionsException(MessageConstant.ASSIGNMENT_SUBMISSIONS_NOT_END);
        }

        //2. 校验当前用户的当前作业下的作业提交记录是否存在
        Optional<Long> studentIdOpt = BaseContext.getCurrentId();
        if (studentIdOpt.isEmpty()){
            throw new UserScopeException(MessageConstant.USER_SCOPE_ERROR);
        }
        if (this.exists(new LambdaQueryWrapper<Submissions>()
                .eq(Submissions::getAssignmentId,assignmentId)
                .eq(Submissions::getStudentId,studentIdOpt.get()))){
            throw new SubmissionsException(MessageConstant.SUBMISSIONS_EXIST);
        }

        //3. 校验当前作业下的全部题目是否都已经提交
        List<Questions> questionsList = questionsMapper.selectList(
                new LambdaQueryWrapper<Questions>()
                        .eq(Questions::getAssignmentId,assignmentId)
                        .select(Questions::getId));
        for (Questions question : questionsList) {
            if (!questionSubmissionsMapper.exists(
                    new LambdaQueryWrapper<QuestionSubmissions>()
                            .eq(QuestionSubmissions::getQuestionId, question.getId())
                            .eq(QuestionSubmissions::getStudentId, studentIdOpt.get()))){
                throw new SubmissionsException(MessageConstant.QUESTION_SUBMISSIONS_NOT_EXIST);
            }
        }

        if (!dictDataMapper.exists(new LambdaQueryWrapper<DictData>()
                .eq(DictData::getDataCode, "SUBMITTED")
                .eq(DictData::getActive, true))) {
            throw new DictDataException(MessageConstant.DICT_DATA_NOT_FOUND);
        }

        //4. 添加作业提交详情
        Submissions submissions = Submissions.builder()
                .assignmentId(assignmentId)
                .studentId(studentIdOpt.get())
                .submittedAt(LocalDateTime.now())
                .ipAddress(IpUtil.getClientIp())
                .userAgent(UserAgentUtil.getUserAgent())
                .submissionStatusCode("SUBMITTED")
                .build();
        this.save(submissions);

        //5. 开始自动判题
        testCaseResultsService.autoJudge(assignmentId, studentIdOpt.get(), submissions.getId());
    }

    /**
     * 查询作业提交详情
     * @param assignmentId 作业ID
     * @return 提交详情
     */
    @Override
    public SubmissionsVO queryById(Long assignmentId) {
        if (assignmentId == null){
            throw new SubmissionsException(MessageConstant.ID_NOT_NULL);
        }
        Optional<Long> studentIdOpt = BaseContext.getCurrentId();
        if (studentIdOpt.isEmpty()){
            throw new UserScopeException(MessageConstant.USER_SCOPE_ERROR);
        }
        Submissions submissions = this.getOne(
                new LambdaQueryWrapper<Submissions>()
                        .eq(Submissions::getAssignmentId,assignmentId)
                        .eq(Submissions::getStudentId,studentIdOpt.get())
                        .select(
                                Submissions::getTotalScore,
                                Submissions::getSubmittedAt,
                                Submissions::getGradingCompletedAt,
                                Submissions::getSubmissionStatusCode
                        ));
        if (submissions == null){
            throw new SubmissionsException(MessageConstant.SUBMISSIONS_NOT_FOUND);
        }
        Assignments assignments = assignmentsMapper.selectOne(
                new LambdaQueryWrapper<Assignments>()
                        .eq(Assignments::getId,assignmentId)
                        .select(Assignments::getTitle));
        DictData dictData = dictDataMapper.selectOne(
                new LambdaQueryWrapper<DictData>()
                        .eq(DictData::getDataCode,submissions.getSubmissionStatusCode())
                        .select(DictData::getDataValue));
        return SubmissionsVO.builder()
                .title(assignments.getTitle())
                .totalScore(submissions.getTotalScore())
                .submittedAt(submissions.getSubmittedAt())
                .gradingCompletedAt(submissions.getGradingCompletedAt())
                .submissionStatusValue(dictData.getDataValue())
                .build();
    }

    /**
     * 教师查询作业提交详情
     * @param assignmentId 作业ID
     * @param studentNumber 学生编号
     * @return 提交详情
     */
    @Override
    public SubmissionsVO teacherQueryById(Long assignmentId, String studentNumber) {
        if (assignmentId == null){
            throw new SubmissionsException(MessageConstant.ID_NOT_NULL);
        }
        if (StringUtils.isBlank(studentNumber)){
            throw new SubmissionsException(MessageConstant.CODE_NOT_NULL);
        }
        Users users = usersMapper.selectOne(
                new LambdaQueryWrapper<Users>()
                        .eq(Users::getUserNumber,studentNumber)
                        .select(Users::getId));
        if (users == null){
            throw new SubmissionsException(MessageConstant.USER_NOT_FOUND);
        }
        Submissions submissions = this.getOne(
                new LambdaQueryWrapper<Submissions>()
                        .eq(Submissions::getAssignmentId,assignmentId)
                        .eq(Submissions::getStudentId,users.getId())
                        .select(
                                Submissions::getTotalScore,
                                Submissions::getSubmittedAt,
                                Submissions::getGradingCompletedAt,
                                Submissions::getSubmissionStatusCode
                        ));
        if (submissions == null){
            throw new SubmissionsException(MessageConstant.SUBMISSIONS_NOT_FOUND);
        }
        Assignments assignments = assignmentsMapper.selectOne(
                new LambdaQueryWrapper<Assignments>()
                        .eq(Assignments::getId,assignmentId)
                        .select(Assignments::getTitle));
        DictData dictData = dictDataMapper.selectOne(
                new LambdaQueryWrapper<DictData>()
                        .eq(DictData::getDataCode,submissions.getSubmissionStatusCode())
                        .select(DictData::getDataValue));
        return SubmissionsVO.builder()
                .title(assignments.getTitle())
                .totalScore(submissions.getTotalScore())
                .submittedAt(submissions.getSubmittedAt())
                .gradingCompletedAt(submissions.getGradingCompletedAt())
                .submissionStatusValue(dictData.getDataValue())
                .build();
    }
}
