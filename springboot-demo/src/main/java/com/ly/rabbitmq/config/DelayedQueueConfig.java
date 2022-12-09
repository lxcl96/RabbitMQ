package com.ly.rabbitmq.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * FileName:DelayedQueueConfig.class
 * Author:ly
 * Date:2022/12/9 0009
 * Description: 基于延迟插件的 交换机-队列-路由key
 */
@Configuration
public class DelayedQueueConfig {
    public static final String DELAYED_EXCHANGE = "delayed.exchange";
    public static final String DELAYED_QUEUE = "delayed.queue";
    public static final String DELAYED_ROUTE_KEY = "delayed.routingKey";
    public static final String DELAYED_EXCHANGE_TYPE = "x-delayed-message";


    /**
     * 基于延迟插件的延迟交换机
     * @return 自定义交换机即 x-delayed-message
     */
    @Bean(DELAYED_EXCHANGE)
    public CustomExchange getDelayedExchange(){
        Map<String, Object> map = new HashMap<>();
        map.put("x-delayed-type","direct"); //定义自定义交换机的参数
        return new CustomExchange(DELAYED_EXCHANGE,DELAYED_EXCHANGE_TYPE,true,false,map);
    }

    /**
     * 消息的延迟由交换机决定，则队列直接用于消费即可
     * @return 延迟队列（其实就是一个没有任何属性的普通队列）
     */
    @Bean(DELAYED_QUEUE)
    public Queue getDelayedQueue(){
        return QueueBuilder.durable(DELAYED_QUEUE).build();
    }

    /**
     * 自定义的延迟交换机
     * @param queue 延迟队列
     * @param exchange 延迟交换机
     * @return 延迟交换机和延迟队列的绑定关系机路由key
     */
    @Bean(DELAYED_ROUTE_KEY)
    public Binding getBingDelayed(@Qualifier(DELAYED_QUEUE) Queue queue,
                                  @Qualifier(DELAYED_EXCHANGE) CustomExchange exchange){
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with(DELAYED_ROUTE_KEY)
                .noargs();
    }

}
