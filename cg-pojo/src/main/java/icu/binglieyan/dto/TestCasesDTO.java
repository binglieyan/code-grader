package icu.binglieyan.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;


/**
 * 测试用例DTO类
 *
 * @author binglieyan
 */
@Data
@Schema(description = "测试用例信息传递时的数据模型")
public class TestCasesDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 题目ID
     */
    @NotNull(message = "题目ID不能为空")
    @Schema(description = "题目ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long questionId;

    /**
     * 测试用例顺序
     */
    @NotNull(message = "测试用例顺序不能为空")
    @Schema(description = "测试用例顺序", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long caseOrder;

    /**
     * 输入数据
     */
    @NotEmpty(message = "输入数据不能为空")
    @Size(min = 1, max = 2000, message = "输入数据长度必须在1-2000个字符之间")
    @Schema(description = "测试结果ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<String> inputData;

    /**
     * 期望输出
     */
    @NotBlank(message = "期望输出不能为空")
    @Size(min = 1, max = 2000, message = "期望输出长度必须在1-2000个字符之间")
    @Schema(description = "期望输出", requiredMode = Schema.RequiredMode.REQUIRED)
    private String expectedOutput;

    /**
     * 是否为隐藏
     */
    @TableField("is_hidden")
    @Schema(description = "是否为隐藏", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Boolean hidden;

}
