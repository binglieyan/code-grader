package icu.binglieyan.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 作业DTO类
 *
 * @author binglieyan
 */
@Data
@Schema(description = "作业信息传递时的数据模型")
public class AssignmentsDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 作业标题
     */
    @NotBlank(message = "作业标题不能为空")
    @Size(min = 2, max = 100, message = "作业标题长度必须在2-100个字符之间")
    @Schema(description = "作业标题", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    /**
     * 作业总分值
     */
    @NotNull(message = "作业总分值不能为空")
    @Schema(description = "作业总分值", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal totalScore;

    /**
     * 所属班级代码
     */
    @NotBlank(message = "所属班级代码不能为空")
    @Size(min = 2, max = 20, message = "班级代码长度必须在2-20个字符之间")
    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "班级代码只能包含字母和数字")
    @Schema(description = "所属班级ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String classCode;

    /**
     * 开始时间
     */
    @NotNull(message = "开始时间不能为空")
    @Schema(description = "开始时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime startTime;

    /**
     * 截止时间
     */
    @NotNull(message = "截止时间不能为空")
    @Schema(description = "截止时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime deadline;

}
