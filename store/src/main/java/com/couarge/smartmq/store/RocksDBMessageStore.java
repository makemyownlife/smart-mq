package com.couarge.smartmq.store;

import com.couarge.smartmq.store.config.MessageStoreConfig;
import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * 使用 RocksDB 做为延迟消息的存储容器
 */
public class RocksDBMessageStore implements MessageStore {

    private final static Logger logger = LoggerFactory.getLogger(RocksDBMessageStore.class);

    private static String storeDir = System.getProperty("user.home") + File.separator + "rocksDB";

    private MessageStoreConfig messageStoreConfig;

    private RocksDB rocksDB;

    public RocksDBMessageStore(MessageStoreConfig messageStoreConfig) {
        this.messageStoreConfig = messageStoreConfig;
    }

    @Override
    public synchronized boolean load() {
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
            return false;
        }
    }

    @Override
    public void start() throws Exception {
        this.rocksDB.put("hello".getBytes(), "courqge".getBytes());
        byte[] valueBytes = this.rocksDB.get("hello".getBytes());
        String value = new String(valueBytes);
        logger.info("value:" + value);
    }

    @Override
    public void shutdown() {

    }

    @Override
    public void destroy() {

    }

}
