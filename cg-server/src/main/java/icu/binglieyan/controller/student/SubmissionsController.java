package icu.binglieyan.controller.student;

import icu.binglieyan.result.Result;
import icu.binglieyan.service.SubmissionsService;
import icu.binglieyan.vo.SubmissionsVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

/**
 * 作业提交详情控制器类
 * @author binglieyan
 */
@RestController("studentSubmissionsController")
@RequestMapping("/student/submissions")
@Tag(name = "作业提交详情相关接口")
@Log4j2
@RequiredArgsConstructor
public class SubmissionsController {

    private final SubmissionsService submissionsService;

    /**
     * 提交作业
     * @param assignmentId 作业ID
     */
    @PostMapping("/addSubmissions/{assignmentId}")
    @Operation(summary = "提交作业")
    public Result<Void> addSubmissions(@PathVariable Long assignmentId){
        log.info("提交作业");
        submissionsService.addSubmissions(assignmentId);
        return Result.success();
    }

    /**
     * 查询作业提交详情
     * @param assignmentId 作业ID
     * @return 提交详情
     */
    @GetMapping("/queryById/{assignmentId}")
    @Operation(summary = "查询作业提交详情")
    public Result<SubmissionsVO> queryById(@PathVariable Long assignmentId){
        log.info("查询作业提交详情");
        return Result.success(submissionsService.queryById(assignmentId));
    }
}
