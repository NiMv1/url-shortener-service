package com.urlshortener.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация RabbitMQ
 */
@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.queues.analytics}")
    private String analyticsQueue;

    @Value("${rabbitmq.queues.notifications}")
    private String notificationsQueue;

    @Value("${rabbitmq.exchanges.analytics}")
    private String analyticsExchange;

    @Value("${rabbitmq.routing-keys.click}")
    private String clickRoutingKey;

    @Value("${rabbitmq.routing-keys.created}")
    private String createdRoutingKey;

    @Bean
    public Queue analyticsQueue() {
        return QueueBuilder.durable(analyticsQueue).build();
    }

    @Bean
    public Queue notificationsQueue() {
        return QueueBuilder.durable(notificationsQueue).build();
    }

    @Bean
    public TopicExchange analyticsExchange() {
        return new TopicExchange(analyticsExchange);
    }

    @Bean
    public Binding clickBinding(Queue analyticsQueue, TopicExchange analyticsExchange) {
        return BindingBuilder.bind(analyticsQueue).to(analyticsExchange).with(clickRoutingKey);
    }

    @Bean
    public Binding createdBinding(Queue analyticsQueue, TopicExchange analyticsExchange) {
        return BindingBuilder.bind(analyticsQueue).to(analyticsExchange).with(createdRoutingKey);
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
