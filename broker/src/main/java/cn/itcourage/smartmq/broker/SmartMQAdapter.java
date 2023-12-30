package cn.itcourage.smartmq.broker;

import cn.itcourage.smartmq.broker.support.MQAdapterState;
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

    //适配器状态
    private MQAdapterState adapterState = MQAdapterState.CREATE_JUST;

    public SmartMQAdapter(SmartMQConfig smartMQConfig) {
        this.smartMQConfig = smartMQConfig;
    }

    public void start() {
        this.adapterState = MQAdapterState.RUNNING;
    }

    public void standby() {
        this.adapterState = MQAdapterState.STANDBY;
    }

    public void shutdown() {
        this.adapterState = MQAdapterState.SHUTDOWN_ALREADY;
    }

    public MQAdapterState getAdapterState() {
        return this.adapterState;
    }

}
