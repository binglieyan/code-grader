package icu.binglieyan.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户分页查询VO类
 *
 * @author binglieyan
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "用户分页查询返回的视图模型")
public class UsersPageQueryVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户名
     */
    @Schema(description = "用户名", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String username;

    /**
     * 邮箱
     */
    @Schema(description = "邮箱", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String email;

    /**
     * 真实姓名
     */
    @Schema(description = "真实姓名", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String realName;

    /**
     * 用户编号
     */
    @Schema(description = "用户编号", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String userNumber;

    /**
     * 专业名称
     */
    @Schema(description = "专业名称", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String majorName;

    /**
     * 班级名称
     */
    @Schema(description = "专业名称", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String className;

}
