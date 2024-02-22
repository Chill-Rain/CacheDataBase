package asia.serverchillrain.cache.threads;

import asia.serverchillrain.cache.dataline.MemoryData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

/**
 * &#064;auther  2024 02 21
 */

public class ProduceThread extends Thread{
    private static final Logger logger = LoggerFactory.getLogger(ProduceThread.class);
    private final BlockingQueue<String> queue;
    private final Map<String, MemoryData> memoryDataBase;
    private boolean flag;

    public ProduceThread(BlockingQueue<String> queue, Map<String, MemoryData> memoryDataBase, boolean flag) {
        this.queue = queue;
        this.memoryDataBase = memoryDataBase;
        this.flag = flag;
    }
    @Override
    public void run() {
        super.run();
        try{
            while(flag){
                //读取库容量大小，并动态确定要标记的数量
                int size = memoryDataBase.size();
                //动态确定淘汰数量
                int checkCount = 5;
                int count = (size / checkCount) < checkCount ? 0 : (size / checkCount);
                Random random = new Random();
                if(!memoryDataBase.isEmpty()){
                    for(int i = 0; i < count; i ++){
                        int index = random.nextInt(size);
                        Set<String> keys = memoryDataBase.keySet();
                        String[] array = keys.toArray(new String[0]);
                        String key = array[index];
                        if(!queue.contains(key) && memoryDataBase.get(key).getNeedExpired()){
                            queue.put(key);
                        }
                    }
                }
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
