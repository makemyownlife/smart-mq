package com.courage.smartmq.adapter.core.spi;

import java.util.Properties;

public interface SmartMQConsumer {

    void init(Properties properties, String topic, String group);

    void start();

    void stop();

}