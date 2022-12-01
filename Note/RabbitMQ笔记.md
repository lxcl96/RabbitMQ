# 前言

四大章节，带 * 的为重点章节

# 第一章 入门

## 1.  MQ的相关概念

### 1.1  什么是MQ

​	MQ(message queue)，从字面意思上看，本质是个队列，FIFO 先入先出，只不过队列中存放的内容是 message 而已，还是一种跨进程的通信机制，用于上下游传递消息。在互联网架构中，MQ 是一种非常常见的上下游“逻辑解耦+物理解耦”的消息通信服务。使用了 MQ 之后，消息发送上游只需要依赖 MQ，不用依赖其他服务。

### 1.2 为什么要用MQ

+ 流量消峰

  > 举个例子，如果订单系统最多能处理一万次订单，这个处理能力应付正常时段的下单时绰绰有余，正 常时段我们下单一秒后就能返回结果。但是在高峰期，如果有两万次下单操作系统是处理不了的，只能限 制订单超过一万后不允许用户下单。使用消息队列做缓冲，我们可以取消这个限制，把一秒内下的订单分 散成一段时间来处理，这时有些用户可能在下单十几秒后才能收到下单成功的操作，但是比不能下单的体 验要好。
  >
  > <img src='img\image-20221201100325836.png'>

+ 应用解耦

  > 以电商应用为例（图1），应用中有订单系统、库存系统、物流系统、支付系统。用户创建订单后，如果耦合 调用库存系统、物流系统、支付系统，任何一个子系统出了故障，都会造成下单操作异常。
  >
  > 当转变成基于 消息队列的方式后（图2），系统间调用的问题会减少很多，比如物流系统因为发生故障，需要几分钟来修复。在 这几分钟的时间里，物流系统要处理的内存被缓存在消息队列中，用户的下单操作可以正常完成。当物流 系统恢复后，继续处理订单信息即可，中单用户感受不到物流系统的故障，提升系统的可用性。
  >
  > <img src='img\image-20221201100756331.png'>

+ 异步处理

  > 有些服务间调用是异步的，例如 A 调用 B，B 需要花费很长时间执行，但是 A 需要知道 B 什么时候可 以执行完，以前一般有两种方式
  >
  > + A 过一段时间去调用 B 的查询 api 查询
  > + 或者 A 提供一个 callback api， B 执行完之后调用 api 通知 A 服务。
  >
  > 这两种方式都不是很优雅。
  >
  > 使用消息总线，可以很方便解决这个问题， A 调用 B 服务后，只需要监听 B 处理完成的消息，当 B 处理完成后，会发送一条消息给 MQ，MQ 会将此消 息转发给 A 服务。这样 A 服务既不用循环调用 B 的查询 api，也不用提供 callback api。同样B 服务也不用 做这些操作。A 服务还能及时的得到异步处理成功的消息。
  >
  > <img src='img\image-20221201101020270.png'>

### 1.3 MQ分类

+ ActiveMQ

  > 优点：单机吞吐量万级，时效性 ms 级，可用性高，基于主从架构实现高可用性，消息可靠性较 低的概率丢失数据 
  >
  > 缺点:官方社区现在对 **ActiveMQ 5.x 维护越来越少，高吞吐量场景较少使用**。

+ Kafka

  > 大数据的杀手锏，谈到大数据领域内的消息传输，则绕不开 Kafka，这款**为大数据而生**的消息中间件， 以其**百万级 TPS **的吞吐量名声大噪，迅速成为大数据领域的宠儿，在数据采集、传输、存储的过程中发挥 着举足轻重的作用。目前已经被 LinkedIn，Uber, Twitter, Netflix 等大公司所采纳。
  >
  >  优点: 性能卓越，单机写入 TPS 约在百万条/秒，最大的优点，就是**吞吐量高**。时效性 ms 级可用性非 常高，kafka 是分布式的，一个数据多个副本，少数机器宕机，不会丢失数据，不会导致不可用,消费者采 用 Pull 方式获取消息, 消息有序, 通过控制能够保证所有消息被消费且仅被消费一次;有优秀的第三方Kafka Web 管理界面 Kafka-Manager；在日志领域比较成熟，被多家公司和多个开源项目使用；功能支持： 功能 较为简单，**主要支持简单的 MQ 功能，在大数据领域的实时计算以及日志采集被大规模使用 **
  >
  > 缺点：Kafka 单机超过 64 个队列/分区，Load 会发生明显的飙高现象，队列越多，load 越高，发送消 息响应时间变长，使用短轮询方式，实时性取决于轮询间隔时间，消费失败不支持重试；支持消息顺序， 但是一台代理宕机后，就会产生消息乱序，**社区更新较慢**；

