package com.ly.rabbitmq.publishAndSubscribe.fanout;

import com.ly.rabbitmq.utils.RabbitMQUtils;
import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

/**
 * FileName:Consumer_1.class
 * Author:ly
 * Date:2022/12/5 0005
 * Description: 消费者1 消费队列1
 */
public class Consumer_1 {
    //队列名字
    private static final String queue_1 = RoutingKey.FANOUT_1.getQueueName();

    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabbitMQUtils.getNewChannel();

        channel.basicConsume(
                queue_1,
                //手动应答
                false,
                //成功消费
                (consumerTag, message) -> {
                    System.out.println("消费者C1 获取消息：msg=" + new String(message.getBody(), StandardCharsets.UTF_8));
                    channel.basicAck(message.getEnvelope().getDeliveryTag(),false);
                },
                //取消消费
                (consumerTag, sig) -> System.out.println("消费者C1取消消费！")
                );
    }
}
