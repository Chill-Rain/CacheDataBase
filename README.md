一个简单的缓存件，提供了一个带有过期删除功能的线程安全的Map，拥有的功能：
1.实例化：每一步操作都会生成当前Map的快照(data.mp),使用fastJson2,支持Native image
2.自动过期与定时删除：内部启动三个线程分别用于检查，标记和删除实现过期功能
使用：

```java
import asia.serverchillrain.cache.MapCreater;
import asia.serverchillrain.cache.core.AutoExpiredMap;

public static void main(String[] args) {
    AutoExpiredMap cache = MapCreater.newMap();
    //存储数据
    cache.put("key","value");
    cache.save();
    //读取数据
    cache.get("key");
    //移除数据
    cache.remove("key");
    //设置过期
    cache.expired(long time);
}

```
