package icu.binglieyan.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT配置类
 * @author binglieyan
 */
@Component
@ConfigurationProperties(prefix = "cg.jwt")
@Data
public class JwtProperties {

    /**
     * 用户端生成jwt令牌相关配置
     */
    private String userSecretKey;
    private long userTtl;
    private String userTokenName;
}
