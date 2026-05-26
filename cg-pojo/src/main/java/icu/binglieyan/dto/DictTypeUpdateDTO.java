package icu.binglieyan.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 字典类型更新DTO类
 *
 * @author binglieyan
 */
@Data
@Schema(description = "字典类型信息传递时的数据模型")
public class DictTypeUpdateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 字典类型ID
     */
    @Schema(description = "字典类型ID",  requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    /**
     * 字典类型编码
     */
    @Size(min = 1, max = 100, message = "字典类型编码长度必须在1-100个字符之间")
    @Schema(description = "字典类型编码", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String typeCode;

    /**
     * 字典类型名称
     */
    @Size(min = 1, max = 100, message = "字典类型名称长度必须在1-100个字符之间")
    @Schema(description = "字典类型名称", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String typeName;


    /**
     * 字典类型描述
     */
    @Size(min = 1, max = 100, message = "字典类型描述长度必须在1-100个字符之间")
    @Schema(description = "字典类型描述", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String description;

    /**
     * 是否为系统字典
     */
    @TableField("is_system")
    @Schema(description = "是否为系统字典", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Boolean system;

}
