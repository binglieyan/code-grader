package icu.binglieyan.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.List;

/**
 * @author binglieyan
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "分页查询返回结果")
public class PageResult<T> {

    @Schema(description = "总记录数", requiredMode = Schema.RequiredMode.REQUIRED)
    private long total;

    @Schema(description = "当前页数据", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<T> records;
}