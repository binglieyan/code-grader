package icu.binglieyan.controller.student;

import icu.binglieyan.result.Result;
import icu.binglieyan.service.TestCasesService;
import icu.binglieyan.vo.TestCasesVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 测试用例控制器类
 * @author binglieyan
 */
@RestController("studentTestCasesController")
@RequestMapping("/student/testCases")
@Tag(name = "测试用例相关接口")
@Log4j2
@RequiredArgsConstructor
public class TestCasesController {

    private final TestCasesService testCasesService;

    /**
     * 根据题目ID获取所有非隐藏的测试用例信息
     * @param questionId 题目ID
     * @return 测试用例数据传输对象
     */
    @GetMapping("/getVisibleTestCases/{questionId}")
    @Operation(summary = "根据题目ID获取所有非隐藏的测试用例信息")
    public Result<List<TestCasesVO>> getVisibleTestCases(@PathVariable Long questionId) {
        log.info("题目ID：{}", questionId);
        return Result.success(testCasesService.getVisibleTestCases(questionId));
    }
}
