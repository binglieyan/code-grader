package icu.binglieyan.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 作业更新DTO类
 *
 * @author binglieyan
 */
@Data
@Schema(description = "作业信息传递时的数据模型")
public class AssignmentsUpdateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 作业ID
     */
    @NotNull(message = "作业ID不能为空")
    @Schema(description = "作业ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    /**
     * 作业标题
     */
    @Size(min = 2, max = 100, message = "作业标题长度必须在2-100个字符之间")
    @Schema(description = "作业标题", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String title;

    /**
     * 作业总分值
     */
    @Schema(description = "作业总分", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private BigDecimal totalScore;

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
     * 作业状态编码
     */
    @Pattern(regexp = "^(DRAFT|PUBLISHED|CLOSED)$", message = "作业状态编码只能是DRAFT、PUBLISHED或CLOSED")
    @Schema(description = "作业状态编码", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String assignmentStatusCode;

}
