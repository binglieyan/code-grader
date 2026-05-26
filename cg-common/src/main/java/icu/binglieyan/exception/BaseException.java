package icu.binglieyan.exception;

/**
 * 业务异常
 * @author binglieyan
 */
public class BaseException extends RuntimeException {

    public BaseException(String message) {
        super(message);
    }
}