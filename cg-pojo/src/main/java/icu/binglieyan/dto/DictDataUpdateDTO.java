package icu.binglieyan.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;


/**
 * 字典数据更新DTO类
 *
 * @author binglieyan
 */
@Data
@Schema(description = "字典数据信息传递时的数据模型")
public class DictDataUpdateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 字典数据ID
     */
    @Schema(description = "字典数据ID",  requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    /**
     * 字典类型编码
     */
    @Size
    @Size(min = 1, max = 100, message = "字典类型编码长度必须在1-100个字符之间")
    @Schema(description = "字典类型编码", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String typeCode;

    /**
     * 字典数据编码
     */
    @Size(min = 1, max = 100, message = "字典数据编码长度必须在1-100个字符之间")
    @Schema(description = "字典数据编码", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String dataCode;

    /**
     * 字典数据值
     */
    @Size(min = 1, max = 100, message = "字典数据值长度必须在1-100个字符之间")
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
    @Size(min = 1, max = 100, message = "字典数据描述长度必须在1-100个字符之间")
    @Schema(description = "字典数据描述", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String description;

    /**
     * 是否启用
     */
    @TableField("is_active")
    @Schema(description = "是否启用", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Boolean active;

}
