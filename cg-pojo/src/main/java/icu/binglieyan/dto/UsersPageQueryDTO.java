package icu.binglieyan.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户分页查询DTO类
 *
 * @author binglieyan
 */
@Data
@Schema(description = "用户分页查询信息传递时的数据模型")
public class UsersPageQueryDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户名
     */
    @Schema(description = "用户名", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String username;


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
     * 页码编号
     * 用于指定当前查询的页码，以便进行分页查询
     */
    @Schema(description = "页码编号", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer pageNum;

    /**
     * 每页记录数
     * 用于指定每页包含的记录数，与pageNum一起使用进行分页查询
     */
    @Schema(description = "每页记录数", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer pageSize;
}
