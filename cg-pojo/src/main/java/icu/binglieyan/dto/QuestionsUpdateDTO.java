package icu.binglieyan.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 题目更新DTO类
 *
 * @author binglieyan
 */
@Data
@Schema(description = "题目信息传递时的数据模型")
public class QuestionsUpdateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 题目ID
     */
    @NotNull(message = "题目ID不能为空")
    @Schema(description = "题目ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    /**
     * 题目顺序
     */
    @Schema(description = "题目顺序", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long questionOrder;

    /**
     * 题目标题
     */
    @Size(min = 1, max = 100, message = "题目标题长度必须在1-100个字符之间")
    @Schema(description = "题目标题", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String title;

    /**
     * 题目内容
     */
    @Size(min = 1, max = 3000, message = "题目内容长度必须在1-3000个字符之间")
    @Schema(description = "题目内容", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String content;

    /**
     * 初始代码
     */
    @Size(min = 1, max = 1000, message = "初始代码长度必须在1-1000个字符之间")
    @Schema(description = "初始代码", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String initialCode;

    /**
     * 题目分值
     */
    @Schema(description = "题目分值", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private BigDecimal maxScore;

}
