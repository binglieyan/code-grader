package icu.binglieyan.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 判题结果 DTO
 * 通过 RocketMQ 从 judge 发送回 code-grader
 *
 * @author binglieyan
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JudgeResultDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 作业提交ID
     */
    private Long submissionId;

    /**
     * 状态码：GRADED / AUTO_JUDGE_FAILED
     */
    private String statusCode;

    /**
     * 总分（判题成功时有值）
     */
    private BigDecimal totalScore;

    /**
     * 错误信息（判题失败时有值）
     */
    private String errorMessage;
}
