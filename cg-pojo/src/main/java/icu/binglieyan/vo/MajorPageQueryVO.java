package icu.binglieyan.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 专业分页查询VO类
 *
 * @author binglieyan
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "专业分页查询返回的视图模型")
public class MajorPageQueryVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 专业代码
     */
    @Schema(description = "专业代码", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String majorCode;

    /**
     * 专业名称
     */
    @Schema(description = "专业名称", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String majorName;

    /**
     * 院系名称
     */
    @Schema(description = "院系名称", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String departmentName;

}
