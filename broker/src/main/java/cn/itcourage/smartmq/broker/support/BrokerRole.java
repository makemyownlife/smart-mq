package cn.itcourage.smartmq.broker.support;

public enum BrokerRole {

    //将消息存储在本地存储，通过需要将消息传输给
    SYNC_MASTER,

    //slave 启动后，争抢不到 master，则只接收master发送过来的存储信息，存储如本地存储 ，但不负责调度
    SLAVE_STORE_ONLY,

    // 监听 master 挂掉之后，状态变更为 SLAVE_SCHEDULE ，
    SLAVE_BEGIN_SCHEDULE;

}
