package com.courage.smartmq.broker;

import com.courage.smartmq.store.MessageStore;
import com.courage.smartmq.store.RocksDBMessageStore;
import com.courage.smartmq.store.config.MessageStoreConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SmartMQ控制器
 */
public class SmartMQController {

    private final static Logger logger = LoggerFactory.getLogger(SmartMQController.class);

    private MessageStore messageStore;

    public SmartMQController() {
        initialize();
    }

    private void initialize() {
        // 1. 启动本地存储
        MessageStoreConfig messageStoreConfig = new MessageStoreConfig();
        this.messageStore = new RocksDBMessageStore(messageStoreConfig);
        this.messageStore.load();
    }

    // 1. 启动本地存储。
    // 2. 启动 RPC 服务，支持通过RPC查询保存新的消息。
    // 3. 初始化适配器对象(Kafka/RocketMQ)。
    // 4. 读取两种 standalone 独立运行模式，还是 zookeeper 高可用模式。
    // 5. 若是 standalone 模式，直接启动适配器消费者服务。
    // 6. 若是 zookeeper 高可用模式， 通过zk节点抢占锁 ，抢占成功，则启动消费者服务。
    // 7. 若 master 宕机 ，Slave 角色会尝试抢占M aster 节点 ，抢占成功后，会自动启动消费者服务。
    public void start() throws Exception{
        if (this.messageStore != null) {
            this.messageStore.start();
        }
    }

    //1.修改服务状态关闭中。
    //2.若是高可用机制，则从zk节点下删除本服务节点。
    //3.关闭适配器消费者服务。
    //4.关闭RPC服务。
    //5.关闭本地存储
    public void shutdown() throws Exception {
        if (this.messageStore != null) {
            this.messageStore.shutdown();
        }
    }

}
