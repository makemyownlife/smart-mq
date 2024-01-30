package cn.itcourage.smartmq.adapter.core.spi;

import cn.itcourage.smartmq.adapter.core.consumer.CommonMessage;
import cn.itcourage.smartmq.adapter.core.producer.ProducerSendStatus;
import cn.itcourage.smartmq.adapter.core.util.Callback;

import java.util.Properties;

@SPI("rocketmq")
public interface SmartMQProducer {

    void init(Properties properties);

    void start();

    ProducerSendStatus sendMessage(CommonMessage commonMessage);

    void stop();

}
