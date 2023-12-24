package com.courage.smartmq.store.test;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class RocksDBUnitTest {

    private static final Logger logger = LoggerFactory.getLogger(RocksDBUnitTest.class);

    static {
        RocksDB.loadLibrary();
    }

    private static String storeDir = System.getProperty("user.home") + File.separator + "rocksDB";

    private RocksDB rocksDB;

    @Before
    public void init() throws RocksDBException {
        File file = new File(storeDir);
        if (!file.exists()) {
            file.mkdirs();
        }
        Options options = new Options().setCreateIfMissing(true);
        this.rocksDB = RocksDB.open(options, storeDir);
    }

    @Test
    public void simplePutAndGet() throws RocksDBException {
        // 写入数据
        byte[] key = "example_key".getBytes();
        byte[] value = "example_value".getBytes();
        rocksDB.put(key, value);

        // 读取数据
        byte[] result = rocksDB.get(key);
        if (result != null) {
            System.out.println("Value for key 'example_key': " + new String(result));
        } else {
            System.out.println("Key 'example_key' not found.");
        }

        // 删除数据
        rocksDB.delete(key);

        // 再次读取数据
        result = rocksDB.get(key);
        if (result != null) {
            System.out.println("Value for key 'example_key': " + new String(result));
        } else {
            System.out.println("Key 'example_key' not found after deletion.");
        }

    }

}
