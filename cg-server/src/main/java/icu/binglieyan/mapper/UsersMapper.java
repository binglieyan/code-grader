package icu.binglieyan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import icu.binglieyan.dto.UsersPageQueryDTO;
import icu.binglieyan.entity.Users;
import icu.binglieyan.vo.UsersPageQueryVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author binglieyan
 */
@Mapper
public interface UsersMapper extends BaseMapper<Users>{

    /**
     * 分页查询
     * @param page 分页参数
     * @param usersPageQueryDTO 查询参数
     * @return 查询结果
     */
    Page<UsersPageQueryVO> pageQuery(@Param("page") Page<UsersPageQueryVO> page,
                   @Param("usersPageQueryDTO") UsersPageQueryDTO usersPageQueryDTO);
}
