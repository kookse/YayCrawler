package yaycrawler.worker.executor.core;

import yaycrawler.worker.model.ScheduleResult;

import java.util.List;

/**
 * @ClassName: ITaskExecutor
 * @Description:        实际和数据库,缓存进行操作的任务执行者.
 *                        获取任务数据
 *                        @see
 * @Author Abi
 * @Email 380285138@qq.com
 * @Date 2017/5/27 17:21
 */
public interface ITaskExecutor<T> {

    /**
     * 执行任务
     * @param info
     * @return
     */
    ScheduleResult execute(T info);

    /**
     * 多个类型类似的任务
     * @param info
     * @return
     */
    ScheduleResult execute(List<T> info);

    /**
     * 执行失败的回调
     * @param info
     */
    default void failureCallback(T info){
        System.out.println("fail times too many.");
    }
}
