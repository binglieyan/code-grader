package icu.binglieyan.controller.teacher;

import icu.binglieyan.dto.ClassesDTO;
import icu.binglieyan.dto.ClassesUpdateDTO;
import icu.binglieyan.result.Result;
import icu.binglieyan.service.ClassesService;
import icu.binglieyan.vo.ClassesVO;
import icu.binglieyan.vo.StudentVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 班级控制器类
 * @author binglieyan
 */
@RestController("teacherClassesController")
@RequestMapping("/teacher/classes")
@Tag(name = "班级相关接口")
@Log4j2
@RequiredArgsConstructor
public class ClassesController {

    private final ClassesService classesService;

    /**
     * 添加班级
     * @param classesDTO 班级数据传输对象，包含班级的基本信息
     * @return 添加结果
     */
    @PostMapping("/addClasses")
    @Operation(summary = "添加班级")
    public Result<Void> addClasses(@RequestBody @Validated ClassesDTO classesDTO){
        log.info("添加班级信息：{}", classesDTO);
        classesService.addClasses(classesDTO);
        return Result.success();
    }

    /**
     * 删除班级
     * @param classCode 班级编号
     */
    @DeleteMapping("/deleteClasses/{classCode}")
    @Operation(summary = "删除班级")
    public Result<Void> deleteClasses(@PathVariable String classCode){
        log.info("删除班级编号：{}", classCode);
        classesService.deleteClasses(classCode);
        return Result.success();
    }
    /**
     * 修改班级信息
     * @param classesUpdateDTO 班级数据传输对象，包含班级的基本信息
     * @return 修改结果
     */
    @PutMapping("/updateClasses")
    @Operation(summary = "修改班级信息")
    public Result<Void> updateClasses(@RequestBody @Validated ClassesUpdateDTO classesUpdateDTO){
        log.info("修改班级信息：{}", classesUpdateDTO);
        classesService.updateClasses(classesUpdateDTO);
        return Result.success();
    }

    /**
     * 查询自己所拥有的全部班级
     * @return 班级信息
     */
    @GetMapping("/teacherQueryClassesById")
    @Operation(summary = "查询自己所拥有的班级信息")
    public Result<List<ClassesVO>> teacherQueryClassesById(){
        log.info("查询自己所拥有的班级信息");
        return Result.success(classesService.teacherQueryClassesById());
    }


    /**
     * 查询某班级下的全部学生信息
     * @return 班级信息
     */
    @GetMapping("/queryStudentById/{classCode}")
    @Operation(summary = "查询某班级下的全部学生信息")
    public Result<List<StudentVO>> queryStudentByCode(@PathVariable String classCode){
        log.info("查询班级中学生信息");
        return Result.success(classesService.queryStudentByCode(classCode));
    }

    /**
     * 移除班级中的学生
     * @param studentNumber 学生编号
     * @return 移除结果
     */
    @DeleteMapping("/removeStudent/{studentNumber}")
    @Operation(summary = "移除班级中的学生")
    public Result<Void> removeStudent (@PathVariable String studentNumber){
        log.info("移除班级中的学生");
        classesService.removeStudent(studentNumber);
        return Result.success();
    }
}
