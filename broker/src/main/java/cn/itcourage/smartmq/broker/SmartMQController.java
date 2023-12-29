package cn.itcourage.smartmq.broker;

import cn.itcourage.smartmq.adapter.core.spi.ExtensionLoader;
import cn.itcourage.smartmq.adapter.core.spi.SmartMQConsumer;
import cn.itcourage.smartmq.adapter.core.spi.SmartMQProducer;
import cn.itcourage.smartmq.adapter.rocketmq.config.RocketMQConstants;
import cn.itcourage.smartmq.store.MessageStore;
import cn.itcourage.smartmq.store.RocksDBMessageStore;
import cn.itcourage.smartmq.store.config.MessageStoreConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * SmartMQ控制器
 */
public class SmartMQController {

    private final static Logger logger = LoggerFactory.getLogger(SmartMQController.class);

    private static final String CONNECTOR_SPI_DIR = "/plugin";

    private static final String CONNECTOR_STANDBY_SPI_DIR = "/smart-mq-broker/plugin";

    private MessageStore messageStore;

    private SmartMQConsumer smartMQConsumer;

    private SmartMQProducer smartMQProducer;

    public SmartMQController() {
        initialize();
    }

    private synchronized void initialize() {
        // 1. 初始化本地存储
        MessageStoreConfig messageStoreConfig = new MessageStoreConfig();
        this.messageStore = new RocksDBMessageStore(messageStoreConfig);
        this.messageStore.load();
        // 2. 初始化适配器对象
        ExtensionLoader<SmartMQConsumer> loader = ExtensionLoader.getExtensionLoader(SmartMQConsumer.class);
        this.smartMQConsumer = loader.getExtension("rocketmq", CONNECTOR_SPI_DIR, CONNECTOR_STANDBY_SPI_DIR);
        Properties properties = new Properties();
        properties.put(RocketMQConstants.ROCKETMQ_NAMESRV_ADDR, "192.168.1.9:9876");
        this.smartMQConsumer.init(properties, "mytest", "smartMQConsumer");
    }

    // 1. 启动本地存储。
    // 2. 启动 RPC 服务，支持通过RPC查询保存新的消息。
    // 3. 初始化适配器对象(Kafka/RocketMQ)。
    // 4. 读取两种 standalone 独立运行模式，还是 zookeeper 高可用模式。
    // 5. 若是 standalone 模式，直接启动适配器消费者服务。
    // 6. 若是 zookeeper 高可用模式， 通过zk节点抢占锁 ，抢占成功，则启动消费者服务。
    // 7. 若 master 宕机 ，Slave 角色会尝试抢占M aster 节点 ，抢占成功后，会自动启动消费者服务。
    public void start() throws Exception {
        if (this.messageStore != null) {
            this.messageStore.start();
        }
        if (this.smartMQConsumer != null) {
            this.smartMQConsumer.start();
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
