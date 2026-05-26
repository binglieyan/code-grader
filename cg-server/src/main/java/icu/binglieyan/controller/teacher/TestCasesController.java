package icu.binglieyan.controller.teacher;

import icu.binglieyan.dto.TestCasesDTO;
import icu.binglieyan.dto.TestCasesUpdateDTO;
import icu.binglieyan.result.Result;
import icu.binglieyan.service.TestCasesService;
import icu.binglieyan.vo.TestCasesVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 测试用例控制器类
 * @author binglieyan
 */
@RestController("teacherTestCasesController")
@RequestMapping("/teacher/testCases")
@Tag(name = "测试用例相关接口")
@Log4j2
@RequiredArgsConstructor
public class TestCasesController {

    private final TestCasesService testCasesService;

    /**
     * 添加测试用例
     * @param testCasesDTO 测试用例数据传输对象，包含测试用例的基本信息
     * @return 添加结果
     */
    @PostMapping("/addTestCases")
    @Operation(summary = "添加测试用例")
    public Result<Void> addTestCases(@RequestBody @Validated TestCasesDTO testCasesDTO) {
        log.info("添加测试用例信息：{}", testCasesDTO);
        testCasesService.addTestCases(testCasesDTO);
        return Result.success();
    }

    /**
     * 删除测试用例
     * @param id 测试用例ID
     * @return 删除结果
     */
    @DeleteMapping("/deleteTestCases/{id}")
    @Operation(summary = "删除测试用例")
    public Result<Void> deleteTestCases(@PathVariable Long id) {
        log.info("删除测试用例id：{}", id);
        testCasesService.deleteTestCases(id);
        return Result.success();
    }

    /**
     * 修改测试用例信息
     * @param testCasesUpdateDTO 测试用例数据传输对象，包含测试用例的修改信息
     * @return 修改结果
     */
    @PutMapping("/updateTestCases")
    @Operation(summary = "修改测试用例信息")
    public Result<Void> updateTestCases(@RequestBody @Validated TestCasesUpdateDTO testCasesUpdateDTO) {
        log.info("修改测试用例信息：{}", testCasesUpdateDTO);
        testCasesService.updateTestCases(testCasesUpdateDTO);
        return Result.success();
    }

    /**
     * 根据题目ID获取所有测试用例信息
     * @param id 题目ID
     * @return 测试用例数据传输对象
     */
    @GetMapping("/getTestCases/{id}")
    @Operation(summary = "根据题目ID获取所有测试用例信息")
    public Result<List<TestCasesVO>> getTestCases(@PathVariable Long id) {
        log.info("题目ID：{}", id);
        return Result.success(testCasesService.getTestCases(id));
    }
}
