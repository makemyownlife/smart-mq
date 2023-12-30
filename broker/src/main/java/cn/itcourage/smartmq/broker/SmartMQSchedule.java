package cn.itcourage.smartmq.broker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SmartMQ 消息调度器
 */
public class SmartMQSchedule {

    private final static Logger logger = LoggerFactory.getLogger(SmartMQSchedule.class);

    private SmartMQController smartMQController;

    public SmartMQSchedule(SmartMQController smartMQController) {
        this.smartMQController = smartMQController;
    }

    public void start() {

    }

    public void shutdown() {

    }

}
