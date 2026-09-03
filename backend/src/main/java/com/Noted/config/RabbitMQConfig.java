package com.Noted.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public MessageConverter jsonMessageConverter(){
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public Queue queue(){
        return new Queue("summary-job-queue", true);
    }

    @Bean
    public Exchange exchange(){
        return new DirectExchange("summary-exchange");
    }

    @Bean
    public Binding binding(Queue queue, Exchange exchange){
        return BindingBuilder.bind(queue)
                .to(exchange)
                .with("routing-key")
                .noargs();
    }
}
