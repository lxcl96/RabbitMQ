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

如果针对多个生产者多个消费者同时工作在同一个队列，rabbitmq默认采用的就是轮询分发消息，同一个队列的消息，采用“你一个我一个”的模式被消费掉

***生产者代码：***

```java
public class Producer {

    public static void main(String[] args) throws IOException, TimeoutException {
        String thread = UUID.randomUUID().toString().replace("-", "").substring(0, 5);
        Channel channel = RabbitMQUtils.getNewChannel();

        System.out.println("生产者 [ " + thread + " ]开始生产大量的消息！");
        //声明一个队列
        channel.queueDeclare(RabbitMQUtils.getQueueName(),false,false,false,null);

        //从控制台接收消息
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            System.out.println("请输入要发送的消息：");
            String message = scanner.next();

            channel.basicPublish(
                    //默认交换机
                    "",
                    //路由key 此处为名字为hello的队列
                    RabbitMQUtils.getQueueName(),
                //无参数
                    null,
                //消息体
                    message.getBytes()
            );

            System.out.println("消息：【" + message + "】 已经成功发送！");
        }
    }
}
```

***消费者代码：***

```java
public class Consumer {

    public static void main(String[] args) throws IOException, TimeoutException {
        String thread = UUID.randomUUID().toString().replace("-", "").substring(0, 5);
        Channel channel = RabbitMQUtils.getNewChannel();
        System.out.println("消费者 [ "  + thread + " ] 等待消费。。。");
        // 1/n 众多消费者之一
        channel.basicConsume(
                // 名字为 hello 的队列
                RabbitMQUtils.getQueueName(),
                true,
                // 接收队列中消息的回调函数
                (consumerTag, message) -> {
                    System.out.println("消费者 [ "  + thread + " ]接收到的消息：" + new String(message.getBody()));
                },
                // 消费者取消消费的回调函数
                consumerTag -> {});
    }
}
```

***测试：***

<img src='img\image-20221202101941115.png'>

### 2.2 消息应答（Message Acknowledge）

<font color='red'>如果队列在消费设置为手动应答`ACK Manual`则rabbitmq会自动将断开连接的消费者所消费的消息自动重新入队，按照轮询（一人一个）的原则分发消息</font>

#### 1. 概念

​	消费者完成一个任务可能需要一段时间，如果其中一个消费者处理一个长的任务并仅只完成 了部分突然它挂掉了，会发生什么情况。RabbitMQ 一旦向消费者传递了一条消息，便立即将该消 息标记为删除。在这种情况下，突然有个消费者挂掉了，我们将丢失正在处理的消息。以及后续 发送给该消费这的消息，因为它无法接收到。

​	为了保证消息在发送过程中不丢失，rabbitmq 引入消息应答机制，消息应答就是:**消费者在接收 到消息并且处理该消息之后，告诉 rabbitmq 它已经处理了，rabbitmq 可以把该消息删除了。**

#### 2. 自动应答（Auto ACK）

​	**消息发送后立即被认为已经传送成功（不推荐）**，这种模式需要在高吞吐量和数据传输安全性方面做权 衡,因为这种模式如果消息在接收到之前，消费者那边出现连接或者 channel 关闭，那么消息就丢失 了,当然另一方面这种模式消费者那边可以传递过载的消息，没有对传递的消息数量进行限制，当 然这样有可能使得消费者这边由于接收太多还来不及处理的消息，导致这些消息的积压，最终使 得内存耗尽，最终这些消费者线程被操作系统杀死，所以**这种模式仅适用在消费者可以高效并以 某种速率能够处理这些消息的情况下使用**。

#### 3. 消息手动应答的方法

消息的重新入队可能会产生二次消费问题，所以需要谨慎处理

