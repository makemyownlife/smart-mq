package cn.itcourage.smartmq.broker.support;

import cn.itcourage.smartmq.adapter.core.spi.ExtensionLoader;
import cn.itcourage.smartmq.adapter.core.spi.SmartMQConsumer;
import cn.itcourage.smartmq.adapter.core.spi.SmartMQProducer;
import cn.itcourage.smartmq.broker.SmartMQConfig;

/**
 * 适配器引擎(初始化，启动，关闭适配器)
 */
public class AdapterEngine {

    private static final String CONNECTOR_SPI_DIR = "/plugin";

    private static final String CONNECTOR_STANDBY_SPI_DIR = "/smart-mq-broker/plugin";

    private SmartMQConfig smartMQConfig;

    private SmartMQProducer currentMQProducer;

    private SmartMQConsumer currentMQConsumer;

    public AdapterEngine(SmartMQConfig smartMQConfig) {
        this.smartMQConfig = smartMQConfig;
    }

    public synchronized void start() {
        this.currentMQProducer = startProducer();
        this.currentMQConsumer = startConsumer();
    }

    private SmartMQProducer startProducer() {
        ExtensionLoader<SmartMQProducer> loader = ExtensionLoader.getExtensionLoader(SmartMQProducer.class);
        SmartMQProducer smartMQProducer = loader.getExtension(
                smartMQConfig.getMqType(),
                CONNECTOR_SPI_DIR,
                CONNECTOR_STANDBY_SPI_DIR
        );
        if (smartMQProducer != null) {
            smartMQProducer.init(smartMQConfig.getProducerProperties());
        }
        return smartMQProducer;
    }

    private SmartMQConsumer startConsumer() {
        ExtensionLoader<SmartMQConsumer> loader = ExtensionLoader.getExtensionLoader(SmartMQConsumer.class);
        SmartMQConsumer smartMQConsumer = loader.getExtension(
                smartMQConfig.getMqType(),
                CONNECTOR_SPI_DIR,
                CONNECTOR_STANDBY_SPI_DIR
        );
        if (smartMQConsumer != null) {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader(smartMQConsumer.getClass().getClassLoader());
            smartMQConsumer.init(
                    smartMQConfig.getConsumerProperties(),
                    smartMQConfig.getTopic(),
                    smartMQConfig.getGroupName()
            );
            smartMQConsumer.start();
            Thread.currentThread().setContextClassLoader(cl);
        }
        return smartMQConsumer;
    }

    public void stopMQConsumer() {
        if (this.currentMQConsumer != null) {
            this.currentMQConsumer.stop();
        }
    }

    public void stopMQProducer() {
        if (this.currentMQProducer != null) {
            this.currentMQProducer.stop();
        }
    }

    public void stop() {
        stopMQConsumer();
        stopMQProducer();
    }

}
