package icu.binglieyan.controller.teacher;

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
@RestController("teacherSubmissionsController")
@RequestMapping("/teacher/submissions")
@Tag(name = "作业提交详情相关接口")
@Log4j2
@RequiredArgsConstructor
public class SubmissionsController {

    private final SubmissionsService submissionsService;

    /**
     * 查询作业提交详情
     * @param assignmentId 作业ID
     * @param studentNumber 学生编号
     * @return 提交详情
     */
    @GetMapping("/queryById/{assignmentId}/{studentNumber}")
    @Operation(summary = "查询作业提交详情")
    public Result<SubmissionsVO> teacherQueryById(@PathVariable Long assignmentId, @PathVariable String studentNumber){
        return Result.success(submissionsService.teacherQueryById(assignmentId,studentNumber));
    }
}
