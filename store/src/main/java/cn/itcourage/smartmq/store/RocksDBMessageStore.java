package cn.itcourage.smartmq.store;

import cn.itcourage.smartmq.store.config.MessageStoreConfig;
import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Map;

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
        logger.info("启动 RocksDB 存储服务");
    }

    @Override
    public PutMessageResult putMessage(final MessageBrokerInner messageBrokerInner) {
        try {
            // 存储 body 和 properties 两个属性
            // RowKey的设计规则是：timestamp + msgId
            String messageId = messageBrokerInner.getMessageId();
            Map<String, String> properties = messageBrokerInner.getProperties();
            Long delayTime = messageBrokerInner.getDelayTime();
            String uniqueKey = String.valueOf(delayTime) + messageId;
            rocksDB.put(uniqueKey.getBytes(DEFAULT_CHARSET), messageBrokerInner.getBody());
            return new PutMessageResult(PutMessageStatus.PUT_OK);
        } catch (Exception e) {
            logger.error("RocksDB putMessage error:", e);
            return new PutMessageResult(PutMessageStatus.PUT_FAIL);
        }
    }

    @Override
    public void doIteratorForTest() {
        RocksIterator iterator = rocksDB.newIterator();
        iterator.seekToFirst();

        // Iterate over the key-value pairs
        while (iterator.isValid()) {
            byte[] key = iterator.key();
            byte[] value = iterator.value();
            // Process the key and value
            logger.info("Key: " + new String(key) + ", Value: " + new String(value));
            // Move to the next key-value pair
            iterator.next();
        }
        iterator.close();
    }

    @Override
    public void shutdown() {
        logger.info("关闭 RocksDB 存储服务");
        if (this.rocksDB != null) {
            this.rocksDB.close();
        }
    }

    @Override
    public void destroy() {


    }

}
