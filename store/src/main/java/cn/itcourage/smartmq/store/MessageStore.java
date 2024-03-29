package cn.itcourage.smartmq.store;

import org.rocksdb.RocksDBException;

import java.util.List;

public interface MessageStore {

    /**
     * Load previously stored messages.
     *
     * @return true if success; false otherwise.
     */
    boolean load();

    /**
     * Launch this message store.
     *
     * @throws Exception if there is any error.
     */
    void start() throws Exception;

    PutMessageResult putMessage(final MessageBrokerInner msg);

    List<MessageBrokerInner> selectMessagesByOffset(String firstKey, int size);

    /**
     * Just For Test
     */
    void doIteratorForTest();

    /**
     * Shutdown this message store.
     */
    void shutdown();

    /**
     * Destroy this message store. Generally, all persistent files should be removed after invocation.
     */
    void destroy();

}
