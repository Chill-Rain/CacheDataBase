package asia.serverchillrain.cache.dataline;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * &#064;auther  2024 01 27
 * 数据载体
 */
@Data
@NoArgsConstructor
public class MemoryData  implements Serializable {
    @Serial
    private static final long serialVersionUID = 4262229809827907727L;
    private Object data;
    private Integer length;
    private Long createTime = System.currentTimeMillis();
    private Boolean needExpired = false;
    private Boolean isDelete = false;
    private Long expiredTime = 0L;

    public MemoryData(Object data) {
        this.data = data;
    }
    public void expired(long time) {
        needExpired = true;
        expiredTime = createTime + time;
    }
}
