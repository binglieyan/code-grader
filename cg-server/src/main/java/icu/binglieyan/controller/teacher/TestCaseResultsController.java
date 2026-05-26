package icu.binglieyan.controller.teacher;

import icu.binglieyan.result.Result;
import icu.binglieyan.service.TestCaseResultsService;
import icu.binglieyan.vo.TestCaseResultsVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 测试用例结果控制器类
 * @author binglieyan
 */
@RestController("teacherTestCaseResultsController")
@RequestMapping("/teacher/testCaseResults")
@Tag(name = "测试用例结果相关接口")
@Log4j2
@RequiredArgsConstructor
public class TestCaseResultsController {

    private final TestCaseResultsService testCaseResultsService;

    /**
     * 根据题目提交ID获取所有测试用例结果信息
     * @param questionSubmissionId 题目提交ID
     * @return 获取结果
     */
    @GetMapping("/getTestCaseResults/{questionSubmissionId}")
    @Operation(summary = "根据题目提交ID获取所有测试用例结果信息")
    public Result<List<TestCaseResultsVO>> getTestCaseResults(@PathVariable Long questionSubmissionId) {
        log.info("题目提交ID：{}", questionSubmissionId);
        return Result.success(testCaseResultsService.getTestCaseResults(questionSubmissionId));
    }
}


