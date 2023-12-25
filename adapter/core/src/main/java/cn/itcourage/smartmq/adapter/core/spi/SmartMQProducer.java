package cn.itcourage.smartmq.adapter.core.spi;

import java.util.Properties;

public interface SmartMQProducer {

    void init(Properties properties);

    void start();

    void stop();

}
