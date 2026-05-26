package icu.binglieyan.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户登录VO类
 *
 * @author binglieyan
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "用户登录返回的视图模型")
public class UsersLoginVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户名
     */
    @Schema(description = "用户名", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String username;

    /**
     * 用户编号
     */
    @Schema(description = "用户编号", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String userNumber;

    /**
     * jwt令牌
     */
    @Schema(description = "jwt令牌", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String token;

}
