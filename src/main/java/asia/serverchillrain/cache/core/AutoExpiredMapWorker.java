package asia.serverchillrain.cache.core;

import asia.serverchillrain.cache.dataline.MemoryData;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * &#064;auther  2024 01 27
 * Map工作器 负责磁盘IO工作
 */

public class AutoExpiredMapWorker {
    private static final Logger logger = LoggerFactory.getLogger(AutoExpiredMapWorker.class);
    //默认名称
    @Setter
    protected static String path = "data.bin";

    /**
     * 获取库
     * @return 一个库
     * @throws IOException IO异常
     * @throws ClassNotFoundException 反射异常
     */
    public static Map<String, MemoryData> getDataBase() throws IOException, ClassNotFoundException {
        if(new File(path).exists()){
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path));
            Object o = ois.readObject();
            ois.close();
            if(o instanceof String){
                logger.info("get data from--->" + path);
                return readJsonDataBase((String) o);
            }else {
                logger.info("null data map---> now create new map");
                return new ConcurrentHashMap<>();
            }
        }else {
            logger.info("null data map---> now create new map");
            return new ConcurrentHashMap<>();
        }
    }

    /**
     * 序列化库
     * @param database 库
     */
    protected static void saveDataBase(Map<String, MemoryData> database){
        try {
            String databaseJson = JSON.toJSONString(database);
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path));
            oos.writeObject(databaseJson);
            oos.close();
            logger.info("数据已保存于--->" + path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private static ConcurrentHashMap<String, MemoryData> readJsonDataBase(String json){
        return JSON.parseObject(json, new TypeReference<>() {
        });
    }

}
