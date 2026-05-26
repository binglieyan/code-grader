package icu.binglieyan.service;

import icu.binglieyan.dto.ClassesDTO;
import icu.binglieyan.dto.ClassesPageQueryDTO;
import icu.binglieyan.dto.ClassesUpdateDTO;
import icu.binglieyan.result.PageResult;
import icu.binglieyan.vo.ClassesPageQueryVO;
import icu.binglieyan.vo.ClassesVO;
import icu.binglieyan.vo.StudentVO;

import java.util.List;

/**
 * @author binglieyan
 */
public interface ClassesService {

    /**
     * 添加班级
     * @param classesDTO 班级数据传输对象，包含班级的基本信息
     */
    void addClasses(ClassesDTO classesDTO);

    /**
     * 删除班级
     * @param classCode 班级编号
     */
    void deleteClasses(String classCode);

    /**
     * 修改班级信息
     * @param classesUpdateDTO 班级修改信息数据传输对象，包含班级的修改信息
     */
    void updateClasses(ClassesUpdateDTO classesUpdateDTO);

    /**
     * 分页查询班级信息
     * @param classesPageQueryDTO 班级分页查询数据传输对象，包含分页查询的参数
     * @return 分页查询结果
     */
    PageResult<ClassesPageQueryVO> pageQuery(ClassesPageQueryDTO classesPageQueryDTO);

    /**
     * 根据班级ID查询班级信息
     * @return 班级信息
     */
    ClassesVO queryById();

    /**
     * 根据班级编号查询学生信息
     * @param classCode 班级编号
     * @return 学生信息
     */
    List<StudentVO> queryStudentByCode(String classCode);

    /**
     * 根据教师ID查询所拥有班级信息
     * @return 班级信息
     */
    List<ClassesVO> teacherQueryClassesById();

    /**
     * 移除班级下的学生
     * @param studentNumber 班级编号
     */
    void removeStudent(String studentNumber);
}
