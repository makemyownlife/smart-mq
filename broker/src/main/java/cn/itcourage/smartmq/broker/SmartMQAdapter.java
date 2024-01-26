package cn.itcourage.smartmq.broker;

import cn.itcourage.smartmq.adapter.core.spi.ExtensionLoader;
import cn.itcourage.smartmq.adapter.core.spi.SmartMQConsumer;
import cn.itcourage.smartmq.adapter.core.spi.SmartMQProducer;
import cn.itcourage.smartmq.broker.config.SmartMQConfig;
import cn.itcourage.smartmq.broker.support.ServiceState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * 封装适配器创建工具类
 */
public class SmartMQAdapter {

    private final static Logger logger = LoggerFactory.getLogger(SmartMQAdapter.class);

    private static final String CONNECTOR_SPI_DIR = "/plugin";

    private static final String CONNECTOR_STANDBY_SPI_DIR = "/smart-mq-broker/plugin";

    //适配器状态
    private ServiceState serviceState = ServiceState.CREATE_JUST;

    private SmartMQConfig smartMQConfig;

    public SmartMQAdapter(SmartMQConfig smartMQConfig) {
        this.smartMQConfig = smartMQConfig;
    }

    public SmartMQConsumer createAndGetMQConsumerInstance() {
        ExtensionLoader<SmartMQConsumer> loader = ExtensionLoader.getExtensionLoader(SmartMQConsumer.class);
        SmartMQConsumer smartMQConsumer = loader.getExtension(
                smartMQConfig.getConsumer().getType(),
                CONNECTOR_SPI_DIR,
                CONNECTOR_STANDBY_SPI_DIR
        );
        if (smartMQConsumer != null) {
            Properties properties = new Properties();
            properties.putAll(smartMQConfig.getProps());
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader(smartMQConsumer.getClass().getClassLoader());
            smartMQConsumer.init(
                    properties,
                    smartMQConfig.getConsumer().getTopic(),
                    smartMQConfig.getConsumer().getGroupName()
            );
            smartMQConsumer.start();
            Thread.currentThread().setContextClassLoader(cl);
        }
        return smartMQConsumer;
    }

    public SmartMQProducer createAndGetMQProducerInstance() {
        ExtensionLoader<SmartMQProducer> loader = ExtensionLoader.getExtensionLoader(SmartMQProducer.class);
        SmartMQProducer smartMQProducer = loader.getExtension(
                smartMQConfig.getConsumer().getType(),
                CONNECTOR_SPI_DIR,
                CONNECTOR_STANDBY_SPI_DIR
        );
        if (smartMQProducer != null) {
            Properties properties = new Properties();
            properties.putAll(smartMQConfig.getProps());
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader(smartMQProducer.getClass().getClassLoader());
            smartMQProducer.init(
                    properties
            );
            smartMQProducer.start();
            Thread.currentThread().setContextClassLoader(cl);
        }
        return smartMQProducer;
    }

    public void shutdown() {
        this.serviceState = ServiceState.SHUTDOWN_ALREADY;
    }

    public ServiceState getAdapterState() {
        return this.serviceState;
    }

}
