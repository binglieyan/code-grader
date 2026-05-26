package icu.binglieyan.handler;

import icu.binglieyan.constant.MessageConstant;
import icu.binglieyan.exception.BaseException;
import icu.binglieyan.result.Result;
import io.jsonwebtoken.MalformedJwtException;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * 全局异常处理器，处理项目中抛出的业务异常
 * @author binglieyan
 */
@Hidden
@RestControllerAdvice
@Log4j2
public class GlobalExceptionHandler {

    /**
     * 捕获业务异常
     * @param ex 业务异常对象
     * @return Result 错误结果
     */
    @ExceptionHandler(BaseException.class)
    public Result<String> exceptionHandler(BaseException ex){
        log.error("异常信息：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    /**
     * 处理SQL异常
     * @param ex SQL完整性约束违反异常对象
     * @return Result 错误结果
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public Result<String> exceptionHandler(SQLIntegrityConstraintViolationException ex){
        String message = ex.getMessage();
        if(message.contains(MessageConstant.DUPLICATE_ENTRY)){
            String[] split = message.split(" ");
            String username = split[2];
            String msg = username + MessageConstant.ALREADY_EXISTS;
            return Result.error(msg);
        }else{
            return Result.error(MessageConstant.UNKNOWN_ERROR);
        }
    }

    /**
     * 处理参数验证异常
     * @param ex 参数验证异常对象
     * @return Result 错误结果
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<String> exceptionHandler(MethodArgumentNotValidException ex) {
        log.error("参数验证异常：{}", ex.getMessage());
        // 获取第一个验证错误信息
        String message = Objects.requireNonNull(ex.getBindingResult().getFieldError()).getDefaultMessage();
        return Result.error(message);
    }

    /**
     * 处理运行时异常
     * @param ex 运行时异常对象
     * @return Result 错误结果
     */
    @ExceptionHandler(RuntimeException.class)
    public Result<String> exceptionHandler(RuntimeException ex) {
        log.error("运行时异常：{}", ex.getMessage(), ex);
        return Result.error(ex.getMessage() != null ? ex.getMessage() : MessageConstant.UNKNOWN_ERROR);
    }

    /**
     * 处理 IO 异常（文件操作、Docker 操作等）
     * @param ex IO 异常对象
     * @return Result 错误结果
     */
    @ExceptionHandler(IOException.class)
    public Result<String> exceptionHandler(IOException ex) {
        log.error("IO 异常：{}", ex.getMessage(), ex);
        return Result.error("文件操作失败：" + (ex.getMessage() != null ? ex.getMessage() : "未知 IO 错误"));
    }

    /**
     * 处理线程中断异常
     * @param ex 中断异常对象
     * @return Result 错误结果
     */
    @ExceptionHandler(InterruptedException.class)
    public Result<String> exceptionHandler(InterruptedException ex) {
        log.error("线程中断异常：{}", ex.getMessage(), ex);
        Thread.currentThread().interrupt();
        return Result.error("操作被中断");
    }

    /**
     * 处理空指针异常
     * @param ex 空指针异常对象
     * @return Result 错误结果
     */
    @ExceptionHandler(NullPointerException.class)
    public Result<String> exceptionHandler(NullPointerException ex) {
        log.error("空指针异常：{}", ex.getMessage(), ex);
        return Result.error("数据为空或格式不正确");
    }

    /**
     * 处理参数非法异常
     * @param ex 参数非法异常对象
     * @return Result 错误结果
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public Result<String> exceptionHandler(IllegalArgumentException ex) {
        log.error("参数非法异常：{}", ex.getMessage(), ex);
        return Result.error("参数错误：" + (ex.getMessage() != null ? ex.getMessage() : "无效的参数"));
    }

    /**
     * 处理状态非法异常
     * @param ex 状态非法异常对象
     * @return Result 错误结果
     */
    @ExceptionHandler(IllegalStateException.class)
    public Result<String> exceptionHandler(IllegalStateException ex) {
        log.error("状态非法异常：{}", ex.getMessage(), ex);
        return Result.error("系统状态错误：" + (ex.getMessage() != null ? ex.getMessage() : "非法的系统状态"));
    }

    /**
     * 处理并发执行异常（如 CompletableFuture 等）
     * @param ex 执行异常对象
     * @return Result 错误结果
     */
    @ExceptionHandler(ExecutionException.class)
    public Result<String> exceptionHandler(ExecutionException ex) {
        log.error("并发执行异常：{}", ex.getMessage(), ex);
        Throwable cause = ex.getCause();
        String errorMsg = cause != null ? cause.getMessage() : ex.getMessage();
        return Result.error("任务执行失败：" + (errorMsg != null ? errorMsg : "未知错误"));
    }

    /**
     * 处理安全异常（如 JWT 相关异常）
     * @param ex 安全异常对象
     * @return Result 错误结果
     */
    @ExceptionHandler(MalformedJwtException.class)
    public Result<String> exceptionHandler(MalformedJwtException ex) {
        log.error("JWT 格式错误：{}", ex.getMessage());
        return Result.error("Token 格式错误或已过期");
    }

    /**
     * 处理算术异常（如除以零等）
     * @param ex 算术异常对象
     * @return Result 错误结果
     */
    @ExceptionHandler(ArithmeticException.class)
    public Result<String> exceptionHandler(ArithmeticException ex) {
        log.error("算术异常：{}", ex.getMessage(), ex);
        return Result.error("计算错误：" + (ex.getMessage() != null ? ex.getMessage() : "算术运算失败"));
    }

    /**
     * 处理索引越界异常
     * @param ex 索引越界异常对象
     * @return Result 错误结果
     */
    @ExceptionHandler(IndexOutOfBoundsException.class)
    public Result<String> exceptionHandler(IndexOutOfBoundsException ex) {
        log.error("索引越界异常：{}", ex.getMessage(), ex);
        return Result.error("数据访问越界");
    }

    /**
     * 处理类型转换异常
     * @param ex 类型转换异常对象
     * @return Result 错误结果
     */
    @ExceptionHandler(ClassCastException.class)
    public Result<String> exceptionHandler(ClassCastException ex) {
        log.error("类型转换异常：{}", ex.getMessage(), ex);
        return Result.error("数据类型不匹配");
    }

    /**
     * 处理NoSuchElementException（如 Iterator、Optional 等）
     * @param ex 元素不存在异常对象
     * @return Result 错误结果
     */
    @ExceptionHandler(java.util.NoSuchElementException.class)
    public Result<String> exceptionHandler(java.util.NoSuchElementException ex) {
        log.error("元素不存在异常：{}", ex.getMessage(), ex);
        return Result.error("请求的数据不存在");
    }

    /**
     * 处理超时异常（Docker、网络请求等）
     * @param ex 超时异常对象
     * @return Result 错误结果
     */
    @ExceptionHandler(TimeoutException.class)
    public Result<String> exceptionHandler(TimeoutException ex) {
        log.error("操作超时：{}", ex.getMessage(), ex);
        return Result.error("操作超时，请稍后重试");
    }

    /**
     * 处理 JSON 处理异常（Jackson、Gson 等）
     * @param ex JSON 处理异常对象
     * @return Result 错误结果
     */
    @ExceptionHandler(com.fasterxml.jackson.core.JsonProcessingException.class)
    public Result<String> exceptionHandler(com.fasterxml.jackson.core.JsonProcessingException ex) {
        log.error("JSON 处理异常：{}", ex.getMessage(), ex);
        return Result.error("数据格式错误");
    }

    /**
     * 处理 JPlag 查重库的 ExitException
     * @param ex ExitException 异常对象
     * @return Result 错误结果
     */
    @ExceptionHandler(de.jplag.exceptions.ExitException.class)
    public Result<String> exceptionHandler(de.jplag.exceptions.ExitException ex) {
        log.error("JPlag 查重异常：{}", ex.getMessage(), ex);
        return Result.error("代码查重失败");
    }

    /**
     * 处理文件未找到异常
     * @param ex 文件未找到异常对象
     * @return Result 错误结果
     */
    @ExceptionHandler(java.io.FileNotFoundException.class)
    public Result<String> exceptionHandler(java.io.FileNotFoundException ex) {
        log.error("文件未找到：{}", ex.getMessage(), ex);
        return Result.error("文件不存在或无法访问");
    }

    /**
     * 处理不支持的操作异常
     * @param ex 不支持的操作异常对象
     * @return Result 错误结果
     */
    @ExceptionHandler(UnsupportedOperationException.class)
    public Result<String> exceptionHandler(UnsupportedOperationException ex) {
        log.error("不支持的操作：{}", ex.getMessage(), ex);
        return Result.error("当前操作不被支持");
    }

    /**
     * 处理安全异常（权限不足等）
     * @param ex 安全异常对象
     * @return Result 错误结果
     */
    @ExceptionHandler(SecurityException.class)
    public Result<String> exceptionHandler(SecurityException ex) {
        log.error("安全异常：{}", ex.getMessage(), ex);
        return Result.error("权限不足或访问被拒绝");
    }

    /**
     * 处理验证错误异常（JSR-303/JSR-380）
     * @param ex 约束违反异常对象
     * @return Result 错误结果
     */
    @ExceptionHandler(jakarta.validation.ConstraintViolationException.class)
    public Result<String> exceptionHandler(jakarta.validation.ConstraintViolationException ex) {
        log.error("验证约束违反：{}", ex.getMessage(), ex);
        return Result.error("参数验证失败");
    }

    /**
     * 处理类型不匹配异常（数据库操作等）
     * @param ex 类型不匹配异常对象
     * @return Result 错误结果
     */
    @ExceptionHandler(org.springframework.dao.TypeMismatchDataAccessException.class)
    public Result<String> exceptionHandler(org.springframework.dao.TypeMismatchDataAccessException ex) {
        log.error("数据类型不匹配：{}", ex.getMessage(), ex);
        return Result.error("数据类型不匹配");
    }

    /**
     * 处理资源未找到异常（Spring Data Access）
     * @param ex 资源未找到异常对象
     * @return Result 错误结果
     */
    @ExceptionHandler(org.springframework.dao.EmptyResultDataAccessException.class)
    public Result<String> exceptionHandler(org.springframework.dao.EmptyResultDataAccessException ex) {
        log.error("数据未找到：{}", ex.getMessage(), ex);
        return Result.error("查询的数据不存在");
    }

    /**
     * 处理数据库访问异常
     * @param ex 数据库访问异常对象
     * @return Result 错误结果
     */
    @ExceptionHandler(org.springframework.dao.DataAccessException.class)
    public Result<String> exceptionHandler(org.springframework.dao.DataAccessException ex) {
        log.error("数据库访问异常：{}", ex.getMessage(), ex);
        return Result.error("数据库操作失败");
    }

    /**
     * 处理其他所有未捕获的异常
     * @param ex 异常对象
     * @return Result 错误结果
     */
    @ExceptionHandler(Exception.class)
    public Result<String> exceptionHandler(Exception ex) {
        log.error("未预期的异常：{}", ex.getMessage(), ex);
        return Result.error(MessageConstant.UNKNOWN_ERROR);
    }

}
