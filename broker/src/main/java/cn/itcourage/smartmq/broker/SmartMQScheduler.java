package cn.itcourage.smartmq.broker;

import cn.itcourage.smartmq.store.MessageStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SmartMQ 消息调度器
 * 从存储中查询出数据，然后将消息发送发送到目标消息队列Broker集群
 */
public class SmartMQScheduler {

    private final static Logger logger = LoggerFactory.getLogger(SmartMQScheduler.class);

    protected volatile boolean stopped = false;

    private MessageStore messageStore;

    private final Thread schedulerThread;

    public SmartMQScheduler(SmartMQController smartMQController) {
        this.messageStore = smartMQController.getMessageStore();
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
                Thread.sleep(500);
                messageStore.doIteratorForTest();
            } catch (Exception e) {
                logger.error("dispatchMessage error:", e);
            }
        }
    }

    public void shutdown() {
        this.stopped = true;
    }

}
