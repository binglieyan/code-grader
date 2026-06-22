package icu.binglieyan.config;

import lombok.extern.log4j.Log4j2;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RocketMQ 配置类
 * 所有 RocketMQ 参数统一从 cg.rocketmq.* 读取
 *
 * @author binglieyan
 */
@Log4j2
@Configuration
public class RocketMQConfiguration {

    @Value("${cg.rocketmq.name-server}")
    private String nameServer;

    @Value("${cg.rocketmq.producer-group}")
    private String producerGroup;

    @Bean
    public RocketMQTemplate rocketMQTemplate() {
        DefaultMQProducer producer = new DefaultMQProducer();
        producer.setNamesrvAddr(nameServer);
        producer.setProducerGroup(producerGroup);
        RocketMQTemplate template = new RocketMQTemplate();
        template.setProducer(producer);
        return template;
    }
}
