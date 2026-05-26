package icu.binglieyan.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 作业提交VO类
 *
 * @author binglieyan
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "作业提交信息查询返回的视图模型")
public class SubmissionsVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 作业标题
     */
    @Schema(description = "作业标题", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String title;

    /**
     * 作业总得分
     */
    @Schema(description = "作业得分", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private BigDecimal totalScore;

    /**
     * 提交时间
     */
    @Schema(description = "提交时间", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private LocalDateTime submittedAt;

    /**
     * 批改完成时间
     */
    @Schema(description = "批改完成时间", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private LocalDateTime gradingCompletedAt;

    /**
     * 提交状态值
     */
    @Schema(description = "提交状态值", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String submissionStatusValue;
}
