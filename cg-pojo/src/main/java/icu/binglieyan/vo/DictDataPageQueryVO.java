package icu.binglieyan.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;


/**
 * 字典数据DTO类
 *
 * @author binglieyan
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "字典数据信息传递时的数据模型")
public class DictDataPageQueryVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 字典数据ID
     */
    @Schema(description = "字典数据ID",  requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long id;

    /**
     * 字典类型编码
     */
    @Schema(description = "字典类型编码", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String typeCode;

    /**
     * 字典数据编码
     */
    @Schema(description = "字典数据编码", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String dataCode;

    /**
     * 字典数据值
     */
    @Schema(description = "字典数据值", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String dataValue;

    /**
     * 排序顺序
     */
    @Schema(description = "排序顺序", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer sortOrder;

    /**
     * 字典数据描述
     */
    @Schema(description = "字典数据描述", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String description;

    /**
     * 是否启用
     */
    @TableField("is_active")
    @Schema(description = "是否启用", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Boolean active;

}