+ RocketMQ

  > RocketMQ 出自阿里巴巴的开源产品，用 Java 语言实现，在设计时参考了 Kafka，并做出了自己的一 些改进。被阿里巴巴广泛应用在订单，交易，充值，流计算，消息推送，日志流式处理，binglog 分发等场 景。
  >
  >  优点:**单机吞吐量十万级,可用性非常高**，分布式架构,**消息可以做到 0 丢失**,MQ 功能较为完善，还是分 布式的，扩展性好,**支持 10 亿级别的消息堆积**，不会因为堆积导致性能下降,源码是 java 我们可以自己阅 读源码，定制自己公司的 MQ 
  >
  > 缺点：**支持的客户端语言不多**，目前是 java 及 c++，其中 c++不成熟；社区活跃度一般,没有在MQ 核心中去实现 JMS 等接口,有些系统要迁移需要修改大量代码

+ RabbitMQ

  > 官网：https://www.rabbitmq.com/news.html 
  >
  > 2007 年发布，是一个在AMQP(高级消息队列协议)基础上完成的，可复用的企业消息系统，是当前**最主流的消息中间件之一**。 
  >
  > 优点:由于 **erlang 语言的高并发特性，性能较好；吞吐量到万级，MQ 功能比较完备,健壮、稳定、易 用、跨平台、支持多种语言** 如：Python、Ruby、.NET、Java、JMS、C、PHP、ActionScript、XMPP、STOMP 等，支持 AJAX 文档齐全；开源提供的管理界面非常棒，用起来很好用,**社区活跃度高**；更新频率相当高 
  >
  > 缺点：商业版需要收费,学习成本较高

### 1.4 MQ的选择

+ Kafka

  > Kafka 主要特点是基于Pull 的模式来处理消息消费，追求高吞吐量，一开始的目的就是用于日志收集 和传输，适合产生**大量数据**的互联网服务的数据收集业务。**大型公司建议可以选用，如果有日志采集功能， 肯定是首选 kafka 了**。

+ RocketMQ

  > 天生为**金融互联网领域**而生，对于**可靠性要求很高**的场景，**尤其是电商里面的订单扣款，以及业务削 峰，在大量交易涌入时，后端可能无法及时处理的情况**。RoketMQ 在稳定性上可能更值得信赖，这些业务 场景在阿里双 11 已经经历了多次考验，如果你的业务有上述并发场景，建议可以选择 RocketMQ。

+ RabbitMQ

  > 结合 erlang 语言本身的并发优势，性能好**时效性微秒级，社区活跃度也比较高**，管理界面用起来十分 方便，如果你的**数据量没有那么大**，**中小型公司**优先选择功能比较完备的 RabbitMQ。

## 2. RabbitMQ

### 2.1 RabbitMQ的概念

RabbitMQ 是一个消息中间件：它接受并转发消息。你可以把它当做一个快递站点，当你要发送一个包 裹时，你把你的包裹放到快递站，快递员最终会把你的快递送到收件人那里，按照这种逻辑 RabbitMQ 是 一个快递站，一个快递员帮你传递快件。

**RabbitMQ** 与快递站的主要区别在于，它不处理快件而是**接收， 存储和转发消息数据。**

### 2.2 四大核心概念

+ 生产者

  > 产生数据发送消息的程序是生产者

+ 交换机

  > 交换机是 RabbitMQ 非常重要的一个部件，
  >
  > + 一方面它接收来自生产者的消息，
  > + 另一方面它将消息 推送到队列中。
  >
  > 交换机必须确切知道如何处理它接收到的消息，是将这些消息推送到特定队列还是推 送到多个队列，亦或者是把消息丢弃，这个得由**交换机类型决定**

+ 队列

  > 队列是 RabbitMQ 内部使用的一种数据结构，尽管消息流经 RabbitMQ 和应用程序，但它们只能存 储在队列中。**队列仅受主机的内存和磁盘限制的约束**，本质上是一个大的消息缓冲区。许多生产者可 以将消息发送到一个队列，许多消费者可以尝试从一个队列接收数据。这就是我们使用队列的方式

+ 消费者

  > 消费与接收具有相似的含义。消费者大多时候是一个等待接收消息的程序。请注意生产者，消费 者和消息中间件很多时候并不在同一机器上。同一个应用程序既可以是生产者又是可以是消费者。

