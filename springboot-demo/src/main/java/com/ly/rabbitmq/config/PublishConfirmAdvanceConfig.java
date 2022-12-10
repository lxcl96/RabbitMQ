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
    public static final String BACKUP_EXCHANGE = "backup.exchange";//fanout类型交换机
    public static final String BACKUP_QUEUE = "backup.queue";
    public static final String WARNING_QUEUE = "warning.queue";

    @Bean(CONFIRM_EXCHANGE)
    public DirectExchange getExchangeInstance(){
        return ExchangeBuilder
                .directExchange(CONFIRM_EXCHANGE)
                .durable(true)
                //设置备用交换机（不可路由消息）
                .withArgument("alternate-exchange",BACKUP_EXCHANGE)
                .build();
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

    /**
     * 备份交换机、备份队列（备份消费者）以及告警系列（告警消费者）
     */
    @Bean(BACKUP_EXCHANGE)
    public FanoutExchange getBackupExchange() {
        return ExchangeBuilder.fanoutExchange(BACKUP_EXCHANGE).durable(true).build();
    }
    @Bean(BACKUP_QUEUE)
    public Queue getBackupQueue(){
        return QueueBuilder.durable(BACKUP_QUEUE).maxPriority(10).build();
    }

    @Bean
    public Binding bindBackupExchangeAndQueue(
            @Qualifier(BACKUP_QUEUE) Queue queue,
            @Qualifier(BACKUP_EXCHANGE) FanoutExchange exchange){
        return BindingBuilder.bind(queue).to(exchange);
    }
    //警告队列
    @Bean(WARNING_QUEUE)
    public Queue getWarningQueue(){
        return QueueBuilder.durable(WARNING_QUEUE).build();
    }

    @Bean
    public Binding bindBackupExchangeAndWarningQueue(
            @Qualifier(WARNING_QUEUE) Queue queue,
            @Qualifier(BACKUP_EXCHANGE) FanoutExchange exchange){
        return BindingBuilder.bind(queue).to(exchange);
    }
}
