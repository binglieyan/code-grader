package icu.binglieyan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import icu.binglieyan.constant.MessageConstant;
import icu.binglieyan.dto.DictTypeDTO;
import icu.binglieyan.dto.DictTypePageQueryDTO;
import icu.binglieyan.dto.DictTypeUpdateDTO;
import icu.binglieyan.entity.DictType;
import icu.binglieyan.exception.DictTypeException;
import icu.binglieyan.mapper.DictTypeMapper;
import icu.binglieyan.result.PageResult;
import icu.binglieyan.service.DictTypeService;
import icu.binglieyan.vo.DictTypePageQueryVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;


/**
 * @author binglieyan
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class DictTypeServiceImpl extends ServiceImpl<DictTypeMapper, DictType> implements DictTypeService {

    private final DictTypeMapper dictTypeMapper;

    /**
     * 添加字典类型
     * @param dictTypeDTO 字典类型DTO对象
     */
    @Override
    public void addDictType(DictTypeDTO dictTypeDTO) {
        //1. 检查type_code是否已经存在
        LambdaQueryWrapper<DictType> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DictType::getTypeCode, dictTypeDTO.getTypeCode());
        if (this.exists(queryWrapper)) {
            throw new DictTypeException(MessageConstant.DICT_TYPE_CODE_EXISTS);
        }

        //2. 添加字典类型
        DictType dictType = new DictType();
        BeanUtils.copyProperties(dictTypeDTO, dictType);
        this.save(dictType);
    }

    /**
     * 修改字典类型
     * @param dictTypeUpdateDTO 字典类型DTO对象
     */
    @Override
    public void updateDictType(DictTypeUpdateDTO dictTypeUpdateDTO) {
        //1. 检查DictType是否存在
        if (!this.exists(new LambdaQueryWrapper<DictType>().eq(DictType::getId, dictTypeUpdateDTO.getId()))) {
            throw new DictTypeException(MessageConstant.DICT_TYPE_NOT_EXISTS);
        }

        //2. 检查type_code是否已经存在
        if (this.exists(new LambdaQueryWrapper<DictType>()
                .eq(DictType::getTypeCode, dictTypeUpdateDTO.getTypeCode())
                .ne(DictType::getId, dictTypeUpdateDTO.getId()))) {
            throw new DictTypeException(MessageConstant.DICT_TYPE_CODE_EXISTS);
        }

        //3. 手动设置每个非空字段
        LambdaUpdateWrapper<DictType> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(DictType::getId, dictTypeUpdateDTO.getId());
        if (StringUtils.isNotBlank(dictTypeUpdateDTO.getTypeCode())) {
            updateWrapper.set(DictType::getTypeCode, dictTypeUpdateDTO.getTypeCode());
        }
        if (StringUtils.isNotBlank(dictTypeUpdateDTO.getTypeName())) {
            updateWrapper.set(DictType::getTypeName, dictTypeUpdateDTO.getTypeName());
        }
        if (StringUtils.isNotBlank(dictTypeUpdateDTO.getDescription())) {
            updateWrapper.set(DictType::getDescription, dictTypeUpdateDTO.getDescription());
        }
        if (dictTypeUpdateDTO.getSystem() != null) {
            updateWrapper.set(DictType::getSystem, dictTypeUpdateDTO.getSystem());
        }

        //4. 更新字典类型
        this.update(updateWrapper);
    }

    /**
     * 分页查询字典类型
     * @param dictTypePageQueryDTO 查询条件
     * @return 字典类型分页结果
     */
    @Override
    public PageResult<DictTypePageQueryVO> pageQuery(DictTypePageQueryDTO dictTypePageQueryDTO) {
        Integer pageNum = dictTypePageQueryDTO.getPageNum();
        Integer pageSize = dictTypePageQueryDTO.getPageSize();
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }
        Page<DictTypePageQueryVO> page = new Page<>(pageNum, pageSize);
        dictTypeMapper.pageQuery(page, dictTypePageQueryDTO);
        return new PageResult<>(page.getTotal(), page.getRecords());
    }
}