<img src='img\image-20221201104052549.png'>

### 2.3 RabbitMQ的六大核心模式

<img src='img\image-20221201104646116.png'>



### 2.4 RobbitMQ的工作原理

<img src='img\image-20221201110055297.png'>

> **Borker：**接收和分发消息的应用，RabbitMQ Server 就是 Message Broker
>
> **Virtual Host：**出于多租户和安全因素设计的，把 AMQP 的基本组件划分到一个虚拟的分组中，类似 于网络中的 namespace 概念。当多个不同的用户使用同一个 RabbitMQ server 提供的服务时，可以划分出 多个 vhost，每个用户在自己的 vhost 创建 exchange／queue 等
>
> **Connection：**：publisher／consumer 和 broker 之间的 TCP 连接
>
> **Channel：**如果每一次访问 RabbitMQ 都建立一个 Connection，在消息量大的时候建立 TCP Connection 的**开销将是巨大的，效率也较低**。Channel 是在 connection 内部建立的逻辑连接，如果应用程 序支持多线程，通常每个 thread 创建单独的 channel 进行通讯，AMQP method 包含了 channel id 帮助客 户端和 message broker 识别 channel，所以 channel 之间是完全隔离的。**Channel 作为轻量级的 Connection 极大减少了操作系统建立 TCP connection 的开销**
>
> **Exchange：**message 到达 broker 的第一站，根据分发规则，匹配查询表中的 routing key，分发 消息到 queue 中去。常用的类型有：direct (point-to-point), topic (publish-subscribe) and fanout (multicast)
>
> **Queue：**消息最终被送到这里等待 consumer 取走
>
> **Binding：**exchange 和 queue 之间的虚拟连接，binding 中可以包含 routing key，Binding 信息被保 存到 exchange 中的查询表中，用于 message 的分发依据

### 2.5 安装

#### 下载安装软件：

```sh
# 下载 erlang-21.3.8.14-1.el7.x86_64.rpm 
curl -s https://packagecloud.io/install/repositories/rabbitmq/erlang/script.rpm.sh | sudo bash
sudo yum install erlang-21.3.8.14-1.el7.x86_64

# 检验erlang是否成功安装
erl
ssl:versions().  #注意最后面有个点的
halt(). #退出验证
```

<img src='img\image-20221201135004376.png'>

```sh
# 安装rabbitmq的依赖（必须安装）
yum install socat -y
# 离线安装 rabbitmq-server-3.8.8-1.el7.moarch.rpm，支持erlang21.3 兼容erlang23 
rpm -ivh rabbitmq-server-3.8.8-1.el7.noarch.rpm
```

#### ***Rabbit服务常用命令：***

```sh
# 添加开机启动RabbitMQ服务
chkconfig rabbitmq-server on
# 启动rabbitmq服务
/sbin/service rabbitmq-server start
# 查看rabbitmq服务状态
/sbin/service rabbitmq-server status
# 停止rabbitmq服务
/sbin/service rabbitmq-service stop	
```

<img src='img\image-20221201135530445.png'>

<img src='img\image-20221201135611228.png'>

#### ***开启rabbitmq的web管理插件***

rabbitmq默认由一个guest用户密码也是guest（管理员权限）但是只允许本地登陆

```sh
# 开启web管理插件
rabbitmq-plugins enable rabbitmq_management
# 创建账户 ly 密码 1024
rabbitmqctl add_user ly 1024
# 设置用户角色
rabbitmqctl set_user_tags ly administrator

# 设置用户权限 
# 格式：set_permissions [-p <vhostpath>] <user> <conf> <write> <read>
rabbitmqctl set_permissions -p "/" ly ".*" ".*" ".*" #用户ly具有vhost1 这个virtual host中所有资源的配置、读、写权限

#列出当前rabbitmq所有的用户和角色
rabbitmqctl list_users
```

访问地址：http://127.0.0.1:15672

<img src='img\image-20221201140605739.png'>

# 第二章 核心部分*

## 1. Hello World

<img src='img\image-20221201163548269.png'>

在本教程的这一部分中，我们将用 Java 编写两个程序。发送单个消息的生产者和接收消息并打印 出来的消费者。我们将介绍 Java API 中的一些细节。 

在下图中，“ P”是我们的生产者，“ C”是我们的消费者。中间的框是一个队列-RabbitMQ 代 表使用者保留的消息缓冲区

<img src='img\image-20221201143757087.png'>

### 1.1 创建maven工程，并引入依赖

