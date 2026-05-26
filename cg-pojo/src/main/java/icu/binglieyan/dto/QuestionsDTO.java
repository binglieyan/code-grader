package icu.binglieyan.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 题目DTO类
 *
 * @author binglieyan
 */
@Data
@Schema(description = "题目信息传递时的数据模型")
public class QuestionsDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 所属作业ID
     */
    @NotNull(message = "所属作业ID不能为空")
    @Schema(description = "所属作业ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long assignmentId;

    /**
     * 题目顺序
     */
    @NotNull(message = "题目顺序不能为空")
    @Schema(description = "题目顺序", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer questionOrder;

    /**
     * 题目标题
     */
    @NotBlank(message = "题目标题不能为空")
    @Size(min = 2, max = 100, message = "题目标题长度必须在2-100个字符之间")
    @Schema(description = "题目标题", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    /**
     * 题目内容
     */
    @NotBlank(message = "题目内容不能为空")
    @Size(min = 2, max = 3000, message = "题目内容长度必须在2-2000个字符之间")
    @Schema(description = "题目内容", requiredMode = Schema.RequiredMode.REQUIRED)
    private String content;

    /**
     * 初始代码
     */
    @NotBlank(message = "初始代码不能为空")
    @Size(min = 1, max = 2000, message = "初始代码长度必须在1-2000个字符之间")
    @Schema(description = "初始代码", requiredMode = Schema.RequiredMode.REQUIRED)
    private String initialCode;

    /**
     * 题目分值
     */
    @NotNull(message = "题目分值不能为空")
    @Schema(description = "题目分值", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal maxScore;

}
