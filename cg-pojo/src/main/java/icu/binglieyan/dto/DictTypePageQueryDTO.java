package icu.binglieyan.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 字典类型分页查询DTO类
 *
 * @author binglieyan
 */
@Data
@Schema(description = "字典类型信息传递时的数据模型")
public class DictTypePageQueryDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

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
     * 是否为系统字典
     */
    @TableField("is_system")
    @Schema(description = "是否为系统字典", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Boolean system;

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
