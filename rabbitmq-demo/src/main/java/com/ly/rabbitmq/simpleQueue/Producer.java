package com.ly.rabbitmq.simpleQueue;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * FileName:Producer.class
 * Author:ly
 * Date:2022/12/1 0001
 * Description: 第一次 写 生产者代码 ，给hello队列，发送消息
 */
public class Producer {
    //队列名称 （大写转换快捷键 ctrl + shift + U）
    public static final String QUEUE_NAME = "hello";

    //发消息
    public static void main(String[] args) throws IOException, TimeoutException {
        //设置rabbitmq连接工厂
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("192.168.77.3");
        connectionFactory.setUsername("ly");
        connectionFactory.setPassword("1024");

        //得到一个rabbitmq连接
        Connection connection = connectionFactory.newConnection();
        //由于rabbit的connect连接创建消耗很大，所以是通过信道channel进行通信，获取信道
        Channel channel = connection.createChannel(1);

        /**
         * 使用默认交换机，则直接跳过创建交换机，直接连接队列
         * 参数：
         *      queue：队列名称
         *      durable：是否持久化（保存到硬盘还是内存），服务重启后依然可用
         *      exclusive：是否排他，即是否为独占队列
         *      autoDelete：是否自动删除（服务器不使用它时自动删除）
         *      arguments： 传递的参数
         */
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);

        /**
         * 发送消息
         * 参数：
         *  exchange：消息发布到的交换机，第一次使用默认的
         *  routingKey：路由key，后面再学,本次是队列名称
         *  props：消息的其他属性，如路由的表头等等
         *  body：消息体（byte[]）
         */

        channel.basicPublish("",QUEUE_NAME,null,"hello rabbitmq".getBytes());

        System.out.println("消息发送完毕!");

    }
}
