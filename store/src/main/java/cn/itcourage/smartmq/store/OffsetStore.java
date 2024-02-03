package cn.itcourage.smartmq.store;

import cn.itcourage.smartmq.store.MessageStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 消息偏移量存储
 */
public interface OffsetStore {

    void load();

    void updateOffset(String commitKey);

}
