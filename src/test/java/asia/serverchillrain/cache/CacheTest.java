package asia.serverchillrain.cache;

import asia.serverchillrain.cache.core.AutoExpiredMap;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * &#064;auther  2024 02 21
 */

public class CacheTest {
    public static void main(String[] args) throws IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
//        AutoExpiredMap cache = new AutoExpiredMap();
        System.out.println();
//        Class<AutoExpiredMap> clazz = AutoExpiredMap.class;
//        Constructor<AutoExpiredMap> constructor = clazz.getDeclaredConstructor();
//        constructor.setAccessible(true);
//        AutoExpiredMap map = constructor.newInstance();
        for(int i = 0; i < 100; i ++){
            AutoExpiredMap autoExpiredMap = MapCreater.newMap();
        }
    }
}
