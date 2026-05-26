package icu.binglieyan.result;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author binglieyan
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "统一返回结果")
public class Result<T> {

    @Schema(description = "状态码", requiredMode = Schema.RequiredMode.REQUIRED)
    private int code;

    @Schema(description = "返回信息", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String message;

    @Schema(description = "返回数据", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private T data;

    /**
     * 创建成功结果(无数据)
     * @param <T> 返回数据类型
     * @return 成功结果
     */
    public static <T> Result<T> success() {
        Result<T> result = new Result<>();
        result.code = 200;
        return result;
    }

    /**
     * 创建成功结果(带数据)
     * @param data 返回数据
     * @param <T> 返回数据类型
     * @return 成功结果
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.data = data;
        result.code = 200;
        return result;
    }

    /**
     * 创建错误结果
     * @param message 错误信息
     * @param <T> 返回数据类型
     * @return 错误结果
     */
    public static <T> Result<T> error(String message) {
        Result<T> result = new Result<>();
        result.message = message;
        result.code = 500;
        return result;
    }
}
