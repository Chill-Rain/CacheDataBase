package asia.serverchillrain.cache;


import asia.serverchillrain.cache.dataline.MemoryData;
import asia.serverchillrain.cache.utils.DataBaseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * &#064;auther  2024 01 27
 * 缓存库 该部分用于实现一个类似于redis的简单存储库 只提供添加，删除，获取方法
 */
public class CacheDataBase {

    private final Map<String, MemoryData> memoryDataBase = DataBaseUtil.getDataBase();
    private final Logger logger = LoggerFactory.getLogger(CacheDataBase.class);
    private final BlockingQueue<String> queue = new ArrayBlockingQueue<>(100);
    private final boolean flag = true;
    private MemoryData dataline = null;

    public CacheDataBase() throws IOException, ClassNotFoundException {
        logger.info("内存数据管理器已创建！");
        new Thread(() -> {
            try {
                logger.info("检查线程已启动！");
                produce();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
        new Thread(() -> {
            try {
                logger.info("标记删除线程已启动！");
                consume();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
        new Thread(() -> {
            try {
                logger.info("删除线程已启动！");
                delete();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            DataBaseUtil.saveDataBase(memoryDataBase);
        }));
    }

    /**
     * 设置过期
     * @param time 时间
     */
    public void expired(long time){

    }

    /**
     * 从库中读取数据
     * @param key 键
     * @return 值
     */
    public String get(String key) {
        MemoryData data = memoryDataBase.get(key);
        if(data == null || data.getIsDelete()){
            return null;
        }
        logger.info("获取了数据---> " + key + "-" + data.getData());
        return data.getData();
    }

    /**
     * 向库中写入数据
     * @param key 键
     * @param data 值
     */
    public void put(String key, String data) {
        memoryDataBase.put(key, new MemoryData(data));
        logger.info("添加了数据---> " + key + "-" + data);
        DataBaseUtil.saveDataBase(memoryDataBase);
    }

    /**
     * 向库中写入数据
     * @param key 键
     * @param data 值
     */
    public MemoryData put(String key, MemoryData data) {
        if(memoryDataBase.get(key) != null){
            memoryDataBase.put(key, data);
            logger.info("修改了数据---> " + key + "-" + data);
            return data;
        }
        memoryDataBase.put(key, data);
        logger.info("添加了数据---> " + key + "-" + data);
        DataBaseUtil.saveDataBase(memoryDataBase);
        return data;
    }

    /**
     * 从库中移除数据
     * @param key 键
     * @return 被移除数据的值
     */
    public String remove(String key) {
        MemoryData data = memoryDataBase.get(key);
        if(data == null || data.getIsDelete()){
            return null;
        }
        data.setIsDelete(true);
        logger.info("移除了数据---> " + key + "-" + data.getData());
        DataBaseUtil.saveDataBase(memoryDataBase);
        return data.getData();
    }

    /**
     * 抽样器
     */
    private void produce() throws InterruptedException {
        while(flag){
            //读取库容量大小，并动态确定要标记的数量
            int size = memoryDataBase.size();
            //动态确定淘汰数量
            int checkCount = 25;
            int count = (size / checkCount) < checkCount ? 0 : (size / checkCount);
            Random random = new Random();
            memoryDataBase.forEach((key, data) ->{
                int index = random.nextInt(size);
                Set<String> keys = memoryDataBase.keySet();
                String[] array = keys.toArray(new String[0]);
                key = array[index];
                try {
                    if(!queue.contains(key) && memoryDataBase.get(key).getNeedExpired()){
                        queue.put(key);
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
            Thread.sleep(1000);
        }
    }

    /**
     * 标记器
     */
    private void consume() throws InterruptedException {
        while(flag){
            for (String key : queue) {
                long now = System.currentTimeMillis();
                MemoryData data = memoryDataBase.get(key);
                if (data != null && data.getExpiredTime() != 0L && data.getExpiredTime() - now <= 0) {
                    data.setIsDelete(true);
                }
            }
            Thread.sleep(1000);
        }
    }

    /**
     * 定期删除器
     */
    private void delete() throws InterruptedException {
        while(flag){
            Iterator<Map.Entry<String, MemoryData>> iterator = memoryDataBase.entrySet().iterator();
            while (iterator.hasNext()){
                Map.Entry<String, MemoryData> next = iterator.next();
                if(next.getValue().getIsDelete()){
                    iterator.remove();
                    logger.info("删除了数据--->" + next.getValue());
                }
            }
            Thread.sleep(10 * 1000);
        }
    }

}
