package cn.itcourage.smartmq.adapter.rocketmq.consumer;

import cn.itcourage.smartmq.adapter.core.consumer.CommonMessage;
import cn.itcourage.smartmq.adapter.core.spi.SPI;
import cn.itcourage.smartmq.adapter.core.spi.SmartMQConsumer;
import cn.itcourage.smartmq.adapter.rocketmq.config.RocketMQConstants;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@SPI(value = "rocketmq")
public class SmartMQRocketMQConsumer implements SmartMQConsumer {

    private final static Logger logger = LoggerFactory.getLogger(SmartMQRocketMQConsumer.class);

    private BlockingQueue<ConsumerBatchMessage<CommonMessage>> messageBlockingQueue;

    private long batchProcessTimeout = 60 * 1000;

    private DefaultMQPushConsumer rocketMQConsumer;

    private String nameServer;

    private String topic;

    private String groupName;

    private Integer batchSize;

    private String namespace;

    private String filter = "*";

    @Override
    public void init(Properties properties, String topic, String groupName) {
        this.messageBlockingQueue = new LinkedBlockingQueue<>(1024);
        this.nameServer = properties.getProperty(RocketMQConstants.ROCKETMQ_NAMESRV_ADDR);
        this.topic = topic;
        this.groupName = groupName;
        this.namespace = properties.getProperty(RocketMQConstants.ROCKETMQ_NAMESPACE);
        String batchSize = properties.getProperty(RocketMQConstants.ROCKETMQ_BATCH_SIZE);
        if (StringUtils.isNotEmpty(batchSize)) {
            this.batchSize = Integer.parseInt(batchSize);
        }
    }

    @Override
    public synchronized void start() {
        rocketMQConsumer = new DefaultMQPushConsumer(groupName);
        if (!StringUtils.isEmpty(this.namespace)) {
            rocketMQConsumer.setNamespace(this.namespace);
        }
        if (batchSize != -1) {
            rocketMQConsumer.setConsumeMessageBatchMaxSize(batchSize);
        }
        try {
            rocketMQConsumer.subscribe(this.topic, this.filter);
            rocketMQConsumer.registerMessageListener((MessageListenerOrderly) (messageExts, context) -> {
                context.setAutoCommit(true);
                boolean isSuccess = process(messageExts);
                if (isSuccess) {
                    return ConsumeOrderlyStatus.SUCCESS;
                } else {
                    return ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
                }
            });
            rocketMQConsumer.start();
        } catch (MQClientException ex) {
            logger.error("Start RocketMQ consumer error", ex);
        }
    }

    private boolean process(List<MessageExt> messageExts) {
        List<CommonMessage> messageList = Lists.newArrayList();
        for (MessageExt messageExt : messageExts) {
            byte[] data = messageExt.getBody();
            if (data != null) {
                CommonMessage commonMessage = new CommonMessage(messageExt.getMsgId(), data);
                messageList.add(commonMessage);
            }
        }
        ConsumerBatchMessage<CommonMessage> batchMessage = new ConsumerBatchMessage<>(messageList);
        try {
            messageBlockingQueue.put(batchMessage);
        } catch (InterruptedException e) {
            logger.error("Put message to queue error", e);
            throw new RuntimeException(e);
        }
        boolean isCompleted;
        try {
            isCompleted = batchMessage.waitFinish(batchProcessTimeout);
        } catch (InterruptedException e) {
            logger.error("Interrupted when waiting messages to be finished.", e);
            throw new RuntimeException(e);
        }
        boolean isSuccess = batchMessage.isSuccess();
        return isCompleted && isSuccess;
    }

    @Override
    public void stop() {
        rocketMQConsumer.unsubscribe(topic);
        rocketMQConsumer.shutdown();
    }

}
