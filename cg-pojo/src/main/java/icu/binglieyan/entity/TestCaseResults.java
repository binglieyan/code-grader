package icu.binglieyan.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 测试结果实体类
 *
 * @author binglieya
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestCaseResults implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 测试用例结果ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 题目提交详情ID
     */
    private Long questionSubmissionId;

    /**
     * 测试用例ID
     */
    private Long testCaseId;

    /**
     * 作业提交记录ID
     */
    private Long submissionId;

    /**
     * 实际输出
     */
    private String actualOutput;

    /**
     * 是否通过
     */
    @TableField("is_passed")
    private Boolean  passed;

    /**
     * 执行时长
     */
    private Integer executionTime;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

}
