package icu.binglieyan.controller.teacher;

import icu.binglieyan.dto.AssignmentsDTO;
import icu.binglieyan.dto.AssignmentsUpdateDTO;
import icu.binglieyan.result.Result;
import icu.binglieyan.service.AssignmentsService;
import icu.binglieyan.vo.AssignmentsVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 作业控制器类
 * @author binglieyan
 */
@RestController("teacherAssignmentsController")
@RequestMapping("/teacher/assignments")
@Tag(name = "作业相关接口")
@Log4j2
@RequiredArgsConstructor
public class AssignmentsController {

    private final AssignmentsService assignmentsService;

    /**
     * 添加作业
     * @param assignmentsDTO 作业数据传输对象，包含作业信息
     * @return 添加结果
     */
    @PostMapping("/addAssignments")
    @Operation(summary = "添加作业")
    public Result<Void> addAssignments(@RequestBody @Validated AssignmentsDTO assignmentsDTO){
        log.info("添加作业信息：{}", assignmentsDTO);
        assignmentsService.addAssignments(assignmentsDTO);
        return Result.success();
    }

    /**
     * 删除作业
     * @param id 作业ID
     */
    @DeleteMapping("/deleteAssignments/{id}")
    @Operation(summary = "删除作业")
    public Result<Void> deleteAssignments(@PathVariable Long id){
        log.info("删除作业ID：{}", id);
        assignmentsService.deleteAssignments(id);
        return Result.success();
    }

    /**
     * 修改作业信息
     * @param assignmentsUpdateDTO 作业数据传输对象，包含作业信息
     * @return 修改结果
     */
    @PutMapping("/updateAssignments")
    @Operation(summary = "修改作业信息")
    public Result<Void> updateAssignments(@RequestBody @Validated AssignmentsUpdateDTO assignmentsUpdateDTO){
        log.info("修改作业信息：{}", assignmentsUpdateDTO);
        assignmentsService.updateAssignments(assignmentsUpdateDTO);
        return Result.success();
    }

    /**
     * 根据班级代码查询作业信息
     * @param classCode 班级代码
     * @return 作业信息
     */
    @GetMapping("/teacherQueryById/{classCode}")
    @Operation(summary = "查询当前班级的作业信息")
    public Result<List<AssignmentsVO>> teacherQueryByCode(@PathVariable String classCode){
        log.info("查询当前班级的作业信息");
        return Result.success(assignmentsService.teacherQueryByCode(classCode));
    }
}
