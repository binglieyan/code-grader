package icu.binglieyan.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 查重任务DTO类
 *
 * @author binglieyan
 */
@Data
@Schema(description = "查重任务信息传递时的数据模型")
public class PlagiarismChecksUpdateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 查重任务ID
     */
    @Schema(description = "查重任务ID", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long id;

    /**
     * 总比较数
     */
    @Schema(description = "总比较数", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer totalComparisons;

    /**
     * 执行时长
     */
    @Schema(description = "执行时长", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer executionTime;

    /**
     * 报告文件路径
     */
    @Schema(description = "报告目录路径", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String reportPath;

    /**
     * 查重状态编码
     */

    @Schema(description = "查重状态编码", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String statusCode;

    /**
     * 错误信息
     */
    @Schema(description = "错误信息", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String errorMessage;

    /**
     * 开始时间
     */
    @Schema(description = "开始时间", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private LocalDateTime startTime;

    /**
     * 完成时间
     */
    @Schema(description = "完成时间", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private LocalDateTime completedAt;

}
