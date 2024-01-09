package cn.itcourage.smartmq.adapter.rocketmq.producer.test;

import cn.itcourage.smartmq.adapter.core.util.SmartMQAdapterConstants;
import com.alibaba.fastjson.JSON;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.junit.Test;

import java.io.UnsupportedEncodingException;

public class RocketMQProducerUnitTest {

    public static final String NAMESRVADDR = "192.168.1.9:9876";

    public static final String TOPIC = "mytest";

    @Test
    public void testSendDelayMessage() throws MQClientException, UnsupportedEncodingException, MQBrokerException, RemotingException, InterruptedException {
        String tag = "myTag";
        DefaultMQProducer producer = new DefaultMQProducer("testGroup");
        producer.setNamesrvAddr(NAMESRVADDR);
        producer.start();
        for (int i = 0; i < 10; i++) {
            Message msg = new Message(TOPIC, tag, ("Hello RocketMQ 我的你的" + i).getBytes(RemotingHelper.DEFAULT_CHARSET));
            msg.putUserProperty(SmartMQAdapterConstants.DEST_TOPIC, "order-topic");
            msg.putUserProperty(SmartMQAdapterConstants.DELAY_TIME, String.valueOf(System.currentTimeMillis() + 1000 * 30L));
            SendResult sendResult = producer.send(msg);
            System.out.println(JSON.toJSONString(sendResult));
        }
        producer.shutdown();
    }

}
