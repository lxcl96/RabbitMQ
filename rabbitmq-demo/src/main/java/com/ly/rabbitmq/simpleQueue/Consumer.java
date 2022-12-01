package com.ly.rabbitmq.simpleQueue;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * FileName:Consumer.class
 * Author:ly
 * Date:2022/12/1 0001
 * Description: 第一次 写 消费者代码 ，从hello队列中取消息
 */
public class Consumer {

    public static String QUEUE_NAME = "hello";

    public static void main(String[] args) throws IOException, TimeoutException {
        //设置rabbitmq连接工厂
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("192.168.77.3");
        connectionFactory.setUsername("ly");
        connectionFactory.setPassword("1024");

        //得到一个rabbitmq连接
        Connection connection = connectionFactory.newConnection();
        //创建信道接受消息
        Channel channel = connection.createChannel();
        System.out.println("信道id = " + channel.getChannelNumber());

        /**
         *
         * 参数：
         *  queue：要消费的队列名称
         *  autoAck： true表示消费成功后自动应答，false表示手动应答
         *  deliverCallback：消息被传递时的回调
         *  cancelCallback：消费取消时的回调
         */
        channel.basicConsume(
                QUEUE_NAME,
                true,
                (consumerTag, message) -> {
                    //lambda表达式，接受mq队列中的消息时的回调
                    System.out.println("hello队列消息被传递");
                    System.out.println("consumerTag=" + consumerTag);
                    System.out.println("message=" + new String(message.getBody()));

                },
                consumerTag -> {
                    //lambda表达式，消费行为被取消的回调
                    System.out.println("hello队列的消费行为被取消");
                    System.out.println("consumerTag=" + consumerTag);
                });

    }
}
