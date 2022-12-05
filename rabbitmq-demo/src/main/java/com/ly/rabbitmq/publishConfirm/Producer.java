package com.ly.rabbitmq.publishConfirm;

import com.ly.rabbitmq.utils.RabbitMQUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmCallback;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.TimeoutException;

/**
 * FileName:Producer.class
 * Author:ly
 * Date:2022/12/2 0002
 * Description: 开启发布确认功能
 *   开启策略：
 *      1.单个确认发布 ：同步，可靠，但是速度慢
 *      2.批量确认发布 ：同步，速度较快，但是不可靠，不知道哪一个消息失败了
 *      3.异步确认发布 ： 异步，可靠，速度快
 */
public class Producer {

    private static final int MESSAGE_MAX_COUNT = 1000;


    public static void main(String[] args) throws IOException, InterruptedException, TimeoutException {
        //singleConfirm();//1000个消息，【单个消息发布确认】 完成时间：2534ms
        //batchConfirm();//1000个消息，【批量消息发布确认】 完成时间：345ms
        asyncConfirm();//1000个消息，【异步消息发布确认】 完成时间：165ms

    }

    /**
     * 单个消息确认
     */
    public static void singleConfirm() throws IOException, TimeoutException, InterruptedException {
        String QUEUE = UUID.randomUUID().toString();
        Channel channel = RabbitMQUtils.getNewChannel();
        channel.confirmSelect();

        //创建队列
        channel.queueDeclare(QUEUE, true, false, false, null);

        long begin = System.currentTimeMillis();
        //发送消息
        for (int i = 0; i < MESSAGE_MAX_COUNT; i++) {
            channel.basicPublish("",QUEUE,MessageProperties.PERSISTENT_TEXT_PLAIN,(i + "").getBytes());
            //单个消息确认
            boolean tag = channel.waitForConfirms();
            if (!tag) {
                System.out.println("【单个消息发布确认】 第" + i + " 失败！");
            }
        }
        long end = System.currentTimeMillis();
        System.out.println("1000个消息，【单个消息发布确认】 完成时间：" + (end - begin) + "ms");
    }
    /**
     * 批量消息确认
     */
    public static void batchConfirm() throws IOException, TimeoutException, InterruptedException {
        String QUEUE = UUID.randomUUID().toString();
        Channel channel = RabbitMQUtils.getNewChannel();
        channel.confirmSelect();

        //创建队列
        channel.queueDeclare(QUEUE, true, false, false, null);

        long begin = System.currentTimeMillis();
        //发送消息
        for (int i = 0; i < MESSAGE_MAX_COUNT; i++) {
            channel.basicPublish("",QUEUE,MessageProperties.PERSISTENT_TEXT_PLAIN,(i + "").getBytes());
        }
        //1000条确认一次
        boolean tag = channel.waitForConfirms();
        if (!tag) {
            System.out.println("【批量消息发布确认】 有 失败的！");
        }
        long end = System.currentTimeMillis();
        System.out.println("1000个消息，【批量消息发布确认】 完成时间：" + (end - begin) + "ms");
    }

    /**
     * 异步消息确认
     */
    public static void asyncConfirm() throws IOException, TimeoutException {
        String QUEUE = UUID.randomUUID().toString();
        Channel channel = RabbitMQUtils.getNewChannel();
        channel.confirmSelect();

        //创建队列
        channel.queueDeclare(QUEUE, true, false, false, null);

        // 准备一个线程安全的哈希表ConcurrentSkipListMap，用于存储所有的发送消息
        //1.ConcurrentSkipListMap可以轻松的将序号与消息进行关联
        //2.轻松批量删除条目，只要有序号
        //3.支持高并发（多线程）
        ConcurrentSkipListMap<Long, String> map = new ConcurrentSkipListMap<>();

        long begin = System.currentTimeMillis();

        /**
         * 因为消息发成功还是失败，是由mq服务器主动告诉生产者的，所以需要一个监听器来监听来自server的异步通知
         *    监听器分两种：
         *      只监听成功的
         *     void addConfirmListener(ConfirmListener listener);
         *      即监听成功的，也监听失败的
         *     ConfirmListener addConfirmListener(ConfirmCallback ackCallback, ConfirmCallback nackCallback);
         */
        channel.addConfirmListener(
                new ConfirmCallback() {
                    @Override
                    public void handle(long deliveryTag, boolean multiple) throws IOException {
                        //监听成功的
                        System.out.println(deliveryTag);
                        //删除成功发布的消息(因为有可能是批量确认的，所有不止一条消息)
                        if (multiple) {
                            //如果是批量处理，tag小于deliveryTag的均已经被确认了
                            ConcurrentNavigableMap<Long, String> headMap = map.headMap(deliveryTag);
                            headMap.clear();
                        } else {
                            //System.out.println(deliveryTag + ":" + map.get(deliveryTag));
                            map.remove(deliveryTag);
                        }

                    }
                },
                new ConfirmCallback() {
                    /**
                     * 异步通知消息处理失败
                     * @param deliveryTag 消息的标识
                     * @param multiple 是否为批量确认
                     * @throws IOException 异常
                     */
                    @Override
                    public void handle(long deliveryTag, boolean multiple) throws IOException {
                        //监听失败的
                        System.out.println("异步确认失败的deliveryTag=" + deliveryTag);
                    }
                }
        );

        //发送消息
        for (int i = 0; i < MESSAGE_MAX_COUNT; i++) {

            /**
             * 如何处理异步未确认的消息？
             *  答：最好的解决方法就是把未确认的消息放在一个基于内存的能被发布线程（生产者）访问的队列中，如CurrentLinkedQueue，
             *      这个队列在 confirm callbacks 与发布线程（生产者进行消息的传递）
             * 怎么实现？
             *  答：
             *      1、发布消息时将消息放在线程安全的 CurrentLinkedQueue 队列中
             *      2、rabbitmq服务器在进行异步 正确的回调函数时 将CurrentLinkedQueue以及确认的消息删掉，那么剩下的就是未确认的
             *      3、rabbitmq在确认发布失败，调用失败的回调函数 对失败的消息重新处理如：重新发布
             *
             *  注意 channel.getNextPublishSeqNo() 每发布消息basicPublish调用一次，都会+1，所以注意put存放时序号的前后位置和要不要-1
             */
            map.put(channel.getNextPublishSeqNo(),i + "");//序号从1 开始

            channel.basicPublish("",QUEUE,MessageProperties.PERSISTENT_TEXT_PLAIN,(i + "").getBytes());//此时发布消息的序号为1
            System.out.println(channel.getNextPublishSeqNo());//2
            //System.exit(0);
            //map.put(channel.getNextPublishSeqNo() - 1,i + "");

        }
        System.out.println(map);

        long end = System.currentTimeMillis();
        System.out.println("1000个消息，【异步消息发布确认】 完成时间：" + (end - begin) + "ms");
    }
}
