package yaycrawler.worker.mapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;
import yaycrawler.dao.datasource.DatabaseType;
import yaycrawler.dao.datasource.MultiDataSource;
import yaycrawler.worker.model.persistence.TaskTypeInfo;

import java.util.List;

/**
 * @ClassName: TaskTypeInfoMapper
 * @Description:
 * @Author Abi
 * @Email 380285138@qq.com
 * @Date 2017/6/1 15:09
 */
@Repository
@MultiDataSource(DatabaseType.second)
public interface TaskTypeInfoMapper {

    /**
     *
     * @param name  根据name查找
     * @return
     */
    @Select("select id as id, base_task_type as baseTaskType, heart_beat_rate as heartBeatRate," +
            " judge_dead_interval as judgeDeadInterval, sleep_time_no_data as sleepTimeNoData," +
            " sleep_time_interval as sleepTimeInterval, fetch_data_number as fetchDataNumber," +
            " execute_number as executeNumber, thread_number as threadNumber, processor_type as processorType," +
            " permit_run_start_time as permitRunStartTime, permit_run_end_time as permitRunEndTime, " +
            " deal_bean_name as dealBeanName, task_parameter as taskParameter, task_items_string as taskItemsString," +
            " own_sign as ownSign, schedule_rate as scheduleRate, task_select_tag as taskSelectTag," +
            " detail_time_to_execute as detailTimeToExecute, detail_task_type as detailTaskType," +
            " need_register_zk as needRegisterZk " +
            " from schedule_task_type_info where base_task_type = #{name}")
    TaskTypeInfo selectOneByName(@Param("name") String name);

    /**
     * 根据执行的beanName和具体的任务类型查找
     * @param beanName
     * @param detailTaskType
     * @return
     */
    @Select("select id as id, base_task_type as baseTaskType, heart_beat_rate as heartBeatRate," +
            " judge_dead_interval as judgeDeadInterval, sleep_time_no_data as sleepTimeNoData," +
            " sleep_time_interval as sleepTimeInterval, fetch_data_number as fetchDataNumber," +
            " execute_number as executeNumber, thread_number as threadNumber, processor_type as processorType," +
            " permit_run_start_time as permitRunStartTime, permit_run_end_time as permitRunEndTime, " +
            " deal_bean_name as dealBeanName, task_parameter as taskParameter, task_items_string as taskItemsString," +
            " own_sign as ownSign, schedule_rate as scheduleRate, task_select_tag as taskSelectTag," +
            " detail_time_to_execute as detailTimeToExecute, detail_task_type as detailTaskType," +
            " need_register_zk as needRegisterZk " +
            " from schedule_task_type_info " +
            " where deal_bean_name = #{beanName} and detail_task_type = #{detailTaskType}")
    TaskTypeInfo selectOneByBeanNameAndDeatilTaskType(@Param("beanName") String beanName, @Param("detailTaskType") String detailTaskType);


    /**
     * 根据ID查询
     * @param id
     * @return
     */
    @Select("select id as id, base_task_type as baseTaskType, heart_beat_rate as heartBeatRate," +
            " judge_dead_interval as judgeDeadInterval, sleep_time_no_data as sleepTimeNoData," +
            " sleep_time_interval as sleepTimeInterval, fetch_data_number as fetchDataNumber," +
            " execute_number as executeNumber, thread_number as threadNumber, processor_type as processorType," +
            " permit_run_start_time as permitRunStartTime, permit_run_end_time as permitRunEndTime," +
            " deal_bean_name as dealBeanName, task_parameter as taskParameter, task_items_string as taskItemsString," +
            " own_sign as ownSign, schedule_rate as scheduleRate, task_select_tag as taskSelectTag," +
            " detail_time_to_execute as detailTimeToExecute, detail_task_type as detailTaskType," +
            " need_register_zk as needRegisterZk " +
            " from schedule_task_type_info where id = #{id}")
    TaskTypeInfo selectOneById(@Param("id") Integer id);


