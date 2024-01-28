package cn.itcourage.smartmq.broker;

import cn.itcourage.smartmq.adapter.core.consumer.CommonMessage;
import cn.itcourage.smartmq.adapter.core.spi.SmartMQProducer;
import cn.itcourage.smartmq.adapter.core.util.Callback;
import cn.itcourage.smartmq.store.MessageBrokerInner;
import cn.itcourage.smartmq.store.MessageStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * SmartMQ 消息调度器
 * 从存储中查询出数据，然后将消息发送发送到目标消息队列Broker集群
 */
public class SmartMQScheduler {

    private final static Logger logger = LoggerFactory.getLogger(SmartMQScheduler.class);

    protected volatile boolean stopped = false;

    private final int batchSize = 5;

    private MessageStore messageStore;

    private SmartMQProducer smartMQProducer;

    private final Thread schedulerThread;

    public SmartMQScheduler(SmartMQController smartMQController) {
        this.messageStore = smartMQController.getMessageStore();
        this.smartMQProducer = smartMQController.getSmartMQAdapter().createAndGetMQProducerInstance();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                doSchedule();
            }
        };
        this.schedulerThread = new Thread(runnable, "schedulerThread");
    }

    public void start() {
        //启动调度线程
        this.schedulerThread.start();
        logger.info("启动调度线程");
    }

    private void doSchedule() {
        while (!stopped) {
            try {
                // 每隔 20 毫秒扫描一次存储
                Thread.sleep(10);
                Long currentTime = System.currentTimeMillis();
                List<MessageBrokerInner> messageBrokerInnerList = messageStore.selectMessagesByOffset(null, batchSize);
                for (MessageBrokerInner messageBrokerInner : messageBrokerInnerList) {
                    // 判断是否到期了
                    if (currentTime >= messageBrokerInner.getDelayTime()) {
                        //若可以发送，则发送消息到目的 Broker 集群
                        CommonMessage commonMessage = new CommonMessage(
                                messageBrokerInner.getTopic(),
                                messageBrokerInner.getMessageId(),
                                messageBrokerInner.getBody(),
                                messageBrokerInner.getProperties()
                        );

                        smartMQProducer.sendMessage(commonMessage, new Callback() {
                            @Override
                            public void commit() {

                            }

                            @Override
                            public void rollback() {

                            }
                        });
                    }
                }
                // 修改发送过的消息偏移量 
            } catch (Exception e) {
                logger.error("dispatchMessage error:", e);
            }
        }
    }

    public void shutdown() {
        this.stopped = true;
    }

}
