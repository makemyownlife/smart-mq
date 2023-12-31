package cn.itcourage.smartmq.broker;

import cn.itcourage.smartmq.adapter.core.consumer.CommonMessage;
import cn.itcourage.smartmq.adapter.core.spi.SmartMQConsumer;
import cn.itcourage.smartmq.common.timer.utils.CollectionUtils;
import cn.itcourage.smartmq.common.util.ThreadFactoryImpl;
import cn.itcourage.smartmq.store.MessageStore;
import com.alibaba.fastjson.JSON;
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

    private Thread dispatchMessageThread;

    public SmartMQDispatcher(SmartMQController smartMQController) {
        this.smartMQAdapter = smartMQController.getSmartMQAdapter();
        this.messageStore = smartMQController.getMessageStore();
        //消息分发线程池，并执行拉取消息任务
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                dispatchMessages();
            }
        };
        this.dispatchMessageThread = new Thread(runnable, "dispatchMessageThread");
    }

    public synchronized void start() {
        //创建消费者适配器
        this.smartMQConsumer = smartMQAdapter.createAndGetMQConsumerInstance();
        //启动分发线程
        this.dispatchMessageThread.start();
    }

    private void dispatchMessages() {
        while (!stopped) {
            try {
                List<CommonMessage> messageList = smartMQConsumer.getMessage(30L, TimeUnit.SECONDS);
                if (CollectionUtils.isNotEmpty(messageList)) {
                    for (CommonMessage commonMessage : messageList) {
                        String messageId = commonMessage.getMessageId();
                        logger.info("messageId:" + messageId);
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
        if (this.smartMQConsumer != null) {
            this.smartMQConsumer.stop();
        }
    }

}
