package icu.binglieyan.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 测试用例结果VO类
 *
 * @author binglieya
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "测试用例结果查询返回的视图模型")
public class HiddenTestCaseResultsVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 通过数
     */
    @Schema(description = "通过数", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer passedCount;

    /**
     * 总数
     */
    @Schema(description = "总数", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer totalCount;
}
