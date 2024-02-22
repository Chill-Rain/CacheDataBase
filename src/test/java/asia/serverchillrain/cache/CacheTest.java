package asia.serverchillrain.cache;

import java.io.IOException;

/**
 * &#064;auther  2024 02 21
 */

public class CacheTest {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        CacheDataBase cache = new CacheDataBase();
        for(int i = 0; i < 100; i ++){
            int finalI = i;
            new Thread(() -> {
                cache.put(String.valueOf(finalI), "测试数据");
                cache.expired(1000 * 5);
                cache.save();
            }).start();
        }
        System.out.println(cache.get("111"));

    }
}
