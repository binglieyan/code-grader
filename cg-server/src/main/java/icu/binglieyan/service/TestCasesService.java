package icu.binglieyan.service;

import icu.binglieyan.dto.TestCasesDTO;
import icu.binglieyan.dto.TestCasesUpdateDTO;
import icu.binglieyan.vo.TestCasesVO;

import java.util.List;

/**
 * @author binglieyan
 */
public interface TestCasesService {

    /**
     * 添加测试用例
     * @param testCasesDTO 测试用例数据传输对象
     */
    void addTestCases(TestCasesDTO testCasesDTO);

    /**
     * 删除测试用例
     * @param id 测试用例ID
     */
    void deleteTestCases(Long id);

    /**
     * 修改测试用例信息
     * @param testCasesUpdateDTO 测试用例数据传输对象
     */
    void updateTestCases(TestCasesUpdateDTO testCasesUpdateDTO);

    /**
     * 根据题目ID获取所有测试用例信息
     * @param id 测试用例ID
     * @return 测试用例信息
     */
    List<TestCasesVO> getTestCases(Long id);

    /**
     * 获取所有非隐藏的测试用例信息
     * @param questionId 题目ID
     * @return 测试用例信息
     */
    List<TestCasesVO> getVisibleTestCases(Long questionId);
}
