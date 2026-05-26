package icu.binglieyan.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 题目提交详情VO类
 *
 * @author binglieya
 */
@Data
@Builder
@Schema(description = "题目查询返回的视图模型")
public class QuestionSubmissionsVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 题目提交详情ID
     */
    @Schema(description = "题目提交详情ID", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long id;

    /**
     * 题目ID
     */
    @Schema(description = "题目ID", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long questionId;

    /**
     * 学生代码
     */
    @Schema(description = "学生ID", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String studentNumber;

    /**
     * 学生代码文件内容
     */
    @Schema(description = "学生代码文件内容", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String studentAnswerCode;

    /**
     * 题目得分
     */
    @Schema(description = "题目得分", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private BigDecimal score;

    /**
     * 批改教师姓名
     */
    @Schema(description = "批改教师姓名", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String gradedByName;

    /**
     * 批改完成时间
     */
    @Schema(description = "批改完成时间", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private LocalDateTime gradingCompletedAt;

    /**
     * 教师反馈
     */
    @Schema(description = "教师反馈", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String teacherFeedback;

}
