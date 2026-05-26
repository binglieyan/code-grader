package icu.binglieyan.service;

import icu.binglieyan.dto.AssignmentsDTO;
import icu.binglieyan.dto.AssignmentsUpdateDTO;
import icu.binglieyan.vo.AssignmentsVO;

import java.util.List;

/**
 * @author binglieyan
 */
public interface AssignmentsService {

    /**
     * 添加作业
     * @param assignmentsDTO 作业数据传输对象，包含作业信息
     */
    void addAssignments(AssignmentsDTO assignmentsDTO);

    /**
     * 删除作业
     * @param id 作业ID
     */
    void deleteAssignments(Long id);

    /**
     * 修改作业信息
     * @param assignmentsUpdateDTO 作业数据传输对象，包含作业信息
     */
    void updateAssignments(AssignmentsUpdateDTO assignmentsUpdateDTO);


    /**
     * 根据作业ID查询作业信息
     * @return 作业信息
     */
    List<AssignmentsVO> queryById();

    /**
     * 根据班级代码查询作业信息
     * @param classCode 班级代码
     * @return 作业信息
     */
    List<AssignmentsVO> teacherQueryByCode(String classCode);
}
