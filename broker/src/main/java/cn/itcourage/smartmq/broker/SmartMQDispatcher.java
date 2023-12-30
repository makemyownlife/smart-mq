package cn.itcourage.smartmq.broker;

import cn.itcourage.smartmq.adapter.core.spi.SmartMQConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SmartMQ 消费分发服务 (只有master才能启动该服务)
 * 1.拉取消息 2.存储消息到本地磁盘 3.发送消息到slave服务器
 */
public class SmartMQDispatcher {

    private final static Logger logger = LoggerFactory.getLogger(SmartMQDispatcher.class);

    private SmartMQAdapter smartMQAdapter;

    private SmartMQConsumer smartMQConsumer;

    public SmartMQDispatcher(SmartMQController smartMQController) {
        this.smartMQAdapter = smartMQController.getSmartMQAdapter();
    }

    public void start() {
        //创建消费者适配器
        this.smartMQConsumer = smartMQAdapter.createAndGetMQConsumerInstance();
    }

    public void shutdown() {
        if (this.smartMQConsumer != null) {
            this.smartMQConsumer.stop();
        }
    }

}
