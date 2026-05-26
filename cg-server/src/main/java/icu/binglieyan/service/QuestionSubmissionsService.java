package icu.binglieyan.service;

import icu.binglieyan.dto.QuestionSubmissionsUpdateDTO;
import icu.binglieyan.vo.QuestionSubmissionsVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


/**
 * @author binglieyan
 */
public interface QuestionSubmissionsService {
    /**
     * 上传答案
     * @param questionId 题目ID
     * @param file 答案文件
     */
    void uploadWithFile(Long questionId, MultipartFile file);

    /**
     * 查询批改情况
     * @param questionId 题目ID
     * @return 批改情况
     */
    List<QuestionSubmissionsVO> queryById(Long questionId);

    /**
     * 教师查询批改情况
     * @param questionId 题目ID
     * @param studentNumber 学生学号
     * @return 批改情况
     */
    List<QuestionSubmissionsVO> teacherQueryById(Long questionId, String studentNumber);

    /**
     * 手动评分
     * @param questionSubmissionsUpdateDTO 评分信息
     */
    void manualScore(QuestionSubmissionsUpdateDTO questionSubmissionsUpdateDTO);
}
