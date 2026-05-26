package icu.binglieyan.service;

import icu.binglieyan.dto.QuestionsDTO;
import icu.binglieyan.dto.QuestionsUpdateDTO;
import icu.binglieyan.vo.QuestionsBriefVO;
import icu.binglieyan.vo.QuestionsVO;

import java.util.List;

/**
 * @author binglieyan
 */
public interface QuestionsService {
    /**
     * 添加题目
     * @param questionsDTO 题目数据传输对象，包含了题目信息
     */
    void addQuestions(QuestionsDTO questionsDTO);

    /**
     * 删除题目
     * @param id 题号
     */
    void deleteQuestions(Long id);

    /**
     * 修改题目
     * @param questionsUpdateDTO 题目数据传输对象，包含了题目信息
     */
    void updateQuestions(QuestionsUpdateDTO questionsUpdateDTO);

    /**
     * 根据作业ID获取所有题目信息
     * @param assignmentId 作业ID
     * @return 获取结果
     */
    List<QuestionsBriefVO> getQuestions(Long assignmentId);

    /**
     * 根据题目ID获取题目信息
     * @param id 题目ID
     * @return 获取结果
     */
    QuestionsVO getQuestionsById(Long id);
}
