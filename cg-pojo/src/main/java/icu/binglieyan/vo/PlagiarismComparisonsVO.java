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
 * 相似性比较VO类
 *
 * @author binglieyan
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "查重结果查询返回的简易视图模型")
public class PlagiarismComparisonsVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 比较ID
     */
    @Schema(description = "比较ID", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long id;

    /**
     * 查重任务ID
     */
    @Schema(description = "查重任务ID", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long plagiarismCheckId;

    /**
     * 第一个提交名称
     */
    @Schema(description = "第一个提交名称", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String firstSubmissionName;

    /**
     * 第二个提交名称
     */
    @Schema(description = "第二个提交名称", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String secondSubmissionName;

    /**
     * 平均相似度
     */
    @Schema(description = "平均相似度", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private BigDecimal avgSimilarity;

    /**
     * 最大相似度
     */
    @Schema(description = "最大相似度", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private BigDecimal maxSimilarity;

    /**
     * 最大长度
     */
    @Schema(description = "最大长度", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private BigDecimal maximumLength;

    /**
     * 最长匹配
     */
    @Schema(description = "最长匹配", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private BigDecimal longestMatch;

}
