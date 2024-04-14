package asia.serverchillrain.cache.core;


import asia.serverchillrain.cache.dataline.MemoryData;
import asia.serverchillrain.cache.threads.ConsumeThread;
import asia.serverchillrain.cache.threads.DeleteThread;
import asia.serverchillrain.cache.threads.ProduceThread;
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
 * Map主体
 */
public class AutoExpiredMap {
    //缓存
    private final Map<String, MemoryData> map = AutoExpiredMapWorker.getDataBase();
    //日志记录器
    private final Logger logger = LoggerFactory.getLogger(AutoExpiredMap.class);
    //存储数据缓存数据体
    private MemoryData data = null;
    //存储数据缓存键
    private String key;
    //并发锁
    private final Lock lock = new ReentrantLock();
    //可序列化
    private final boolean isSerializable;

    /**
     * 构造方法只允许通过MapCreater访问！
     */
    public AutoExpiredMap() throws IOException, ClassNotFoundException {
        this(false);
    }
    /**
     * 构造方法只允许通过MapCreater访问！
     */
    public AutoExpiredMap(boolean isSerializable) throws IOException, ClassNotFoundException {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        int j = 0;
        for(int i = stackTrace.length - 1; i >= 0;  i --){
            StackTraceElement stack = stackTrace[i];
            String methodName = stack.getMethodName();
            String className = stack.getClassName();
            if(methodName.equals("<init>") && className.equals(AutoExpiredMap.class.getName())){
                j = i;
                break;
            }
        }
        if(j == 0 ||  stackTrace.length  > j + 1){
            StackTraceElement stackTraceElement = stackTrace[j + 1];
            if (!stackTraceElement.getMethodName().equals("newMap")){
                throw new SecurityException("Unsafe operation!");
            }
        }
        this.isSerializable = isSerializable;
        //标记队列
        BlockingQueue<String> queue = new ArrayBlockingQueue<>(100);
        //检查标志
        boolean flag = true;
        //启动工作线程
        new ProduceThread(queue, map, flag).start();
        logger.info("produce thread start working!");
        new ConsumeThread(queue, map, flag).start();
        logger.info("consume thread start working!");
        new DeleteThread(map, flag).start();
        logger.info("delete thread start working!");
        logger.info("map create success!");
        //运行期钩子在Native状态下不可用，需要有VM
        if(isSerializable){
            //运行期钩子
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                AutoExpiredMapWorker.saveDataBase(map);
            }));
        }
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
        map.put(key, data);
        logger.info("save data--->{}-{}", key, data.getData().toString());
        if(isSerializable){
            AutoExpiredMapWorker.saveDataBase(map);
        }
        key = null;
        data = null;
    }
    /**
     * 从库中读取字符串
     * @param key 键
     * @return 值
     */
    public String getStr(String key) {
        Object data = this.get(key);
        if(data instanceof String){
            return (String) data;
        }
        return null;
    }
    /**
     * 从库中读取整数
     * @param key 键
     * @return 值
     */
    public Integer getInt(String key) {
        Object data = this.get(key);
        if(data instanceof Integer){
            return (Integer) data;
        }
        return null;
    }
    /**
     * 从库中读取布尔
     * @param key 键
     * @return 值
     */
    public Boolean getBoolean(String key) {
        Object data = this.get(key);
        if(data instanceof Boolean){
            return (Boolean) data;
        }
        return null;
    }

    /**
     * 向库中写入数据
     * @param key 键
     * @param data 值
     */
    public Object put(String key, Object data) {
        lock.lock();
        MemoryData old_data = map.get(key);
        this.key = key;
        this.data = new MemoryData(data);
        lock.unlock();
        if(old_data != null){
            logger.info("modify data---> {}-{}", key, data.toString());
            return old_data.getData();
        }
        return data;
    }
    /**
     * 从库中移除数据
     * @param key 键
     * @return 被移除数据的值
     */
    public Object remove(String key) {
        MemoryData data = map.get(key);
        if(data == null || data.getIsDelete()){
            return null;
        }
        data.setIsDelete(true);
        logger.info("remove data---> {}-{}", key, data.getData().toString());
        if(isSerializable){
            AutoExpiredMapWorker.saveDataBase(map);
        }
        return data.getData();
    }

    /**
     * 从库中读取数据
     * @param key 键
     * @return 数据
     */
    public Object get(String key){
        MemoryData data = map.get(key);
        if(data == null || data.getIsDelete()){
            return null;
        }
        logger.info("get data---> {}-{}", key, data.getData().toString());
        return data.getData();
    }
}
