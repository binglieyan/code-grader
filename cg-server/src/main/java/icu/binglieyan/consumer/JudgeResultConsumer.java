package icu.binglieyan.consumer;

import icu.binglieyan.dto.JudgeResultDTO;
import lombok.extern.log4j.Log4j2;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;

/**
 * 判题结果消费者
 * 接收 judge 返回的 JUDGE_RESULT 消息
 *
 * @author binglieyan
 */
@Log4j2
@Service
@RocketMQMessageListener(
        topic = "${cg.rocketmq.judge-result-topic:JUDGE_RESULT}",
        consumerGroup = "${cg.rocketmq.consumer-group:judge-result-consumer}"
)
public class JudgeResultConsumer implements RocketMQListener<JudgeResultDTO> {

    /**
     * 消费判题结果消息
     *
     * @param result 判题结果
     */
    @Override
    public void onMessage(JudgeResultDTO result) {
        log.info("收到判题结果 - submissionId: {}, statusCode: {}, totalScore: {}",
                result.getSubmissionId(), result.getStatusCode(), result.getTotalScore());

        if ("AUTO_JUDGE_FAILED".equals(result.getStatusCode())) {
            log.error("判题失败 - submissionId: {}, error: {}",
                    result.getSubmissionId(), result.getErrorMessage());
        }
    }
}
