package com.Noted.controller;

import com.Noted.messaging.RabbitMQProducer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/jobs")
public class JobController {

    private final RabbitMQProducer rabbitMQProducer;

    public JobController(RabbitMQProducer rabbitMQProducer) {
        this.rabbitMQProducer = rabbitMQProducer;
    }

    @PostMapping("/test/{message}")
    public ResponseEntity<Void> testJobMessaging(@PathVariable String message){
        rabbitMQProducer.sendMessage(message);
        return ResponseEntity.noContent().build();
    }
}
