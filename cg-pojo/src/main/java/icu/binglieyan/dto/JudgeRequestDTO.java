package icu.binglieyan.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 判题请求 DTO
 * 通过 RocketMQ 从 code-grader 发送到 judge
 *
 * @author binglieyan
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JudgeRequestDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 作业提交ID
     */
    private Long submissionId;

    /**
     * 作业ID
     */
    private Long assignmentId;

    /**
     * 学生ID
     */
    private Long studentId;
}