```java
//com.rabbitmq.client.Channel

	/**
	 * 消息的肯定应答
	 * @deliveryTag 消息携带的tag标签 
	 * @multiple 表示是否批量ack处理当前信道内tag小于本条消息tag的消息（已经被处理，但是没有应答ack），true表示批量ack消息
	 *      例子：比如当前信道内（多个队列）有消息tag为56789，当前处理的消息tag为8
	 *            如果multiple=true的话则 则5，6，7，8均会被ack确认
     */
void basicAck(long deliveryTag, boolean multiple) throws IOException;

    /**
     * 消息的否定应答（单条或多条）
     * @deliveryTag 消息携带的tag标签 
	 * @multiple 表示是否批量ack处理当前信道内tag小于本条消息tag的消息（已经被处理，但是没有应答ack），true表示批量ack消息
	 *      例子：比如当前信道内（多个队列）有消息tag为56789，当前处理的消息tag为8
	 *            如果multiple=true的话则 则5，6，7，8均会被ack确认
	 * @requeue 表示被拒绝消息们是否重新入队，true，重新入队，false，不入队
     */
    void basicNack(long deliveryTag, boolean multiple, boolean requeue)
            throws IOException;

     /**
     * 单条消息的否定应答
     * @deliveryTag 消息的携带的tag标签 
     * @requeue 表示被消费后的拒绝的此条消息是否重新入队
     */
    void basicReject(long deliveryTag, boolean requeue) throws IOException;
```

#### 4. 批量处理Multiple

表示是否批量ack处理**当前信道（多队列）内tag小于本条消息tag的消息（已经被处理，但是没有应答ack）**，true表示批量ack消息
例子：比如当前信道内（多个队列）有消息tag为56789，当前处理的消息tag为8，如果multiple=true的话则 则5，6，7，8均会被ack确认。如果为false则只会处理当前本条消息

<img src='img\image-20221202111106731.png'>

#### 5. 消息重新入队

如果消费者由于某些原因失去连接(其通道已关闭，连接已关闭或 TCP 连接丢失)，导致消息 未发送 ACK 确认，RabbitMQ 将了解到消息未完全处理，并将对其重新排队。如果此时其他消费者 可以处理，它将很快将其重新分发给另一个消费者。这样，即使某个消费者偶尔死亡，也可以确 保不会丢失任何消息。

> **两个问题**
>
> + 消息重新入队的配置？
> + 二次消费问题

<img src='img\image-20221202111221320.png'>

#### 6.消息手动应答（代码演示）

注意新的队列，必须要先启动生产者建立新的队列否则，先启动消费者会报错，因为要消费的队列不存在

***生产者：***

```java
/**
 * FileName:Producer.class
 * Author:ly
 * Date:2022/12/1 0001
 * Description: 消息的手动应答保证不丢失，多个消费者消费时间久的，放回队列重新消费 - 生产者
 */
public class Producer {
    private static final String TASK_QUEUE_NAME = "ack_queue";

    //发消息
    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabbitMQUtils.getNewChannel();

        System.out.println("生产者 [producer]开始生产大量的消息！");
        //声明一个队列
        channel.queueDeclare(TASK_QUEUE_NAME, false, false, false, null);

        //从控制台接收消息
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            System.out.println("请输入要发送的消息：");
            String message = scanner.next();

            channel.basicPublish(
                    //默认交换机
                    "",
                    //路由key 此处为名字为hello的队列
                    TASK_QUEUE_NAME,
                    null,
                    message.getBytes(StandardCharsets.UTF_8)
            );

            System.out.println("消息：【" + message + "】 已经成功发送！");
        }
    }

}
```

***消费者_1：***

