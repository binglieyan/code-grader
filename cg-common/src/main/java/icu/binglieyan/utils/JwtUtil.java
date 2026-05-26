package icu.binglieyan.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

/**
 * JWT工具类
 * @author binglieyan
 */
public class JwtUtil {

    /**
     * 生成JWT (新版本JJWT)
     * 使用HS256算法, 密钥使用固定秘钥
     *
     * @param secretKey jwt秘钥
     * @param ttlMillis jwt过期时间(毫秒)
     * @param claims    设置的信息
     * @return JWT字符串
     */
    public static String createJwt(String secretKey, long ttlMillis, Map<String, Object> claims) {
        // 1. 将字符串密钥转换为安全的SecretKey对象
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

        // 2. 计算过期时间
        long expMillis = System.currentTimeMillis() + ttlMillis;
        Date exp = new Date(expMillis);

        // 3. 使用新API构建JWT
        JwtBuilder builder = Jwts.builder()
                // 新版设置claims
                .claims(claims)
                // 设置过期时间
                .expiration(exp)
                // 使用密钥签名（自动识别算法）
                .signWith(key);

        return builder.compact();
    }

    /**
     * Token解密 (新版本JJWT)
     *
     * @param secretKey jwt秘钥
     * @param token     加密后的token
     * @return 解析后的Claims
     */
    public static Claims parseJwt(String secretKey, String token) {
        // 1. 将字符串密钥转换为安全的SecretKey对象
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

        // 2. 使用新解析器构建模式
        return Jwts.parser()
                // 设置验证密钥
                .verifyWith(key)
                .build()
                // 解析签名token
                .parseSignedClaims(token)
                // 获取claims主体
                .getPayload();
    }
}