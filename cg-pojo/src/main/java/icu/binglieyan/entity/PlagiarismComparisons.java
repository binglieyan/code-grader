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
 * 相似性比较实体类
 *
 * @author binglieyan
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlagiarismComparisons implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 比较ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 查重任务ID
     */
    private Long plagiarismCheckId;

    /**
     * 第一个提交名称
     */
    private String firstSubmissionName;

    /**
     * 第二个提交名称
     */
    private String secondSubmissionName;

    /**
     * 平均相似度
     */
    private BigDecimal avgSimilarity;

    /**
     * 最大相似度
     */
    private BigDecimal maxSimilarity;

    /**
     * 最大长度
     */
    private BigDecimal maximumLength;

    /**
     * 最长匹配
     */
    private BigDecimal longestMatch;

    /**
     * 详细匹配文件路径
     */
    private String matchDetailsPath;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

}
