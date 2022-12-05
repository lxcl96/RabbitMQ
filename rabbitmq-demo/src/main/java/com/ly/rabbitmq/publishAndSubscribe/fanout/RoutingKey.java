package com.ly.rabbitmq.publishAndSubscribe.fanout;

/**
 * FileName:RoutingKey.class
 * Author:ly
 * Date:2022/12/5 0005
 * Description:
 */
public enum RoutingKey {
    EXCHANGE_NAME("ly.fanout",""),
    FANOUT_1("fanout.1","fanout.queue.1"),
    FANOUT_2("fanout.2","fanout.queue.2");

    private final String routingKey;
    private final String queueName;

    RoutingKey(String key,String queue) {
        this.routingKey=key;
        this.queueName=queue;
    }

    public String getRoutingKey() {
        return routingKey;
    }

    public String getQueueName() {
        return queueName;
    }
}
