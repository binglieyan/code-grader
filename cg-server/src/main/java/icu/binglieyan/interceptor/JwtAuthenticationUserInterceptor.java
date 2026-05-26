package icu.binglieyan.interceptor;

import icu.binglieyan.constant.JwtClaimsConstant;
import icu.binglieyan.constant.MessageConstant;
import icu.binglieyan.properties.JwtProperties;
import icu.binglieyan.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.jspecify.annotations.NonNull;
import org.springframework.data.redis.core.RedisTemplate;

import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.Map;

/**
 * JWT认证过滤器 - 用户
 * @author binglieyan
 */
@Component
@Log4j2
@RequiredArgsConstructor
public class JwtAuthenticationUserInterceptor implements HandlerInterceptor {

    private static final String REDIS_USER_TOKEN_PREFIX = "user:token:";
    private final JwtProperties jwtProperties;
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String REDIS_TOKEN_PREFIX = "login:user:";

    /**
     * 角色对应的路径前缀
     */
    private static final Map<String, String> ROLE_PATH_PREFIX = Map.of(
            "/admin/", "ADMIN",
            "/student/", "STUDENT",
            "/teacher/", "TEACHER"
    );

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,
                             @NonNull HttpServletResponse response,
                             @NonNull Object handler) throws IOException {

        //判断当前拦截到的是Controller的方法还是其他资源
        if (!(handler instanceof HandlerMethod)) {
            //当前拦截到的不是动态方法，直接放行
            return true;
        }

        // 1、从请求头中获取令牌
        String token = request.getHeader(jwtProperties.getUserTokenName());

        if (token == null || token.isBlank()) {
            sendUnauthorizedResponse(response, "未登录");
            return false;
        }

        // 2、先检查Redis中是否存在该Token
        String redisTokenKey = REDIS_TOKEN_PREFIX + token;
        Object userIdObj = redisTemplate.opsForValue().get(redisTokenKey);

        if (userIdObj == null) {
            sendUnauthorizedResponse(response, "Token 已失效或不存在");
            return false;
        }

        // 从 Map 中提取用户 ID（存储格式：{"id": userId}）
        @SuppressWarnings("unchecked")
        Map<String, Object> userMap = (Map<String, Object>) userIdObj;
        Integer redisUserId = (Integer) userMap.get("id");

        // 3、校验JWT令牌
        try {
            log.info("jwt校验:{}", token);
            Claims claims = JwtUtil.parseJwt(jwtProperties.getUserSecretKey(), token);
            Integer userId = (Integer) claims.get(JwtClaimsConstant.USER_ID);
            String userRoleCode = (String) claims.get(JwtClaimsConstant.USER_ROLE_CODE);


            if (!userId.equals(redisUserId)) {
                sendUnauthorizedResponse(response, "Token信息不匹配");
                return false;
            }

            // 4、单点登录验证：检查该token是否是用户当前最新的token
            String userTokenKey = REDIS_USER_TOKEN_PREFIX + userId;
            String currentToken = (String) redisTemplate.opsForValue().get(userTokenKey);
            if (!token.equals(currentToken)){
                log.info("当前用户不是最新的token");
                sendUnauthorizedResponse(response, MessageConstant.USER_TOKEN_EXPIRED);
                return false;
            }

            // 5、校验用户token中的角色编码是否匹配url请求头
            String requestUri = request.getRequestURI();
            log.info("请求路径: {}, 用户角色: {}", requestUri, userRoleCode);
            for (Map.Entry<String, String> entry : ROLE_PATH_PREFIX.entrySet()) {
                if (requestUri.startsWith(entry.getKey())) {
                    // 如果路径匹配，检查角色是否一致
                    if (!entry.getValue().equals(userRoleCode)) {
                        log.info("角色权限不足，请求路径: {}, 需要角色: {}, 当前角色: {}",
                                requestUri, entry.getValue(), userRoleCode);
                        sendUnauthorizedResponse(response, "权限不足，需要" + entry.getValue() + "角色");
                        return false;
                    }
                    break; // 找到匹配的路径后跳出循环
                }
            }
            log.info("当前用户id：{}", userId);

            return true;
        } catch (Exception ex) {
            sendUnauthorizedResponse(response, "JWT校验失败: " + ex.getMessage());
        }
        return false;
    }

    private void sendUnauthorizedResponse(HttpServletResponse response, String message) throws IOException {
        log.error("认证失败: {}", message);
        response.setStatus(401);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\":401,\"message\":\"" + message + "\"}");
    }
}