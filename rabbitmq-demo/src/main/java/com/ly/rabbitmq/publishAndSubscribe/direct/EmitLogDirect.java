package com.ly.rabbitmq.publishAndSubscribe.direct;

import com.ly.rabbitmq.utils.RabbitMQUtils;
import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * FileName:EmitLogDirect.class
 * Author:ly
 * Date:2022/12/5 0005
 * Description: direct类的交换机-发送方
 */
public class EmitLogDirect {
    private static final String EXCHANGE_NAME = "direct_logs";

    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabbitMQUtils.getNewChannel();
        channel.basicPublish(EXCHANGE_NAME,"info",null,"hello info".getBytes());
        channel.basicPublish(EXCHANGE_NAME,"warning",null,"hello warning".getBytes());
        channel.basicPublish(EXCHANGE_NAME,"error",null,"hello error".getBytes());

    }
}
