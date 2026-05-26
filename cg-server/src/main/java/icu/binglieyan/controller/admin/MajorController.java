package icu.binglieyan.controller.admin;

import icu.binglieyan.dto.MajorDTO;
import icu.binglieyan.dto.MajorPageQueryDTO;
import icu.binglieyan.dto.MajorUpdateDTO;
import icu.binglieyan.result.PageResult;
import icu.binglieyan.result.Result;
import icu.binglieyan.service.MajorService;
import icu.binglieyan.vo.MajorPageQueryVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 专业控制器类
 * @author binglieyan
 */
@RestController("adminMajorController")
@RequestMapping("/admin/major")
@Tag(name = "专业相关接口")
@Log4j2
@RequiredArgsConstructor
public class MajorController {
    private final MajorService majorService;

    /**
     * 添加专业
     * @param majorDTO 专业数据传输对象，包含了专业信息
     * @return 添加结果
     */
    @PostMapping("/addMajor")
    @Operation(summary = "添加专业")
    public Result<Void> addMajor(@RequestBody @Validated MajorDTO majorDTO){
        log.info("添加专业信息：{}", majorDTO);
        majorService.addMajor(majorDTO);
        return Result.success();
    }

    /**
     * 删除专业
     * @param majorCode 专业编号
     * @return 删除结果
     */
    @DeleteMapping("/deleteMajor/{majorCode}")
    @Operation(summary = "删除专业")
    public Result<Void> deleteMajor(@PathVariable String majorCode){
        log.info("删除专业编号：{}", majorCode);
        majorService.deleteMajor(majorCode);
        return Result.success();
    }

    /**
     * 修改专业信息
     * @param majorUpdateDTO 专业信息
     * @return 修改结果
     */
    @PutMapping("/updateMajor")
    @Operation(summary = "修改专业信息")
    public Result<Void> updateMajor(@RequestBody @Validated MajorUpdateDTO majorUpdateDTO){
        log.info("修改的专业信息：{}", majorUpdateDTO);
        majorService.updateMajor(majorUpdateDTO);
        return Result.success();
    }

    /**
     * 分页查询专业信息
     * @param majorPageQueryDTO 查询条件
     * @return 查询结果
     */
    @PostMapping("/page")
    @Operation(summary = "分页查询专业信息")
    public Result<PageResult<MajorPageQueryVO>> pageQuery(@RequestBody MajorPageQueryDTO majorPageQueryDTO){
        log.info("分页查询专业信息：{}", majorPageQueryDTO);
        return Result.success(majorService.pageQuery(majorPageQueryDTO));
    }
}
