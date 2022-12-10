package com.ly.rabbitmq.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * FileName：PublishConfirmController.java
 * Author：Ly
 * Date：2022/12/10
 * Description： 发布确认
 */
@Slf4j
@RestController
@RequestMapping("/publishConfirm")
public class PublishConfirmController {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 发布确认高级 - rabbitmq服务宕机消息丢失
     * @param msg 消息
     * @return ok
     */
    @GetMapping("/sendMsg/{msgType}/{msg}")
    public String sendConfirmMsg(@PathVariable String msgType, @PathVariable String msg){
        log.info("{} [sendConfirmMsg] 接收到生产者消息：{}，类型为：{}",new Date(),msg,msgType);
        String routingKey = "k1";
        if ("error".equals(msgType)) {
            //错误消息
            routingKey = "error";
        }
        rabbitTemplate.convertAndSend(
                "confirm.exchange",
                routingKey,
                msg,
                message -> {
                    message.getMessageProperties().setPriority(5);
                    return message;
                },
                new CorrelationData(msg.length() + "")
        );
        return "OK";
    }
}
