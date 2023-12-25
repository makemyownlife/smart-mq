package cn.itcourage.smartmq.store.test;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.rocksdb.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RocksDBUnitTest {

    static {
        RocksDB.loadLibrary();
    }

    private static String storeDir = System.getProperty("user.home") + File.separator + "rocksDB";

    @Before
    public void init() throws RocksDBException {
        File file = new File(storeDir);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    @Test
    public void simplePutAndGet() throws RocksDBException {
        Options options = new Options().setCreateIfMissing(true);
        RocksDB rocksDB = RocksDB.open(options, storeDir);
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

    @Test
    public void columnFamilyPutAndGet() throws RocksDBException {
        try (final ColumnFamilyOptions cfOpts = new ColumnFamilyOptions().optimizeLevelStyleCompaction()) {
            String cfName = "mycf";
            // list of column family descriptors, first entry must always be default column family
            final List<ColumnFamilyDescriptor> cfDescriptors = Arrays.asList(new ColumnFamilyDescriptor(RocksDB.DEFAULT_COLUMN_FAMILY, cfOpts), new ColumnFamilyDescriptor(cfName.getBytes(), cfOpts));
            List<ColumnFamilyHandle> cfHandles = new ArrayList<>();
            try (final DBOptions dbOptions = new DBOptions().setCreateIfMissing(true).setCreateMissingColumnFamilies(true); final RocksDB db = RocksDB.open(dbOptions, storeDir, cfDescriptors, cfHandles)) {
                ColumnFamilyHandle cfHandle = cfHandles.stream().filter(x -> {
                    try {
                        return (new String(x.getName())).equals(cfName);
                    } catch (RocksDBException e) {
                        return false;
                    }
                }).collect(Collectors.toList()).get(0);

                try {
                    // put and get from non-default column family
                    db.put(cfHandles.get(1), new WriteOptions(), "key".getBytes(), "value".getBytes());

                    // atomic write
                    try (final WriteBatch wb = new WriteBatch()) {
                        wb.put(cfHandles.get(0), "key2".getBytes(), "value2".getBytes());
                        wb.put(cfHandles.get(1), "key3".getBytes(), "value3".getBytes());
//                        wb.delete(cfHandles.get(1), "key".getBytes());
                        db.write(new WriteOptions(), wb);
                    }

                    System.out.println("newIterator方法获取");
                    //如果不传columnFamilyHandle，则获取默认的列簇，如果传了columnFamilyHandle，则获取指定列簇的
                    RocksIterator iter = db.newIterator(cfHandles.get(1));
                    for (iter.seekToFirst(); iter.isValid(); iter.next()) {
                        System.out.println(String.format("key:%s,value:%s", new String(iter.key()), new String(iter.value())));
                    }

                    // drop column family
                    db.dropColumnFamily(cfHandles.get(1));

                } finally {
                    for (final ColumnFamilyHandle handle : cfHandles) {
                        handle.close();
                    }
                }
            }
        } catch (RocksDBException e) {
            e.printStackTrace();
        }
    }

}
