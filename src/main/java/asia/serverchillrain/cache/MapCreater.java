package asia.serverchillrain.cache;

import asia.serverchillrain.cache.core.AutoExpiredMap;
import asia.serverchillrain.cache.core.AutoExpiredMapWorker;

import java.io.IOException;

/**
 * &#064;auther  2024 02 22
 * Map创建器
 */
public class MapCreater {
    public static AutoExpiredMap newMap() throws IOException, ClassNotFoundException {
        return new AutoExpiredMap();
    }
    public static AutoExpiredMap newMap(boolean isSerializable) throws IOException, ClassNotFoundException {
        return new AutoExpiredMap(isSerializable);
    }
    public static AutoExpiredMap newMap(boolean isSerializable, String saveName) throws IOException, ClassNotFoundException {
        AutoExpiredMapWorker.setPath(saveName);
        return new AutoExpiredMap(isSerializable);
    }
}
