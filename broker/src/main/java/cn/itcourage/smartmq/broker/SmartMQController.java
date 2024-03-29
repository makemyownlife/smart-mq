package cn.itcourage.smartmq.broker;

import cn.itcourage.smartmq.broker.config.ConfigConstants;
import cn.itcourage.smartmq.broker.config.SmartMQConfig;
import cn.itcourage.smartmq.broker.support.BrokerRole;
import cn.itcourage.smartmq.store.MessageStore;
import cn.itcourage.smartmq.store.RocksDBMessageStore;
import cn.itcourage.smartmq.store.config.MessageStoreConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SmartMQ 控制器
 */
public class SmartMQController {

    private final static Logger logger = LoggerFactory.getLogger(SmartMQController.class);

    public SmartMQController(SmartMQConfig smartMQConfig) {
        this.smartMQConfig = smartMQConfig;
    }

    private MessageStore messageStore;

    private SmartMQAdapter smartMQAdapter;

    private SmartMQConfig smartMQConfig;

    //消息分发器（主服务器消费功能）
    private SmartMQDispatcher smartMQDispatcher;

    //消息调度器（定时发送存储消息到目标Broker集群）
    private SmartMQScheduler smartMQScheduler;

    // 默认初始化是：Master同步模式
    private BrokerRole brokerRole = BrokerRole.MASTER;

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
        // 2. 判断当前Broker的角色
        if (ConfigConstants.STANDALONE_MODE.equals(this.smartMQConfig.getRunmode())) {
            this.brokerRole = BrokerRole.MASTER;
        } else {
            // 通过 zookeeper 来抢占最小节点 判断是否是 Master or Slave
            this.brokerRole = BrokerRole.SLAVE;
        }
        this.smartMQAdapter = new SmartMQAdapter(this.smartMQConfig);
        // 3. 判断是否启动拉取服务 ，只有主服务器才能拉取消息并存储
        if (this.brokerRole != BrokerRole.SLAVE) {
            this.smartMQDispatcher = new SmartMQDispatcher(this);
            this.smartMQDispatcher.start();
            // 4. 启动调度器 ，两种情况：1、主服务器  2、主服务器挂掉，slave 才会启动调度
            this.smartMQScheduler = new SmartMQScheduler(this);
            this.smartMQScheduler.start();
        }
    }

    //============================================================ get 方法  start ============================================================
    public MessageStore getMessageStore() {
        return this.messageStore;
    }

    public SmartMQAdapter getSmartMQAdapter() {
        return smartMQAdapter;
    }

    public SmartMQScheduler getSmartMQScheduler() {
        return smartMQScheduler;
    }

    public BrokerRole getBrokerRole() {
        return brokerRole;
    }

    public SmartMQConfig getSmartMQConfig() {
        return smartMQConfig;
    }

    //============================================================ get 方法  end  ============================================================

    //1.修改服务状态关闭中。
    //2.若是高可用机制，则从zk节点下删除本服务节点。
    //3.关闭适配器消费者服务。
    //4.关闭RPC服务。
    //5.关闭本地存储
    public synchronized void shutdown() throws Exception {
        if (this.smartMQDispatcher != null) {
            this.smartMQDispatcher.shutdown();
        }
        if (this.smartMQScheduler != null) {
            this.smartMQScheduler.shutdown();
        }
        if (this.messageStore != null) {
            this.messageStore.shutdown();
        }
    }

}
