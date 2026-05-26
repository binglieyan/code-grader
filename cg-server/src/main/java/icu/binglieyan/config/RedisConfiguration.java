package icu.binglieyan.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import tools.jackson.databind.json.JsonMapper;


/**
 * Redis配置类
 * @author binglieyan
 */
@Configuration
@Log4j2
@RequiredArgsConstructor
public class RedisConfiguration {

    private final JsonMapper jsonMapper;

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        log.info("开始初始化RedisTemplate");
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();

        // 创建 JSON 序列化器（用于值）
        GenericJacksonJsonRedisSerializer jsonSerializer =
                new GenericJacksonJsonRedisSerializer(jsonMapper);

        // 关键配置：KEY 必须使用字符串序列化器
        redisTemplate.setKeySerializer(RedisSerializer.string());
        redisTemplate.setValueSerializer(jsonSerializer);
        redisTemplate.setHashKeySerializer(RedisSerializer.string());
        redisTemplate.setHashValueSerializer(jsonSerializer);

        // 这些也必须使用字符串序列化器
        redisTemplate.setStringSerializer(RedisSerializer.string());

        // 设置连接工厂
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.afterPropertiesSet();

        return redisTemplate;
    }
}
