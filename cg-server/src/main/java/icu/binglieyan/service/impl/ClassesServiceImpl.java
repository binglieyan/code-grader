package icu.binglieyan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import icu.binglieyan.constant.MessageConstant;
import icu.binglieyan.context.BaseContext;
import icu.binglieyan.dto.ClassesDTO;
import icu.binglieyan.dto.ClassesPageQueryDTO;
import icu.binglieyan.dto.ClassesUpdateDTO;
import icu.binglieyan.entity.Classes;
import icu.binglieyan.entity.QuestionSubmissions;
import icu.binglieyan.entity.Submissions;
import icu.binglieyan.entity.Users;
import icu.binglieyan.exception.ClassesException;
import icu.binglieyan.exception.UserScopeException;
import icu.binglieyan.exception.UsersException;
import icu.binglieyan.mapper.ClassesMapper;
import icu.binglieyan.mapper.QuestionSubmissionsMapper;
import icu.binglieyan.mapper.SubmissionsMapper;
import icu.binglieyan.mapper.UsersMapper;
import icu.binglieyan.result.PageResult;
import icu.binglieyan.service.ClassesService;
import icu.binglieyan.vo.ClassesPageQueryVO;
import icu.binglieyan.vo.ClassesVO;
import icu.binglieyan.vo.StudentVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.Optional;

