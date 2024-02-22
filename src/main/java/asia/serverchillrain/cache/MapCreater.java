package asia.serverchillrain.cache;

import asia.serverchillrain.cache.core.AutoExpiredMap;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
}
