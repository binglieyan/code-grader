package icu.binglieyan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import icu.binglieyan.dto.DepartmentPageQueryDTO;
import icu.binglieyan.entity.Department;
import icu.binglieyan.vo.DepartmentPageQueryVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author binglieyan
 */
@Mapper
public interface DepartmentMapper extends BaseMapper<Department> {

    /**
     * 分页查询院系信息
     * @param page 分页参数
     * @param departmentPageQueryDTO 查询条件
     * @return 院系信息
     */
    Page<DepartmentPageQueryVO> pageQuery(@Param("page")Page<DepartmentPageQueryVO> page,
                   @Param("departmentPageQueryDTO") DepartmentPageQueryDTO departmentPageQueryDTO);
}
