package icu.binglieyan.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 查重任务VO类
 *
 * @author binglieyan
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "查重任务查询返回的简易视图模型")
public class PlagiarismChecksVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 查重任务ID
     */
    @Schema(description = "查重任务ID", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long id;

    /**
     * 作业ID
     */
    @Schema(description = "查重任务ID", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long assignmentId;

    /**
     * 发教师代码
     */
    @Schema(description = "教师代码", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String initiatedByNumber;

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
     * 查重状态值
     */
    @Schema(description = "查重状态值", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String statusValue;

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
