package icu.binglieyan.controller.admin;

import icu.binglieyan.dto.DepartmentDTO;
import icu.binglieyan.dto.DepartmentPageQueryDTO;
import icu.binglieyan.dto.DepartmentUpdateDTO;
import icu.binglieyan.result.PageResult;
import icu.binglieyan.result.Result;
import icu.binglieyan.service.DepartmentService;
import icu.binglieyan.vo.DepartmentPageQueryVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 院系控制器类
 * @author binglieyan
 */
@RestController("adminDepartmentController")
@RequestMapping("/admin/department")
@Tag(name = "院系相关接口")
@Log4j2
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    /**
     * 添加院系
     * @param departmentDTO 部门数据传输对象，包含院系基本信息
     * @return 添加结果
     */
    @PostMapping("/addDepartment")
    @Operation(summary = "添加院系")
    public Result<Void> addDepartment(@RequestBody @Validated DepartmentDTO departmentDTO){
        log.info("添加院系：{}", departmentDTO);
        departmentService.addDepartment(departmentDTO);
        return Result.success();
    }

    /**
     * 删除院系
     * @param departmentCode 院系编号
     */
    @DeleteMapping("/deleteDepartment/{departmentCode}")
    @Operation(summary = "删除院系")
    public Result<Void> deleteDepartment(@PathVariable String departmentCode){
        log.info("删除院系编号：{}", departmentCode);
        departmentService.deleteDepartment(departmentCode);
        return Result.success();
    }

    /**
     * 修改院系信息
     * @param departmentUpdateDTO 院系信息
     * @return 修改结果
     */
    @PutMapping("/updateDepartment/{departmentCode}")
    @Operation(summary = "修改院系信息")
    public Result<Void> updateDepartment(@RequestBody @Validated DepartmentUpdateDTO departmentUpdateDTO){
        log.info("修改的院系信息：{}", departmentUpdateDTO);
        departmentService.updateDepartment(departmentUpdateDTO);
        return Result.success();
    }

    /**
     * 分页查询院系信息
     * @param departmentPageQueryDTO 查询条件
     * @return 院系信息
     */
    @PostMapping("/page")
    @Operation(summary = "分页查询院系信息")
    public Result<PageResult<DepartmentPageQueryVO>> pageQuery(@RequestBody DepartmentPageQueryDTO departmentPageQueryDTO){
        log.info("分页查询院系编号：{}", departmentPageQueryDTO);
        return Result.success(departmentService.pageQuery(departmentPageQueryDTO));
    }

}
