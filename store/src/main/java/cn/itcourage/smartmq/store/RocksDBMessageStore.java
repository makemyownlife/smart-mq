package cn.itcourage.smartmq.store;

import cn.itcourage.smartmq.store.config.MessageStoreConfig;
import com.alibaba.fastjson.JSON;
import org.rocksdb.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 使用 RocksDB 做为延迟消息的存储容器
 */
public class RocksDBMessageStore implements MessageStore {
    private final static Logger logger = LoggerFactory.getLogger(RocksDBMessageStore.class);

    private final static String DEFAULT_CHARSET = "UTF-8";

    private final static String MESSAGE_COLUMN_FAMILY = "messageQueueColumnFamily";

    private static String storeDir = System.getProperty("user.home") + File.separator + "rocksDB";

    private MessageStoreConfig messageStoreConfig;

    private RocksDB rocksDB;

    private ColumnFamilyHandle messageQueueFamilyHandler;

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

            // 创建列族选项
            final ColumnFamilyOptions cfOptions = new ColumnFamilyOptions().optimizeLevelStyleCompaction();
            final List<ColumnFamilyDescriptor> cfDescriptors = Arrays.asList(
                    new ColumnFamilyDescriptor(RocksDB.DEFAULT_COLUMN_FAMILY, cfOptions),
                    new ColumnFamilyDescriptor(MESSAGE_COLUMN_FAMILY.getBytes(), cfOptions));
            List<ColumnFamilyHandle> cfHandles = new ArrayList<>();

            // 创建 rocksDB 实例
            final DBOptions dbOptions = new DBOptions().setCreateIfMissing(true).setCreateMissingColumnFamilies(true);
            this.rocksDB = RocksDB.open(dbOptions, storeDir, cfDescriptors, cfHandles);

            this.messageQueueFamilyHandler = cfHandles.stream().filter(x -> {
                try {
                    return (new String(x.getName())).equals(MESSAGE_COLUMN_FAMILY);
                } catch (RocksDBException e) {
                    return false;
                }
            }).collect(Collectors.toList()).get(0);

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
            // 存储 body 和 properties 两个属性 RowKey的设计规则是：timestamp + msgId
            String messageId = messageBrokerInner.getMessageId();
            Map<String, String> properties = messageBrokerInner.getProperties();
            byte[] body = messageBrokerInner.getBody();
            Long delayTime = messageBrokerInner.getDelayTime();
            String uniqueKey = String.valueOf(delayTime) + messageId;

            //组装写入的字节数组
            byte[] propertiesBytes = JSON.toJSONString(properties).getBytes(DEFAULT_CHARSET);
            ByteBuffer byteBuffer = ByteBuffer.allocate(4 + body.length + 4 + propertiesBytes.length);
            byteBuffer.putInt(body.length);
            byteBuffer.put(body);
            byteBuffer.putInt(propertiesBytes.length);
            byteBuffer.put(propertiesBytes);
            byteBuffer.flip();

            // 写入数据到自定义列族
            WriteOptions writeOptions = new WriteOptions();
            rocksDB.put(messageQueueFamilyHandler, writeOptions, uniqueKey.getBytes(DEFAULT_CHARSET), byteBuffer.array());
            return new PutMessageResult(PutMessageStatus.PUT_OK);
        } catch (Exception e) {
            logger.error("RocksDB putMessage error:", e);
            return new PutMessageResult(PutMessageStatus.PUT_FAIL);
        }
    }

    @Override
    public void doIteratorForTest() {
        try {
            RocksIterator iterator = rocksDB.newIterator(messageQueueFamilyHandler);
            iterator.seekToFirst();

            // Iterate over the key-value pairs
            while (iterator.isValid()) {
                byte[] key = iterator.key();
                byte[] value = iterator.value();

                ByteBuffer byteBuffer = ByteBuffer.allocate(value.length);
                byteBuffer.put(value);
                byteBuffer.flip();

                int bodySize = byteBuffer.getInt();
                byte[] body = new byte[bodySize];
                byteBuffer.get(body);
                int propertiesLength = byteBuffer.getInt();
                byte[] propertiesBytes = new byte[propertiesLength];
                byteBuffer.get(propertiesBytes);
                Map<String, String> properties = JSON.parseObject(new String(propertiesBytes, DEFAULT_CHARSET), HashMap.class);

                // Process the key and value
                logger.info("Key: " + new String(key) + ", Value: [" + new String(body, DEFAULT_CHARSET) + "] properties:" + properties);
                // Move to the next key-value pair
                iterator.next();
            }
            iterator.close();
        } catch (Exception e) {
            logger.error("doIteratorForTest error: ", e);
        }
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
