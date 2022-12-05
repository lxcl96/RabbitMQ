package com.ly.rabbitmq.publishAndSubscribe.fanout;

import com.ly.rabbitmq.utils.RabbitMQUtils;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

/**
 * FileName:Producer.class
 * Author:ly
 * Date:2022/12/5 0005
 * Description: 发布订阅模式 fanout ，生产者发布一个消息给一个路由的两个队列由两个消费者接收
 */
public class Producer {
    //交换机名称和RoutingKey和队列名字
    private static final String EXCHANGE_NAME = RoutingKey.EXCHANGE_NAME.getRoutingKey();
    private static final String ROUTING_KEY_1 = RoutingKey.FANOUT_1.getRoutingKey();
    private static final String ROUTING_KEY_2 = RoutingKey.FANOUT_2.getRoutingKey();
    private static final String queue_1 = RoutingKey.FANOUT_1.getQueueName();
    private static final String queue_2 = RoutingKey.FANOUT_2.getQueueName();

    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabbitMQUtils.getNewChannel();
        //打开发布确认功能
        channel.confirmSelect();

        //创新新的fanout类型的交换机
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);
        //创建队列
        channel.queueDeclare(queue_1,true,false,false,null);
        channel.queueDeclare(queue_2,true,false,false,null);
        //绑定队列，设置路由key （复习枚举类） fanout类型的routingKey可以不写，只要绑定的队列都能收到
        channel.queueBind(queue_1,EXCHANGE_NAME,ROUTING_KEY_1);
        channel.queueBind(queue_2,EXCHANGE_NAME,ROUTING_KEY_2);

        //接受异步发布确认函数
        channel.addConfirmListener(
                null,
                //失败处理，可以放在juc包里的线程安全类中，根据deliverTag进行重发
                (deliveryTag, multiple) -> System.out.println("有消息发布失败，deliverTag=" + deliveryTag));

        //发送消息
        channel.basicPublish(EXCHANGE_NAME,ROUTING_KEY_1, null,"hello fanout！".getBytes(StandardCharsets.UTF_8));
        //channel.basicPublish(EXCHANGE_NAME,ROUTING_KEY_1, null,"hello fanout！ -c1".getBytes(StandardCharsets.UTF_8));
        //channel.basicPublish(EXCHANGE_NAME,ROUTING_KEY_2, null,"hello fanout！ -c2".getBytes(StandardCharsets.UTF_8));
        System.out.println("生产者消息发布成功：" + EXCHANGE_NAME + " - " + queue_1 + " - " + ROUTING_KEY_1);
        System.out.println("生产者消息发布成功：" + EXCHANGE_NAME + " - " + queue_2 + " - " + ROUTING_KEY_2);

    }
}
