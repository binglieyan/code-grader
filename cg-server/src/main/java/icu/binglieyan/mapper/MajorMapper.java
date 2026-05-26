package icu.binglieyan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import icu.binglieyan.dto.MajorPageQueryDTO;
import icu.binglieyan.entity.Major;
import icu.binglieyan.vo.MajorPageQueryVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author binglieyan
 */
@Mapper
public interface MajorMapper extends BaseMapper<Major> {

    /**
     * 分页查询
     * @param page 分页参数
     * @param majorPageQueryDTO 查询参数
     * @return 专业信息
     */
    Page<MajorPageQueryVO> pageQuery(@Param("page") Page<MajorPageQueryVO> page,
                   @Param("majorPageQueryDTO")MajorPageQueryDTO majorPageQueryDTO);
}
