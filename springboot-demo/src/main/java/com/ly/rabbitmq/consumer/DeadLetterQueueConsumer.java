package com.ly.rabbitmq.consumer;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;


/**
 * FileName:DeadLetterQueueConsumer.class
 * Author:ly
 * Date:2022/12/6 0006
 * Description: 死信消费者，是一个监听器
 */
@Slf4j
@Component
public class DeadLetterQueueConsumer {

    @RabbitListener(queues = {"QD"})
    public void consumeMessageOfQD(Message message, Channel channel) {
        String msg = new String(message.getBody(), StandardCharsets.UTF_8);
        log.info("{} 接收到死信队列消息：{}",new Date().toString(),msg);
    }
}
