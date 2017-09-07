package yaycrawler.api.thread;

import yaycrawler.common.model.SystemConsts;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName: DynamicThreadPoolExecutorMaintainer
 * @Description:
 */
public class DynamicThreadPoolExecutorMaintainer {

    private static final ConcurrentHashMap<String, ThreadPoolExecutor> THREAD_POOL_CACHE =
            new ConcurrentHashMap<>(4);


    public static ThreadPoolExecutor get(String executorName){

        if (!THREAD_POOL_CACHE.containsKey(executorName)){
            ThreadPoolExecutor executor = produce();
            THREAD_POOL_CACHE.put(executorName, executor);
            return executor;
        }
        return THREAD_POOL_CACHE.get(executorName);
    }

    public static ThreadPoolExecutor get(String executorName,int coreSize){

        if (!THREAD_POOL_CACHE.containsKey(executorName)){
            ThreadPoolExecutor executor = produce(coreSize);
            THREAD_POOL_CACHE.put(executorName, executor);
            return executor;
        }
        return THREAD_POOL_CACHE.get(executorName);
    }

    /**
     *
     * @param coreSize
     * @param maxPoolSize
     * @param keepAliveTime
     * @param blockQueueSize
     * @return
     */
    private static ThreadPoolExecutor produce(
            int coreSize, int maxPoolSize, int keepAliveTime, TimeUnit timeUnit, int blockQueueSize){

        ThreadPoolExecutor executor =
                new ThreadPoolExecutor(
                        coreSize, maxPoolSize, keepAliveTime, timeUnit,
                        new ArrayBlockingQueue<>(blockQueueSize), new NoFreeSpaceProcesser());
        
        return executor;
    }

    private static ThreadPoolExecutor produce(){

        int coreSize = SystemConsts.DEFAULT_CORE_THREAD_POOL_SIZE;
        int maxPoolSize = SystemConsts.DEFAULT_MAX_THREAD_POOL_SIZE;
        int keepAliveTime = SystemConsts.DEFAULT_KEEP_ALIVE_TIME;
        int blockQueueSize = SystemConsts.DEFAULT_BLOCK_QUEUE_SIZE;
        TimeUnit timeUnit = SystemConsts.DEFAULT_KEEP_ALIVE_TIMEUTIL;
        return produce(coreSize, maxPoolSize, keepAliveTime, timeUnit, blockQueueSize);
    }

    private static ThreadPoolExecutor produce(int coreSize){

        int maxPoolSize = 20000;
        int keepAliveTime = SystemConsts.DEFAULT_KEEP_ALIVE_TIME;
        int blockQueueSize = 20000000;
        TimeUnit timeUnit = SystemConsts.DEFAULT_KEEP_ALIVE_TIMEUTIL;
        return produce(coreSize, maxPoolSize, keepAliveTime, timeUnit, blockQueueSize);
    }
}
