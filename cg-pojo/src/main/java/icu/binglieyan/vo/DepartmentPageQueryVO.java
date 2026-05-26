package icu.binglieyan.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 院系分页查询VO类
 *
 * @author binglieyan
 *
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "院系分页查询返回的视图模型")
public class DepartmentPageQueryVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 院系代码
     */
    @Schema(description = "院系代码", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String departmentCode;

    /**
     * 院系名称
     */
    @Schema(description = "院系名称", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String departmentName;

}
