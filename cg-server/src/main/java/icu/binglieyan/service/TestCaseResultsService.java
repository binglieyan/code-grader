package icu.binglieyan.service;

import icu.binglieyan.vo.HiddenTestCaseResultsVO;
import icu.binglieyan.vo.TestCaseResultsVO;

import java.util.List;

/**
 * @author binglieyan
 */
public interface TestCaseResultsService {

    /**
     * 获取测试用例结果信息
     * @param questionSubmissionId 题目提交记录ID
     * @return 获取结果
     */
    List<TestCaseResultsVO> getTestCaseResults(Long questionSubmissionId);

    /**
     * 学生获取测试用例结果信息
     * @param questionSubmissionId 题目提交记录ID
     * @return 获取结果
     */
    List<TestCaseResultsVO> studentGetTestCaseResults(Long questionSubmissionId);

    /**
     * 学生获取隐藏测试用例结果信息
     * @param questionSubmissionId 题目提交记录ID
     * @return 获取结果
     */
    HiddenTestCaseResultsVO studentGetHiddenTestCaseResults(Long questionSubmissionId);
}
