package icu.binglieyan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import icu.binglieyan.constant.MessageConstant;
import icu.binglieyan.dto.TestCasesDTO;
import icu.binglieyan.dto.TestCasesUpdateDTO;
import icu.binglieyan.entity.TestCases;
import icu.binglieyan.exception.TestCasesException;
import icu.binglieyan.mapper.TestCasesMapper;
import icu.binglieyan.service.TestCasesService;
import icu.binglieyan.vo.TestCasesVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

/**
 * @author binglieyan
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class TestCasesServiceImpl extends ServiceImpl<TestCasesMapper, TestCases> implements TestCasesService {

    private final ObjectMapper objectMapper;

    /**
     * 添加测试用例
     * @param testCasesDTO 测试用例数据传输对象
     */
    @Override
    public void addTestCases(TestCasesDTO testCasesDTO) {
        TestCases testCases = new TestCases();
        BeanUtils.copyProperties(testCasesDTO, testCases);
        this.save(testCases);
    }

    /**
     * 删除测试用例
     * @param id 测试用例ID
     */
    @Override
    public void deleteTestCases(Long id) {
        if (id == null) {
            throw new TestCasesException(MessageConstant.ID_NOT_NULL);
        }
        //查询测试用例是否存在
        if (!this.exists(new LambdaQueryWrapper<TestCases>().eq(TestCases::getId, id))) {
            throw new TestCasesException(MessageConstant.TEST_CASES_NOT_FOUND);
        }
        this.removeById(id);
    }

    /**
     * 修改测试用例信息
     * @param testCasesUpdateDTO 测试用例数据传输对象
     */
    @Override
    public void updateTestCases(TestCasesUpdateDTO testCasesUpdateDTO) {
        //1. 查询测试用例是否存在
        if (!this.exists(new LambdaQueryWrapper<TestCases>().eq(TestCases::getId, testCasesUpdateDTO.getId()))) {
            throw new TestCasesException(MessageConstant.TEST_CASES_NOT_FOUND);
        }
        //2. 手动设置每个非空字段
        LambdaUpdateWrapper<TestCases> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(TestCases::getId, testCasesUpdateDTO.getId());
        if (testCasesUpdateDTO.getQuestionId() != null){
            updateWrapper.set(TestCases::getQuestionId, testCasesUpdateDTO.getQuestionId());
        }
        if (testCasesUpdateDTO.getCaseOrder() != null){
            updateWrapper.set(TestCases::getCaseOrder, testCasesUpdateDTO.getCaseOrder());
        }
        if (testCasesUpdateDTO.getInputData() != null && !testCasesUpdateDTO.getInputData().isEmpty()){
            String inputDataJson = objectMapper.writeValueAsString(testCasesUpdateDTO.getInputData());
            updateWrapper.set(TestCases::getInputData, inputDataJson);
        }
        if (StringUtils.isNotBlank(testCasesUpdateDTO.getExpectedOutput())){
            updateWrapper.set(TestCases::getExpectedOutput, testCasesUpdateDTO.getExpectedOutput());
        }
        if (testCasesUpdateDTO.getHidden() != null){
            updateWrapper.set(TestCases::getHidden, testCasesUpdateDTO.getHidden());
        }
        //3. 修改测试用例
        this.update(updateWrapper);
    }

    /**
     * 根据题目ID获取所有测试用例信息
     * @param id 作业ID
     * @return 获取结果
     */
    @Override
    public List<TestCasesVO> getTestCases(Long id) {
        if (id == null) {
            throw new TestCasesException(MessageConstant.ID_NOT_NULL);
        }
        List<TestCases> testCasesList = this.list(
                new LambdaQueryWrapper<TestCases>()
                        .eq(TestCases::getQuestionId, id)
                        .orderByAsc(TestCases::getCaseOrder)
                        .select(
                                TestCases::getId,
                                TestCases::getQuestionId,
                                TestCases::getCaseOrder,
                                TestCases::getInputData,
                                TestCases::getExpectedOutput
                        ));
        return testCasesList.stream()
                .map(testCases -> TestCasesVO.builder()
                        .id(testCases.getId())
                        .questionId(testCases.getQuestionId())
                        .caseOrder(testCases.getCaseOrder())
                        .inputData(testCases.getInputData())
                        .expectedOutput(testCases.getExpectedOutput())
                        .build())
                .toList();
    }

    /**
     * 获取所有可见的测试用例信息
     * @param questionId 题目ID
     * @return 获取结果
     */
    @Override
    public List<TestCasesVO> getVisibleTestCases(Long questionId) {
        if (questionId == null) {
            throw new TestCasesException(MessageConstant.ID_NOT_NULL);
        }
        LambdaQueryWrapper<TestCases> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TestCases::getQuestionId, questionId);
        queryWrapper.eq(TestCases::getHidden, false).orderByAsc(TestCases::getCaseOrder);
        List<TestCases> testCasesList = this.list(
                new LambdaQueryWrapper<TestCases>()
                        .eq(TestCases::getQuestionId, questionId)
                        .eq(TestCases::getHidden, false)
                        .orderByAsc(TestCases::getCaseOrder)
                        .select(
                                TestCases::getId,
                                TestCases::getQuestionId,
                                TestCases::getCaseOrder,
                                TestCases::getInputData,
                                TestCases::getExpectedOutput
                        ));
        return testCasesList.stream()
                .map(testCases -> TestCasesVO.builder()
                        .id(testCases.getId())
                        .questionId(testCases.getQuestionId())
                        .caseOrder(testCases.getCaseOrder())
                        .inputData(testCases.getInputData())
                        .expectedOutput(testCases.getExpectedOutput())
                        .build())
                .toList();
    }

}
