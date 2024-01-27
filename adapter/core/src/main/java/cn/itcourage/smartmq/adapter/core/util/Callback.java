package cn.itcourage.smartmq.adapter.core.util;

/**
 * MQ 回调类
 */
public interface Callback {

    void commit();

    void rollback();
}
