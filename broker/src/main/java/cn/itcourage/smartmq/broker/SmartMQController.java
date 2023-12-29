package cn.itcourage.smartmq.broker;

import cn.itcourage.smartmq.broker.support.AdapterHolder;
import cn.itcourage.smartmq.store.MessageStore;
import cn.itcourage.smartmq.store.RocksDBMessageStore;
import cn.itcourage.smartmq.store.config.MessageStoreConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SmartMQ控制器
 */
public class SmartMQController {

    private final static Logger logger = LoggerFactory.getLogger(SmartMQController.class);

    public SmartMQController(SmartMQConfig smartMQConfig) {
        this.smartMQConfig = smartMQConfig;
    }

    private MessageStore messageStore;

    private AdapterHolder adapterHolder;

    private SmartMQConfig smartMQConfig;

    // 1. 启动本地存储。
    // 2. 读取两种 standalone 独立运行模式，还是 zookeeper 高可用模式。
    // 3. 若是 standalone 模式，直接启动适配器消费者服务。
    // 4. 若是 zookeeper 高可用模式， 通过zk节点抢占锁 ，抢占成功，则启动适配器对象(Kafka/RocketMQ)。
    // 5. 若 master 宕机 ，Slave 角色会尝试抢占 Master 节点 ，抢占成功后，会自动适配器对象(Kafka/RocketMQ)。
    public synchronized void start() throws Exception {
        // 1. 初始化本地存储
        MessageStoreConfig messageStoreConfig = new MessageStoreConfig();
        this.messageStore = new RocksDBMessageStore(messageStoreConfig);
        this.messageStore.load();
        this.messageStore.start();
        // 3. 初始化适配器对象
        this.adapterHolder = new AdapterHolder(this.smartMQConfig);
    }

    //============================================================ get 方法  start ============================================================
    public MessageStore getMessageStore() {
        return this.messageStore;
    }
    //============================================================ get 方法  end  ============================================================

    //1.修改服务状态关闭中。
    //2.若是高可用机制，则从zk节点下删除本服务节点。
    //3.关闭适配器消费者服务。
    //4.关闭RPC服务。
    //5.关闭本地存储
    public synchronized void shutdown() throws Exception {
        if (this.messageStore != null) {
            this.messageStore.shutdown();
        }
        if (this.adapterHolder != null) {
            this.adapterHolder.stop();
        }
    }

}
