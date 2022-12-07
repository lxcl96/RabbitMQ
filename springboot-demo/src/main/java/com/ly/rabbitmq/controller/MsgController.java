package com.ly.rabbitmq.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * FileName:MsgConroller.class
 * Author:ly
 * Date:2022/12/6 0006
 * Description: 通过请求发送(延迟)数据 - 生产者
 */
@Slf4j
@RestController
@RequestMapping(path = "/ttl")
public class MsgController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RequestMapping(path = "/sendMsg/{msg}")
    public String sendMsg(@PathVariable("msg") String msg) {
        log.info("{} 接收到生产者消息：{}",new Date().toString(),msg);

        //发送到延迟10s的 队列
        rabbitTemplate.convertSendAndReceive(
                "X",
                "XA",
                "[ttl_10_s] " + msg
        );

        //发送到延迟40s的 队列
        rabbitTemplate.convertSendAndReceive(
                "X",
                "XB",
                "[ttl_40_s] " + msg
        );

        return "OK";
    }

    @GetMapping("/sendExpMsg/{msg}/{ttl}")
    public String sendExpMsg(@PathVariable String msg,@PathVariable String ttl) {
        log.info("{} [sendExpMsg] 接收到生产者消息：{},过期时间为{}ms",new Date().toString(),msg,ttl);

        /**
         * 发送 自定义延迟时间的消息
         * 但是存在问题：先入先出 ，会堵塞后面过期时间短的
         *      如：第一个消息：m1 延迟20s
         *          第二个消息：m2 延迟2s
         *          本来以为是2秒后m2先出来，
         *              实际是20秒后m1出来，然后m2出来
         */
        rabbitTemplate.convertSendAndReceive(
                "X",
                "XC",
                "[sendExpMsg] " + msg,
                //message 的后置处理器，将消息object类型转化为Message类型后调用
                message -> {
                    message.getMessageProperties().setExpiration(ttl);//单位ms
                    return message;
                }
        );
        return "ok";
    }
}
