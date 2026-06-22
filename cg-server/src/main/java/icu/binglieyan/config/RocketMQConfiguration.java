package icu.binglieyan.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Configuration;

/**
 * RocketMQ 配置类
 * rocketmq-spring-boot-starter 提供自动配置，此处仅做额外定制
 *
 * @author binglieyan
 */
@Log4j2
@Configuration
public class RocketMQConfiguration {

    // RocketMQTemplate 由 rocketmq-spring-boot-starter 自动配置
    // 如需自定义消息转换器或发送超时等参数，可在此添加 @Bean 方法
}
