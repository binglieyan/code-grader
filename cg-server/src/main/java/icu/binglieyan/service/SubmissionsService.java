package icu.binglieyan.service;

import icu.binglieyan.vo.SubmissionsVO;

/**
 * @author binglieyan
 */
public interface SubmissionsService {

    /**
     * 添加作业提交详情
     * @param assignmentId 作业ID
     */
    void addSubmissions(Long assignmentId);

    /**
     * 查询作业提交详情
     * @param assignmentId 作业ID
     * @return 提交详情
     */
    SubmissionsVO queryById(Long assignmentId);

    /**
     * 教师查询题目批改情况
     * @param assignmentId 作业ID
     * @param studentNumber 学生编号
     * @return 批改情况
     */
    SubmissionsVO teacherQueryById(Long assignmentId, String studentNumber);
}
