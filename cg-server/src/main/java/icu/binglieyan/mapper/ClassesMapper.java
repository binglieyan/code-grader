package icu.binglieyan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import icu.binglieyan.dto.ClassesPageQueryDTO;
import icu.binglieyan.entity.Classes;
import icu.binglieyan.vo.ClassesPageQueryVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author binglieyan
 */
@Mapper
public interface ClassesMapper extends BaseMapper<Classes> {

    /**
     * 分页查询
     * @param page 分页参数
     * @param classesPageQueryDTO 查询参数
     * @return 查询结果
     */
    Page<ClassesPageQueryVO> pageQuery(@Param("page")Page<ClassesPageQueryVO> page,
                                       @Param("classesPageQueryDTO") ClassesPageQueryDTO classesPageQueryDTO);
}
