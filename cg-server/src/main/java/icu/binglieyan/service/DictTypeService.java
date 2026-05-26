package icu.binglieyan.service;

import icu.binglieyan.dto.DictTypeDTO;
import icu.binglieyan.dto.DictTypePageQueryDTO;
import icu.binglieyan.dto.DictTypeUpdateDTO;
import icu.binglieyan.result.PageResult;
import icu.binglieyan.vo.DictTypePageQueryVO;

/**
 * @author binglieyan
 */
public interface DictTypeService {

    /**
     * 添加字典类型
     * @param dictTypeDTO 字典类型DTO对象
     */
    void addDictType(DictTypeDTO dictTypeDTO);

    /**
     * 修改字典类型
     * @param dictTypeUpdateDTO 字典类型DTO对象
     */
    void updateDictType(DictTypeUpdateDTO dictTypeUpdateDTO);

    /**
     * 分页查询字典数据
     * @param dictTypePageQueryDTO 查询条件
     * @return 查询结果
     */
    PageResult<DictTypePageQueryVO> pageQuery(DictTypePageQueryDTO dictTypePageQueryDTO);
}
