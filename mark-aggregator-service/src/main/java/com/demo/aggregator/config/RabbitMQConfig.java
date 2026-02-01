package com.demo.aggregator.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.exchange.notification}")
    private String exchange;

    @Value("${rabbitmq.queue.notify-mark}")
    private String queue;

    @Value("${rabbitmq.routing-key.notify-mark}")
    private String routingKey;

    @Bean
    public TopicExchange notificationExchange() {
        return new TopicExchange(exchange);
    }

    @Bean
    public Queue notifyMarkQueue() {
        return new Queue(queue, true);
    }

    @Bean
    public Binding notifyMarkBinding(Queue notifyMarkQueue, TopicExchange notificationExchange) {
        return BindingBuilder.bind(notifyMarkQueue).to(notificationExchange).with(routingKey);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}
