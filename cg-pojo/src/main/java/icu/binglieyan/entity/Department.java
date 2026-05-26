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
 * 院系实体类
 *
 * @author binglieyan
 *
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Department implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 院系ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 院系代码
     */
    private String departmentCode;

    /**
     * 院系名称
     */
    private String departmentName;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}
