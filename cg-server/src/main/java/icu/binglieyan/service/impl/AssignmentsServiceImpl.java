package icu.binglieyan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import icu.binglieyan.constant.MessageConstant;
import icu.binglieyan.context.BaseContext;
import icu.binglieyan.dto.AssignmentsDTO;
import icu.binglieyan.dto.AssignmentsUpdateDTO;
import icu.binglieyan.entity.Assignments;
import icu.binglieyan.entity.Classes;
import icu.binglieyan.entity.DictData;
import icu.binglieyan.entity.Users;
import icu.binglieyan.exception.AssignmentsException;
import icu.binglieyan.exception.ClassesException;
import icu.binglieyan.exception.DictDataException;
import icu.binglieyan.exception.UserScopeException;
import icu.binglieyan.mapper.AssignmentsMapper;
import icu.binglieyan.mapper.ClassesMapper;
import icu.binglieyan.mapper.DictDataMapper;
import icu.binglieyan.mapper.UsersMapper;
import icu.binglieyan.service.AssignmentsService;
import icu.binglieyan.vo.AssignmentsVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author binglieyan
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class AssignmentsServiceImpl extends ServiceImpl<AssignmentsMapper, Assignments> implements AssignmentsService {

    private final ClassesMapper classesMapper;
    private final UsersMapper usersMapper;
    private final DictDataMapper dictDataMapper;

    /**
     * 添加作业
     * @param assignmentsDTO 作业数据传输对象，包含作业的基本信息
     */
    @Override
    public void addAssignments(AssignmentsDTO assignmentsDTO) {
        //1. 班级代码 -> 班级ID
        Classes classes = classesMapper.selectOne(
                new LambdaQueryWrapper<Classes>()
                        .eq(Classes::getClassCode, assignmentsDTO.getClassCode())
                        .select(Classes::getId));
        if (classes == null) {
            throw new ClassesException(MessageConstant.CLASSES_NOT_FOUND);
        }
        if (!dictDataMapper.exists(new LambdaQueryWrapper<DictData>()
                .eq(DictData::getDataCode, "DRAFT")
                .eq(DictData::getActive, true))) {
            throw new DictDataException(MessageConstant.DICT_DATA_NOT_FOUND);
        }
        Assignments assignments = Assignments.builder()
                .title(assignmentsDTO.getTitle())
                .totalScore(assignmentsDTO.getTotalScore())
                .classId(classes.getId())
                .startTime(assignmentsDTO.getStartTime())
                .deadline(assignmentsDTO.getDeadline())
                .assignmentStatusCode("DRAFT")
                .build();

        //2. 添加作业
        this.save(assignments);
    }

    /**
     * 删除作业
     * @param id 作业ID
     */
    @Override
    public void deleteAssignments(Long id) {
        if (id == null){
            throw new AssignmentsException(MessageConstant.ID_NOT_NULL);
        }
        //查询作业是否存在
        if (!this.exists(new LambdaQueryWrapper<Assignments>().eq(Assignments::getId, id))) {
            throw new AssignmentsException(MessageConstant.ASSIGNMENTS_NOT_FOUND);
        }
        this.removeById(id);
    }

    /**
     * 修改作业信息
     * @param assignmentsUpdateDTO 作业数据传输对象，包含作业的基本信息
     */
    @Override
    public void updateAssignments(AssignmentsUpdateDTO assignmentsUpdateDTO) {
        //1. 先查询作业是否存在
        if (!this.exists(new LambdaQueryWrapper<Assignments>().eq(Assignments::getId, assignmentsUpdateDTO.getId()))) {
            throw new AssignmentsException(MessageConstant.ASSIGNMENTS_NOT_FOUND);
        }
        //2. 手动设置每个非空字段
        LambdaUpdateWrapper<Assignments> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Assignments::getId, assignmentsUpdateDTO.getId());
        if (StringUtils.isNotBlank(assignmentsUpdateDTO.getTitle())) {
            updateWrapper.set(Assignments::getTitle, assignmentsUpdateDTO.getTitle());
        }
        if (assignmentsUpdateDTO.getTotalScore() != null) {
            updateWrapper.set(Assignments::getTotalScore, assignmentsUpdateDTO.getTotalScore());
        }
        if (assignmentsUpdateDTO.getStartTime() != null) {
            updateWrapper.set(Assignments::getStartTime, assignmentsUpdateDTO.getStartTime());
        }
        if (assignmentsUpdateDTO.getDeadline() != null) {
            updateWrapper.set(Assignments::getDeadline, assignmentsUpdateDTO.getDeadline());
        }
        if (StringUtils.isNotBlank(assignmentsUpdateDTO.getAssignmentStatusCode())) {
            if (!dictDataMapper.exists(new LambdaQueryWrapper<DictData>()
                    .eq(DictData::getDataCode, assignmentsUpdateDTO.getAssignmentStatusCode())
                    .eq(DictData::getActive, true))) {
                throw new DictDataException(MessageConstant.DICT_DATA_NOT_FOUND);
            }
            updateWrapper.set(Assignments::getAssignmentStatusCode, assignmentsUpdateDTO.getAssignmentStatusCode());
        }

        //3. 更新作业
        this.update(updateWrapper);
    }

    /**
     * 查询作业
     * @return 作业
     */
    @Override
    public List<AssignmentsVO> queryById() {
        Optional<Long> studentIdOpt = BaseContext.getCurrentId();
        if (studentIdOpt.isEmpty()) {
            throw new UserScopeException(MessageConstant.USER_SCOPE_ERROR);
        }
        //1. 获取学生的班级 ID
        Users users = usersMapper.selectOne(
                new LambdaQueryWrapper<Users>()
                        .eq(Users::getId, studentIdOpt.get())
                        .select(Users::getClassId));
        if (users == null) {
            throw new AssignmentsException(MessageConstant.USER_NOT_FOUND);
        }
        
        //2. 查询该班级的所有作业
        List<Assignments> assignmentsList = this.list(
                new LambdaQueryWrapper<Assignments>()
                        .eq(Assignments::getClassId, users.getClassId())
                        .ne(Assignments::getAssignmentStatusCode, "DRAFT")
                        .select(
                                Assignments::getId,
                                Assignments::getTitle,
                                Assignments::getTotalScore,
                                Assignments::getClassId,
                                Assignments::getStartTime,
                                Assignments::getDeadline,
                                Assignments::getAssignmentStatusCode
                        ));
        
        if (assignmentsList.isEmpty()) {
            return List.of();
        }
        
        //3. 批量查询字典数据（避免N+1）
        List<String> statusCodes = assignmentsList.stream()
                .map(Assignments::getAssignmentStatusCode)
                .distinct()
                .toList();
        
        List<DictData> dictDataList = dictDataMapper.selectList(
                new LambdaQueryWrapper<DictData>()
                        .in(DictData::getDataCode, statusCodes)
                        .select(DictData::getDataCode, DictData::getDataValue));
        
        Map<String, String> statusCodeToValueMap = dictDataList.stream()
                .collect(Collectors.toMap(DictData::getDataCode, DictData::getDataValue));
        
        //4. 查询班级代码（只需一次）
        Classes classes = classesMapper.selectOne(
                new LambdaQueryWrapper<Classes>()
                        .eq(Classes::getId, users.getClassId())
                        .select(Classes::getClassCode));
        
        String classCode = classes != null ? classes.getClassCode() : "";
        
        //5. 转换为VO
        return assignmentsList.stream()
                .map(assignments -> {
                    String statusValue = statusCodeToValueMap.getOrDefault(assignments.getAssignmentStatusCode(), "");
                    return AssignmentsVO.builder()
                            .id(assignments.getId())
                            .title(assignments.getTitle())
                            .totalScore(assignments.getTotalScore())
                            .startTime(assignments.getStartTime())
                            .deadline(assignments.getDeadline())
                            .assignmentStatusValue(statusValue)
                            .classCode(classCode)
                            .build();
                })
                .toList();
    }

    @Override
    public List<AssignmentsVO> teacherQueryByCode(String classCode) {
        if (classCode == null) {
            throw new AssignmentsException(MessageConstant.CODE_NOT_NULL);
        }
        //1. 先查询作业是不是自己班级下的
        Classes classes = classesMapper.selectOne(
                new LambdaQueryWrapper<Classes>()
                        .eq(Classes::getClassCode, classCode)
                        .select(Classes::getTeacherId, Classes::getClassCode, Classes::getId));
        if (classes == null) {
            throw new AssignmentsException(MessageConstant.CLASSES_NOT_FOUND);
        }
        Optional<Long> teacherIdOpt = BaseContext.getCurrentId();
        if (teacherIdOpt.isEmpty()) {
            throw new UserScopeException(MessageConstant.USER_SCOPE_ERROR);
        }
        if (!teacherIdOpt.get().equals(classes.getTeacherId())) {
            throw new AssignmentsException(MessageConstant.ASSIGNMENT_NOT_MATCH);
        }
        //2. 查询该班级的所有作业;
        List<Assignments> assignmentsList = this.list(
                new LambdaQueryWrapper<Assignments>()
                        .eq(Assignments::getClassId, classes.getId())
                        .select(
                                Assignments::getId,
                                Assignments::getTitle,
                                Assignments::getTotalScore,
                                Assignments::getStartTime,
                                Assignments::getDeadline,
                                Assignments::getAssignmentStatusCode
                        ));

        if (assignmentsList.isEmpty()) {
            return List.of();
        }
        
        //3. 批量查询字典数据（避免N+1）
        List<String> statusCodes = assignmentsList.stream()
                .map(Assignments::getAssignmentStatusCode)
                .distinct()
                .toList();
        
        List<DictData> dictDataList = dictDataMapper.selectList(
                new LambdaQueryWrapper<DictData>()
                        .in(DictData::getDataCode, statusCodes)
                        .select(DictData::getDataCode, DictData::getDataValue));
        
        Map<String, String> statusCodeToValueMap = dictDataList.stream()
                .collect(Collectors.toMap(DictData::getDataCode, DictData::getDataValue));
        
        String classCodeValue = classes.getClassCode();

        //4. 转换为VO
        return assignmentsList.stream()
                .map(assignments -> {
                    String statusValue = statusCodeToValueMap.getOrDefault(assignments.getAssignmentStatusCode(), "");
                    return AssignmentsVO.builder()
                            .id(assignments.getId())
                            .title(assignments.getTitle())
                            .totalScore(assignments.getTotalScore())
                            .startTime(assignments.getStartTime())
                            .deadline(assignments.getDeadline())
                            .assignmentStatusValue(statusValue)
                            .classCode(classCodeValue)
                            .build();
                })
                .toList();
    }
}
