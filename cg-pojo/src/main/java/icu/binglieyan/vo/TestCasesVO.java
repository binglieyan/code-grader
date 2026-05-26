package icu.binglieyan.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;


/**
 * 测试用例查询VO类
 *
 * @author binglieyan
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "测试用例查询返回的视图模型")
public class TestCasesVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 测试用例ID
     */
    @Schema(description = "测试用例ID", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long id;

    /**
     * 题目ID
     */
    @Schema(description = "题目ID", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long questionId;

    /**
     * 测试用例顺序
     */
    @Schema(description = "测试用例顺序", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long caseOrder;

    /**
     * 输入数据
     */
    @Schema(description = "测试结果ID", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<String> inputData;

    /**
     * 期望输出
     */
    @Schema(description = "期望输出", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String expectedOutput;

}
