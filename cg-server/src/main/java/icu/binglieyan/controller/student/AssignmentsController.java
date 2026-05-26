package icu.binglieyan.controller.student;

import icu.binglieyan.result.Result;
import icu.binglieyan.service.AssignmentsService;
import icu.binglieyan.vo.AssignmentsVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 作业控制器类
 * @author binglieyan
 */
@RestController("studentAssignmentsController")
@RequestMapping("/student/assignments")
@Tag(name = "作业相关接口")
@Log4j2
@RequiredArgsConstructor
public class AssignmentsController {

    private final AssignmentsService assignmentsService;

    /**
     * 查询所加入班级的作业信息
     * @return 作业信息
     */
    @GetMapping("/queryById")
    @Operation(summary = "查询所加入班级的作业信息")
    public Result<List<AssignmentsVO>> queryById(){
        log.info("查询所加入班级的作业信息");
        return Result.success(assignmentsService.queryById());
    }
}
