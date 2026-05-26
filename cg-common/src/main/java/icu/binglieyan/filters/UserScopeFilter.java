package icu.binglieyan.filters;

import icu.binglieyan.constant.JwtClaimsConstant;
import icu.binglieyan.context.BaseContext;

import icu.binglieyan.properties.JwtProperties;
import icu.binglieyan.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.util.StringUtils;

import java.io.IOException;

/**
 * 使用 Scoped Values 替代拦截器的过滤器
 *
 * @author binglieyan
 */
@Log4j2
@RequiredArgsConstructor
public class UserScopeFilter implements Filter {

    private final JwtProperties jwtProperties;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String token = httpRequest.getHeader(jwtProperties.getUserTokenName());

        if (!StringUtils.hasText(token)) {
            chain.doFilter(request, response);
            return;
        }

        Long idToBind = null;
        String userNumberToBind = null;

        try {
            Claims claims = JwtUtil.parseJwt(jwtProperties.getUserSecretKey(), token);
            Integer userId = (Integer) claims.get(JwtClaimsConstant.USER_ID);
            String userNumber = (String) claims.get(JwtClaimsConstant.USER_NUMBER);

            if (userId != null) {
                idToBind = Long.valueOf(userId);
            }
            if (StringUtils.hasText(userNumber)) {
                userNumberToBind = userNumber;
            }
        } catch (Exception ex) {
            sendUnauthorizedResponse(httpResponse, "JWT 校验失败：" + ex.getMessage());
            return;
        }

        if (idToBind != null && userNumberToBind != null) {
            ScopedValue
                    .where(BaseContext.CURRENT_ID, idToBind)
                    .where(BaseContext.CURRENT_USER_NUMBER, userNumberToBind)
                    .run(() -> {
                        try {
                            chain.doFilter(request, response);
                        } catch (IOException | ServletException e) {
                            throw new RuntimeException(e);
                        }
                    });
        } else {
            chain.doFilter(request, response);
        }
    }

    private void sendUnauthorizedResponse(HttpServletResponse response, String message) throws IOException {
        log.error("认证失败: {}", message);
        response.setStatus(401);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\":401,\"message\":\"" + message + "\"}");
    }
}