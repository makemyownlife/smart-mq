package cn.itcourage.smartmq.store;

public class PutMessageResult {

    private PutMessageStatus putMessageStatus;

    public PutMessageResult(PutMessageStatus putMessageStatus) {
        this.putMessageStatus = putMessageStatus;
    }

    public PutMessageStatus getPutMessageStatus() {
        return putMessageStatus;
    }

}
