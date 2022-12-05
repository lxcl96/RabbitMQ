package com.ly.rabbitmq.publishAndSubscribe.direct;

import com.ly.rabbitmq.utils.RabbitMQUtils;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

/**
 * FileName:ReceiveLogsDirect01.class
 * Author:ly
 * Date:2022/12/5 0005
 * Description: direct类的交换机-接收方1
 */
public class ReceiveLogsDirect01 {
    private static final String EXCHANGE_NAME = "direct_logs";
    private static final String queue = "console";

    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabbitMQUtils.getNewChannel();
        //唯一的区别 direct类型
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        channel.queueDeclare(queue,false,false,false,null);
        channel.queueBind(queue,EXCHANGE_NAME,"info");
        channel.queueBind(queue,EXCHANGE_NAME,"warning");

        channel.basicConsume(
                queue,
                //手动应答
                false,
                //成功消费
                (consumerTag, message) -> {
                    System.out.println("接收方01 获取消息：msg=" + new String(message.getBody(), StandardCharsets.UTF_8));
                    channel.basicAck(message.getEnvelope().getDeliveryTag(),false);
                },
                //取消消费
                (consumerTag, sig) -> System.out.println("接收方01取消消费！")
        );
    }
}
