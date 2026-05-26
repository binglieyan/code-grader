package icu.binglieyan.controller.student;

import icu.binglieyan.dto.ClassesPageQueryDTO;
import icu.binglieyan.result.PageResult;
import icu.binglieyan.result.Result;
import icu.binglieyan.service.ClassesService;
import icu.binglieyan.vo.ClassesPageQueryVO;
import icu.binglieyan.vo.ClassesVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

/**
 * 班级控制器类
 * @author binglieyan
 */
@RestController("studentClassesController")
@RequestMapping("/student/classes")
@Tag(name = "班级相关接口")
@Log4j2
@RequiredArgsConstructor
public class ClassesController {

    private final ClassesService classesService;

    /**
     * 分页查询班级信息
     * @param classesPageQueryDTO 查询条件
     * @return 查询结果
     */
    @PostMapping("/page")
    @Operation(summary = "分页查询班级信息")
    public Result<PageResult<ClassesPageQueryVO>> pageQuery(@RequestBody ClassesPageQueryDTO classesPageQueryDTO){
        log.info("分页查询班级信息：{}", classesPageQueryDTO);
        return Result.success(classesService.pageQuery(classesPageQueryDTO));
    }

    /**
     * 查询所加入班级的信息
     * @return 班级信息
     */
    @GetMapping("/queryById")
    @Operation(summary = "查询所加入班级的信息")
    public Result<ClassesVO> queryById(){
        log.info("查询所加入班级的信息");
        return Result.success(classesService.queryById());
    }
}
