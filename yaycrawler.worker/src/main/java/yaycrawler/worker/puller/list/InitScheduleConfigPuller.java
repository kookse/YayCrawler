package yaycrawler.worker.puller.list;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import yaycrawler.worker.exception.InitConfigException;
import yaycrawler.worker.mapper.ScheduleStrategyInfoMapper;
import yaycrawler.worker.mapper.TaskTypeInfoMapper;
import yaycrawler.worker.model.persistence.ScheduleStrategyInfo;
import yaycrawler.worker.model.persistence.TaskTypeInfo;
import yaycrawler.worker.zookeeper.ConfigRegister;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: InitScheduleConfigPuller
 * @Description:    拉取初始化配置信息
 * @Author Abi
 * @Email 380285138@qq.com
 * @Date 2017/6/1 19:07
 */
@Service("initScheduleConfigPuller")
public class InitScheduleConfigPuller implements TaskListPuller<Map<String, Object>> {

    @Autowired
    private ScheduleStrategyInfoMapper scheduleStrategyInfoMapper;
    @Autowired
    private TaskTypeInfoMapper taskTypeInfoMapper;
    @Autowired
    private ConfigRegister configRegister;

    private static final String SCHEDULE_TASK_TYPE_KEY = "taskType";
    private static final String SCHEDULE_STRATEGY_KEY = "strategy";

    private static final String DEFAULT_STRATEGY_SUFFIX = "-Strategy";
    private static final String DEFAULT_TASK_TYPE_SUFFIX = "$BASE";
    private static final Logger logger = LoggerFactory.getLogger(InitScheduleConfigPuller.class);


    @Override
    public List<Map<String, Object>> selectList(Map<String, Object> conditions, String identification) {

        HashMap<String, Object> scheduleInfo = new HashMap<>(2);
        // 拉取策略信息
        List<ScheduleStrategyInfo> scheduleStrategyInfo = selectAllScheduleStrategyInfo();
        // 拉取任务类型信息
        List<TaskTypeInfo> taskTypeInfo = selectAllTaskTypeInfo(Boolean.TRUE);
        scheduleInfo.put(SCHEDULE_TASK_TYPE_KEY, taskTypeInfo);
        scheduleInfo.put(SCHEDULE_STRATEGY_KEY, scheduleStrategyInfo);

        List<Map<String, Object>> list = new ArrayList<>(1);
        list.add(scheduleInfo);
        return list;
    }

    /**
     * 初始化调度配置信息
     */
    public void initScheduleConfig(){

        List<Map<String, Object>> list = selectList(null, null);
        if (list == null || list.size() < 1) {
            logger.error("initializer schedule config data failure!");
            throw new InitConfigException("initializer schedule config data failure!");
        }
        Map<String, Object> scheduleTaskInfo = list.get(0);
        if (!scheduleTaskInfo.containsKey(SCHEDULE_TASK_TYPE_KEY) || !scheduleTaskInfo.containsKey(SCHEDULE_STRATEGY_KEY)) {
            logger.error("initializer schedule config data failure! schedule strategy or taskType is null");
            throw new InitConfigException("initializer schedule config data failure! schedule strategy or taskType is null");
        }
        List<ScheduleStrategyInfo> scheduleStrategyInfo = (List<ScheduleStrategyInfo>) scheduleTaskInfo.get(SCHEDULE_STRATEGY_KEY);
        List<TaskTypeInfo> taskTypeInfo = (List<TaskTypeInfo>) scheduleTaskInfo.get(SCHEDULE_TASK_TYPE_KEY);
        
        Map<String, ScheduleStrategyInfo> scheduleStrategyInfoMap = new HashMap<>(scheduleStrategyInfo.size());
        scheduleStrategyInfo.forEach(strategyInfo -> {
                    strategyInfo = this.validateScheduleStrategyInfo(strategyInfo);
                    scheduleStrategyInfoMap.put(strategyInfo.getTaskName(), strategyInfo);
                }
        );

        taskTypeInfo.forEach(typeInfo -> {
            String strategyTaskName = typeInfo.getBaseTaskType() + "$" + typeInfo.getOwnSign();
            if (scheduleStrategyInfoMap.containsKey(strategyTaskName)) {
                registerConfigToZookeeper(scheduleStrategyInfoMap.get(strategyTaskName), typeInfo);
            }
        });
    }


    private void registerConfigToZookeeper(ScheduleStrategyInfo strategyInfo, TaskTypeInfo taskTypeInfo){

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        configRegister.registerTaskType(taskTypeInfo,taskTypeInfo.getTaskSelectTag());
        configRegister.registerScheduleStrategy(strategyInfo,taskTypeInfo.getTaskSelectTag());
        taskTypeInfoMapper.updateTaskTag(taskTypeInfo.getId(),Boolean.FALSE);
    }


    private void registerConfigToQuartz(){


    }


    /**
     * 获取所有的ScheduleStrategyInfo信息
     * @return
     */
    private List<ScheduleStrategyInfo> selectAllScheduleStrategyInfo(){
        return scheduleStrategyInfoMapper.selectAll();
    }

    /**
     * 获取所有的需要注册到ZK的TaskTypeInfo信息
     * @return
     */
    private List<TaskTypeInfo> selectAllTaskTypeInfo(Boolean isRegister){
        return taskTypeInfoMapper.selectInfoByIsRegisterZk(isRegister);
    }

    /**
     * 校验策略数据
     * 如果策略名称后缀不是-Strategy,在这里加上
     * 如果任务类型名称不包含$，在这里加上$BASE
     * @param strategyInfo
     * @return
     */
    private ScheduleStrategyInfo validateScheduleStrategyInfo(ScheduleStrategyInfo strategyInfo){

        if (strategyInfo != null){
            if (!StringUtils.isEmpty(strategyInfo.getStrategyName())
                    && !strategyInfo.getStrategyName().endsWith(DEFAULT_STRATEGY_SUFFIX)){
                strategyInfo.setStrategyName(strategyInfo.getStrategyName() + DEFAULT_STRATEGY_SUFFIX);
            }
            if (!StringUtils.isEmpty(strategyInfo.getTaskName())
                    && !strategyInfo.getTaskName().contains("$")){
                strategyInfo.setTaskName(strategyInfo.getTaskName() + DEFAULT_TASK_TYPE_SUFFIX);
            }
        }

        return strategyInfo;
    }
}
