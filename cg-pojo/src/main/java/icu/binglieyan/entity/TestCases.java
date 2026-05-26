package icu.binglieyan.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 测试用例实体类
 *
 * @author binglieyan
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "test_cases", autoResultMap = true)
public class TestCases implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 测试用例ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 题目ID
     */
    private Long questionId;

    /**
     * 测试用例顺序
     */
    private Long caseOrder;

    /**
     * 输入数据
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> inputData;

    /**
     * 期望输出
     */
    private String expectedOutput;

    /**
     * 是否为隐藏测试用例
     */
    @TableField("is_hidden")
    private Boolean hidden;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}
