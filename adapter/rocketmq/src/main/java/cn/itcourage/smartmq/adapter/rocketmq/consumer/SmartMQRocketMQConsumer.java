package cn.itcourage.smartmq.adapter.rocketmq.consumer;

import cn.itcourage.smartmq.adapter.core.spi.SPI;
import cn.itcourage.smartmq.adapter.core.spi.SmartMQConsumer;
import cn.itcourage.smartmq.adapter.rocketmq.config.RocketMQConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

@SPI(value = "rocketmq")
public class SmartMQRocketMQConsumer implements SmartMQConsumer {

    private final static Logger logger = LoggerFactory.getLogger(SmartMQRocketMQConsumer.class);

    private String                                             nameServer;

    private String                                             topic;

    private String                                             groupName;

    private Integer                                            batchSize;

    private DefaultMQPushConsumer rocketMQConsumer;

    @Override
    public void init(Properties properties, String topic, String groupName) {
        this.nameServer = properties.getProperty(RocketMQConstants.ROCKETMQ_NAMESRV_ADDR);
        this.topic = topic;
        this.groupName = groupName;
        String batchSize = properties.getProperty(RocketMQConstants.ROCKETMQ_BATCH_SIZE);
        if (StringUtils.isNotEmpty(batchSize)) {
            this.batchSize = Integer.parseInt(batchSize);
        }
    }

    @Override
    public void start() {
        
    }

    @Override
    public void stop() {

    }

}
