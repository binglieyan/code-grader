package icu.binglieyan.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 查重任务实体类
 *
 * @author binglieyan
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlagiarismChecks implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 查重任务ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 作业ID
     */
    private Long assignmentId;

    /**
     * 教师ID
     */
    private Long initiatedById;

    /**
     * 总比较数
     */
    private Integer totalComparisons;

    /**
     * 执行时长
     */
    private Integer executionTime;

    /**
     * 报告文件路径
     */
    private String reportPath;

    /**
     * 查重状态编码
     */
    private String statusCode;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 完成时间
     */
    private LocalDateTime completedAt;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}
