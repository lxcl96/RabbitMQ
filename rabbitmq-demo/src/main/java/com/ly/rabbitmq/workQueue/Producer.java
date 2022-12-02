package com.ly.rabbitmq.workQueue;

import com.ly.rabbitmq.utils.RabbitMQUtils;
import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

/**
 * FileName:Producer.class
 * Author:ly
 * Date:2022/12/2 0002
 * Description: 工作队列work queue中的生产者，发送大量的消息
 */
public class Producer {

    public static void main(String[] args) throws IOException, TimeoutException {
        String thread = UUID.randomUUID().toString().replace("-", "").substring(0, 5);
        Channel channel = RabbitMQUtils.getNewChannel();

        System.out.println("生产者 [ " + thread + " ]开始生产大量的消息！");
        //声明一个队列
        channel.queueDeclare(RabbitMQUtils.getQueueName(),false,false,false,null);

        //从控制台接收消息
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            System.out.println("请输入要发送的消息：");
            String message = scanner.next();

            channel.basicPublish(
                    //默认交换机
                    "",
                    //路由key 此处为名字为hello的队列
                    RabbitMQUtils.getQueueName(),
                    null,
                    message.getBytes()
            );

            System.out.println("消息：【" + message + "】 已经成功发送！");
        }
    }
}
