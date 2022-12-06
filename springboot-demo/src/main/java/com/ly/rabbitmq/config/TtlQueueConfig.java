package com.ly.rabbitmq.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * FileName:TtlQueueConfig.class
 * Author:ly
 * Date:2022/12/6 0006
 * Description: 专门用于配置 普通交换机，队列，以及死信队列和 所有的路由key
 */
@SuppressWarnings({"all"})
@Configuration
public class TtlQueueConfig {

    //普通交换机 X type=direct
    public static final String X_EXCHANGE =  "X";
    //普通队列 QA
    public static final String QA_NORMAL_QUEUE = "QA";
    //普通队列 QB
    public static final String QB_NORMAL_QUEUE = "QB";
    //普通队列 QA ttl=10s routingKey=XA
    public static final String XA_NORMAL_ROUTE_KEY = "XA";
    //普通队列 QB ttl=40s routingKey=XB
    public static final String XB_NORMAL_ROUTE_KEY = "XB";

    //死信交换机 Y type=direct
    public static final String Y_EXCHANGE = "Y";
    //死信队列 QD routingKey=YD
    public static final String QD_DEAD_QUEUE = "QD";
    //死信队列 QD routingKey=YD
    public static final String DEAD_ROUTE_KEY = "YD";

    /**
     * 普通交换机X
     *      被当成了ioc组件 (注意：beanName是ioc容器中的名字，不是amqp中的)
     * @return 普通交换机X
     */
    @Bean(name = X_EXCHANGE)
    public DirectExchange getNormalExchangeX() {
        return new DirectExchange(X_EXCHANGE);
    }

    /**
     * 普通队列QA 持久化，共享，不自动删除 [但是没有绑定路由key]
     * @ttl=10s
     * @x-letter-exchange=Y
     * @x-letter-route-key=YD
     *      被当成了ioc组件 (注意：beanName是ioc容器中的名字，不是amqp中的)
     * @return 普通队列QA
     */
    @Bean(name = QA_NORMAL_QUEUE)
    public Queue getNormalQueueQA() {
        return QueueBuilder
                .durable(QA_NORMAL_QUEUE)
                .deadLetterExchange(Y_EXCHANGE)
                .deadLetterRoutingKey(DEAD_ROUTE_KEY)
                .ttl(10000)
                .build();
    }

    /**
     * 普通队列QB 持久化，共享，不自动删除 [但是没有绑定路由key]
     * @ttl=40s
     * @x-letter-exchange=Y
     * @x-letter-route-key=YD
     *      被当成了ioc组件 (注意：beanName是ioc容器中的名字，不是amqp中的)
     * @return 普通队列QB
     */
    @Bean(name = QB_NORMAL_QUEUE)
    public Queue getNormalQueueQB() {
        return QueueBuilder
                .durable(QB_NORMAL_QUEUE)
                .deadLetterExchange(Y_EXCHANGE)
                .deadLetterRoutingKey(DEAD_ROUTE_KEY)
                .ttl(40000)
                .build();
    }

    /**
     * 死信交换机Y
     *      被当成了ioc组件 (注意：beanName是ioc容器中的名字，不是amqp中的)
     * @return 死信交换机Y
     */
    @Bean(name = Y_EXCHANGE)
    public DirectExchange getDeadLetterExchangeY() {
        return new DirectExchange(Y_EXCHANGE);
    }


    /**
     * 死信队列QD 持久化，共享，不自动删除 [但是没有绑定路由key]
     *      被当成了ioc组件 (注意：beanName是ioc容器中的名字，不是amqp中的)
     * @return 普通队列QA
     */
    @Bean(name = QD_DEAD_QUEUE)
    public Queue getDeadLetterQueueQD() {
        return QueueBuilder
                .durable(QD_DEAD_QUEUE)
                .build();
    }


    /**
     *  普通绑定关系XA 实体类 【包含queue、exchange、routingKey】
     *      被当成了ioc组件 (注意：beanName是ioc容器中的名字，不是amqp中的)
     * @QA 要绑定的队列 [自动注入（名字或类型） @Autowire省略]
     * @X 要绑定到的交换机 [自动注入（名字或类型） @Autowire省略]
     * @return 绑定关系实体类
     */
    @Bean(name = XA_NORMAL_ROUTE_KEY)
    public Binding getBingXA(Queue QA, DirectExchange X){
        return BindingBuilder
                .bind(QA)
                .to(X)
                .with(XA_NORMAL_ROUTE_KEY);
    }
    /**
     *  普通绑定关系XB 实体类 【包含queue、exchange、routingKey】
     *      被当成了ioc组件 (注意：beanName是ioc容器中的名字，不是amqp中的)
     * @QB 要绑定的队列 [自动注入（名字或类型） @Autowire省略]
     * @X 要绑定到的交换机 [自动注入（名字或类型） @Autowire省略]
     * @return 绑定关系实体类
     */
    @Bean(name = XB_NORMAL_ROUTE_KEY)
    public Binding getBingXB(@Autowired Queue QB, @Autowired DirectExchange X){
        return BindingBuilder
                .bind(QB)
                .to(X)
                .with(XB_NORMAL_ROUTE_KEY);
    }

    /**
     *  死信队列绑定关系YD 实体类 【包含queue、exchange、routingKey】
     *      被当成了ioc组件 (注意：beanName是ioc容器中的名字，不是amqp中的)
     * @QD 要绑定的队列 [自动注入（名字或类型） @Autowire省略]
     * @Y 要绑定到的交换机 [自动注入（名字或类型） @Autowire省略]
     * @return 绑定关系实体类
     */
    @Bean(name = DEAD_ROUTE_KEY)
    public Binding getBingYD(Queue QD,DirectExchange Y){
        return BindingBuilder
                .bind(QD)
                .to(Y)
                .with(DEAD_ROUTE_KEY);
    }
}