/**
 * @author binglieyan
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class ClassesServiceImpl extends ServiceImpl<ClassesMapper, Classes> implements ClassesService {

    private final ClassesMapper classesMapper;
    private final UsersMapper usersMapper;
    private final QuestionSubmissionsMapper questionSubmissionsMapper;
    private final SubmissionsMapper submissionsMapper;

    /**
     * 添加班级
     * @param classesDTO 班级数据传输对象，包含班级的基本信息
     */
    @Override
    public void addClasses(ClassesDTO classesDTO) {
        //1. 校验班级代码是否存在
        if (StringUtils.isNotBlank(classesDTO.getClassCode())) {
            if (this.exists(new LambdaQueryWrapper<Classes>().eq(Classes::getClassCode, classesDTO.getClassCode()))) {
                throw new ClassesException(MessageConstant.CLASSES_CODE_EXISTS);
            }
        }

        //2. 校验班级名称是否存在
        if (StringUtils.isNotBlank(classesDTO.getClassName())) {
            if (this.exists(new LambdaQueryWrapper<Classes>().eq(Classes::getClassName, classesDTO.getClassName()))) {
                throw new ClassesException(MessageConstant.CLASSES_NAME_EXISTS);
            }
        }

        Optional<Long> teacherIdOpt = BaseContext.getCurrentId();
        if (teacherIdOpt.isEmpty()){
            throw new UserScopeException(MessageConstant.USER_SCOPE_ERROR);
        }
        Classes classes = Classes.builder()
                .className(classesDTO.getClassName())
                .classCode(classesDTO.getClassCode())
                .teacherId(teacherIdOpt.get())
                .build();
        this.save(classes);
    }

    /**
     * 删除班级
     * @param classCode 班级编号
     */
    @Override
    public void deleteClasses(String classCode) {
        if (StringUtils.isBlank(classCode)) {
            throw new ClassesException(MessageConstant.CLASSES_NOT_FOUND);
        }
        // 查询班级是否存在
        if (!this.exists(new LambdaQueryWrapper<Classes>().eq(Classes::getClassCode, classCode))) {
            throw new ClassesException(MessageConstant.CLASSES_NOT_FOUND);
        }
        this.lambdaUpdate().eq(Classes::getClassCode, classCode).remove();
    }

    /**
     * 修改班级信息
     *
     * @param classesUpdateDTO 班级修改信息数据传输对象，包含班级的修改信息
     */
    @Override
    public void updateClasses(ClassesUpdateDTO classesUpdateDTO) {
        //1. 查询班级是否存在
        Classes classes = this.getOne(
                new LambdaQueryWrapper<Classes>()
                        .eq(Classes::getClassCode, classesUpdateDTO.getClassCode())
                        .select(Classes::getId));
        if (classes == null){
            throw new ClassesException(MessageConstant.CODE_NOT_NULL);
        }
        //2. 手动设置每个非空字段
        LambdaUpdateWrapper<Classes> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Classes::getId, classes.getId());
        if (StringUtils.isNotBlank(classesUpdateDTO.getClassName())){
            updateWrapper.set(Classes::getClassName, classesUpdateDTO.getClassName());
        }
        if (StringUtils.isNotBlank(classesUpdateDTO.getClassCode())){
            updateWrapper.set(Classes::getClassCode, classesUpdateDTO.getClassCode());
        }
        if (StringUtils.isNotBlank(classesUpdateDTO.getDescription())){
            updateWrapper.set(Classes::getDescription, classesUpdateDTO.getDescription());
        }

        //4. 更新班级
        this.update(updateWrapper);
    }

    /**
     * 分页查询班级信息
     * @param classesPageQueryDTO 班级分页查询数据传输对象，包含分页查询的参数
     * @return 分页查询结果
     */
    @Override
    public PageResult<ClassesPageQueryVO> pageQuery(ClassesPageQueryDTO classesPageQueryDTO) {
        Integer pageNum = classesPageQueryDTO.getPageNum();
        Integer pageSize = classesPageQueryDTO.getPageSize();
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }
        // 使用MyBatis-Plus进行分页查询
        Page<ClassesPageQueryVO> page = new Page<>(pageNum, pageSize);
        classesMapper.pageQuery(page, classesPageQueryDTO);
        return new PageResult<>(page.getTotal(), page.getRecords());
    }

    /**
     * 根据班级ID查询班级信息
     * @return 班级信息
     */
    @Override
    public ClassesVO queryById() {
        Optional<Long> studentIdOpt = BaseContext.getCurrentId();
        if (studentIdOpt.isEmpty()){
            throw new UserScopeException(MessageConstant.USER_SCOPE_ERROR);
        }
        Users users = usersMapper.selectOne(
                new LambdaQueryWrapper<Users>()
                        .eq(Users::getId, studentIdOpt.get())
                        .select(Users::getClassId));
        if (users == null){
            throw new ClassesException(MessageConstant.USER_NOT_FOUND);
        }
        Classes classes = this.getOne(
                new LambdaQueryWrapper<Classes>()
                        .eq(Classes::getId, users.getClassId())
                        .select(
                                Classes::getClassCode,
                                Classes::getClassName,
                                Classes::getDescription,
                                Classes::getTeacherId
                        ));
        Users users1 = usersMapper.selectOne(
                new LambdaQueryWrapper<Users>()
                        .eq(Users::getId, classes.getTeacherId())
                        .select(Users::getUserNumber));
        return ClassesVO.builder()
                .classCode(classes.getClassCode())
                .className(classes.getClassName())
                .description(classes.getDescription())
                .teacherNumber(users1.getUserNumber())
                .build();
    }

    /**
     * 根据班级编号查询学生信息
     * @param classCode 班级编号
     * @return 学生信息
     */
    @Override
    public List<StudentVO> queryStudentByCode(String classCode) {
        if (StringUtils.isBlank(classCode)){
            throw new ClassesException(MessageConstant.CODE_NOT_NULL);
        }
        //1. 先查询班级是否存在，且是自己的班级
        Classes classes = this.getOne(
                new LambdaQueryWrapper<Classes>()
                        .eq(Classes::getClassCode, classCode)
                        .select(
                                Classes::getId,
                                Classes::getTeacherId,
                                Classes::getClassCode));
        if (classes == null){
            throw new ClassesException(MessageConstant.CLASSES_NOT_FOUND);
        }
        Optional<Long> teacherIdOpt = BaseContext.getCurrentId();
        if (teacherIdOpt.isEmpty()){
            throw new UserScopeException(MessageConstant.USER_SCOPE_ERROR);
        }
        if (!teacherIdOpt.get().equals(classes.getTeacherId())){
            throw new ClassesException(MessageConstant.NOT_CLASS_OWNER);
        }

        //2. 查询该班级下的全部学生信息
        List<Users> usersList = usersMapper.selectList(
                new LambdaQueryWrapper<Users>()
                        .eq(Users::getClassId, classes.getId())
                        .select(
                                Users::getUsername,
                                Users::getRealName,
                                Users::getUserNumber
                        ));
        return usersList.stream()
                .map(users -> StudentVO.builder()
                        .username(users.getUsername())
                        .realName(users.getRealName())
                        .userNumber(users.getUserNumber())
                        .classCode(classes.getClassCode())
                        .build())
                .toList();
    }

    /**
     * 获取教师自己的班级信息
     * @return 教师自己的班级信息
     */
    @Override
    public List<ClassesVO> teacherQueryClassesById() {
        Optional<Long> teacherIdOpt = BaseContext.getCurrentId();
        if (teacherIdOpt.isEmpty()){
            throw new UserScopeException(MessageConstant.USER_SCOPE_ERROR);
        }
        Optional<String> teacherNumberOpt = BaseContext.getCurrentUserNumber();
        if (teacherNumberOpt.isEmpty()){
            throw new UserScopeException(MessageConstant.USER_SCOPE_ERROR);
        }
        List<Classes> classesList = this.list(
                new LambdaQueryWrapper<Classes>()
                        .eq(Classes::getTeacherId, teacherIdOpt.get())
                        .select(
                                Classes::getClassCode,
                                Classes::getClassName,
                                Classes::getDescription
                        ));
        return classesList.stream()
                .map(classes -> ClassesVO.builder()
                        .classCode(classes.getClassCode())
                        .className(classes.getClassName())
                        .description(classes.getDescription())
                        .teacherNumber(teacherNumberOpt.get())
                        .build())
                .toList();
    }

    /**
     * 删除班级下的学生
     * @param studentNumber 班级编号
     */
    @Override
    public void removeStudent(String studentNumber) {
        if (StringUtils.isBlank(studentNumber)){
            throw new ClassesException(MessageConstant.CODE_NOT_NULL);
        }
        //1. 先查询该学生是否是自己班级的
        Users users = usersMapper.selectOne(
                new LambdaQueryWrapper<Users>()
                        .eq(Users::getUserNumber, studentNumber)
                        .select(
                                Users::getClassId,
                                Users::getId
                        ));
        if (users == null){
            throw new UsersException(MessageConstant.USER_NOT_FOUND);
        }
        Optional<Long> teacherIdOpt = BaseContext.getCurrentId();
        if (teacherIdOpt.isEmpty()){
            throw new UserScopeException(MessageConstant.USER_SCOPE_ERROR);
        }
        Classes classes = this.getOne(
                new LambdaQueryWrapper<Classes>()
                        .eq(Classes::getTeacherId, teacherIdOpt.get())
                        .select(Classes::getId));
        if (!classes.getId().equals(users.getClassId())){
            throw new ClassesException(MessageConstant.STUDENT_NOT_IN_CLASSES);
        }
        //2. 查询该学生在题目QuestionSubmissions表和Submissions表中是否有记录，如果有则删除
        LambdaQueryWrapper<QuestionSubmissions> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(QuestionSubmissions::getStudentId, users.getId());
        queryWrapper.select(QuestionSubmissions::getStudentAnswer);
        List<QuestionSubmissions> questionSubmissionsList = questionSubmissionsMapper.selectList(queryWrapper);
        if (!questionSubmissionsList.isEmpty()){
            //3. 先删除存放在服务器的学生答案文件
            questionSubmissionsList.forEach(questionSubmissions -> {
                String studentAnswer = questionSubmissions.getStudentAnswer();
                if (StringUtils.isNotBlank(studentAnswer)){
                    File file = new File(studentAnswer);
                    if (file.exists() && !file.delete()){
                        log.error("删除学生答案文件失败，学生ID：{}，学生答案文件位置：{}", users.getId(), studentAnswer);
                    }
                }
            });
            //4. 删除QuestionSubmissions表的记录，对应的TestCaseResults表和Exceptions表中的记录会联级删除
            questionSubmissionsMapper.delete(queryWrapper);
        }
        //5. 删除Submissions表中的记录
        LambdaQueryWrapper<Submissions> queryWrapper2 = new LambdaQueryWrapper<>();
        queryWrapper2.eq(Submissions::getStudentId, users.getId());
        if (submissionsMapper.exists(queryWrapper2)) {
            submissionsMapper.delete(queryWrapper2);
        }

        //6. 设置Users表中该学生的ClassId字段为null
        LambdaUpdateWrapper<Users> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Users::getId, users.getId());
        updateWrapper.setSql("class_id = NULL");
        usersMapper.update(updateWrapper);
    }
}
