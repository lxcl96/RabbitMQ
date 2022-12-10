package com.ly.rabbitmq.deadMessageQueue;

import com.ly.rabbitmq.utils.RabbitMQUtils;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

/**
 * FileName:Producer.class
 * Author:ly
 * Date:2022/12/6 0006
 * Description: 生产者
 */
public class Producer {
    private static final String NORMAL_QUEUE_NAME = "normal_queue";
    private static final String NORMAL_EXCHANGE_NAME = "normal_exchange";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMQUtils.getNewChannel();

        //1. 发送延迟消息[死信队列]
        channel.basicPublish(
                NORMAL_EXCHANGE_NAME,
                "zhangsan",
                //设置消息的属性 如路由头，持久化，过期时间等等
                new AMQP.BasicProperties().builder().expiration("10000").priority(0).build(),//过期时间单位毫秒,优先级为0
                "hello dead-letter".getBytes(StandardCharsets.UTF_8)
        );

        //2.超对队列保存消息的最大长度6[死信队列]
        AMQP.BasicProperties properties = null;
        for (int i = 1; i < 11; i++) {
            if (i==5){
                properties = new AMQP.BasicProperties().builder().priority(5).build();
            }
            channel.basicPublish(
                    NORMAL_EXCHANGE_NAME,
                    "zhangsan",
                    properties,
                    ("msg=" + i).getBytes(StandardCharsets.UTF_8)
            );
        }

         //3.消息被拒绝，并且不重新入队
        for (int i = 1; i < 11; i++) {
            channel.basicPublish(
                    NORMAL_EXCHANGE_NAME,
                    "zhangsan",
                    null,
                    ("msg=" + i).getBytes(StandardCharsets.UTF_8)
            );
        }
    }

}
