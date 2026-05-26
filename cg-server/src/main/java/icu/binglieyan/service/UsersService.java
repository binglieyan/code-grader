package icu.binglieyan.service;

import icu.binglieyan.dto.UsersDTO;
import icu.binglieyan.dto.UsersLoginDTO;
import icu.binglieyan.dto.UsersPageQueryDTO;
import icu.binglieyan.dto.UsersUpdateDTO;
import icu.binglieyan.result.PageResult;
import icu.binglieyan.vo.StudentVO;
import icu.binglieyan.vo.TeacherVO;
import icu.binglieyan.vo.UsersLoginVO;
import icu.binglieyan.vo.UsersPageQueryVO;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @author binglieyan
 */
public interface UsersService {

    /**
     * 添加用户
     * @param usersDTO 用户数据传输对象，包含用户的基本信息
     */
    void addUsers(UsersDTO usersDTO);

    /**
     * 删除用户
     * @param userNumber 用户编号
     */
    void deleteUsers(String userNumber);

    /**
     * 修改用户信息
     * @param usersUpdateDTO 用户修改信息
     */
    void updateUsers(UsersUpdateDTO usersUpdateDTO);

    /**
     * 分页查询用户信息
     *
     * @param usersPageQueryDTO 用户分页查询信息
     * @return 用户分页查询结果
     */
    PageResult<UsersPageQueryVO> pageQuery(UsersPageQueryDTO usersPageQueryDTO);

    /**
     * 用户登录
     *
     * @param usersLoginDTO 用户登录信息
     * @param request 请求对象
     * @return 用户登录结果
     */
    UsersLoginVO usersLogin(UsersLoginDTO usersLoginDTO, HttpServletRequest request);

    /**
     * 学生加入班级
     * @param classCode 班级代码
     */
    void joinClass(String classCode);

    /**
     * 查询学生信息
     * @return 学生信息
     */
    StudentVO studentQueryById();

    /**
     * 获取教师信息
     * @return 教师信息
     */
    TeacherVO teacherQueryById();
}
