package icu.binglieyan.utils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * IP工具类
 * @author binglieyan
 */
@Log4j2
public class IpUtil {
    private static final String UNKNOWN = "unknown";
    private static final String COMMA = ",";

    /**
     * 获取当前请求的客户端IP
     */
    public static String getClientIp() {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            return getClientIp(request);
        } catch (Exception exception) {
            log.error("获取IP失败", exception);
            return UNKNOWN;
        }
    }

    /**
     * 获取客户端IP
     */
    public static String getClientIp(HttpServletRequest request) {
        if (request == null) {
            return UNKNOWN;
        }

        // 1. X-Forwarded-For
        String ip = request.getHeader("X-Forwarded-For");
        if (isValidIp(ip)) {
            // 取第一个IP
            if (ip.contains(COMMA)) {
                ip = ip.split(COMMA)[0].trim();
            }
            return ip;
        }

        // 2. Proxy-Client-IP
        ip = request.getHeader("Proxy-Client-IP");
        if (isValidIp(ip)) {
            return ip;
        }

        // 3. WL-Proxy-Client-IP
        ip = request.getHeader("WL-Proxy-Client-IP");
        if (isValidIp(ip)) {
            return ip;
        }

        // 4. HTTP_CLIENT_IP
        ip = request.getHeader("HTTP_CLIENT_IP");
        if (isValidIp(ip)) {
            return ip;
        }

        // 5. X-Real-IP
        ip = request.getHeader("X-Real-IP");
        if (isValidIp(ip)) {
            return ip;
        }

        // 6. 最后使用getRemoteAddr
        return request.getRemoteAddr();

    }

    /**
     * 判断IP是否有效
     */
    private static boolean isValidIp(String ip) {
        return StringUtils.hasText(ip) && !UNKNOWN.equalsIgnoreCase(ip);
    }

}
