package cn.itcourage.smartmq.client.exception;

public class SmartMQBrokerException extends Exception {

    public SmartMQBrokerException() {
        super();
    }

    public SmartMQBrokerException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public SmartMQBrokerException(final String message) {
        super(message);
    }

    public SmartMQBrokerException(final Throwable cause) {
        super(cause);

    }

}
