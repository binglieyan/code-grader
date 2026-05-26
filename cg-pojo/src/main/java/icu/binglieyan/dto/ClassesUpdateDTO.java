package icu.binglieyan.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;


/**
 * 教学班更新DTO类
 *
 * @author binglieyan
 */
@Data
@Schema(description = "教学班更新信息传递时的数据模型")
public class ClassesUpdateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 班级名称
     */
    @Size(min = 2, max = 100, message = "班级名称长度必须在2-100个字符之间")
    @Schema(description = "班级名称", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String className;

    /**
     * 班级代码
     */
    @NotBlank(message = "班级代码不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "班级代码只能包含字母和数字")
    @Schema(description = "班级代码", requiredMode = Schema.RequiredMode.REQUIRED)
    private String classCode;

    /**
     * 班级描述
     */
    @Size(max = 100, message = "班级描述长度不能超过100个字符")
    @Schema(description = "班级描述", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String description;

}
