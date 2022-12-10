package com.ly.rabbitmq.component;

import com.ly.rabbitmq.config.PublishConfirmAdvanceConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;

/**
 * FileName:MyMessagePublishCallback.class
 * Author:ly
 * Date:2022/12/9 0009
 * Description:
 */
@Slf4j
@Component
public class MyMessagePublishCallback implements RabbitTemplate.ConfirmCallback,RabbitTemplate.ReturnCallback {

    //将自己写的回调函数注入到组件的属性中
    @Autowired
    private RabbitTemplate rabbitTemplate;


    /**
     * 用于生产者发布消息后 回调使用（这个是针对交换机的，交换机接受到消息就返回true，不管队列有没有收到）
     * 1.正确回调函
     *    参数 correlationData：回调消息的ID及相关属性
     *    参数 ack：true
     *    参数 cause： null
     * 2.失败回调函数 回调消息的ID及相关属性
     *    参数 correlationData：
     *    参数 ack：false
     *    参数 cause：失败原因
     */

    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        //这个是针对交换机的，交换机接受到消息就返回true，不管队列有没有收到
        if (ack) {
            log.info("[ConfirmCallback] 消息发布成功！,消息id{}",correlationData.getId());
        } else {
            log.info("消息发布失败，原因：{}",cause);
        }
    }

    //构造完再更新默认组件RabbitTemplate的属性
    @PostConstruct
    public void injectAttribute(){
        //System.out.println("构造完再更新默认组件RabbitTemplate的属性");
        rabbitTemplate.setConfirmCallback(this);
        rabbitTemplate.setReturnCallback(this);//千万别忘记注入
    }

    /**
     * 不可路由消息，通过回调函数回退给生产者 (成功发送给队列就不会调用该回调函数了)
     * @param message 回退给producer的消息
     * @param replyCode 返回代码
     * @param replyText 返回内容
     * @param exchange 交换机名字
     * @param routingKey 路由key
     */
    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        String msg = new String(message.getBody(), StandardCharsets.UTF_8);
        log.info("回退消息：【{}】返回代码replyCode：{},返回内容replyText：{},交换机：{},路由key:{}",
                msg,replyCode,replyText,exchange,routingKey);

        //投递到备用交换机(每次重新发送说明correlationID会丢失，除非包装在Message中)
        log.info("准备将无路由消息id：{}投递到备用交换机：{}",message.getMessageProperties().getCorrelationId(), PublishConfirmAdvanceConfig.BACKUP_EXCHANGE);

        rabbitTemplate.convertAndSend(
                PublishConfirmAdvanceConfig.BACKUP_EXCHANGE,
                "",//fanout交换机的路由key就null
                msg,
                //因为ConfirmCallBack调用中会获取每次消息的correlationDataID，所以每次发布消息都要带上否则会空指针
                new CorrelationData("1" + msg.length())
        );
        log.info("消息投递成功！");

    }
}
