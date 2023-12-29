package cn.itcourage.smartmq.adapter.core.util;

import cn.itcourage.smartmq.adapter.core.spi.ExtensionLoader;
import cn.itcourage.smartmq.adapter.core.spi.SmartMQConsumer;
import cn.itcourage.smartmq.adapter.core.spi.SmartMQProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * 适配器创建类
 */
public class AdapterBuilder {

    private static final String CONNECTOR_SPI_DIR = "/plugin";

    private static final String CONNECTOR_STANDBY_SPI_DIR = "/smart-mq-broker/plugin";

    private final static Logger logger = LoggerFactory.getLogger(AdapterBuilder.class);

    private Properties properties;

    private String topic;

    private String groupName;

    public AdapterBuilder(Properties properties, String topic, String groupName) {
        this.properties = properties;
        this.topic = topic;
        this.groupName = groupName;
    }

    public SmartMQProducer createMQProducer(Properties properties) {
        return null;
    }

    public SmartMQConsumer createMQConsumer() {
        ExtensionLoader<SmartMQConsumer> loader = ExtensionLoader.getExtensionLoader(SmartMQConsumer.class);
        SmartMQConsumer smartMQConsumer = loader.getExtension("rocketmq", CONNECTOR_SPI_DIR, CONNECTOR_STANDBY_SPI_DIR);
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        Properties properties = new Properties();
        properties.put("", "192.168.1.9:9876");
        Thread.currentThread().setContextClassLoader(smartMQConsumer.getClass().getClassLoader());
        smartMQConsumer.init(properties, "mytest", "smartMQConsumer");
        smartMQConsumer.start();
        Thread.currentThread().setContextClassLoader(cl);
        return smartMQConsumer;
    }

}
