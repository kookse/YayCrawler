package yaycrawler.master.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yaycrawler.common.model.CrawlerRequest;
import yaycrawler.common.model.CrawlerResult;
import yaycrawler.common.model.QueueQueryParam;
import yaycrawler.common.model.QueueQueryResult;
import yaycrawler.common.status.CrawlerStatus;
import yaycrawler.dao.domain.CrawlerTask;
import yaycrawler.dao.mapper.CrawlerTaskMapper;
import yaycrawler.dao.repositories.CrawlerTaskRepository;
import yaycrawler.master.service.AbstractICrawlerQueueService;
import yaycrawler.master.service.CrawlerQueueDataType;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 基于mysql的队列服务
 * Created by  yuananyun on 2017/3/24.
 */
@Service(value = "dbQueueService")
@Transactional
public class DBCrawlerQueueService extends AbstractICrawlerQueueService {

    private static final Logger logger  = LoggerFactory.getLogger(DBCrawlerQueueService.class);

    @Autowired
    private CrawlerTaskRepository crawlerTaskRepository;

    @Autowired
    private CrawlerTaskMapper crawlerTaskMapper;

    /**
     * 添加任务到待执行队列
     *
     * @param crawlerRequests  任务
     * @param removeDuplicated 当存在相同任务时是否移除相同任务
     * @return
     */
    @Override
    public boolean pushTasksToWaitingQueue(List<CrawlerRequest> crawlerRequests, boolean removeDuplicated) {
        boolean exists;
        List<CrawlerRequest> requestTmps = transCrawlerQueue(crawlerRequests);
        for (CrawlerRequest crawlerRequest : requestTmps) {
            exists = crawlerTaskRepository.exists(crawlerRequest.getHashCode());
            if (exists && !removeDuplicated) break;
            if (exists)
                crawlerTaskRepository.delete(crawlerRequest.getHashCode());
            CrawlerTask task = CrawlerTask.convertFromCrawlerRequest(crawlerRequest);
            task.setStatus(CrawlerStatus.INIT.getStatus());
            task.setCreatedTime(new Date());
            crawlerTaskRepository.save(task);
        }
        return true;
    }

    /**
     * 从待执行队列中拿取指定数目的任务
     *
     * @param taskCount 任务数目
     * @return
     */
    @Override
    public List<CrawlerRequest> fetchTasksFromWaitingQueue(long taskCount,List<Integer> taskItemIds) {
//        Pageable pageable = new PageRequest(0, (int) taskCount, new Sort(new Sort.Order(Sort.Direction.ASC, "createdTime")));
//        Page<CrawlerTask> pageData = crawlerTaskRepository.findAllByStatus(STATUS_WAITING, pageable);
//        return getCrawlerRequests(pageData.getContent());
        List<CrawlerTask> crawlerTasks = crawlerTaskMapper.selectListForParse(null, taskCount, 10,taskItemIds, CrawlerStatus.INIT.getStatus());
        return getCrawlerRequests(crawlerTasks);
    }


    /**
     * 将任务从待执行移到执行中队列
     *
     * @param workerId        任务在哪个Worker上执行
     * @param crawlerRequests
     */
    @Override
    public boolean moveWaitingTaskToRunningQueue(String workerId, List<CrawlerRequest> crawlerRequests) {
        for (CrawlerRequest request : crawlerRequests) {
            crawlerTaskRepository.updateTaskToRunningStatus(request.getHashCode(), workerId);
        }
        return true;
    }

    /**
     * 当任务执行失败后将任务移到失败队列
     *
     * @param taskCode
     * @param message
     * @return
     */
    @Override
    public boolean moveRunningTaskToFailQueue(String taskCode, String message) {
        crawlerTaskRepository.updateTaskToFailureStatus(taskCode, message);
        return true;
    }

    /**
     * 从执行中队列把成功的任务移到成功队列
     *
     * @param crawlerResult
     * @return
     */
    @Override
    public boolean moveRunningTaskToSuccessQueue(CrawlerResult crawlerResult) {
        crawlerTaskRepository.updateTaskToSuccessStatus(crawlerResult.getKey());
        //把附带的子任务加入队列
        List<CrawlerRequest> childRequestList = crawlerResult.getCrawlerRequestList();
        if (childRequestList != null && childRequestList.size() > 0) {
            pushTasksToWaitingQueue(childRequestList, false);
        }
        return true;
    }

