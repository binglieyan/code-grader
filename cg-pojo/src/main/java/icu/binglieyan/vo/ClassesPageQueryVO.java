package icu.binglieyan.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;


/**
 * 教学班分页查询VO类
 *
 * @author binglieyan
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "教学班分页查询返回的视图模型")
public class ClassesPageQueryVO implements Serializable {

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
     * 教师编号
     */
    @Schema(description = "教师编号", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String teacherNumber;

}
