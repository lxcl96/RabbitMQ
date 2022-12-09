package com.ly.rabbitmq.component;

import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * FileName:MyMessagePublishCallback.class
 * Author:ly
 * Date:2022/12/9 0009
 * Description:
 */
@Component
public class MyMessagePublishCallback implements RabbitTemplate.ConfirmCallback {

    //将自己写的回调函数注入到组件的属性中
    @Autowired
    private RabbitTemplate rabbitTemplate;


    /**
     * 用于生产者发布消息后 回调使用
     * 1.正确回调函
     *    参数 correlationData：回调消息的ID及相关属性
     *    参数 ack：true
     *    参数 cause： null
     * 2.失败回调函数 回调消息的ID及相关属性
     *    参数 correlationData：
     *    参数 ack：false
     *    参数 cause：失败原因
     * @return 回调函数
     */

    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        if (ack) {
            System.out.println("消息发布成功！");
            System.out.println(correlationData.getId());
        } else {
            System.out.println("消息发布失败，原因：" + cause);
        }
    }

    //构造完再更新默认组件RabbitTemplate的属性
    @PostConstruct
    public void injectAttribute(){
        System.out.println("构造完再更新默认组件RabbitTemplate的属性");
        rabbitTemplate.setConfirmCallback(this);
    }
}
