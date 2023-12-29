package cn.itcourage.smartmq.adapter.rocketmq.config;

/**
 * Created by zhangyong on 2023/12/26.
 */
public class RocketMQConstants {


    public static final String ROOT                                  = "rocketmq";

    public static final String ROCKETMQ_NAMESRV_ADDR                 = ROOT + "." + "namesrv.addr";

    public static final String ROCKETMQ_NAMESPACE                    = ROOT + "." + "namespace";

    public static final String ROCKETMQ_BATCH_SIZE                   = ROOT + "." + "batch.size";

    public static final String ROCKETMQ_CONSUME_THREAD_MAX                  = ROOT + "." + "consume.threadMax";

}
