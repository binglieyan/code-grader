package icu.binglieyan.controller.admin;

import icu.binglieyan.dto.DictTypeDTO;
import icu.binglieyan.dto.DictTypePageQueryDTO;
import icu.binglieyan.dto.DictTypeUpdateDTO;
import icu.binglieyan.result.PageResult;
import icu.binglieyan.result.Result;
import icu.binglieyan.service.DictTypeService;
import icu.binglieyan.vo.DictTypePageQueryVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 字典类型控制器类
 * @author binglieyan
 */
@RestController("adminDictTypeController")
@RequestMapping("/admin/dictType")
@Tag(name = "字典类型相关接口")
@Log4j2
@RequiredArgsConstructor
public class DictTypeController {

    private final DictTypeService dictTypeService;

    /**
     * 添加字典类型
     * @param dictTypeDTO 字典类型DTO对象
     * @return 添加结果
     */
    @PostMapping("/addDictType")
    @Operation(summary = "添加字典类型")
    public Result<Void> addDictType(@RequestBody @Validated DictTypeDTO dictTypeDTO) {
        log.info("添加字典类型信息：{}", dictTypeDTO);
        dictTypeService.addDictType(dictTypeDTO);
        return Result.success();
    }

    /**
     * 修改字典类型
     * @param dictTypeUpdateDTO 字典类型DTO对象
     * @return 修改结果
     */
    @PutMapping("/updateDictType")
    @Operation(summary = "修改字典类型")
    public Result<Void> updateDictType(@RequestBody @Validated DictTypeUpdateDTO dictTypeUpdateDTO) {
        log.info("修改字典类型信息：{}", dictTypeUpdateDTO);
        dictTypeService.updateDictType(dictTypeUpdateDTO);
        return Result.success();
    }

    /**
     * 分页查询字典类型
     * @param dictTypePageQueryDTO 查询条件
     * @return 查询结果
     */
    @PostMapping("/page")
    @Operation(summary = "分页查询字典类型")
    public Result<PageResult<DictTypePageQueryVO>> pageQuery (@RequestBody DictTypePageQueryDTO dictTypePageQueryDTO) {
        log.info("分页查询字典类型信息：{}", dictTypePageQueryDTO);
        return Result.success(dictTypeService.pageQuery(dictTypePageQueryDTO));
    }
}
