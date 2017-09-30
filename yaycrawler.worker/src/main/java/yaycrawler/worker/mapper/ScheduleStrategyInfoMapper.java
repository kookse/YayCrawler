package yaycrawler.worker.mapper;


import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import yaycrawler.dao.datasource.DatabaseType;
import yaycrawler.dao.datasource.MultiDataSource;
import yaycrawler.worker.model.persistence.ScheduleStrategyInfo;

import java.util.List;

/**
 * @ClassName: ScheduleStrategyInfoMapper
 * @Description:
 * @Author Abi
 * @Email 380285138@qq.com
 * @Date 2017/6/1 16:10
 */
@Repository
@MultiDataSource(DatabaseType.second)
public interface ScheduleStrategyInfoMapper {

    /**
     * 根据任务名和策略名查找数据
     * @return
     */
    @Select("select distinct(id as id, strategy_name as strategyName, ip_list_string as ipListString," +
            " num_of_single_server as numOfSingleServer, assign_num as assignNum, task_name as taskName," +
            " task_type_id as taskTypeId) " +
            " from schedule_strategy_info " +
            " where strategy_name = #{strategyName} and task_name = #{taskName}")
    ScheduleStrategyInfo selectOneByTaskTypeName(
            @Param("taskName") String taskName, @Param("strategyName") String strategyName);


    /**
     * 根据任务类型ID查找策略
     * @return
     */
    @Select("select id as id, strategy_name as strategyName, ip_list_string as ipListString," +
            " num_of_single_server as numOfSingleServer, assign_num as assignNum, task_name as taskName," +
            " task_type_id as taskTypeId " +
            " from schedule_strategy_info where task_type_id = #{taskTypeId}")
    ScheduleStrategyInfo selectOneByTaskTypeId(@Param("taskTypeId") Integer taskTypeId);

    /**
     * 查询所有的数据
     * @return
     */
    @Select("select id as id, strategy_name as strategyName, ip_list_string as ipListString," +
            " num_of_single_server as numOfSingleServer, assign_num as assignNum, task_name as taskName, " +
            " task_type_id as taskTypeId " +
            " from schedule_strategy_info")
    List<ScheduleStrategyInfo> selectAll();

    @Insert("insert into schedule_strategy_info(strategy_name, ip_list_string, num_of_single_server," +
            " assign_num, task_name, task_type_id) values(#{strategyName}, #{ipListString}, #{numOfSingleServer}, " +
            " #{assignNum}, #{taskName}, #{taskTypeId})")
    Integer insert(ScheduleStrategyInfo info);
}
