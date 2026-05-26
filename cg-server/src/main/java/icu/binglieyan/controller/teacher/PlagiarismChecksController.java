package icu.binglieyan.controller.teacher;

import icu.binglieyan.result.Result;
import icu.binglieyan.service.PlagiarismChecksService;
import icu.binglieyan.vo.PlagiarismChecksVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 查重任务控制器类
 * @author binglieyan
 */
@RestController("teacherPlagiarismChecksController")
@RequestMapping("/teacher/plagiarismChecks")
@Tag(name = "查重任务相关接口")
@Log4j2
@RequiredArgsConstructor
public class PlagiarismChecksController {

    private final PlagiarismChecksService plagiarismChecksService;

    /**
     * 发布查重任务并开始查重
     * @param assignmentId 作业ID
     */
    @RequestMapping("/publish/{assignmentId}")
    @Operation(summary = "发布查重任务并开始查重")
    public Result<Void> publish(@PathVariable Long assignmentId) {
        log.info("查重作业ID: {}", assignmentId);
        plagiarismChecksService.publish(assignmentId);
        return Result.success();
    }

    /**
     * 获取查重任务
     * @param assignmentId 作业ID
     * @return 查重任务
     */
    @GetMapping("/queryPlagiarismChecks/{assignmentId}")
    @Operation(summary = "获取查重任务")
    public Result<List<PlagiarismChecksVO>> queryPlagiarismChecks(@PathVariable Long assignmentId) {
        return Result.success(plagiarismChecksService.queryPlagiarismChecks(assignmentId));
    }

    /**
     * 下载查重报告
     * @param id 查重任务ID
     */
    @GetMapping("/download/{id}")
    @Operation(summary = "下载查重报告")
    public void download(@PathVariable Long id, HttpServletResponse response) {
        log.info("下载查重报告ID: {}", id);
        plagiarismChecksService.download(id, response);
    }
}
