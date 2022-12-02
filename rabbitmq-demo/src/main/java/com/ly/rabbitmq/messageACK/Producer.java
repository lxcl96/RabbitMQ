package com.ly.rabbitmq.messageACK;

import com.ly.rabbitmq.utils.RabbitMQUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

/**
 * FileName:Producer.class
 * Author:ly
 * Date:2022/12/1 0001
 * Description: 消息的手动应答保证不丢失，多个消费者消费时间久的，放回队列重新消费 - 生产者
 */
public class Producer {
    private static final String TASK_QUEUE_NAME = "ack_queue";

    //发消息
    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabbitMQUtils.getNewChannel();

        //开启发布确认功能
//        channel.confirmSelect();

        System.out.println("生产者 [producer]开始生产大量的消息！");

        //设置队列持久化
        boolean durable = true;
        //声明一个队列
        channel.queueDeclare(TASK_QUEUE_NAME, durable, false, false, null);

        //从控制台接收消息
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            System.out.println("请输入要发送的消息：");
            String message = scanner.next();

            channel.basicPublish(
                    //默认交换机
                    "",
                    //路由key 此处为名字为hello的队列
                    TASK_QUEUE_NAME,
                    //消息(text/plain格式 也可以设置octet流文本)持久化
                    MessageProperties.PERSISTENT_TEXT_PLAIN,
                    message.getBytes(StandardCharsets.UTF_8)
            );

            System.out.println("消息：【" + message + "】 已经成功发送！");
        }
    }

}