```java
/**
 * FileName:Consumer_1.class
 * Author:ly
 * Date:2022/12/2 0002
 * Description: 消息的手动应答保证不丢失，多个消费者消费时间久的，放回队列重新消费 - 消费者1
 */
public class Consumer_1 {
    private static final String TASK_QUEUE_NAME = "ack_queue";

    public static void main(String[] args) throws IOException, TimeoutException {
        System.out.println("消费者 [Consumer_1]等待接收消息（处理时间很快的）！");
        Channel channel = RabbitMQUtils.getNewChannel();
        channel.basicConsume(
                //队列名
                TASK_QUEUE_NAME,
                //手动应答
                false,
                //消费消息
                (consumerTag, message) -> {
                    System.out.println("消费者 [Consumer_1] 接收到的消息：" + new String(message.getBody(), StandardCharsets.UTF_8));
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    //手动应答
                    channel.basicAck(message.getEnvelope().getDeliveryTag(),false);
                    System.out.println("=======================本次消费结束==========================");
                },
                //取消消费消息的回调
                consumerTag -> {System.out.println("消费者 [Consumer_1]取消消息回调函数！");}
                );
    }
}
```

***消费者_2：***

```java
public class Consumer_2 {
    private static final String TASK_QUEUE_NAME = "ack_queue";

    public static void main(String[] args) throws IOException, TimeoutException {
        System.out.println("消费者 [Consumer_2]等待接收消息（处理时间很快的）！");
        Channel channel = RabbitMQUtils.getNewChannel();
        channel.basicConsume(
                //队列名
                TASK_QUEUE_NAME,
                //手动应答
                false,
                //消费消息
                (consumerTag, message) -> {
                    System.out.println("消费者 [Consumer_2] 接收到的消息：" + new String(message.getBody(), StandardCharsets.UTF_8));
                    try {
                        Thread.sleep(30000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    //手动应答
                    channel.basicAck(message.getEnvelope().getDeliveryTag(),false);
                    System.out.println("=======================本次消费结束==========================");
                },
                //取消消费消息的回调
                consumerTag -> {System.out.println("消费者 [Consumer_2]取消消息回调函数！");}
        );
    }
}
```

***结果演示：***

<img src='img\image-20221202135437606.png'>

### 2.3 RabbitMQ持久化

​	刚刚我们已经看到了如何处理任务不丢失的情况，但是如何**保障当 RabbitMQ 服务停掉以后消息生产者发送过来的消息不丢失**。**默认情况**下 RabbitMQ 退出或由于某种原因崩溃时，它**忽视队列 和消息**，除非告知它不要这样做。**确保消息不会丢失需要做两件事：我们需要将队列和消息都标 记为持久化。**

#### 2.3.1 队列持久化（Producer/Administrator端）

创建/声明队列时，有一个参数可以选择是否持久化

```java
//设置队列持久化
boolean durable = true;
//声明一个队列
channel.queueDeclare(TASK_QUEUE_NAME, durable, false, false, null);
```

***注意：***

​	如果队列已经创建，是没有办法更改其持久化属性的。除非将该队列删除重新创建时`开启（关闭）持久化`。

否则启动时或报错：<img src='img\image-20221202150315871.png'>

此时就算重启rabbitmq该队列也会存在<img src='img\image-20221202150524930.png'>

#### 2.3.2 消息持久化（Producer端）

发布消息时，有一个参数可以选择是否持久化

```java
channel.basicPublish(
        //默认交换机
        "",
        //路由key 此处为名字为hello的队列
        TASK_QUEUE_NAME,
        //消息(text/plain格式 也可以设置octet流文本)持久化
        MessageProperties.PERSISTENT_TEXT_PLAIN,
        message.getBytes(StandardCharsets.UTF_8)
);

/*
	MessageProperties 枚举类，里面定义了 消息持久化/非持久化类型
	
*/
```

***注意：***

