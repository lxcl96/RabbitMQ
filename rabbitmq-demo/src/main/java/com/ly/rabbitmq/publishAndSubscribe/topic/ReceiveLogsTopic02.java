package com.ly.rabbitmq.publishAndSubscribe.topic;

import com.ly.rabbitmq.utils.RabbitMQUtils;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

/**
 * FileName:ReceiveLogsTopic02.class
 * Author:ly
 * Date:2022/12/5 0005
 * Description: topic类的交换机-接收方
 */
public class ReceiveLogsTopic02 {
    private static final String EXCHANGE_NAME = "topic_logs";
    private static final String queue = "Q2";

    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabbitMQUtils.getNewChannel();
        //topic类型
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);
        channel.queueDeclare(queue,false,false,false,null);
        channel.queueBind(queue,EXCHANGE_NAME,"*.*.rabbit");
        channel.queueBind(queue,EXCHANGE_NAME,"lazy.#");

        channel.basicConsume(
                queue,
                //手动应答
                false,
                //成功消费
                (consumerTag, message) -> {
                    System.out.println(message.getEnvelope().getRoutingKey()+ ":" + new String(message.getBody(), StandardCharsets.UTF_8));
                    channel.basicAck(message.getEnvelope().getDeliveryTag(),false);
                },
                //取消消费
                (consumerTag, sig) -> System.out.println("接收方02取消消费！")
        );
    }
}
