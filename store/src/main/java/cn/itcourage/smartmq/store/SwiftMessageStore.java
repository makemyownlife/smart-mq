package cn.itcourage.smartmq.store;

import cn.itcourage.smartmq.store.config.MessageStoreConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 参考消息队列 QMQ 自定义延迟消息存储
 */
public class SwiftMessageStore implements MessageStore {

    private final static Logger logger = LoggerFactory.getLogger(SwiftMessageStore.class);

    private MessageStoreConfig messageStoreConfig;

    public SwiftMessageStore(MessageStoreConfig messageStoreConfig) {
        this.messageStoreConfig = messageStoreConfig;
    }

    @Override
    public boolean load() {
        return false;
    }

    @Override
    public void start() throws Exception {

    }

    @Override
    public void shutdown() {

    }

    @Override
    public void destroy() {

    }

}
