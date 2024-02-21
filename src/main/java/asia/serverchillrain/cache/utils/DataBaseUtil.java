package asia.serverchillrain.cache.utils;

import asia.serverchillrain.cache.dataline.MemoryData;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * &#064;auther  2024 01 27
 * Jar下的内存处理器
 */

public class DataBaseUtil {
    private static final Logger logger = LoggerFactory.getLogger(DataBaseUtil.class);
    private static final String path = "data.mp";

    /**
     * 获取库
     * @return 一个库
     * @throws IOException IO异常
     * @throws ClassNotFoundException 反射异常
     */
    public static Map<String, MemoryData> getDataBase() throws IOException, ClassNotFoundException {
        if(new File(path).exists()){
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File(path)));
            Object o = ois.readObject();
            ois.close();
            if(o instanceof String){
                logger.info("数据载入于--->" + path);
                return readJsonDataBase((String) o);
            }else {
                logger.info("空内存数据库--->已创建新数据源");
                return new ConcurrentHashMap<>();
            }
        }else {
            logger.info("空内存数据库--->已创建新数据源");
            return new ConcurrentHashMap<>();
        }
    }

    /**
     * 序列化库
     * @param database 库
     */
    public static void saveDataBase(Map<String, MemoryData> database){
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
