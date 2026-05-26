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
 * 作业提交实体类
 *
 * @author binglieyan
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Submissions implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 提交记录ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 作业ID
     */
    private Long assignmentId;

    /**
     * 学生ID
     */
    private Long studentId;

    /**
     * 提交时间
     */
    private LocalDateTime submittedAt;

    /**
     * 提交IP地址
     */
    private String ipAddress;

    /**
     * 提交用户代理(浏览器信息)
     */
    private String userAgent;

    /**
     * 作业总得分
     */
    private BigDecimal totalScore;

    /**
     * 提交状态编码
     */
    private String submissionStatusCode;

    /**
     * 批改完成时间
     */
    private LocalDateTime gradingCompletedAt;

}
