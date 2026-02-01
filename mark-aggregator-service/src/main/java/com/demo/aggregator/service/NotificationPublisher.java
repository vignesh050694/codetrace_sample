package com.demo.aggregator.service;

import com.demo.aggregator.dto.MarkNotificationDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class NotificationPublisher {

    private static final Logger logger = LoggerFactory.getLogger(NotificationPublisher.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.notification}")
    private String exchange;

    @Value("${rabbitmq.routing-key.notify-mark}")
    private String routingKey;

    public void publishMarkNotification(MarkNotificationDTO notification) {
        try {
            rabbitTemplate.convertAndSend(exchange, routingKey, notification);
            logger.info("Published mark notification to RabbitMQ for student: {} subject: {}",
                    notification.getStudentRollNumber(), notification.getSubject());
        } catch (Exception e) {
            logger.error("Failed to publish notification to RabbitMQ: {}", e.getMessage());
        }
    }
}
