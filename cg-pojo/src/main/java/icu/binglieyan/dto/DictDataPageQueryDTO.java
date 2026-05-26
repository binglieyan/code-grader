package icu.binglieyan.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;


/**
 * 字典数据分页查询DTO类
 *
 * @author binglieyan
 */
@Data
@Schema(description = "字典数据信息传递时的数据模型")
public class DictDataPageQueryDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

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
     * 是否启用
     */
    @TableField("is_active")
    @Schema(description = "是否启用", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Boolean active;

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
