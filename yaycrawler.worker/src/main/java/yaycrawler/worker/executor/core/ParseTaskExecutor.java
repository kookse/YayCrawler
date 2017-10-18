package yaycrawler.worker.executor.core;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import yaycrawler.common.model.CrawlerRequest;
import yaycrawler.common.status.CrawlerStatus;
import yaycrawler.dao.domain.CrawlerTask;
import yaycrawler.dao.mapper.CrawlerTaskMapper;
import yaycrawler.worker.communication.MasterActor;
import yaycrawler.worker.model.ScheduleResult;
import yaycrawler.worker.model.WorkerContext;
import yaycrawler.worker.service.TaskScheduleService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @ClassName: CallbackTaskExecutor
 * @Description:
 * @Author Abi
 * @Email 380285138@qq.com
 * @Date 2017/6/16 14:56
 */
@Service("parseTaskExecutor")
public class ParseTaskExecutor implements ITaskExecutor<CrawlerRequest> {

    private static final Logger logger = LoggerFactory.getLogger(ParseTaskExecutor.class);

    @Autowired
    private CrawlerTaskMapper crawlerTaskMapper;

    private final static int MAXFAILURETIMES = 3;

    @Value("${schedule.task.pull.thread.number:500}")
    private Integer defaultMultiThreadNum;

    private AtomicInteger atomicInteger = new AtomicInteger(0);

    @Autowired
    private TaskScheduleService taskScheduleService;

    @Autowired
    private MasterActor masterActor;

    @Override
    public ScheduleResult execute(CrawlerRequest crawlerTask) {
        ScheduleResult result = new ScheduleResult();
        result.setStatus(taskScheduleService.doSchedule(Lists.newArrayList(crawlerTask)));
        return result;
    }

    private List<CrawlerRequest> getCrawlerRequests(List<CrawlerTask> taskList) {
        List<CrawlerRequest> requestList = new ArrayList<>(taskList.size());
        for (CrawlerTask task : taskList) {
            CrawlerRequest crawlerRequest = task.convertToCrawlerRequest();
            crawlerRequest.getExtendMap().put("startTime", task.getStartedTime());
            crawlerRequest.getExtendMap().put("extraInfo", task.getMessage());
            requestList.add(crawlerRequest);
        }
        return requestList;
    }

    @Override
    public ScheduleResult execute(List<CrawlerRequest> crawlerTask) {
        ScheduleResult result = new ScheduleResult();
        try {
            if (crawlerTask == null || crawlerTask.isEmpty()) {
                result.setStatus(Boolean.FALSE);
                return result;
            }
            Map<Object, CrawlerRequest> batchOrderInfoMap = new HashMap<>();
            crawlerTask.forEach(task -> {
                Object keyword = task.getExtendMap().get("$keyword");
                batchOrderInfoMap.put(keyword, task);
            });

            List<Object> ids = Lists.newArrayList(batchOrderInfoMap.keySet());
            logger.info("采集调度任务主线程结束,后台线程执行中...执行数量{}", ids.size());
            if (ids != null && !ids.isEmpty()) {
                masterActor.notifyTaskDealing(ids);
                //crawlerTaskMapper.updateCrawlerTaskByStatus(CrawlerStatus.DEALING.getStatus(), CrawlerStatus.DEALING.getMsg(), CrawlerStatus.READY.getStatus(), ids);
                call(ids, batchOrderInfoMap);
            }
            result.setStatus(Boolean.TRUE);
        } catch (Exception e) {
            Map<Object, CrawlerRequest> batchOrderInfoMap = new HashMap<>();
            crawlerTask.forEach(order -> {
                Object keyword = order.getExtendMap().get("$keyword");
                batchOrderInfoMap.put(keyword, order);
            });
            List<Object> ids = new ArrayList<>();
            ids.addAll(batchOrderInfoMap.keySet());
            masterActor.notifyTaskFailure(ids);
            //crawlerTaskMapper.updateCrawlerTaskByStatus(CrawlerStatus.FAILURE.getStatus(), CrawlerStatus.FAILURE.getMsg(), CrawlerStatus.READY.getStatus(), ids);
            logger.error("出现了异常,执行终止操作,数据：{}", JSON.toJSON(crawlerTask));
            result.setStatus(Boolean.FALSE);
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 失败重试
     *
     * @param crawlerTask
     */
    public void executeSQLWithFailover(CrawlerRequest crawlerTask) {
        int count = 1;
        while (MAXFAILURETIMES >= count) {
            ScheduleResult scheduleResult = new ScheduleResult();
            scheduleResult.setStatus(taskScheduleService.doSchedule(Lists.newArrayList(crawlerTask)));
            Boolean result = scheduleResult != null && scheduleResult.getStatus();
            logger.info("第:[{}]次执行工单解析任务:[{}] {}",
                    count, crawlerTask.getHashCode(), result ? "成功." : "失败.");
            count++;
            if (count >= MAXFAILURETIMES && !result) {
                logger.info("执行工单解析任务:[{}]失败次数过多,开始执行失败回调方法.",
                        crawlerTask.getHashCode());
                failureCallback(crawlerTask);
            }
            if (result) {
                break;
            }
        }
    }

    /**
     * 处理采集调度任务
     *
     * @param ids
     * @Param queryOrderInfoMap
     */
    private void call(List<Object> ids, Map<Object, CrawlerRequest> batchOrderInfoMap) {
        logger.info("工单解析任务开始,后台线程开始轮询解析任务...");
        ids.forEach(id -> {
            try {
                atomicInteger.incrementAndGet();
                logger.info("****{}********工单解析**********" + atomicInteger.get() + "************************************",id);
                executeSQLWithFailover(batchOrderInfoMap.get(id));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void failureCallback(CrawlerRequest crawlerTask) {
        //crawlerTask.setStatus(CrawlerStatus.FAILURE.getStatus());
        //更新状态
        logger.info("工单{}解析任务，重试次数超过{}次", crawlerTask.getHashCode(), MAXFAILURETIMES);
        Object keyword = crawlerTask.getExtendMap().get("$keyword");
        masterActor.notifyTaskFailure(Lists.newArrayList(keyword));
        //crawlerTaskMapper.updateCrawlerTaskStatus(crawlerTask.getCode(),WorkerContext.getWorkerId(), CrawlerStatus.FAILURE.getStatus(), CrawlerStatus.FAILURE.getMsg());
    }

}
