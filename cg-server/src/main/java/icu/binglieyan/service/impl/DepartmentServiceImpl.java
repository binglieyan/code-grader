package icu.binglieyan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import icu.binglieyan.constant.MessageConstant;
import icu.binglieyan.dto.DepartmentDTO;
import icu.binglieyan.dto.DepartmentPageQueryDTO;
import icu.binglieyan.dto.DepartmentUpdateDTO;
import icu.binglieyan.entity.Department;
import icu.binglieyan.exception.DepartmentException;
import icu.binglieyan.mapper.DepartmentMapper;
import icu.binglieyan.result.PageResult;
import icu.binglieyan.service.DepartmentService;
import icu.binglieyan.vo.DepartmentPageQueryVO;
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
public class DepartmentServiceImpl extends ServiceImpl<DepartmentMapper, Department> implements DepartmentService {

    private final DepartmentMapper departmentMapper;

    /**
     * 添加院系
     * @param departmentDTO 院系数据传输对象，包含院系基本信息
     */
    @Override
    public void addDepartment(DepartmentDTO departmentDTO) {
        //1. 校验院系代码是否存在
        if(StringUtils.isNotBlank(departmentDTO.getDepartmentCode())){
            if (this.exists(new LambdaQueryWrapper<Department>().eq(Department::getDepartmentCode, departmentDTO.getDepartmentCode()))){
                throw new DepartmentException(MessageConstant.DEPARTMENT_CODE_EXISTS);
            }
        }

        //2. 校验院系名称是否存在
        if(StringUtils.isNotBlank(departmentDTO.getDepartmentName())){
            if (this.exists(new LambdaQueryWrapper<Department>().eq(Department::getDepartmentName, departmentDTO.getDepartmentName()))){
                throw new DepartmentException(MessageConstant.DEPARTMENT_NAME_EXISTS);
            }
        }

        Department department = new Department();
        BeanUtils.copyProperties(departmentDTO, department);
        this.save(department);
    }

    /**
     * 删除院系
     * @param departmentCode 院系编号
     */
    @Override
    public void deleteDepartment(String departmentCode) {
        if (StringUtils.isBlank(departmentCode)) {
            throw new DepartmentException(MessageConstant.CODE_NOT_NULL);
        }
        // 查询院系是否存在
        if (!this.exists(new LambdaQueryWrapper<Department>().eq(Department::getDepartmentCode, departmentCode))){
            throw new DepartmentException(MessageConstant.DEPARTMENT_NOT_FOUND);
        }
        this.lambdaUpdate().eq(Department::getDepartmentCode, departmentCode).remove();
    }

    /**
     * 修改院系信息
     * @param departmentUpdateDTO 院系修改信息数据传输对象，包含院系修改信息
     */
    @Override
    public void updateDepartment(DepartmentUpdateDTO departmentUpdateDTO) {
        //1. 查询院系是否存在
        if (!this.exists(new LambdaQueryWrapper<Department>().eq(Department::getDepartmentCode, departmentUpdateDTO.getDepartmentCode()))){
            throw new DepartmentException(MessageConstant.DEPARTMENT_NOT_FOUND);
        }
        //2. 手动设置每个非空字段
        LambdaUpdateWrapper<Department> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Department::getDepartmentCode, departmentUpdateDTO.getDepartmentCode());
        if (StringUtils.isNotBlank(departmentUpdateDTO.getDepartmentCode())) {
            updateWrapper.set(Department::getDepartmentCode, departmentUpdateDTO.getDepartmentCode());
        }
        if (StringUtils.isNotBlank(departmentUpdateDTO.getDepartmentName())) {
            updateWrapper.set(Department::getDepartmentName, departmentUpdateDTO.getDepartmentName());
        }

        //3. 更新院系
        this.update(updateWrapper);
    }

    /**
     * 分页查询院系信息
     * @param departmentPageQueryDTO 院系分页查询信息数据传输对象，包含院系分页查询信息
     * @return 院系分页查询结果
     */
    @Override
    public PageResult<DepartmentPageQueryVO> pageQuery(DepartmentPageQueryDTO departmentPageQueryDTO) {
        Integer pageNum = departmentPageQueryDTO.getPageNum();
        Integer pageSize = departmentPageQueryDTO.getPageSize();
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }
        // 使用MyBatis-Plus进行分页查询
        Page<DepartmentPageQueryVO> page = new Page<>(pageNum, pageSize);
        departmentMapper.pageQuery(page, departmentPageQueryDTO);
        return new PageResult<>(page.getTotal(), page.getRecords());
    }
}
