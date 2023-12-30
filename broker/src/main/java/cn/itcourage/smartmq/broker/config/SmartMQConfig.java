package cn.itcourage.smartmq.broker.config;

import java.util.HashMap;

public class SmartMQConfig {

    private String runmode;

    private String storeType;

    private String storePath;

    private String brokerName;

    private ConsumerConfig consumer;

    private HashMap<String, String> props = new HashMap<String, String>();

    public String getRunmode() {
        return runmode;
    }

    public void setRunmode(String runmode) {
        this.runmode = runmode;
    }

    public String getBrokerName() {
        return brokerName;
    }

    public void setBrokerName(String brokerName) {
        this.brokerName = brokerName;
    }

    public ConsumerConfig getConsumer() {
        return consumer;
    }

    public void setConsumer(ConsumerConfig consumer) {
        this.consumer = consumer;
    }


    public HashMap<String, String> getProps() {
        return props;
    }

    public void setProps(HashMap<String, String> props) {
        this.props = props;
    }

    public String getStoreType() {
        return storeType;
    }

    public void setStoreType(String storeType) {
        this.storeType = storeType;
    }

    public String getStorePath() {
        return storePath;
    }

    public void setStorePath(String storePath) {
        this.storePath = storePath;
    }

}
