package icu.binglieyan.producer;

import icu.binglieyan.dto.JudgeRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

/**
 * 判题请求生产者
 * 发送 JUDGE_REQUEST 消息到 RocketMQ
 *
 * @author binglieyan
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class JudgeRequestProducer {

    private final RocketMQTemplate rocketMQTemplate;

    @Value("${cg.rocketmq.judge-request-topic:JUDGE_REQUEST}")
    private String topic;

    /**
     * 发送判题请求
     *
     * @param submissionId 作业提交ID
     * @param assignmentId 作业ID
     * @param studentId    学生ID
     */
    public void send(Long submissionId, Long assignmentId, Long studentId) {
        JudgeRequestDTO request = JudgeRequestDTO.builder()
                .submissionId(submissionId)
                .assignmentId(assignmentId)
                .studentId(studentId)
                .build();

        Message<JudgeRequestDTO> message = MessageBuilder
                .withPayload(request)
                .setHeader("KEYS", submissionId.toString())
                .build();

        SendResult result = rocketMQTemplate.syncSend(topic, message);
        log.info("判题请求已发送 - submissionId: {}, msgId: {}, sendStatus: {}",
                submissionId, result.getMsgId(), result.getSendStatus());
    }
}
