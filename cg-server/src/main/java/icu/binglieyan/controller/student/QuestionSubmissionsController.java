package icu.binglieyan.controller.student;

import icu.binglieyan.result.Result;
import icu.binglieyan.service.QuestionSubmissionsService;
import icu.binglieyan.vo.QuestionSubmissionsVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 题目提交详情控制器类
 * @author binglieyan
 */
@RestController("studentQuestionSubmissionsController")
@RequestMapping("/student/questionSubmissions")
@Tag(name = "题目提交详情相关接口")
@Log4j2
@RequiredArgsConstructor
public class QuestionSubmissionsController {

    private final QuestionSubmissionsService questionSubmissionsService;

    /**
     * 上传提交的答案文件
     *
     * @param questionId 题目ID
     * @param file 答案文件
     * @return 上传结果
     */
    @PostMapping("/upload-with-file")
    @Operation(summary = "上传提交的答案文件")
    public Result<Void> uploadWithFile(@RequestParam("questionId") Long questionId,
                                       @Schema(description = "答案文件" , requiredMode = Schema.RequiredMode.REQUIRED, type = "file")
                                       @RequestParam("file") MultipartFile file) {
        questionSubmissionsService.uploadWithFile(questionId,file);
        return Result.success();
    }

    /**
     * 学生查询题目批改情况
     * @param questionId 题目ID
     * @return 批改情况
     */
    @GetMapping("/queryById/{questionId}")
    @Operation(summary = "学生查询题目批改情况")
    public Result<List<QuestionSubmissionsVO>> queryById(@PathVariable Long questionId){
        log.info("学生查询题目批改情况");
        return Result.success(questionSubmissionsService.queryById(questionId));
    }

}
