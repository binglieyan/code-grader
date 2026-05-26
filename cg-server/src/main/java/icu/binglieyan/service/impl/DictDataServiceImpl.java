package icu.binglieyan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import icu.binglieyan.constant.MessageConstant;
import icu.binglieyan.dto.DictDataDTO;
import icu.binglieyan.dto.DictDataPageQueryDTO;
import icu.binglieyan.dto.DictDataUpdateDTO;
import icu.binglieyan.entity.DictData;
import icu.binglieyan.exception.DictDataException;
import icu.binglieyan.mapper.DictDataMapper;
import icu.binglieyan.result.PageResult;
import icu.binglieyan.service.DictDataService;
import icu.binglieyan.vo.DictDataPageQueryVO;
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
public class DictDataServiceImpl extends ServiceImpl<DictDataMapper, DictData> implements DictDataService {

    private final DictDataMapper dictDataMapper;

    /**
     * 添加字典数据
     * @param dictDataDTO 字典数据传输对象
     */
    @Override
    public void addDictData(DictDataDTO dictDataDTO) {
        //1. 检查同一类型下 dataCode 是否已存在
        if (this.exists(new LambdaQueryWrapper<DictData>()
                .eq(DictData::getTypeCode, dictDataDTO.getTypeCode())
                .eq(DictData::getDataCode, dictDataDTO.getDataCode()))
        ) {
            throw new DictDataException(MessageConstant.DICT_DATA_EXIST);
        }

        //2. 检查同一类型下 sortOrder 是否已存在
        if (this.exists(new LambdaQueryWrapper<DictData>()
                .eq(DictData::getTypeCode, dictDataDTO.getTypeCode())
                .eq(DictData::getDataCode, dictDataDTO.getSortOrder()))
        ) {
            throw new DictDataException(MessageConstant.DICT_DATA_SORT_ORDER_EXIST);
        }

        //3. 添加字典数据
        DictData dictData = new DictData();
        BeanUtils.copyProperties(dictDataDTO, dictData);
        this.save(dictData);
    }

    /**
     * 修改字典数据
     * @param dictDataUpdateDTO 字典数据传输对象
     */
    @Override
    public void updateDictData(DictDataUpdateDTO dictDataUpdateDTO) {
        //1. 检查DictDate是否存在
        if (!this.exists(new LambdaQueryWrapper<DictData>().eq(DictData::getId, dictDataUpdateDTO.getId()))) {
            throw new DictDataException(MessageConstant.DICT_DATA_NOT_FOUND);
        }

        //2. 检查同一类型下 dataCode 是否已存在

        if (this.exists(new LambdaQueryWrapper<DictData>()
                .eq(DictData::getTypeCode, dictDataUpdateDTO.getTypeCode())
                .eq(DictData::getDataCode, dictDataUpdateDTO.getDataCode())
                .ne(DictData::getId, dictDataUpdateDTO.getId()))) {
            throw new DictDataException(MessageConstant.DICT_DATA_EXIST);
        }

        //3. 检查同一类型下 sortOrder 是否已存在
        if (this.exists(new LambdaQueryWrapper<DictData>()
                .eq(DictData::getTypeCode, dictDataUpdateDTO.getTypeCode())
                .eq(DictData::getSortOrder, dictDataUpdateDTO.getSortOrder())
                .ne(DictData::getId, dictDataUpdateDTO.getId()))) {
            throw new DictDataException(MessageConstant.DICT_DATA_SORT_ORDER_EXIST);
        }

        //4. 手动设置每个非空字段
        LambdaUpdateWrapper<DictData> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(DictData::getId, dictDataUpdateDTO.getId());
        if (StringUtils.isNotBlank(dictDataUpdateDTO.getDataCode())) {
            updateWrapper.set(DictData::getDataCode, dictDataUpdateDTO.getDataCode());
        }
        if (StringUtils.isNotBlank(dictDataUpdateDTO.getDataValue())) {
            updateWrapper.set(DictData::getDataValue, dictDataUpdateDTO.getDataValue());
        }
        if (StringUtils.isNotBlank(dictDataUpdateDTO.getDescription())) {
            updateWrapper.set(DictData::getDescription, dictDataUpdateDTO.getDescription());
        }
        if (dictDataUpdateDTO.getSortOrder() != null) {
            updateWrapper.set(DictData::getSortOrder, dictDataUpdateDTO.getSortOrder());
        }
        if (dictDataUpdateDTO.getActive() != null) {
            updateWrapper.set(DictData::getActive, dictDataUpdateDTO.getActive());
        }
        if (dictDataUpdateDTO.getActive() != null) {
            updateWrapper.set(DictData::getActive, dictDataUpdateDTO.getActive());
        }

        //5. 修改字典数据
        this.update(updateWrapper);
    }

    /**
     * 分页查询字典数据
     * @param dictDataPageQueryDTO 查询条件
     * @return 分页结果
     */
    @Override
    public PageResult<DictDataPageQueryVO> pageQuery(DictDataPageQueryDTO dictDataPageQueryDTO) {
        Integer pageNum = dictDataPageQueryDTO.getPageNum();
        Integer pageSize = dictDataPageQueryDTO.getPageSize();
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }
        Page<DictDataPageQueryVO> page = new Page<>(pageNum, pageSize);
        dictDataMapper.pageQuery(page, dictDataPageQueryDTO);
        return new PageResult<>(page.getTotal(), page.getRecords());
    }
}
