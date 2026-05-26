package icu.binglieyan.controller.admin;

import icu.binglieyan.dto.DictDataDTO;
import icu.binglieyan.dto.DictDataPageQueryDTO;
import icu.binglieyan.dto.DictDataUpdateDTO;
import icu.binglieyan.result.PageResult;
import icu.binglieyan.result.Result;
import icu.binglieyan.service.DictDataService;
import icu.binglieyan.vo.DictDataPageQueryVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 字典数据控制器类
 * @author binglieyan
 */
@RestController("adminDictDataController")
@RequestMapping("/admin/dictData")
@Tag(name = "字典数据相关接口")
@Log4j2
@RequiredArgsConstructor
public class DictDataController {

    private final DictDataService dictDataService;

    /**
     * 添加字典数据
     * @param dictDataDTO 字典数据传输对象，包含字典数据信息
     * @return 添加结果
     */
    @PostMapping("/addDictData")
    @Operation(summary = "添加字典数据")
    public Result<Void> addDictData(@RequestBody @Validated DictDataDTO dictDataDTO) {
        log.info("添加字典数据信息：{}", dictDataDTO);
        dictDataService.addDictData(dictDataDTO);
        return Result.success();
    }

    /**
     * 更新字典数据
     * @param dictDataUpdateDTO 字典数据传输对象，包含字典数据信息
     * @return 更新结果
     */
    @PutMapping("/updateDictData")
    @Operation(summary = "更新字典数据")
    public Result<Void> updateDictData(@RequestBody @Validated DictDataUpdateDTO dictDataUpdateDTO) {
        log.info("更新字典数据信息：{}", dictDataUpdateDTO);
        dictDataService.updateDictData(dictDataUpdateDTO);
        return Result.success();
    }

    /**
     * 分页查询字典数据
     * @param dictDataPageQueryDTO 查询条件
     * @return 查询结果
     */
    @PostMapping("/page")
    @Operation(summary = "分页查询字典数据")
    public Result<PageResult<DictDataPageQueryVO>> pageQuery(@RequestBody DictDataPageQueryDTO dictDataPageQueryDTO) {
        log.info("分页查询字典数据信息：{}", dictDataPageQueryDTO);
        return Result.success(dictDataService.pageQuery(dictDataPageQueryDTO));
    }
}
