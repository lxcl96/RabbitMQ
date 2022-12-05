package com.ly.rabbitmq.publishAndSubscribe.topic;

import com.ly.rabbitmq.utils.RabbitMQUtils;
import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * FileName:EmitLogTopic.class
 * Author:ly
 * Date:2022/12/5 0005
 * Description:  topic类的交换机-发送方
 */
public class EmitLogTopic {
    private static final String EXCHANGE_NAME = "topic_logs";

    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabbitMQUtils.getNewChannel();

        //发送方向指定具体的RoutingKey发送数据，接收方通过通配符接收多个队列的消息
        channel.basicPublish(EXCHANGE_NAME,"quick.orange.rabbit",null,"被队列 Q1Q2 接收到".getBytes());
        channel.basicPublish(EXCHANGE_NAME,"lazy.orange.elephant",null,"被队列 Q1Q2 接收到".getBytes());
        channel.basicPublish(EXCHANGE_NAME,"quick.orange.fox",null,"被队列 Q1 接收到".getBytes());
        channel.basicPublish(EXCHANGE_NAME,"lazy.brown.fox",null,"被队列 Q2 接收到".getBytes());
        channel.basicPublish(EXCHANGE_NAME,"lazy.pink.rabbit",null,"虽然满足两个绑定但只被队列 Q2 接收一次".getBytes());
        channel.basicPublish(EXCHANGE_NAME,"quick.brown.fox",null,"不匹配任何绑定不会被任何队列接收到会被丢弃".getBytes());
        channel.basicPublish(EXCHANGE_NAME,"quick.orange.male.rabbit",null,"是四个单词不匹配任何绑定会被丢弃".getBytes());
        channel.basicPublish(EXCHANGE_NAME,"lazy.orange.male.rabbit",null,"是四个单词但匹配 Q2".getBytes());
    }
}
