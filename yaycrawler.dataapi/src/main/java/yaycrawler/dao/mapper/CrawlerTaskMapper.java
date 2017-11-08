package yaycrawler.dao.mapper;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import yaycrawler.dao.datasource.DatabaseType;
import yaycrawler.dao.datasource.MultiDataSource;
import yaycrawler.dao.domain.CrawlerTask;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @ClassName: QueryBatchInfoMapper
 * @Description:
 * @Author Abi
 * @Email 380285138@qq.com
 * @Date 2017/6/9 11:00
 */

/**
 * @ClassName: QueryBatchInfoMapper
 * @Description:
 * @Author Abi
 * @Email 380285138@qq.com
 * @Date 2017/6/9 11:00
 */
@Repository
@MultiDataSource(DatabaseType.primary)
public interface CrawlerTaskMapper {


    @Select("select id as id, orderid as orderId, usergroupid as userGroupId, status as status, success_rate as successRate," +
            " total as total, start_time as startTime, end_time as endTime, userid as userId, method as method," +
            " serialid as serialId, file as file, file_row_count as fileRowCount, success_count as successCount," +
            " failure_count as failureCount, success_row_count as successRowCount, failure_row_count as failureRowCount," +
            " category as category, iscompleted as isCompleted, batch_source as batchSource, batch_priority as batchPriority," +
            " batch_sts as batchSts, query_table_name as queryTableName" +
            " from s_p_base_order order by batch_priority desc")
    List<CrawlerTask> selectAll();

    /**
     * 根据ID查询数据
     * @param id
     * @return
     */
    @Select("select id as id, orderid as orderId, usergroupid as userGroupId, status as status, success_rate as successRate," +
            " total as total, start_time as startTime, end_time as endTime, userid as userId, method as method," +
            " serialid as serialId, file as file, file_row_count as fileRowCount, success_count as successCount," +
            " failure_count as failureCount, success_row_count as successRowCount, failure_row_count as failureRowCount," +
            " category as category, iscompleted as isCompleted, batch_source as batchSource, batch_priority as batchPriority," +
            " batch_sts as batchSts, query_table_name as queryTableName" +
            " from s_p_base_order where id = #{id}")
    CrawlerTask selectOneById(@Param("id") Integer id);

    /**
     * 更加工单ID查找工单信息
     * @param batchId
     * @return
     */
    @Select("select id as id, orderid as orderId, usergroupid as userGroupId, status as status, success_rate as successRate," +
            " total as total, start_time as startTime, end_time as endTime, userid as userId, method as method," +
            " serialid as serialId, file as file, file_row_count as fileRowCount, success_count as successCount," +
            " failure_count as failureCount, success_row_count as successRowCount, failure_row_count as failureRowCount," +
            " category as category, iscompleted as isCompleted, batch_source as batchSource, batch_priority as batchPriority," +
            " batch_sts as batchSts, query_table_name as queryTableName" +
            " from s_p_base_order where orderid = #{batchId}")
    CrawlerTask selectOneByBatchId(@Param("batchId") String batchId);

    /**
     * 根据单个状态查询数据
     * @param status
     * @return
     */
    @Select("select id as id, orderid as orderId, usergroupid as userGroupId, status as status, success_rate as successRate," +
            " total as total, start_time as startTime, end_time as endTime, userid as userId, method as method," +
            " serialid as serialId, file as file, file_row_count as fileRowCount, success_count as successCount," +
            " failure_count as failureCount, success_row_count as successRowCount, failure_row_count as failureRowCount," +
            " category as category, iscompleted as isCompleted, batch_source as batchSource, batch_priority as batchPriority," +
            " batch_sts as batchSts, query_table_name as queryTableName" +
            " from s_p_base_order where batch_sts = #{status} order by batch_priority desc")
    List<CrawlerTask> selectInfoByStatus(@Param("status") Integer status);

    /**
     * 找出暂停状态和Running状态和就绪状态工单
     * @return
     */
    @Select("select id as id, orderid as orderId, usergroupid as userGroupId, status as status, success_rate as successRate," +
            " total as total, start_time as startTime, end_time as endTime, userid as userId, method as method," +
            " serialid as serialId, file as file, file_row_count as fileRowCount, success_count as successCount," +
            " failure_count as failureCount, success_row_count as successRowCount, failure_row_count as failureRowCount," +
            " category as category, iscompleted as isCompleted, batch_source as batchSource, batch_priority as batchPriority," +
            " batch_sts as batchSts, query_table_name as queryTableName" +
            " from s_p_base_order where batch_sts = 1 or batch_sts = 2 or batch_sts = 3 order by batch_priority desc limit #{conditions.limit}")
    List<CrawlerTask> selectInfoByReadyRunningOrPauseSts(@Param("conditions") Map conditions);

    /**
     * 更新工单状态
     * @param batchId
     * @param msg
     * @param status
     * @return
     */
    @Update("<script>update s_p_base_order" +
            "<set>" +
            "batch_sts = #{status},iscompleted = TRUE,status = #{msg}, " +
            "<if test=\"status == 2\"> start_time=now(),</if>" +
            "<if test=\"status == 5\"> end_time=now(),</if>" +
            "</set>" +
            "where orderid = #{batchId}</script>")
    Integer updateBatchInfoStatus(@Param("batchId") String batchId, @Param("msg") String msg, @Param("status") Integer status);

