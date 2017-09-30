package yaycrawler.worker.puller.list;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: TaskListPuller
 * @Description:       拉取任务列表数据
 * @Author Abi
 * @Email 380285138@qq.com
 * @Date 2017/6/1 9:12
 */
public interface TaskListPuller<T> {

    /**
     * 获取任务
     * @param conditions
     * @param identification
     * @return
     */
    List<T> selectList(Map<String, Object> conditions, String identification);
}
