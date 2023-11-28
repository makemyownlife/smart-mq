package com.couarge.smartmq.store;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RocksDBMessageStore implements MessageStore {

    private final static Logger logger = LoggerFactory.getLogger(RocksDBMessageStore.class);

    @Override
    public boolean load() {
        return false;
    }

    @Override
    public void start() throws Exception {

    }

    @Override
    public void shutdown() {

    }

    @Override
    public void destroy() {

    }

}
