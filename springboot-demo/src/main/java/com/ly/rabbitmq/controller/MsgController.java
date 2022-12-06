package com.ly.rabbitmq.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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
    public String hello(@PathVariable("msg") String msg) {
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
}
