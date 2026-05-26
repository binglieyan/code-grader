package icu.binglieyan.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 题目简易查询VO类
 *
 * @author binglieyan
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "题目查询返回的简易视图模型")
public class QuestionsBriefVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 题目ID
     */
    @Schema(description = "题目ID", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long id;

    /**
     * 题目标题
     */
    @Schema(description = "题目标题", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String title;

    /**
     * 题目顺序
     */
    @Schema(description = "题目顺序", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer questionOrder;

    /**
     * 题目分值
     */
    @Schema(description = "题目分值", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private BigDecimal maxScore;

}
