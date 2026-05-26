package icu.binglieyan.context;

import java.util.Optional;

/**
 * @author binglieyan
 */
public class BaseContext {

    /**
     * 使用ScopedValue替代ThreadLocal
     */
    public static final ScopedValue<Long> CURRENT_ID = ScopedValue.newInstance();
    public static final ScopedValue<String> CURRENT_USER_NUMBER = ScopedValue.newInstance();

    /**
     * 获取当前ID，如果不在作用域内，返回Optiwonal.empty()，避免NPE
     */
    public static Optional<Long> getCurrentId() {
        if (CURRENT_ID.isBound()) {
            return Optional.of(CURRENT_ID.get());
        }
        return Optional.empty();
    }
    /**
     * 获取当前用户编号，如果不在作用域内，返回Optiwonal.empty()，避免NPE
     */
    public static Optional<String> getCurrentUserNumber() {
        if (CURRENT_USER_NUMBER.isBound()) {
            return Optional.of(CURRENT_USER_NUMBER.get());
        }
        return Optional.empty();
    }
}