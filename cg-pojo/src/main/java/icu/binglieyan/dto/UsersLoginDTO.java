package icu.binglieyan.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户登录DTO类
 *
 * @author binglieyan
 */
@Data
@Schema(description = "用户登录信息传递时的数据模型")
public class UsersLoginDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户编号
     */
    @NotBlank(message = "用户编号不能为空")
    @Size(min = 2, max = 20, message = "用户编号长度必须在2-20个字符之间")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "用户编号只能包含字母和数字")
    @Schema(description = "用户编号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String userNumber;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20个字符之间")
    @Pattern(regexp = "^[a-zA-Z0-9_@.#$%^&*!]+$", message = "密码只能包含字母、数字和常见特殊字符(@.#$%^&*!)")
    @Schema(description = "密码", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

}
