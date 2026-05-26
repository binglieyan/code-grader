package icu.binglieyan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import icu.binglieyan.dto.DictTypePageQueryDTO;
import icu.binglieyan.entity.DictType;
import icu.binglieyan.vo.DictTypePageQueryVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author binglieyan
 */
@Mapper
public interface DictTypeMapper extends BaseMapper<DictType> {
    /**
     * 分页查询字典类型
     * @param page 分页参数
     * @param dictTypePageQueryDTO 查询条件
     * @return 字典类型信息
     */
    Page<DictTypePageQueryVO> pageQuery(@Param("page") Page<DictTypePageQueryVO> page,
                   @Param("dictTypePageQueryDTO") DictTypePageQueryDTO dictTypePageQueryDTO);
}
