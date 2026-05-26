package icu.binglieyan.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;


/**
 * 教学班DTO类
 *
 * @author binglieyan
 */
@Data
@Schema(description = "教学班信息传递时的数据模型")
public class ClassesDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 班级名称
     */
    @NotBlank(message = "班级名称不能为空")
    @Size(min = 2, max = 100, message = "班级名称长度必须在2-100个字符之间")
    @Schema(description = "班级名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String className;

    /**
     * 班级代码
     */
    @NotBlank(message = "班级代码不能为空")
    @Size(min = 2, max = 20, message = "班级代码长度必须在2-20个字符之间")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "班级代码只能包含字母和数字")
    @Schema(description = "班级代码", requiredMode = Schema.RequiredMode.REQUIRED)
    private String classCode;

    /**
     * 班级描述
     */
    @Size(max = 100, message = "班级描述长度不能超过100个字符")
    @Schema(description = "班级描述", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String description;

}
