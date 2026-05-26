package icu.binglieyan.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 题目提交详情实体类
 *
 * @author binglieya
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionSubmissions implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 题目提交详情ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 题目ID
     */
    private Long questionId;

    /**
     * 学生ID
     */
    private Long studentId;

    /**
     * 学生答案
     */
    private String studentAnswer;

    /**
     * 题目得分
     */
    private BigDecimal score;

    /**
     * 批改教师ID
     */
    private Long gradedById;

    /**
     * 批改完成时间
     */
    private LocalDateTime gradingCompletedAt;

    /**
     * 教师反馈
     */
    private String teacherFeedback;

}
