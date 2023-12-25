package com.courage.smartmq.adapter.core.spi;

import java.util.Properties;

public interface SmartMQProducer {

    void init(Properties properties);

    void start();

    void stop();

}
