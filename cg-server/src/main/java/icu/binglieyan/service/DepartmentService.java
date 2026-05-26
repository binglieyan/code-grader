package icu.binglieyan.service;

import icu.binglieyan.dto.DepartmentDTO;
import icu.binglieyan.dto.DepartmentPageQueryDTO;
import icu.binglieyan.dto.DepartmentUpdateDTO;
import icu.binglieyan.result.PageResult;
import icu.binglieyan.vo.DepartmentPageQueryVO;

/**
 * @author binglieyan
 */
public interface DepartmentService {

    /**
     * 添加院系
     * @param departmentDTO 院系数据传输对象，包含院系基本信息
     */
    void addDepartment(DepartmentDTO departmentDTO);

    /**
     * 删除院系
     * @param departmentCode 院系编号
     */
    void deleteDepartment(String departmentCode);

    /**
     * 修改院系信息
     * @param departmentUpdateDTO 院系数据传输对象，包含院系基本信息
     */
    void updateDepartment(DepartmentUpdateDTO departmentUpdateDTO);

    /**
     * 分页查询院系信息
     * @param departmentPageQueryDTO 院系分页查询信息传递时的数据模型
     * @return 院系信息
     */
    PageResult<DepartmentPageQueryVO> pageQuery(DepartmentPageQueryDTO departmentPageQueryDTO);
}
