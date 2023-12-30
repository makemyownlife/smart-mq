package cn.itcourage.smartmq.broker;

import cn.itcourage.smartmq.broker.config.SmartMQConfig;
import cn.itcourage.smartmq.broker.support.YamlLoader;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SmartMQApplication {
    private final static Logger logger = LoggerFactory.getLogger(SmartMQApplication.class);

    public static void main(String[] args) throws Exception {
        long start = System.currentTimeMillis();
        logger.info("开始启动SmartMQ服务");
        try {
            SmartMQConfig smartMQConfig = YamlLoader.loadConfig();
            logger.info("Broker配置信息:" + JSON.toJSONString(smartMQConfig));
            SmartMQController smartMQController = new SmartMQController(smartMQConfig);
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
        } catch (Exception e) {
            logger.error("SmartMQ start erorr: ", e);
        }
    }

}
