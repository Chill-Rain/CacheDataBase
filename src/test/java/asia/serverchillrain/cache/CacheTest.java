package asia.serverchillrain.cache;

import asia.serverchillrain.cache.MapCreater;
import asia.serverchillrain.cache.core.AutoExpiredMap;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * &#064;auther  2024 02 21
 */

public class CacheTest {
    public static void main(String[] args) throws IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        AutoExpiredMap cache = MapCreater.newMap(true,"data.114514");
        cache.put("1", "1");
        cache.save();

    }
}
