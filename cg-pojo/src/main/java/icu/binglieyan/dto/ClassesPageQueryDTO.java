package icu.binglieyan.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;


/**
 * 教学班分页查询DTO类
 *
 * @author binglieyan
 */
@Data
@Schema(description = "教学班分页查询信息传递时的数据模型")
public class ClassesPageQueryDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 班级名称
     */
    @Schema(description = "班级名称", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String className;

    /**
     * 班级代码
     */
    @Schema(description = "班级代码", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String classCode;

    /**
     * 教师代码
     */
    @Schema(description = "教师代码", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String teacherNumber;

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
