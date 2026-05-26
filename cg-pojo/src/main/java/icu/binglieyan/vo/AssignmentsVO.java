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
 * 作业分页查询VO类
 *
 * @author binglieyan
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "作业分页查询返回的数据模型")
public class AssignmentsVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 作业ID
     */
    @Schema(description = "作业ID", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long id;

    /**
     * 作业标题
     */
    @Schema(description = "作业标题", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String title;

    /**
     * 作业总分值
     */
    @Schema(description = "作业总分值", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private BigDecimal totalScore;

    /**
     * 所属班级代码
     */
    @Schema(description = "所属班级代码", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String classCode;

    /**
     * 开始时间
     */
    @Schema(description = "开始时间", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private LocalDateTime startTime;

    /**
     * 截止时间
     */
    @Schema(description = "截止时间", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private LocalDateTime deadline;

    /**
     * 作业状态值
     */
    @Schema(description = "作业状态值", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String assignmentStatusValue;

}