​	将消息标记为持久化并不能完全保证不会丢失消息。尽管它告诉 RabbitMQ 将消息保存到磁盘，但是 这里依然存在当消息刚准备存储在磁盘的时候 但是还没有存储完，消息还在缓存的一个间隔点。此时并没 有真正写入磁盘。持久性保证并不强，但是对于我们的简单任务队列而言，这已经绰绰有余了。如果需要 更强有力的持久化策略，参考后边的**发布确认章节（[Publisher Confirms](https://www.rabbitmq.com/tutorials/tutorial-seven-java.html)）**。

### 2.4 ==不公平分发（Consumer端）*==

**加权轮询（轮询+prefetch）**

**虽然说得是不公平分发，实际还是公平分发（轮询），因为都是按照预取值`prefetch`的大小进行分发的，唯一可变的就是`prefetch`值的大小**

> RabbitMQ中预取值`prefetch`默认为`0`，这就意味着队列向（连接某一消费者的）信道缓冲区发送消息是无限制，无穷大的。所以按照公平轮询的原则无论消费者客户机处理消息的速度是慢还是快，都会公平的“你一个我一个”进行轮询，最坏的情况就是把处理慢的客户机撑死了（内存溢出）。
>
> 通过修改预取值`prefetch`的大小，来控制消息缓冲区的大小，从而达到不公平分发。比如有5，6，7，8四条消息分发给a，b两个消费客户机，由于a处理消息的速度非常快，b处理消息的速度非常慢，此时**设置prefetch=1**，那么按照轮询方式假设5分配个a，6分配给b。**队列就会按照公平轮询的原则，并同时检测a信道缓冲区的数量，如果变为0就7分配给a**（因为a很快，所以就是0）。**那么在分发消息8时，b还在处理消息6，按照公平轮询的原则，应该发给b，但是b的信道缓冲区内消息数量不为0，为1（就是正在处理的消息6，打到了`prefetch`设置的上限）。那么队列就会轮询下一个消费客户机a，恰好a处理好了消息7，则队列就把消息分发给了a，由a处理。**

```java
//设置预取值为1，实现能者多劳(保证只要队列有消息，每个消费者都有1个消息)
int prefetch = 1;
//设置预取值
channel.basicQos(prefetch);

//下面就是消费的代码
channel.basicConsume(..)
```

<img src='img\image-20221202153308269.png'>



***执行效果：***

<img src='img\image-20221202153600332.png'>

### 2.5 预取值（prefetch）

​	本身消息的发送就是异步发送的，所以在任何时候，channel 上肯定不止只有一个消息另外来自消费 者的手动确认本质上也是异步的。因此这里就存在一个未确认的消息缓冲区，因此希望开发人员能**限制此 缓冲区的大小，以避免缓冲区里面无限制的未确认消息问题**。这个时候就可以通过使用 basic.qos 方法设 置“预取计数”值来完成的。**prefetch该值定义通道上允许的未确认消息的最大数量**。一旦数量达到配置的数量， RabbitMQ 将停止在通道上传递更多消息，除非至少有一个未处理的消息被确认ack，例如，假设在通道上有 未确认的消息 5、6、7，8，并且通道的预取计数设置为 4，此时RabbitMQ 将不会在该通道上再传递任何 消息，除非至少有一个未应答的消息被 ack。比方说 tag=6 这个消息刚刚被确认 ACK，RabbitMQ 将会感知 这个情况到并再发送一条消息。消息应答和 QoS 预取值对用户吞吐量有重大影响。通常，增加预取将提高 向消费者传递消息的速度。

​	***虽然自动应答传输消息速率是最佳的，但是，在这种情况下已传递但尚未处理的消息的数量也会增加，从而增加了消费者的 RAM 消耗**应该小心使用具有无限预处理 的自动确认模式或手动确认模式，消费者消费了大量的消息如果没有确认的话，会导致消费者连接节点的 内存消耗变大，所以找到合适的预取值是一个反复试验的过程，不同的负载该值取值也不同 100 到 300 范 围内的值通常可提供最佳的吞吐量，并且不会给消费者带来太大的风险。预取值为 1 是最保守的。当然这 将使吞吐量变得很低，特别是消费者连接延迟很严重的情况下，特别是在消费者连接等待时间较长的环境 中。对于大多数应用来说，稍微高一点的值将是最佳的。

<img src='img\image-20221202154020035.png'>

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

