package cn.itcourage.smartmq.adapter.core.spi;

import java.util.Properties;

@SPI("rocketmq")
public interface SmartMQProducer {

    void init(Properties properties);

    void start();

    void stop();

}
