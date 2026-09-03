package com.Noted.messaging;

import com.Noted.dto.SummaryJobMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQProducer {

    private final RabbitTemplate rabbitTemplate;


    public RabbitMQProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendMessage(Long jobId, String noteText){
        SummaryJobMessage message = new SummaryJobMessage(jobId, noteText);
        rabbitTemplate.convertAndSend("summary-exchange", "routing-key", message);
    }
}
