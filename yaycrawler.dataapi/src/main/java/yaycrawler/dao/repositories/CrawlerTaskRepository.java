package yaycrawler.dao.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import yaycrawler.dao.domain.CrawlerTask;

/**
 * Created by  yuananyun on 2017/3/24.
 */
@Repository
public interface CrawlerTaskRepository extends PagingAndSortingRepository<CrawlerTask, String> {


    /**
     * 将任务修改为运行中
     *
     * @param code
     * @param workerId
     */
    @Modifying(clearAutomatically = true)
    @Query("update CrawlerTask set status =:status,workerId=:workerId,startedTime=current_timestamp where code =:code")
    void updateTaskToRunningStatus(@Param("code") String code, @Param("workerId") String workerId,@Param("status")Integer status);


    /**
     * 将任务修改为失败状态
     *
     * @param code
     */
    @Modifying(clearAutomatically = true)
    @Query("update CrawlerTask set status =:status,message=:message,completedTime=current_timestamp where code =:code")
    void updateTaskToFailureStatus(@Param("code") String code, @Param("message") String message,@Param("status")Integer status);

    /**
     * 将任务修改为成功状态
     *
     * @param code
     */
    @Modifying(clearAutomatically = true)
    @Query("update CrawlerTask set status =:status,completedTime=current_timestamp where code =:code")
    void updateTaskToSuccessStatus(@Param("code") String code,@Param("status")Integer status);

    /**
     * 将任务修改为超时状态
     *
     * @param code
     */
    @Modifying(clearAutomatically = true)
    @Query(value = "update CrawlerTask set status =:status where code =:code")
    void updateTaskToTimeoutStatus(@Param("code") String code,@Param("status")Integer status);

    /**
     * 刷新超时队列（把超时的运行中队列任务重新加入待执行队列）
     *
     * @param timeout
     */
    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE crawler_task set status = :status where status = :oldStatus and (extract(epoch from current_timestamp(0)) - extract(epoch from to_timestamp(to_char(started_time, 'YYYY-MM-DD HH24:MI:SS'),'YYYY-MM-DD HH24:MI:SS'))) > :timeout",nativeQuery = true)
    void refreshBreakedQueue(@Param("status")Integer status,@Param("oldStatus")Integer oldStatus, @Param("timeout") long timeout);

    Page<CrawlerTask> findAllByStatusOrStatus(int status,int oldStatus, Pageable pageable);

    CrawlerTask findFirstByCode(String code);
}
