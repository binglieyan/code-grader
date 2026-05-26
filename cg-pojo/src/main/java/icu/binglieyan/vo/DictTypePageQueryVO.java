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
 * 字典类型DTO类
 *
 * @author binglieyan
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "字典类型信息传递时的数据模型")
public class DictTypePageQueryVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 字典类型ID
     */
    @Schema(description = "字典类型ID",  requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long id;

    /**
     * 字典类型编码
     */
    @Schema(description = "字典类型编码", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String typeCode;

    /**
     * 字典类型名称
     */
    @Schema(description = "字典类型名称", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String typeName;


    /**
     * 字典类型描述
     */
    @Schema(description = "字典类型描述", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String description;

    /**
     * 是否为系统字典
     */
    @Schema(description = "是否为系统字典", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Boolean system;

}
