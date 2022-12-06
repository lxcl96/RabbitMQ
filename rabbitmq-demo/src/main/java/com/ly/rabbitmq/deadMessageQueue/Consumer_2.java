package com.ly.rabbitmq.deadMessageQueue;

import com.ly.rabbitmq.utils.RabbitMQUtils;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

/**
 * FileName:Consumer_2.class
 * Author:ly
 * Date:2022/12/6 0006
 * Description: 专门消费来自死信队列的消息
 */
public class Consumer_2 {
    private static final String DEAD_QUEUE_NAME = "dead_queue";

    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabbitMQUtils.getNewChannel();
        //可以不创建死信队列直接用，那么必须要求第一次运行时Consumer_1先运行
        channel.basicConsume(
                DEAD_QUEUE_NAME,
                true,
                (consumerTag, message) -> {
                    System.out.println("成功接收到来自 [死信队列] 的消息：" + new String(message.getBody(), StandardCharsets.UTF_8));
                },
                //取消消息的回调
                consumerTag -> {}
        );

    }

}
