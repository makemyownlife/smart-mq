package cn.itcourage.smartmq.broker;

import cn.itcourage.smartmq.adapter.core.consumer.CommonMessage;
import cn.itcourage.smartmq.adapter.core.spi.SmartMQConsumer;
import cn.itcourage.smartmq.adapter.core.util.SmartMQAdapterConstants;
import cn.itcourage.smartmq.common.timer.utils.CollectionUtils;
import cn.itcourage.smartmq.store.MessageBrokerInner;
import cn.itcourage.smartmq.store.MessageStore;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * SmartMQ 消费分发服务 (只有master才能启动该服务)
 * 1.拉取消息 2.存储消息到本地磁盘 3.发送消息到slave服务器
 */
public class SmartMQDispatcher {

    private final static Logger logger = LoggerFactory.getLogger(SmartMQDispatcher.class);

    protected volatile boolean stopped = false;

    private final SmartMQAdapter smartMQAdapter;

    private SmartMQConsumer smartMQConsumer;

    private final MessageStore messageStore;

    private final Thread dispatchMessageThread;

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
            boolean hasException = false;
            try {
                List<CommonMessage> messageList = smartMQConsumer.getMessage(30L, TimeUnit.SECONDS);
                if (CollectionUtils.isNotEmpty(messageList)) {
                    for (CommonMessage commonMessage : messageList) {
                        String rawDelayTime = commonMessage.getProperties().get(SmartMQAdapterConstants.DELAY_TIME);
                        if (StringUtils.isNotEmpty(rawDelayTime)) {
                            Long delayTime = Long.valueOf(rawDelayTime);
                            MessageBrokerInner messageBrokerInner = new MessageBrokerInner(
                                    commonMessage.getTopic(),
                                    commonMessage.getMessageId(),
                                    commonMessage.getBody(),
                                    commonMessage.getProperties(),
                                    delayTime
                            );
                            messageStore.putMessage(messageBrokerInner);
                            logger.warn("消息编号：[" + commonMessage.getMessageId() + "]已被存储在存储引擎中");
                        } else {
                            logger.warn("消息编号：[" + commonMessage.getMessageId() + "]没有延迟时间字段，不予处理!");
                        }
                    }
                    smartMQConsumer.ack();
                }
            } catch (Exception e) {
                logger.error("dispatchMessage error:", e);
                hasException = true;
            }
            if (hasException) {
                // 若失败，则采取回滚的策略
                smartMQConsumer.rollback();
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
