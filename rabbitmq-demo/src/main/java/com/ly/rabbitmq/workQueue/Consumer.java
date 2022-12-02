package com.ly.rabbitmq.workQueue;

import com.ly.rabbitmq.utils.RabbitMQUtils;
import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

/**
 * FileName:Consumer.class
 * Author:ly
 * Date:2022/12/1 0001
 * Description: 工作队列work queue模式中的众多消费者其一
 */
public class Consumer {

    public static void main(String[] args) throws IOException, TimeoutException {
        String thread = UUID.randomUUID().toString().replace("-", "").substring(0, 5);
        Channel channel = RabbitMQUtils.getNewChannel();
        System.out.println("消费者 [ "  + thread + " ] 等待消费。。。");
        // 1/n 众多消费者之一
        channel.basicConsume(
                // 名字为 hello 的队列
                RabbitMQUtils.getQueueName(),
                true,
                // 接收队列中消息的回调函数
                (consumerTag, message) -> {
                    System.out.println("消费者 [ "  + thread + " ]接收到的消息：" + new String(message.getBody()));
                },
                // 消费者取消消费的回调函数
                consumerTag -> {});
    }
}
