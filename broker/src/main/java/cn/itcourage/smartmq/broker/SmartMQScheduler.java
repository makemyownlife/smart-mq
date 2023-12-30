package cn.itcourage.smartmq.broker;

import cn.itcourage.smartmq.broker.support.BrokerRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SmartMQ 消息调度器
 * 从存储中查询出数据，然后将消息发送发送到目标消息队列Broker集群
 */
public class SmartMQScheduler {

    private final static Logger logger = LoggerFactory.getLogger(SmartMQScheduler.class);

    private SmartMQController smartMQController;

    public SmartMQScheduler(SmartMQController smartMQController) {
        this.smartMQController = smartMQController;
    }

    public void start() {

    }

    public void shutdown() {

    }

}
