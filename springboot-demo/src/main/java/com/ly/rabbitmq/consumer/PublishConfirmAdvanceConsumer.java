package com.ly.rabbitmq.consumer;

import com.ly.rabbitmq.config.PublishConfirmAdvanceConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * FileName:PublishConfirmAdvanceConsumer.class
 * Author:ly
 * Date:2022/12/9 0009
 * Description: 消费者
 */

@Slf4j
@Component
public class PublishConfirmAdvanceConsumer {

    @RabbitListener(queues = {PublishConfirmAdvanceConfig.CONFIRM_QUEUE,PublishConfirmAdvanceConfig.BACKUP_QUEUE,PublishConfirmAdvanceConfig.WARNING_QUEUE})
    public void doConsume(Message message){
        String consumer = "";
        String consumerQueue = message.getMessageProperties().getConsumerQueue();
        String msg = new String(message.getBody(), StandardCharsets.UTF_8);

        if (PublishConfirmAdvanceConfig.CONFIRM_QUEUE.equals(consumerQueue)) {
            consumer = "消费者1";
        } else if (PublishConfirmAdvanceConfig.BACKUP_QUEUE.equals(consumerQueue)) {
            consumer = "备份队列消费者";
        } else if (PublishConfirmAdvanceConfig.WARNING_QUEUE.equals(consumerQueue)) {
            consumer = "警告队列消费者";
        }
        log.info("{} 接收到confirm消息：{}",consumer,msg);
    }


}
