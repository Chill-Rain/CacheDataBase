package asia.serverchillrain.cache.threads;

import asia.serverchillrain.cache.dataline.MemoryData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Map;

/**
 * &#064;auther  2024 02 21
 */

public class DeleteThread extends Thread{
    private static final Logger logger = LoggerFactory.getLogger(DeleteThread.class);
    private final Map<String, MemoryData> memoryDataBase;
    private boolean flag;

    public DeleteThread(Map<String, MemoryData> memoryDataBase, boolean flag) {
        this.memoryDataBase = memoryDataBase;
        this.flag = flag;
    }

    @Override
    public void run() {
        super.run();
        while(flag){
            try {
                Iterator<Map.Entry<String, MemoryData>> iterator = memoryDataBase.entrySet().iterator();
                while (iterator.hasNext()){
                    Map.Entry<String, MemoryData> next = iterator.next();
                    if(next.getValue().getIsDelete()){
                        iterator.remove();
                        logger.info("删除了数据--->" + next.getValue());
                    }
                }
                Thread.sleep(10 * 1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
