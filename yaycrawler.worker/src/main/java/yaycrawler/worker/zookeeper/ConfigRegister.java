package yaycrawler.worker.zookeeper;

import com.taobao.pamirs.schedule.strategy.ScheduleStrategy;
import com.taobao.pamirs.schedule.strategy.TBScheduleManagerFactory;
import com.taobao.pamirs.schedule.taskmanager.ScheduleTaskType;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import yaycrawler.common.utils.IpUtils;
import yaycrawler.worker.exception.InitConfigException;
import yaycrawler.worker.mapper.TaskTypeInfoMapper;
import yaycrawler.worker.model.persistence.ScheduleStrategyInfo;
import yaycrawler.worker.model.persistence.TaskTypeInfo;

/**
 * @ClassName: ConfigRegister
 * @Description: 配置文件注册
 * @Author Abi
 * @Email 380285138@qq.com
 * @Date 2017/6/2 15:19
 */
@Service
public class ConfigRegister {
    @Autowired
    private TBScheduleManagerFactory scheduleManagerFactory;
    @Value("job.zkConfig.rootPath")
    private String zkRootPath;

    @Autowired
    private TaskTypeInfoMapper taskTypeInfoMapper;

    private static final Logger logger = LoggerFactory.getLogger(ConfigRegister.class);
    private static final String SCHEDULE_STRATEGY_SUFFIX = "-Strategy";

    //表示基于redis的分布式锁
//    private RedisDistributionLock redisLock;

    public ConfigRegister(@Autowired StringRedisTemplate redisTemplate) {
//        redisLock = new RedisLockImpl(redisTemplate, 10 * 1000);
    }

    /**
     * 组册任务类型,默认删除旧的任务数据
     *
     * @param typeInfo
     */
    public void registerTaskType(TaskTypeInfo typeInfo) {
        Boolean deleteOldTask = Boolean.TRUE;
        this.registerTaskType(typeInfo, deleteOldTask);
    }

    /**
     * 注册调度策略,默认删除旧的调度策略数据
     *
     * @param strategyInfo
     */
    public void registerScheduleStrategy(ScheduleStrategyInfo strategyInfo) {
        Boolean deleteOldStrategy = Boolean.TRUE;
        this.registerScheduleStrategy(strategyInfo, deleteOldStrategy);
    }

    /**
     * 注册任务类型
     *
     * @param typeInfo
     * @param updateTask
     */
    public void registerTaskType(TaskTypeInfo typeInfo, Boolean updateTask) {

        String taskName = typeInfo.getBaseTaskType();

        if (StringUtils.isEmpty(taskName)) {
            logger.error("the task type name could can be empty!");
            throw new InitConfigException("the task type name could can be empty!");
        }
        String taskItemsString = typeInfo.getTaskItemsString();
        if (!StringUtils.isEmpty(taskItemsString)) {
            typeInfo.setTaskItems(TaskTypeInfo.splitTaskItem(taskItemsString));
        }
//        synchronized (redisLock) {
        //分布式锁去注册任务类型
        String zookeeperLockName = "zookeeperLock:" + taskName;
        //加锁时间
        Long lockTime;
        String threadName = Thread.currentThread().getName() + "@" + IpUtils.getLocalHost();
        if (loadTaskTypeBaseInfo(taskName)) {
//                if ((lockTime = redisLock.lock(zookeeperLockName, threadName)) > 0) {
            try {
                if (updateTask) {
                    typeInfo.setSts(ScheduleTaskType.STS_RESUME);
                    scheduleManagerFactory.getScheduleDataManager().updateBaseTaskType(typeInfo);
//                            scheduleManagerFactory.getScheduleDataManager().resumeAllServer(taskName);
//                            scheduleManagerFactory.getScheduleDataManager().updateReloadTaskItemFlag(taskName);
                    logger.info("update task type :{} success.", taskName);
                }

            } catch (Exception e) {
                logger.error("update task type :{} failure.", taskName);
                e.printStackTrace();
            }
//                    redisLock.unlock(zookeeperLockName, lockTime, threadName);
//                }
        } else {
//                if ((lockTime = redisLock.lock(zookeeperLockName, threadName)) > 0) {
            try {
                scheduleManagerFactory.getScheduleDataManager().createBaseTaskType(typeInfo);
                logger.info("create task type :{} success.", taskName);
            } catch (Exception e) {
                logger.error("create task type :{} failure.", taskName);
                e.printStackTrace();
            }
//                    redisLock.unlock(zookeeperLockName, lockTime, threadName);
//                }
        }

//        }

    }

