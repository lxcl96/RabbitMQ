package com.ly.rabbitmq.deadMessageQueue;

import com.ly.rabbitmq.utils.RabbitMQUtils;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * FileName:Consumer_1.class
 * Author:ly
 * Date:2022/12/5 0005
 * Description: 消费者1 对于死信将其转入到私信队列中等待消费者2消费
 */
public class Consumer_1 {
    private static final String NORMAL_EXCHANGE_NAME = "normal_exchange";
    private static final String DEAD_EXCHANGE_NAME = "dead_exchange";
    private static final String NORMAL_QUEUE_NAME = "normal_queue";
    private static final String DEAD_QUEUE_NAME = "dead_queue";

    public static void main(String[] args) throws IOException, TimeoutException {

        //通过参数设置死信格式（通过队列的构造函数方式）
        Map<String, Object> map = new HashMap<>();
        /**
         * 所有可以设置的参数
         *  (1）x-message-ttl：消息的过期时间，单位：毫秒；
         * （2）x-expires：队列过期时间，队列在多长时间未被访问将被删除，单位：毫秒；
         * （3）x-max-length：队列最大长度，超过该最大值，则将从队列头部开始删除消息；
         * （4）x-max-length-bytes：队列消息内容占用最大空间，受限于内存大小，超过该阈值则从队列头部开始删除消息；
         * （5）x-overflow：设置队列溢出行为。这决定了当达到队列的最大长度时消息会发生什么。有效值是drop-head、reject-publish或reject-publish-dlx。仲裁队列类型仅支持drop-head；
         * （6）x-dead-letter-exchange：死信交换器名称，过期或被删除（因队列长度超长或因空间超出阈值）的消息可指定发送到该交换器中；
         * （7）x-dead-letter-routing-key：死信消息路由键，在消息发送到死信交换器时会使用该路由键，如果不设置，则使用消息的原来的路由键值
         * （8）x-single-active-consumer：表示队列是否是单一活动消费者，true时，注册的消费组内只有一个消费者消费消息，其他被忽略，false时消息循环分发给所有消费者(默认false)
         * （9）x-max-priority：队列要支持的最大优先级数;如果未设置，队列将不支持消息优先级；
         * （10）x-queue-mode（Lazy mode）：将队列设置为延迟模式，在磁盘上保留尽可能多的消息，以减少RAM的使用;如果未设置，队列将保留内存缓存以尽可能快地传递消息；
         * （11）x-queue-master-locator：在集群模式下设置镜像队列的主节点信息。
         *
         */
        map.put("x-dead-letter-exchange",DEAD_EXCHANGE_NAME);//指明要转发到的指定死信交换机
        map.put("x-dead-letter-routing-key","lisi");//指明要转发到的指定死信交换机的routingKey
        //map.put("x-max-length",6);//正常队列可以保存的最大长度信息，超过自动转入到死信
        //过期时间：1、生产者指定单个消息过期时间 2、设置队列中所有消息的过期时间
        //map.put("x-message-ttl",10000);//单位毫秒 10s 属于设置队列中所有消息的属性，不推荐；因为每个消息的过期时间应该不一样（所以应该由生产者发送消息时指定过期时间时间）

        Channel channel = RabbitMQUtils.getNewChannel();

        //普通交换机和队列
        channel.exchangeDeclare(NORMAL_EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        channel.queueDeclare(NORMAL_QUEUE_NAME,false,false,false,map);
        channel.queueBind(NORMAL_QUEUE_NAME,NORMAL_EXCHANGE_NAME,"zhangsan");
        //创建死信队列
        channel.exchangeDeclare(DEAD_EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        channel.queueDeclare(DEAD_QUEUE_NAME,false,false,false,null);
        channel.queueBind(DEAD_QUEUE_NAME,DEAD_EXCHANGE_NAME,"lisi");

        channel.basicConsume(
                NORMAL_QUEUE_NAME,
                false,//一定要设置为手动应答 【自动应答不存在拒绝问题】
                //正常消费
                (consumerTag, message) -> {
                    String msg = new String(message.getBody(), StandardCharsets.UTF_8);
                    System.out.println("收到的消息msg 【" + msg + "】");
                    //判断为死信
                    //1.消息被拒绝
                    if ("msg=5".equals(msg)) {
                        channel.basicReject(message.getEnvelope().getDeliveryTag(),false);
                        System.out.println(msg + "此消息是被拒绝的，且不重新入队。[死信队列]");
                        //自动放入死信队列，不需要手动传递
                        //channel.basicPublish(DEAD_EXCHANGE_NAME,"lisi",null,message.getBody());
                    } else {
                        channel.basicAck(message.getEnvelope().getDeliveryTag(),false);
                    }

                    //2.消息过期 [生产者设置]

                    //3. 队列达到最大长度 [6]
                },
                //取消消费
                consumerTag -> {
                    //
                    System.out.println("c1 取消消费！");
                }
        );
    }
}
