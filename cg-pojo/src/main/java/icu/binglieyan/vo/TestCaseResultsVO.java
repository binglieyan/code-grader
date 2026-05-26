package icu.binglieyan.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 测试结果实体类
 *
 * @author binglieya
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestCaseResultsVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 测试用例结果ID
     */
    @Schema(description = "测试用例结果ID", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long id;

    /**
     * 题目提交详情ID
     */
    @Schema(description = "题目提交详情ID", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long questionSubmissionId;

    /**
     * 测试用例ID
     */
    @Schema(description = "测试用例ID", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long testCaseId;

    /**
     * 作业提交记录ID
     */
    @Schema(description = "作业提交记录ID", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long submissionId;

    /**
     * 实际输出
     */
    @Schema(description = "实际输出", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String actualOutput;

    /**
     * 是否通过
     */
    @Schema(description = "是否通过", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Boolean  passed;

    /**
     * 执行时间
     */
    @Schema(description = "执行时间", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer executionTime;

    /**
     * 错误信息
     */
    @Schema(description = "错误信息", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String errorMessage;


}
