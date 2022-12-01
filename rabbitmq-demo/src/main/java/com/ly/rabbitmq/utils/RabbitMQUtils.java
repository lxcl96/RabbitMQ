package com.ly.rabbitmq.utils;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * FileName:RabbitMQUtils.class
 * Author:ly
 * Date:2022/12/1 0001
 * Description: rabbitmq简单的连接工厂
 */
public class RabbitMQUtils {
    private static final String host = "192.168.77.3";
    private static final String username = "ly";
    private static final String passwd = "1024";
    //测试队列名
    private static String QUEUE_NAME = "hello";

    /**
     * 获取一个采用默认交换机的连接
     * @return rabbitmq连接
     * @throws IOException io异常
     * @throws TimeoutException 超时
     */
    public static Connection getNewConnection() throws IOException, TimeoutException {

        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(host);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(passwd);

        return connectionFactory.newConnection();
    }

    /**
     * 获取一个采用默认交换机和默认信道号的信道连接
     * @return rabbitmq信道
     * @throws IOException io异常
     * @throws TimeoutException 超时
     */
    public static Channel getNewChannel() throws IOException, TimeoutException {
        return getNewConnection().createChannel();
    }

    public static String getQueueName() {
        return QUEUE_NAME;
    }
}