    /**
     * 更新工单的失败和成功记录数
     * @param batchId
     * @param failure
     * @param success
     * @return
     */
    @Update("<script>" +
            "update s_p_base_order " +
            " <set>" +
            "   <if test=\"failure != null\"> failure_count = #{failure},</if>" +
            "   <if test=\"success != null\"> success_count = #{success},</if>" +
            "   <if test=\"hit != null\"> hit_count = #{hit},</if>" +
            "   <if test=\"queryProcess != null\"> query_process = #{queryProcess},</if>" +
            " </set>" +
            " where orderid = #{batchId}" +
            "</script>")
    Integer updateBatchInfoCountRecord(@Param("batchId") String batchId, @Param("failure") Long failure,
                                       @Param("success") Long success, @Param("hit") Long hit, @Param("queryProcess") Long queryProcess);

    /**
     * 根据ID更新配额扣费
     * @param id
     * @param cost
     * @return
     */
    @Update("update s_p_base_order set cost = #{cost} where id = #{id}")
    Integer updateBatchCostById(@Param("id") Integer id, @Param("cost") Integer cost);

    /**
     * 根据ID更新开始跑数时间
     * @param id
     * @return
     */
    @Update("update s_p_base_order set start_time = now() where id = #{id}")
    Integer updateBatchStartTimeById(@Param("id") Integer id);


    //todo :测试
    @Insert("insert into s_p_base_order(" +
            "orderid, usergroupid, status, total, start_time, userid, method,file,file_row_count,category," +
            "customer_code,batch_sts,batch_source,batch_priority,query_table_name" +
            ") " +
            "values(" +
            "#{orderid}, #{usergroupid}, #{status}, #{total}, now(), #{userid}, #{method},#{fileName},#{fileRowCount},#{category}," +
            "#{customerCode},#{batchStatus},#{source},#{priority},#{queryTableName}" +
            ")")
    int insertOrder(CrawlerTask order);

    @Select("<script>" +
            " select id as id, code as code,completed_time as completedTime,data as data,extend_data as extendData," +
            " message as message, method as method,started_time as startedTime,status as status," +
            " url as url,worker_id as worker_id " +
            " from crawler_task " +
            " where status = #{status} and mod(id,#{taskItemNum}) in " +
            " <foreach collection=\"taskItemIds\" index=\"index\" item=\"taskItemId\" open=\"(\" separator=\",\" close=\")\">" +
            "  #{taskItemId}" +
            " </foreach>" +
            " order by id asc " +
            " <if test=\"offset != null\"> offset #{offset}</if>" +
            " <if test=\"limit != null\"> limit #{limit}</if>" +
            "  " +
            " </script>")
    List<CrawlerTask> selectListForParse(@Param("offset") Long offset, @Param("limit") Long limit, @Param("taskItemNum") Integer taskItemNum, @Param("taskItemIds") List taskItemIds,
                                         @Param("status") Integer status);

    @Update("update s_p_base_order set batch_sts= #{status},status= #{msg} where batch_sts = #{updateStatus} and id >= #{start} and id <= #{end}")
    Integer updateOrderListByID(@Param("updateStatus") Integer updatedStatus,
                                @Param("start") Integer start, @Param("end") Integer end,
                                @Param("status") Integer status, @Param("msg") String msg);

    @Select("<script>select id from s_p_base_order where id in " +
            " <foreach collection=\"ids\" index=\"index\" item=\"id\" open=\"(\" separator=\",\" close=\")\">" +
            "  #{id}" +
            " </foreach>  " +
            "and batch_sts = #{status}</script>")
    List<Integer> selectBatchOrderIdByBatchSts(@Param("status") Integer status, @Param("ids") Set<Integer> ids);

    @Update("<script>update crawler_task" +
            "<set>" +
            "status = #{status},message = #{msg}," +
            "   <if test=\"status == 2\"> started_time = now(),</if>" +
            "   <if test=\"status == 5\"> completed_time = now(),</if>" +
            "</set>" +
            "where id in " +
            " <foreach collection=\"ids\" index=\"index\" item=\"id\" open=\"(\" separator=\",\" close=\")\">" +
            "  #{id}" +
            " </foreach>   " +
            " and status = #{batchStatus}</script>")
    Integer updateCrawlerTaskByStatus(@Param("status") Integer status, @Param("msg") String msg, @Param("batchStatus") Integer batchStatus, @Param("ids") List<?> ids);

    @Update("<script>" +
            "update crawler_task " +
            "<set>" +
            "status=#{status},worker_id=#{workId},message=#{msg}," +
            "   <if test=\"status == 2\"> started_time = now(),</if>" +
            "   <if test=\"status == 5\"> completed_time = now(),</if>" +
            "</set>" +
            "where code=#{code}" +
            "</script>")
    int updateCrawlerTaskStatus(@Param("code") String code, @Param("workId") String workId, @Param("status") int status, @Param("msg") String msg);

    @Update("update crawler_task set status=#{status} where order_id = #{orderId}")
    Integer updateCrawlerTaskByOrderId(@Param("status") Integer status,@Param("orderId") String orderId);

    @Select("select id as id, code as code,completed_time as completedTime,data as data,extend_data as extendData," +
            "message as message, method as method,started_time as startedTime,status as status," +
            "url as url,worker_id as worker_id from crawler_task where code = #{code} limit 1")
    CrawlerTask findFirstByCode(@Param("code") String code);

    @Delete("delete from crawler_task where code = #{code}")
    Integer deleteByCode(@Param("code") String Code);
}
