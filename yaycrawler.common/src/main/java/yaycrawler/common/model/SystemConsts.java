package yaycrawler.common.model;

import java.util.concurrent.TimeUnit;

/**
 * @ClassName: SystemConsts
 * @Description:
 */
public class SystemConsts {

    /**
     * 拿不到订单数据后重试次数
     */
    public static final int DEFAULT_RETRY_TIMES_FOR_NULL_ORDERS = 10;

    public static final String DEFAULT_CHARSET = "UTF-8";
    /**
     * shell executor
     */
    public static final String DEFAULT_SHELL_EXECUTOR = "sh";
    /**
     * 默认失败后重试频率
     */
    public static final long DEFAULT_EXEC_FAILURE_RETRY_FREQUENCY = 5;
    /**
     * 允许失败重试次数
     */
    public static final int DEFAULT_MAX_FAILURE_TIMES = 10;

    /**
     * 允许小智查询失败重试次数
     */
    public static final int XZ_MAX_FAILURE_TIMES = 3;

    /**
     * 执行脚本超时时间
     */
    public static final long DEFAULT_EXEC_SHELL_TIMEOUT = 30 * 60L;

    /**
     * 执行脚本超时时间
     */
    public static final long DEFAULT_EXEC_XZ_TIMEOUT = 60 * 1000L;

    /**
     * 每天执行
     */
    public static final String NEXT_TIME_TO_EXECUTE_DAILY = "1D";

    /**
     * 每个月执行
     */
    public static final String NEXT_TIME_TO_EXECUTE_EACH_MONTH = "1M";

    /**
     * 具体任务类型,调度监控者,就是执行定时调度任务的承载.
     */
    public static final String EXECUTOR_DETAIL_TASK_TYPE_MONITOR = "MONITOR";

    /**
     * 具体任务类型
     */
    public static final String EXECUTOR_DETAIL_TASK_TYPE_SQL = "SQL";

    /**
     * 具体任务类型
     */
    public static final String EXECUTOR_DETAIL_TASK_TYPE_SHELL = "SHELL";

    public static final String EXECUTOR_DETAIL_TASK_TYPE_XZ = "XZ";

    /**
     * 具体任务类型
     */
    public static final String EXECUTOR_DETAIL_TASK_TYPE_COMMONS = "COMMONS";


    /**
     * 回调失败重试速度
     */
    public static final int DEFAULT_CALLBALE_RATE = 20;

    /**
     * 默认的回调执行方法
     */
    public static final String CALLBACK_EXECUTE_METHOD = "invoke";

    /**
     * 线程池相关.
     */
    public static final int DEFAULT_CORE_THREAD_POOL_SIZE = 10;

    public static final int DEFAULT_MAX_THREAD_POOL_SIZE = 20;

    public static final int DEFAULT_KEEP_ALIVE_TIME = 8000;

    public static final int DEFAULT_BLOCK_QUEUE_SIZE = 1024;

    public static final TimeUnit DEFAULT_KEEP_ALIVE_TIMEUTIL = TimeUnit.MILLISECONDS;

    /**
     * 工单允许最大超时时间,一天
     */
    public static final int BATCH_MAX_EXPIRED_TIMES = 1 * 24 * 60;
}
