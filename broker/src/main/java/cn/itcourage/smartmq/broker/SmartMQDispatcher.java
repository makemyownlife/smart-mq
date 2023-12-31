package cn.itcourage.smartmq.broker;

import cn.itcourage.smartmq.adapter.core.consumer.CommonMessage;
import cn.itcourage.smartmq.adapter.core.spi.SmartMQConsumer;
import cn.itcourage.smartmq.common.timer.utils.CollectionUtils;
import cn.itcourage.smartmq.common.util.ThreadFactoryImpl;
import cn.itcourage.smartmq.store.MessageStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
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

    protected volatile boolean stopped = false;

    private SmartMQAdapter smartMQAdapter;

    private SmartMQConsumer smartMQConsumer;

    private MessageStore messageStore;

    private ThreadPoolExecutor dispatchMessageThreads;

    public SmartMQDispatcher(SmartMQController smartMQController) {
        this.smartMQAdapter = smartMQController.getSmartMQAdapter();
        this.messageStore = smartMQController.getMessageStore();
        //定义线程池，并执行拉取消息任务
        this.dispatchMessageThreads = new ThreadPoolExecutor(
                MAX_THREAD_COUNT,
                MAX_THREAD_COUNT,
                1000 * 60,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(),
                new ThreadFactoryImpl("dispatchMessageThread_"));
    }

    public synchronized void start() {
        //创建消费者适配器
        this.smartMQConsumer = smartMQAdapter.createAndGetMQConsumerInstance();
        for (int i = 0; i < MAX_THREAD_COUNT; i++) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    dispatchMessages();
                }
            };
            this.dispatchMessageThreads.submit(runnable);
        }
    }

    private void dispatchMessages() {
        while (!stopped) {
            try {
                List<CommonMessage> messageList = smartMQConsumer.getMessage(30L, TimeUnit.SECONDS);
                if (CollectionUtils.isNotEmpty(messageList)) {
                    for (CommonMessage commonMessage : messageList) {

                    }
                    smartMQConsumer.ack();
                }
            } catch (Exception e) {
                logger.error("dispatchMessage error:", e);
            }
        }

    }

    public synchronized void shutdown() {
        this.stopped = true;
        if (this.dispatchMessageThreads != null) {
            try {
                this.dispatchMessageThreads.awaitTermination(20, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
            }
        }
        if (this.smartMQConsumer != null) {
            this.smartMQConsumer.stop();
        }

    }

}
