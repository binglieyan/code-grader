package icu.binglieyan.controller.student;

import icu.binglieyan.result.Result;
import icu.binglieyan.service.QuestionsService;
import icu.binglieyan.vo.QuestionsBriefVO;
import icu.binglieyan.vo.QuestionsVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 题目控制器类
 * @author binglieyan
 */
@RestController("studentQuestionsController")
@RequestMapping("/student/questions")
@Tag(name = "题目相关接口")
@Log4j2
@RequiredArgsConstructor
public class QuestionsController {

    private final QuestionsService questionsService;

    /**
     * 根据作业ID获取所有题目信息
     * @param assignmentId 作业ID
     * @return 获取结果
     */
    @GetMapping("/getQuestions/{assignmentId}")
    @Operation(summary = "根据作业ID获取所有题目信息")
    public Result<List<QuestionsBriefVO>> getQuestions(@PathVariable Long assignmentId){
        log.info("作业ID：{}", assignmentId);
        return Result.success(questionsService.getQuestions(assignmentId));
    }

    /**
     * 根据题目ID查询题目具体信息
     * @param id 题目ID
     * @return 获取结果
     */
    @GetMapping("/getQuestionsById/{id}")
    @Operation(summary = "根据题目ID查询题目具体信息")
    public Result<QuestionsVO> getQuestionsById(@PathVariable Long id){
        log.info("题目ID：{}", id);
        return Result.success(questionsService.getQuestionsById(id));
    }

}
