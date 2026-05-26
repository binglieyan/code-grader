package icu.binglieyan.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户更新DTO类
 *
 * @author binglieyan
 */
@Data
@Schema(description = "用户更新信息传递时的数据模型")
public class UsersUpdateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户名
     */
    @Size(min = 2, max = 20, message = "用户名长度必须在2-20个字符之间")
    @Pattern(regexp = "^[\\u4e00-\\u9fa5a-zA-Z0-9_]+$", message = "用户名只能包含中文、字母、数字和下划线")
    @Schema(description = "用户名", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String username;

    /**
     * 邮箱
     */
    @Email(message = "邮箱格式错误")
    @Size(max = 50, message = "邮箱长度不能超过50个字符")
    @Schema(description = "邮箱", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String email;

    /**
     * 真实姓名
     */
    @Size(min = 2, max = 20, message = "真实姓名长度必须在2-20个字符之间")
    @Pattern(regexp = "^[\\u4e00-\\u9fa5a-zA-Z·]+$", message = "真实姓名只能包含中文、字母和间隔号(·)")
    @Schema(description = "真实姓名", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String realName;

    /**
     * 用户编号
     */
    @NotBlank(message = "用户编号不能为空")
    @Size(min = 2, max = 20, message = "用户编号长度必须在2-20个字符之间")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "用户编号只能包含字母和数字")
    @Schema(description = "用户编号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String userNumber;

    /**
     * 专业代码
     */
    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "专业代码只能包含字母和数字")
    @Schema(description = "专业代码", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String majorCode;

    /**
     * 班级代码
     */
    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "班级代码只能包含字母和数字")
    @Schema(description = "班级代码", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String classCode;

}