    private boolean loadTaskTypeBaseInfo(String taskName) {
        try {
            ScheduleTaskType scheduleTaskType = scheduleManagerFactory.getScheduleDataManager().loadTaskTypeBaseInfo(taskName);
            logger.info("get task type :{} success.", taskName);
            if (scheduleTaskType != null)
                return Boolean.TRUE;
            else
                return Boolean.FALSE;
        } catch (Exception e) {
            logger.error("get task type :{} failure.", taskName);
            e.printStackTrace();
            return Boolean.FALSE;
        }
    }

    private boolean loadStrategy(String strategyName) {
        try {
            ScheduleStrategy scheduleStrategy = scheduleManagerFactory.getScheduleStrategyManager().loadStrategy(strategyName);
            logger.info("get schedule strategy :{} success.", strategyName);
            if (scheduleStrategy != null)
                return Boolean.TRUE;
            else
                return Boolean.FALSE;
        } catch (Exception e) {
            logger.error("get schedule strategy :{} failure.", strategyName);
            e.printStackTrace();
            return Boolean.FALSE;
        }
    }

    /**
     * 注册调度策略
     *
     * @param strategyInfo
     * @param updateStrategy
     */
    public void registerScheduleStrategy(ScheduleStrategyInfo strategyInfo, Boolean updateStrategy) {

        String strategyName = strategyInfo.getStrategyName();
        if (StringUtils.isEmpty(strategyName)) {
            logger.error("the schedule strategy name could can be empty!");
            throw new InitConfigException("the schedule strategy name could can be empty!");
        }
        strategyName =
                strategyName.endsWith(SCHEDULE_STRATEGY_SUFFIX) ? strategyName : strategyName + SCHEDULE_STRATEGY_SUFFIX;
        String ipListString = strategyInfo.getIpListString();
        if (!StringUtils.isEmpty(ipListString)) {
            strategyInfo.setIPList(ipListString.split(","));
        }
//        synchronized (redisLock) {            //分布式锁去注册任务类型
        String zookeeperLockName = "zookeeperLock:" + strategyName;
        //加锁时间
        Long lockTime;
        String threadName = Thread.currentThread().getName() + "@" + IpUtils.getLocalHost();

        if (loadStrategy(strategyName)) {
//                if ((lockTime = redisLock.lock(zookeeperLockName, threadName)) > 0) {
            try {
                if (updateStrategy) {
                    strategyInfo.setSts(ScheduleStrategy.STS_RESUME);
                    scheduleManagerFactory.getScheduleStrategyManager().updateScheduleStrategy(strategyInfo);
//                            scheduleManagerFactory.getScheduleStrategyManager().resume(strategyName);
                    logger.info("update schedule strategy :{} success.", strategyName);
                }
            } catch (Exception e) {
                logger.error("update schedule strategy :{} failure.", strategyName);
                e.printStackTrace();
            }
//                    redisLock.unlock(zookeeperLockName, lockTime, threadName);
//                }
        } else {
//                if ((lockTime = redisLock.lock(zookeeperLockName, threadName)) > 0) {
            try {
                scheduleManagerFactory.getScheduleStrategyManager().createScheduleStrategy(strategyInfo);
                logger.info("create schedule strategy :{} success.", strategyName);
            } catch (Exception e) {
                logger.error("create schedule strategy :{} failure.", strategyName);
                e.printStackTrace();
            }
//                    redisLock.unlock(zookeeperLockName, lockTime, threadName);
//                }
        }

    }

}
