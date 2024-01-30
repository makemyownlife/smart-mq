package cn.itcourage.smartmq.adapter.rocketmq.producer;

import cn.itcourage.smartmq.adapter.core.consumer.CommonMessage;
import cn.itcourage.smartmq.adapter.core.producer.ProducerSendStatus;
import cn.itcourage.smartmq.adapter.core.spi.SPI;
import cn.itcourage.smartmq.adapter.core.spi.SmartMQProducer;
import cn.itcourage.smartmq.adapter.core.util.Callback;
import cn.itcourage.smartmq.adapter.rocketmq.config.RocketMQConstants;
import com.alibaba.fastjson.JSON;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Properties;

@SPI(value = "rocketmq")
public class SmartMQRocketMQProducer implements SmartMQProducer {

    private static final Logger logger = LoggerFactory.getLogger(SmartMQRocketMQProducer.class);

    private static final String MQ_PRODUCER_GROUP = "smartMQProduerGroup";

    private static final String PROPERTIES_TAGS = "TAGS";

    private DefaultMQProducer defaultMQProducer;

    private String nameServer;

    @Override
    public void init(Properties properties) {
        this.nameServer = properties.getProperty(RocketMQConstants.ROCKETMQ_NAMESRV_ADDR);
        this.defaultMQProducer = new DefaultMQProducer(MQ_PRODUCER_GROUP);
        this.defaultMQProducer.setNamesrvAddr(this.nameServer);
    }

    @Override
    public synchronized void start() {
        try {
            this.defaultMQProducer.start();
        } catch (Exception e) {
            logger.error("SmartMQRocketMQProducer start error:", e);
        }
    }

    @Override
    public ProducerSendStatus sendMessage(CommonMessage commonMessage) {
        boolean sendSuccess = false;
        try {
            Message message = new Message();
            message.setTopic(commonMessage.getTopic());
            message.setBody(commonMessage.getBody());
            SendResult sendResult = defaultMQProducer.send(message);
            logger.info("sendResult:" + JSON.toJSONString(sendResult));
            if (sendResult != null) {
                if (sendResult.getSendStatus() == SendStatus.SEND_OK) {
                    sendSuccess = true;
                }
            }
        } catch (Exception e) {
            logger.error("sendMessage error: ", e);
        }
        if (sendSuccess) {
            return ProducerSendStatus.SEND_OK;
        } else {
            return ProducerSendStatus.SEND_FAIL.SEND_OK;
        }
    }

    @Override
    public void stop() {
        if (this.defaultMQProducer != null) {
            this.defaultMQProducer.shutdown();
        }
    }

}
