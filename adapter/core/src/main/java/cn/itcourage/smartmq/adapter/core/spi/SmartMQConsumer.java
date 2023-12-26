package cn.itcourage.smartmq.adapter.core.spi;

import java.util.Properties;

public interface SmartMQConsumer {

    void init(Properties properties, String topic, String groupName);

    void start();

    void stop();

}
