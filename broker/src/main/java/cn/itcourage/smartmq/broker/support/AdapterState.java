package cn.itcourage.smartmq.broker.support;

public enum AdapterState {

    /**
     * Service just created,not start
     */
    CREATE_JUST,
    /**
     * Service Running
     */
    RUNNING,
    /**
     * Service standby ,not provide producer and consumer
     */
    STANDBY,
    /**
     * Service shutdown
     */
    SHUTDOWN_ALREADY,
    /**
     * Service Start failure
     */
    START_FAILED;

}
