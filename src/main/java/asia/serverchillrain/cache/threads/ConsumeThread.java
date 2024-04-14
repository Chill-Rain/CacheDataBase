package asia.serverchillrain.cache.threads;

import asia.serverchillrain.cache.dataline.MemoryData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.BlockingQueue;

/**
 * &#064;auther  2024 02 21
 * 检查线程
 */

public class ConsumeThread  extends Thread{
    private static final Logger logger = LoggerFactory.getLogger(ConsumeThread.class);
    private final BlockingQueue<String> queue;
    private final Map<String, MemoryData> memoryDataBase;
    private boolean flag;

    public ConsumeThread(BlockingQueue<String> queue, Map<String, MemoryData> memoryDataBase, boolean flag) {
        this.queue = queue;
        this.memoryDataBase = memoryDataBase;
        this.flag = flag;
    }
    @Override
    public void run() {
        super.run();
        try{
            while(flag){
                for (String key : queue) {
                    long now = System.currentTimeMillis();
                    MemoryData data = memoryDataBase.get(key);
                    if (data != null && data.getExpiredTime() != 0L
                            && data.getExpiredTime() - now <= 0
                            && !data.getIsDelete()) {
                        data.setIsDelete(true);
                        logger.info("annotated data--->" + memoryDataBase.get(key));
                    }
                }
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
