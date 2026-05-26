package icu.binglieyan.utils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @author binglieyan
 */
@Log4j2
public class UserAgentUtil {

    private static final String UNKNOWN = "unknown";

    /**
     * 获取当前请求的User-Agent
     */
    public static String getUserAgent() {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            return getUserAgent(request);
        } catch (Exception e) {
            log.error("获取User-Agent失败", e);
            return UNKNOWN;
        }
    }

    /**
     * 获取User-Agent
     */
    public static String getUserAgent(HttpServletRequest request) {
        if (request == null) {
            return UNKNOWN;
        }
        String ua = request.getHeader("User-Agent");
        if (ua == null){
            return UNKNOWN;
        }
        return ua;
    }
}
