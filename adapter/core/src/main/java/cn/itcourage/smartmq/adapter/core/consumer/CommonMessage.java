package cn.itcourage.smartmq.adapter.core.consumer;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 基础消息
 */
public class CommonMessage implements Serializable {

    private static final long serialVersionUID = 2611556444074013268L;

    private String msgId;

    private byte[] body;

    public CommonMessage(String msgId, byte[] body) {
        this.msgId = msgId;
        this.body = body;
    }

    public String getMsgId() {
        return msgId;
    }

    public byte[] getBody() {
        return body;
    }

}
