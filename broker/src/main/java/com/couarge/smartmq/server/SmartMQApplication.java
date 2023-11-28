package com.couarge.smartmq.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SmartMQApplication {
    private final static Logger logger = LoggerFactory.getLogger(SmartMQApplication.class);

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        logger.info("开始启动SmartMQ服务");
        SmartMQController smartMQController = new SmartMQController();
        smartMQController.start();
        logger.info("结束启动SmartMQ服务,耗时：" + (System.currentTimeMillis() - start) + "毫秒");
    }

}
