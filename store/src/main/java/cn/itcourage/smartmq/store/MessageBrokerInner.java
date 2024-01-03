package cn.itcourage.smartmq.store;

import java.util.Map;

public class MessageBrokerInner {

    private String topic;

    private String messageId;

    private byte[] body;

    private Long delayTime;

    private Map<String, String> properties;

    public MessageBrokerInner(String topic, String messageId, byte[] body, Map<String, String> properties, Long delayTime) {
        this.topic = topic;
        this.messageId = messageId;
        this.body = body;
        this.delayTime = delayTime;
        this.properties = properties;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    public Long getDelayTime() {
        return delayTime;
    }

    public void setDelayTime(Long delayTime) {
        this.delayTime = delayTime;
    }

    public String getTopic() {
        return topic;
    }

}
