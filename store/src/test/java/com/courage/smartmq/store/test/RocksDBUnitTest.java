package com.courage.smartmq.store.test;

import org.junit.Test;
import org.rocksdb.RocksDB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RocksDBUnitTest {

    private static final Logger LOG = LoggerFactory.getLogger(RocksDBUnitTest.class);

    static {
        RocksDB.loadLibrary();
    }

    

}
