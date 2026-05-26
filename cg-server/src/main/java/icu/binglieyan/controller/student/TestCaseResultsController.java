package icu.binglieyan.controller.student;

import icu.binglieyan.result.Result;
import icu.binglieyan.service.TestCaseResultsService;
import icu.binglieyan.vo.HiddenTestCaseResultsVO;
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
@RestController("studentTestCaseResultsController")
@RequestMapping("/student/testCaseResults")
@Tag(name = "测试用例结果相关接口")
@Log4j2
@RequiredArgsConstructor
public class TestCaseResultsController {

    private final TestCaseResultsService testCaseResultsService;

    /**
     * 根据题目提交ID获取所有非隐藏测试用例结果信息
     * @param questionSubmissionId 题目提交ID
     * @return 获取结果
     */
    @GetMapping("/studentGetTestCaseResults/{questionSubmissionId}")
    @Operation(summary = "根据题目提交ID获取所有非隐藏测试用例结果信息")
    public Result<List<TestCaseResultsVO>> studentGetTestCaseResults(@PathVariable Long questionSubmissionId) {
        log.info("根据题目提交ID：{}查询非隐藏测试用例结果信息", questionSubmissionId);
        return Result.success(testCaseResultsService.studentGetTestCaseResults(questionSubmissionId));
    }

    /**
     * 根据题目提交ID获取所有隐藏测试用例结果通过统计
     * @param questionSubmissionId 题目提交ID
     * @return 获取结果
     */
    @GetMapping("/studentGetHiddenTestCaseResults/{questionSubmissionId}")
    @Operation(summary = "根据题目提交ID获取所有隐藏测试用例结果通过统计")
    public Result<HiddenTestCaseResultsVO> studentGetHiddenTestCaseResults(@PathVariable Long questionSubmissionId) {
        log.info("根据题目提交ID：{}查询隐藏测试用例结果通过统计", questionSubmissionId);
        return Result.success(testCaseResultsService.studentGetHiddenTestCaseResults(questionSubmissionId));
    }
}


