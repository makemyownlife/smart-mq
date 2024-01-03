package cn.itcourage.smartmq.store;

import cn.itcourage.smartmq.store.config.MessageStoreConfig;
import jdk.nashorn.internal.runtime.JSONFunctions;
import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Map;
import java.util.Properties;

/**
 * 使用 RocksDB 做为延迟消息的存储容器
 */
public class RocksDBMessageStore implements MessageStore {
    private final static Logger logger = LoggerFactory.getLogger(RocksDBMessageStore.class);

    private final static String DEFAULT_CHARSET = "UTF-8";

    private static String storeDir = System.getProperty("user.home") + File.separator + "rocksDB";

    private MessageStoreConfig messageStoreConfig;

    private RocksDB rocksDB;

    public RocksDBMessageStore(MessageStoreConfig messageStoreConfig) {
        this.messageStoreConfig = messageStoreConfig;
    }

    @Override
    public synchronized boolean load() {
        boolean result = true;
        try {
            RocksDB.loadLibrary();
            File file = new File(storeDir);
            if (!file.exists()) {
                file.mkdirs();
            }
            Options options = new Options().setCreateIfMissing(true);
            this.rocksDB = RocksDB.open(options, storeDir);
            return true;
        } catch (Exception e) {
            logger.error("load error:", e);
            result = false;
        }
        return result;
    }

    @Override
    public void start() throws Exception {

    }

    @Override
    public PutMessageResult putMessage(final MessageBrokerInner messageBrokerInner) throws Exception {
        // 存储 body 和 properties 两个属性
        // RowKey的设计规则是：timestamp + msgId
        String messageId = messageBrokerInner.getMessageId();
        Map<String, String> properties = messageBrokerInner.getProperties();
        Long delayTime = messageBrokerInner.getDelayTime();
        String uniqueKey = String.valueOf(delayTime) + messageId;
        logger.info("uniqueKey:" + uniqueKey);
        rocksDB.put(uniqueKey.getBytes(DEFAULT_CHARSET), messageBrokerInner.getBody());
        return new PutMessageResult(PutMessageStatus.PUT_OK);
    }

    @Override
    public void shutdown() {

    }

    @Override
    public void destroy() {

    }

}
