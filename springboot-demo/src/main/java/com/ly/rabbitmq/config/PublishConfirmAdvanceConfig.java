package com.ly.rabbitmq.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * FileName:PublishConfirmAdvanceConfig.class
 * Author:ly
 * Date:2022/12/9 0009
 * Description: 发布确认高级版 -- broker宕机情况
 */
@Configuration
public class PublishConfirmAdvanceConfig {
    public static final String CONFIRM_EXCHANGE = "confirm.exchange";
    public static final String CONFIRM_QUEUE = "confirm.queue";
    public static final String CONFIRM_ROUTING_KEY = "k1";

    @Bean(CONFIRM_EXCHANGE)
    public DirectExchange getExchangeInstance(){
        return ExchangeBuilder.directExchange(CONFIRM_EXCHANGE).durable(true).build();
    }

    @Bean(CONFIRM_QUEUE)
    public Queue getQueueInstance(){
        return QueueBuilder.durable(CONFIRM_QUEUE).build();
    }

    @Bean(CONFIRM_ROUTING_KEY)
    public Binding bindExchangeAndQueue(
            @Qualifier(CONFIRM_QUEUE) Queue queue,
            @Qualifier(CONFIRM_EXCHANGE) DirectExchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(CONFIRM_ROUTING_KEY);
    }

}
