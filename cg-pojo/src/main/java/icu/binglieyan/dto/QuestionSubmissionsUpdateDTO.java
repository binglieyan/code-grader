package icu.binglieyan.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 题目提交详情更新DTO类
 *
 * @author binglieya
 */
@Data
public class QuestionSubmissionsUpdateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 题目提交详情ID
     */
    @Schema(description = "题目提交详情ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    /**
     * 题目得分
     */
    @Schema(description = "题目得分", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal score;

    /**
     * 教师反馈
     */
    @Schema(description = "教师反馈", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String teacherFeedback;

}
