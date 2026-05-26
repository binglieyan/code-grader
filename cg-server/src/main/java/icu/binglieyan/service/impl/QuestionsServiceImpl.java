package icu.binglieyan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import icu.binglieyan.constant.MessageConstant;
import icu.binglieyan.dto.QuestionsDTO;
import icu.binglieyan.dto.QuestionsUpdateDTO;
import icu.binglieyan.entity.Questions;
import icu.binglieyan.exception.QuestionsException;
import icu.binglieyan.mapper.QuestionsMapper;
import icu.binglieyan.service.QuestionsService;
import icu.binglieyan.vo.QuestionsBriefVO;
import icu.binglieyan.vo.QuestionsVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author binglieyan
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class QuestionsServiceImpl extends ServiceImpl<QuestionsMapper, Questions> implements QuestionsService {

    /**
     * 添加题目
     * @param questionsDTO 题目数据传输对象，包含了题目信息
     */
    @Override
    public void addQuestions(QuestionsDTO questionsDTO) {
        Questions questions = new Questions();
        BeanUtils.copyProperties(questionsDTO, questions);
        this.save(questions);
    }

    /**
     * 删除题目
     * @param id 题号
     */
    @Override
    public void deleteQuestions(Long id) {
        if (id == null) {
            throw new QuestionsException(MessageConstant.ID_NOT_NULL);
        }
        //查询题目是否存在
        if (!this.exists(new LambdaQueryWrapper<Questions>().eq(Questions::getId, id))) {
            throw new QuestionsException(MessageConstant.QUESTIONS_NOT_FOUND);
        }
        this.removeById(id);
    }

    /**
     * 修改题目信息
     * @param questionsUpdateDTO 题目数据传输对象，包含了题目信息
     */
    @Override
    public void updateQuestions(QuestionsUpdateDTO questionsUpdateDTO) {
        //1. 查询题目是否存在
        Questions questions = this.getOne(new LambdaQueryWrapper<Questions>().eq(Questions::getId, questionsUpdateDTO.getId()));
        if (questions == null) {
            throw new QuestionsException(MessageConstant.QUESTIONS_NOT_FOUND);
        }
        //2. 手动设置每个非空字段
        LambdaUpdateWrapper<Questions> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Questions::getId, questions.getId());
        if (questionsUpdateDTO.getQuestionOrder() != null){
            updateWrapper.set(Questions::getQuestionOrder, questionsUpdateDTO.getQuestionOrder());
        }
        if (StringUtils.isNotBlank(questionsUpdateDTO.getTitle())){
            updateWrapper.set(Questions::getTitle, questionsUpdateDTO.getTitle());
        }
        if (StringUtils.isNotBlank(questionsUpdateDTO.getContent())){
            updateWrapper.set(Questions::getContent, questionsUpdateDTO.getContent());
        }
        if (StringUtils.isNotBlank(questionsUpdateDTO.getInitialCode())){
            updateWrapper.set(Questions::getInitialCode, questionsUpdateDTO.getInitialCode());
        }
        if (questionsUpdateDTO.getMaxScore() != null){
            updateWrapper.set(Questions::getMaxScore, questionsUpdateDTO.getMaxScore());
        }
        // 3. 更新题目
        this.update(updateWrapper);
    }

    /**
     * 根据作业ID获取所有题目信息
     * @param assignmentId 作业ID
     * @return 作业题目
     */
    @Override
    public List<QuestionsBriefVO> getQuestions(Long assignmentId) {
        if (assignmentId == null) {
            throw new QuestionsException(MessageConstant.ID_NOT_NULL);
        }
        List<Questions> questionsList = this.list(new LambdaQueryWrapper<Questions>().eq(Questions::getAssignmentId, assignmentId).orderByAsc(Questions::getQuestionOrder));
        return questionsList.stream()
                .map(questions -> {
                    QuestionsBriefVO questionsBriefVO = new QuestionsBriefVO();
                    BeanUtils.copyProperties(questions, questionsBriefVO);
                    return questionsBriefVO;
                })
                .toList();
    }

    /**
     * 根据题目ID获取题目信息
     * @param id 作业ID
     * @return 题目信息
     */
    @Override
    public QuestionsVO getQuestionsById(Long id) {
        if (id == null) {
            throw new QuestionsException(MessageConstant.ID_NOT_NULL);
        }
        Questions questions = this.getOne(new LambdaQueryWrapper<Questions>()
                .eq(Questions::getId, id)
                .select(
                        Questions::getId,
                        Questions::getTitle,
                        Questions::getContent,
                        Questions::getInitialCode,
                        Questions::getMaxScore
                ));
        QuestionsVO questionsVO = new QuestionsVO();
        BeanUtils.copyProperties(questions, questionsVO);
        return questionsVO;
    }
}
