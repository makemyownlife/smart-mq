package cn.itcourage.smartmq.broker;

import cn.itcourage.smartmq.adapter.core.spi.SmartMQConsumer;
import cn.itcourage.smartmq.common.util.ThreadFactoryImpl;
import cn.itcourage.smartmq.store.MessageStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * SmartMQ 消费分发服务 (只有master才能启动该服务)
 * 1.拉取消息 2.存储消息到本地磁盘 3.发送消息到slave服务器
 */
public class SmartMQDispatcher {

    private final static Logger logger = LoggerFactory.getLogger(SmartMQDispatcher.class);

    private final static int MAX_THREAD_COUNT = 4;

    private SmartMQAdapter smartMQAdapter;

    private SmartMQConsumer smartMQConsumer;

    private MessageStore messageStore;

    private ThreadPoolExecutor pullMessageThreads;

    public SmartMQDispatcher(SmartMQController smartMQController) {
        this.smartMQAdapter = smartMQController.getSmartMQAdapter();
        this.messageStore = smartMQController.getMessageStore();
    }

    public void start() {
        //创建消费者适配器
        this.smartMQConsumer = smartMQAdapter.createAndGetMQConsumerInstance();
        //定义线程池，拉取消息
        this.pullMessageThreads = new ThreadPoolExecutor(
                MAX_THREAD_COUNT,
                MAX_THREAD_COUNT,
                1000 * 60,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(),
                new ThreadFactoryImpl("pullMessageThread_"));

    }

    public void shutdown() {
        if (this.pullMessageThreads != null) {
            try {
                this.pullMessageThreads.awaitTermination(20, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
            }
        }
        if (this.smartMQConsumer != null) {
            this.smartMQConsumer.stop();
        }

    }

}
