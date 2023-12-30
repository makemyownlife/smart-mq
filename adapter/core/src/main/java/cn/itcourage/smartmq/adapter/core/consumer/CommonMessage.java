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

    private byte[] body;

    public CommonMessage(String messageId, byte[] body) {
        this.messageId = messageId;
        this.body = body;
    }

    public String getMessageId() {
        return messageId;
    }

    public byte[] getBody() {
        return body;
    }

}
