package icu.binglieyan.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 院系更新DTO类
 *
 * @author binglieyan
 */
@Data
@Schema(description = "部门更新信息传递时的数据模型")
public class DepartmentUpdateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 院系代码
     */
    @NotBlank(message = "院系代码不能为空")
    @Size(min = 2, max = 20, message = "院系代码长度必须在2-20个字符之间")
    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "院系代码只能包含字母和数字")
    @Schema(description = "院系代码", requiredMode = Schema.RequiredMode.REQUIRED)
    private String departmentCode;

    /**
     * 院系名称
     */
    @Size(min = 2, max = 100, message = "院系名称长度必须在2-100个字符之间")
    @Pattern(regexp = "^[\\u4e00-\\u9fa5a-zA-Z0-9（）()]+$", message = "院系名称只能包含中文、字母、数字和括号")
    @Schema(description = "院系名称", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String departmentName;

}
