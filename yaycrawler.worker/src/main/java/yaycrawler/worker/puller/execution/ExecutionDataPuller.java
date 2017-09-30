package yaycrawler.worker.puller.execution;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: ExecutionDataPuller
 * @Description:    获取需执行的数据
 * @Author Abi
 * @Email 380285138@qq.com
 * @Date 2017/5/27 18:09
 */
public interface ExecutionDataPuller<T> {

    /**
     * 从数据库或者缓存查询数据
     * @param conditions
     * @param offset
     * @param limit
     * @return
     */
    List<T> select(Map<String, Object> conditions, Long offset, Long limit);
}