    /**
     * 查找所有的信息
     * @return
     */
    @Select("select id as id, base_task_type as baseTaskType, heart_beat_rate as heartBeatRate," +
            " judge_dead_interval as judgeDeadInterval, sleep_time_no_data as sleepTimeNoData," +
            " sleep_time_interval as sleepTimeInterval, fetch_data_number as fetchDataNumber," +
            " execute_number as executeNumber, thread_number as threadNumber, processor_type as processorType," +
            " permit_run_start_time as permitRunStartTime, permit_run_end_time as permitRunEndTime," +
            " deal_bean_name as dealBeanName, task_parameter as taskParameter, task_items_string as taskItemsString," +
            " own_sign as ownSign, schedule_rate as scheduleRate, task_select_tag as taskSelectTag," +
            " detail_time_to_execute as detailTimeToExecute, detail_task_type as detailTaskType," +
            " need_register_zk as needRegisterZk " +
            " from schedule_task_type_info")
    List<TaskTypeInfo> selectAll();

    /**
     * 拉取是否要注册到zk的配置信息
     * @param isRegister
     * @return
     */
    @Select("select id as id, base_task_type as baseTaskType, heart_beat_rate as heartBeatRate," +
            " judge_dead_interval as judgeDeadInterval, sleep_time_no_data as sleepTimeNoData," +
            " sleep_time_interval as sleepTimeInterval, fetch_data_number as fetchDataNumber," +
            " execute_number as executeNumber, thread_number as threadNumber, processor_type as processorType," +
            " permit_run_start_time as permitRunStartTime, permit_run_end_time as permitRunEndTime," +
            " deal_bean_name as dealBeanName, task_parameter as taskParameter, task_items_string as taskItemsString," +
            " own_sign as ownSign, schedule_rate as scheduleRate, task_select_tag as taskSelectTag," +
            " detail_time_to_execute as detailTimeToExecute, detail_task_type as detailTaskType," +
            " need_register_zk as needRegisterZk " +
            " from schedule_task_type_info where need_register_zk = #{isRegister}")
    List<TaskTypeInfo> selectInfoByIsRegisterZk(@Param("isRegister") Boolean isRegister);


    @Insert("insert into schedule_task_type_info(base_task_type, heart_beat_rate, judge_dead_interval, sleep_time_no_data," +
            " sleep_time_interval, fetch_data_number, execute_number, thread_number, processor_type, " +
            " permit_run_start_time, permit_run_end_time, deal_bean_name, task_parameter, task_items_string," +
            " own_sign, schedule_rate, task_select_tag, detail_time_to_execute, detail_task_type, need_register_zk) " +
            " values(#{baseTaskType}, #{heartBeatRate}, #{judgeDeadInterval}, #{sleepTimeNoData}," +
            " #{sleepTimeInterval}, #{fetchDataNumber}, #{executeNumber}, #{threadNumber}, #{processorType}, " +
            " #{permitRunStartTime}, #{permitRunEndTime}, #{dealBeanName}, #{taskParameter}, #{taskItemsString}, " +
            " #{ownSign}, #{scheduleRate}, #{taskSelectTag}, #{detailTimeToExecute}, #{detailTaskType}, #{needRegisterZk})")
    Integer insert(TaskTypeInfo info);

    @Select("select id as id, base_task_type as baseTaskType, heart_beat_rate as heartBeatRate," +
            " judge_dead_interval as judgeDeadInterval, sleep_time_no_data as sleepTimeNoData," +
            " sleep_time_interval as sleepTimeInterval, fetch_data_number as fetchDataNumber," +
            " execute_number as executeNumber, thread_number as threadNumber, processor_type as processorType," +
            " permit_run_start_time as permitRunStartTime, permit_run_end_time as permitRunEndTime," +
            " deal_bean_name as dealBeanName, task_parameter as taskParameter, task_items_string as taskItemsString," +
            " own_sign as ownSign, schedule_rate as scheduleRate, task_select_tag as taskSelectTag," +
            " detail_time_to_execute as detailTimeToExecute, detail_task_type as detailTaskType," +
            " need_register_zk as needRegisterZk " +
            " from schedule_task_type_info where detail_task_type = #{detailType}")
    List<TaskTypeInfo> selectTaskTypeInfoByDetailType(@Param("detailType") String detailType);

    @Update("update schedule_task_type_info set task_select_tag = #{selectTag} where id = #{id}")
    Integer updateTaskTag(@Param("id") Integer id, @Param("selectTag") Boolean selectTag);

    @Update("update schedule_task_type_info set task_select_tag = #{selectTag}")
    Integer updateAllTaskTag(@Param("selectTag") Boolean selectTag);

}
