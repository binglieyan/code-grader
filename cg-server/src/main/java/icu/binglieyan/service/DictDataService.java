package icu.binglieyan.service;

import icu.binglieyan.dto.DictDataDTO;
import icu.binglieyan.dto.DictDataPageQueryDTO;
import icu.binglieyan.dto.DictDataUpdateDTO;
import icu.binglieyan.result.PageResult;
import icu.binglieyan.vo.DictDataPageQueryVO;

/**
 * @author binglieyan
 */
public interface DictDataService {

    /**
     * 添加字典数据
     * @param dictDataDTO 字典数据传输对象
     */
    void addDictData(DictDataDTO dictDataDTO);

    /**
     * 修改字典数据
     * @param dictDataUpdateDTO 字典数据传输对象
     */
    void updateDictData(DictDataUpdateDTO dictDataUpdateDTO);

    /**
     * 分页查询字典数据
     * @param dictDataPageQueryDTO 查询条件
     * @return 查询结果
     */
    PageResult<DictDataPageQueryVO> pageQuery(DictDataPageQueryDTO dictDataPageQueryDTO);
}
