package com.courage.smartmq.broker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SmartMQApplication {
    private final static Logger logger = LoggerFactory.getLogger(SmartMQApplication.class);

    public static void main(String[] args) throws Exception {
        long start = System.currentTimeMillis();
        logger.info("开始启动SmartMQ服务");
        SmartMQController smartMQController = new SmartMQController();
        smartMQController.start();
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    smartMQController.shutdown();
                } catch (Throwable e) {
                    logger.error("smartMQController shutdown error: ", e);
                }
            }
        }));
        logger.info("结束启动SmartMQ服务,耗时：" + (System.currentTimeMillis() - start) + "毫秒");
    }

}
