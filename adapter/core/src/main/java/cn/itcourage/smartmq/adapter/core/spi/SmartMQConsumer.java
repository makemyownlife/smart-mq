package cn.itcourage.smartmq.adapter.core.spi;

import cn.itcourage.smartmq.adapter.core.consumer.CommonMessage;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

@SPI("rocketmq")
public interface SmartMQConsumer {

    void init(Properties properties, String topic, String groupName);

    void start();

    List<CommonMessage> getMessage(Long timeout, TimeUnit unit);

    void rollback();

    void ack();

    void stop();

}
