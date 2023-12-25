package cn.itcourage.smartmq.client.exception;

public class SmartMQClientException extends Exception {

    public SmartMQClientException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public SmartMQClientException(final String message) {
        super(message);
    }


    public SmartMQClientException(final Throwable cause) {
        super(cause);
    }

}