```xml
<dependencies>
    <!--rabbitmq 依赖客户端-->
    <dependency>
        <groupId>com.rabbitmq</groupId>
        <artifactId>amqp-client</artifactId>
        <version>5.8.0</version>
    </dependency>
    <!--操作文件流的一个依赖-->
    <dependency>
        <groupId>commons-io</groupId>
        <artifactId>commons-io</artifactId>
        <version>2.6</version>
    </dependency>
</dependencies>
```

### 1.2  生产者代码

```java
public class Producer {
    //队列名称 （大写转换快捷键 ctrl + shift + U）
    public static final String QUEUE_NAME = "hello";

    //发消息
    public static void main(String[] args) throws IOException, TimeoutException {
        //设置rabbitmq连接工厂（不用指定port）
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("192.168.77.3");
        connectionFactory.setUsername("ly");
        connectionFactory.setPassword("1024");

        //得到一个rabbitmq连接
        Connection connection = connectionFactory.newConnection();
        //由于rabbit的connect连接创建消耗很大，所以是通过信道channel进行通信，获取信道
        Channel channel = connection.createChannel(1);

        /**
         * 使用默认交换机，则直接跳过创建交换机，直接连接队列
         * 参数：
         *      queue：队列名称
         *      durable：是否持久化（保存到硬盘还是内存），服务重启后依然可用
         *      exclusive：是否排他，即是否为独占队列
         *      autoDelete：是否自动删除（服务器不使用它时自动删除）
         *      arguments： 传递的参数
         */
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);

        /**
         * 发送消息
         * 参数：
         *  exchange：消息发布到的交换机，第一次使用默认的
         *  routingKey：路由key，后面再学,本次是队列名称
         *  props：消息的其他属性，如路由的表头等等
         *  body：消息体（byte[]）
         */

        channel.basicPublish("",QUEUE_NAME,null,"hello rabbitmq".getBytes());

        System.out.println("消息发送完毕!");

    }
}
```

<img src='img\image-20221201152330807.png'>

### 1.3 消费者代码

```java
public class Consumer {

    public static String QUEUE_NAME = "hello";

    public static void main(String[] args) throws IOException, TimeoutException {
        //设置rabbitmq连接工厂
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("192.168.77.3");
        connectionFactory.setUsername("ly");
        connectionFactory.setPassword("1024");

        //得到一个rabbitmq连接
        Connection connection = connectionFactory.newConnection();
        //创建信道接受消息
        Channel channel = connection.createChannel();
        System.out.println("信道id = " + channel.getChannelNumber());

        /**
         *
         * 参数：
         *  queue：要消费的队列名称
         *  autoAck： true表示消费成功后自动应答，false表示手动应答
         *  deliverCallback：消息被传递时的回调
         *  cancelCallback：消费取消时的回调
         */
        channel.basicConsume(
                QUEUE_NAME,
                true,
                (consumerTag, message) -> {
                    //lambda表达式，接受mq队列中的消息时的回调
                    System.out.println("hello队列消息被传递");
                    System.out.println("consumerTag=" + consumerTag);
                    System.out.println("message=" + new String(message.getBody()));

                },
                consumerTag -> {
                    //lambda表达式，消费行为被取消的回调
                    System.out.println("hello队列的消费行为被取消");
                    System.out.println("consumerTag=" + consumerTag);
                });

    }
}
```

<img src='img\image-20221201162025155.png'>

## 2. Work Queues

<img src='img\image-20221201163627576.png'>

​	工作队列（又叫任务队列）的主要思想是避免立即执行资源密集型任务，而不得不等待它完成。相反我们安排任务，在之后执行。我们把任务封装为消息并将其发送到队列。在后台运行的工作进程将弹出任务并最终执行作业。当有多个工作线程时，这些线程将一起竞争处理这些任务。（一个任务只能被一个线程处理）

<img src='img\image-20221201162943117.png'>

### 2.1 轮询分发消息



## 3. Publish/Subcribe

## 4. Routing

## 5.Topics

## 6. Publisher Confirms



# 第三章 高级部分*

## 1. 死信队列

## 2. 延迟队列

## 3.  发布确认高级

### 3.1 发布确认

### 3.2 回退消息

### 3.3. 备份交换机

## 4. 幂等性

## 5. 优先级队列

## 6. 惰性队列

# 第四章 集群部分*

## 1. Clustering

## 2. 镜像队列

## 3. Haproxy + Keepalive实现高可用负载均衡

## 4. Federation Exchange

## 5. Federation Queue

## 6. Shovel

