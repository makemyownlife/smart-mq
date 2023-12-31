package cn.itcourage.smartmq.adapter.core.consumer;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 基础消息
 */
public class CommonMessage implements Serializable {

    private static final long serialVersionUID = 2611556444074013268L;

    private String messageId;

    private String topic;

    private byte[] body;

    private Map<String, String> properties;

    public CommonMessage(String topic, String messageId, byte[] body, Map<String, String> properties) {
        this.topic = topic;
        this.messageId = messageId;
        this.body = body;
        this.properties = properties;
    }

    public String getMessageId() {
        return messageId;
    }

    public byte[] getBody() {
        return body;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public String getTopic() {
        return topic;
    }

}
