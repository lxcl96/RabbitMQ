package com.ly.rabbitmq.messageACK;

import com.ly.rabbitmq.utils.RabbitMQUtils;
import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

/**
 * FileName:Consumer_1.class
 * Author:ly
 * Date:2022/12/2 0002
 * Description: 消息的手动应答保证不丢失，多个消费者消费时间久的，放回队列重新消费 - 消费者1
 */
public class Consumer_1 {
    private static final String TASK_QUEUE_NAME = "ack_queue";

    public static void main(String[] args) throws IOException, TimeoutException {
        System.out.println("消费者 [Consumer_1]等待接收消息（处理时间很快的）！");
        Channel channel = RabbitMQUtils.getNewChannel();

        //设置预取值为1，实现能者多劳(保证只要队列有消息，每个消费者都有1个消息)
        int prefetch = 1;
        //设置预取值
        channel.basicQos(prefetch);

        channel.basicConsume(
                //队列名
                TASK_QUEUE_NAME,
                //手动应答
                false,
                //消费消息
                (consumerTag, message) -> {
                    System.out.println("消费者 [Consumer_1] 接收到的消息：" + new String(message.getBody(), StandardCharsets.UTF_8));
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    //手动应答
                    channel.basicAck(message.getEnvelope().getDeliveryTag(),false);
                    System.out.println("=======================本次消费结束==========================");
                },
                //取消消费消息的回调
                consumerTag -> {System.out.println("消费者 [Consumer_1]取消消息回调函数！");}
                );
    }
}
