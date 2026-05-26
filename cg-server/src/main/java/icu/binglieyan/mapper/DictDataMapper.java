package icu.binglieyan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import icu.binglieyan.dto.DictDataPageQueryDTO;
import icu.binglieyan.entity.DictData;
import icu.binglieyan.vo.DictDataPageQueryVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author binglieyan
 */
@Mapper
public interface DictDataMapper extends BaseMapper<DictData> {
    /**
     * 分页查询字典数据
     * @param page 分页参数
     * @param dictDataPageQueryDTO 查询参数
     * @return 查询结果
     */
    Page<DictDataPageQueryVO> pageQuery(@Param("page")Page<DictDataPageQueryVO> page,
                   @Param("dictDataPageQueryDTO")DictDataPageQueryDTO dictDataPageQueryDTO);
}
