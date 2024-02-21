package asia.serverchillrain.cache;


import asia.serverchillrain.cache.dataline.MemoryData;
import asia.serverchillrain.cache.threads.ConsumeThread;
import asia.serverchillrain.cache.threads.DeleteThread;
import asia.serverchillrain.cache.threads.ProduceThread;
import asia.serverchillrain.cache.utils.DataBaseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * &#064;auther  2024 01 27
 * 缓存库 该部分用于实现一个类似于redis的简单存储库 只提供添加，删除，获取方法
 */
public class CacheDataBase {
    //缓存
    private final Map<String, MemoryData> memoryDataBase = DataBaseUtil.getDataBase();
    //日志记录器
    private final Logger logger = LoggerFactory.getLogger(CacheDataBase.class);
    //标记队列
    private final BlockingQueue<String> queue = new ArrayBlockingQueue<>(100);
    //检查标志
    private final boolean flag = true;
    //存储数据缓存数据体
    private MemoryData data = null;
    //存储数据缓存键
    private String key;
    //并发锁
    private Lock lock = new ReentrantLock();

    public CacheDataBase() throws IOException, ClassNotFoundException {
        new ProduceThread(queue,memoryDataBase,flag).start();
        logger.info("检查线程已启动！");
        new ConsumeThread(queue, memoryDataBase, flag).start();
        logger.info("标记删除线程已启动！");
        new DeleteThread(memoryDataBase, flag).start();
        logger.info("删除线程已启动！");
        logger.info("内存数据管理器已创建！");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            DataBaseUtil.saveDataBase(memoryDataBase);
        }));
    }

    /**
     * 设置过期
     * @param time 时间
     */
    public void expired(long time){
        data.expired(time);
    }

    /**
     * 保存数据
     */
    public void save(){
        memoryDataBase.put(key, data);
        logger.info("保存了数据--->" + key + "-" + data.getData());
        DataBaseUtil.saveDataBase(memoryDataBase);
        key = null;
        data = null;
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
    public String put(String key, String data) {
        lock.lock();
        MemoryData old_data = memoryDataBase.get(key);
        this.key = key;
        this.data = new MemoryData(data);
        lock.unlock();
        if(old_data != null){
            logger.info("修改了数据---> " + key + "-" + data);
            return old_data.getData();
        }
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


}
