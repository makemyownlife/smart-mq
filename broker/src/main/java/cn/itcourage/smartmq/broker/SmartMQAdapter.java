package cn.itcourage.smartmq.broker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 封装适配器相关启动，关闭，暂停等功能。
 */
public class SmartMQAdapter {

    private final static Logger logger = LoggerFactory.getLogger(SmartMQAdapter.class);

    private static final String CONNECTOR_SPI_DIR = "/plugin";

    private static final String CONNECTOR_STANDBY_SPI_DIR = "/smart-mq-broker/plugin";

    private SmartMQConfig smartMQConfig;

    public SmartMQAdapter(SmartMQConfig smartMQConfig) {
        this.smartMQConfig = smartMQConfig;
    }



}