    /**
     * 刷新超时队列（把超时的运行中队列任务重新加入待执行队列）
     *
     * @param timeout
     */
    @Override
    public void refreshBreakedQueue(Long timeout) {
        crawlerTaskRepository.refreshBreakedQueue(timeout);
    }


    /**
     * 查询待执行队列
     *
     * @param queryParam
     * @return
     */
    @Override
    public QueueQueryResult queryWaitingQueues(QueueQueryParam queryParam) {
        Pageable pageable = new PageRequest(queryParam.getPageIndex(), queryParam.getPageSize(), new Sort(new Sort.Order(Sort.Direction.ASC, "createdTime")));
        Page<CrawlerTask> pageData = crawlerTaskRepository.findAllByStatus(CrawlerStatus.INIT.getStatus(), pageable);
        return new QueueQueryResult(getCrawlerRequests(pageData.getContent()), pageData.getTotalPages(), pageData.getTotalElements());
    }

    /**
     * 查询执行中队列
     *
     * @param queryParam
     * @return
     */
    @Override
    public QueueQueryResult queryRunningQueues(QueueQueryParam queryParam) {
        Pageable pageable = new PageRequest(queryParam.getPageIndex(), queryParam.getPageSize(), new Sort(new Sort.Order(Sort.Direction.DESC, "startedTime")));
        Page<CrawlerTask> pageData = crawlerTaskRepository.findAllByStatus(CrawlerStatus.DEALING.getStatus(), pageable);
        return new QueueQueryResult(getCrawlerRequests(pageData.getContent()), pageData.getTotalPages(), pageData.getTotalElements());
    }

    /**
     * 查询失败队列
     *
     * @param queryParam
     * @return
     */
    @Override
    public QueueQueryResult queryFailQueues(QueueQueryParam queryParam) {
        Pageable pageable = new PageRequest(queryParam.getPageIndex(), queryParam.getPageSize(), new Sort(new Sort.Order(Sort.Direction.DESC, "completedTime")));
        Page<CrawlerTask> pageData = crawlerTaskRepository.findAllByStatus(CrawlerStatus.FAILURE.getStatus(), pageable);
        return new QueueQueryResult(getCrawlerRequests(pageData.getContent()), pageData.getTotalPages(), pageData.getTotalElements());
    }

    /**
     * 查询成功队列
     *
     * @param queryParam
     * @return
     */
    @Override
    public QueueQueryResult querySuccessQueues(QueueQueryParam queryParam) {
        Pageable pageable = new PageRequest(queryParam.getPageIndex(), queryParam.getPageSize(), new Sort(new Sort.Order(Sort.Direction.DESC, "completedTime")));
        Page<CrawlerTask> pageData = crawlerTaskRepository.findAllByStatus(CrawlerStatus.SUCCESS.getStatus(), pageable);
        return new QueueQueryResult(getCrawlerRequests(pageData.getContent()), pageData.getTotalPages(), pageData.getTotalElements());
    }


    private List<CrawlerRequest> getCrawlerRequests(List<CrawlerTask> taskList) {
        List<CrawlerRequest> requestList = new ArrayList<>(taskList.size());
        for (CrawlerTask task : taskList) {
            CrawlerRequest crawlerRequest = task.convertToCrawlerRequest();
            Map<String,Object> extendMap = crawlerRequest.getExtendMap();
            extendMap.put("startTime", task.getStartedTime());
            extendMap.put("extraInfo", task.getMessage());
            requestList.add(crawlerRequest);
        }
        return requestList;
    }

    @Override
    public String getSupportedDataType() {
        return CrawlerQueueDataType.DB;
    }

    @Override
    public Integer moveWaitingTaskToReadyQueue(List<?> ids) {
        return crawlerTaskMapper.updateCrawlerTaskByStatus(CrawlerStatus.READY.getStatus(),CrawlerStatus.READY.getMsg(), CrawlerStatus.INIT.getStatus(), ids);
    }

    @Override
    public Integer moveReadyTaskToRunningQueue(List<?> ids) {
        return crawlerTaskMapper.updateCrawlerTaskByStatus(CrawlerStatus.DEALING.getStatus(),CrawlerStatus.DEALING.getMsg(), CrawlerStatus.READY.getStatus(), ids);
    }

    @Override
    public Integer moveRunningTaskToFailureQueue(List<?> ids) {
        return null;
    }
}
