package icu.binglieyan.controller.teacher;

import icu.binglieyan.dto.QuestionSubmissionsUpdateDTO;
import icu.binglieyan.result.Result;
import icu.binglieyan.service.QuestionSubmissionsService;
import icu.binglieyan.vo.QuestionSubmissionsVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 题目提交详情控制器类
 * @author binglieyan
 */
@RestController("teacherQuestionSubmissionsController")
@RequestMapping("/teacher/questionSubmissions")
@Tag(name = "题目提交详情相关接口")
@Log4j2
@RequiredArgsConstructor
public class QuestionSubmissionsController {

    private final QuestionSubmissionsService questionSubmissionsService;

    /**
     * 教师查询题目批改情况
     * @param questionId 题目ID
     * @return 批改情况
     */
    @GetMapping("/teacherQueryById/{questionId}/{studentNumber}")
    @Operation(summary = "教师查询题目批改情况")
    public Result<List<QuestionSubmissionsVO>> teacherQueryById(@PathVariable Long questionId, @PathVariable String studentNumber){
        log.info("教师查询题目批改情况，题目id{}，学生代码{}", questionId, studentNumber );
        return Result.success(questionSubmissionsService.teacherQueryById(questionId,studentNumber));
    }

    /**
     * 教师手动评分
     * @param questionSubmissionsUpdateDTO 题目提交详情数据传输对象，包含题目提交详情的基础信息
     * @return 修改结果
     */
    @PutMapping("/manualScore")
    @Operation(summary = "教师手动评分")
    public Result<Void> manualScore(@RequestBody QuestionSubmissionsUpdateDTO questionSubmissionsUpdateDTO){
        log.info("教师评分结果：{}",questionSubmissionsUpdateDTO);
        questionSubmissionsService.manualScore(questionSubmissionsUpdateDTO);
        return Result.success();
    }

}
