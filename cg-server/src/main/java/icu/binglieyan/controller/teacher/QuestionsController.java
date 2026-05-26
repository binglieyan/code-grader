package icu.binglieyan.controller.teacher;

import icu.binglieyan.dto.QuestionsDTO;
import icu.binglieyan.dto.QuestionsUpdateDTO;
import icu.binglieyan.result.Result;
import icu.binglieyan.service.QuestionsService;
import icu.binglieyan.vo.QuestionsBriefVO;
import icu.binglieyan.vo.QuestionsVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 题目控制器类
 * @author binglieyan
 */
@RestController("teacherQuestionsController")
@RequestMapping("/teacher/questions")
@Tag(name = "题目相关接口")
@Log4j2
@RequiredArgsConstructor
public class QuestionsController {

    private final QuestionsService questionsService;

    /**
     * 添加题目
     * @param questionsDTO 题目数据传输对象，包含了题目信息
     * @return 添加结果
     */
    @PostMapping("/addQuestions")
    @Operation(summary = "添加题目")
    public Result<Void> addQuestions(@RequestBody @Validated QuestionsDTO questionsDTO){
        log.info("添加题目信息：{}", questionsDTO);
        questionsService.addQuestions(questionsDTO);
        return Result.success();
    }

    /**
     * 删除题目
     * @param id 题号
     * @return 删除结果
     */
    @DeleteMapping("/deleteQuestions/{id}")
    @Operation(summary = "删除题目")
    public Result<Void> deleteQuestions(@PathVariable Long id){
        log.info("删除题目id：{}", id);
        questionsService.deleteQuestions(id);
        return Result.success();
    }

    /**
     * 修改题目
     * @param questionsUpdateDTO 题目数据传输对象，包含了题目信息
     * @return 修改结果
     */
    @PutMapping("/updateQuestions")
    @Operation(summary = "修改题目")
    public Result<Void> updateQuestions(@RequestBody @Validated QuestionsUpdateDTO questionsUpdateDTO){
        log.info("修改题目信息：{}", questionsUpdateDTO);
        questionsService.updateQuestions(questionsUpdateDTO);
        return Result.success();
    }

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
