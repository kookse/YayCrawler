package yaycrawler.worker.executor.schedule;

import com.google.common.collect.ImmutableMap;
import com.taobao.pamirs.schedule.IScheduleTaskDealMulti;
import com.taobao.pamirs.schedule.TaskItemDefine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import yaycrawler.common.model.CrawlerRequest;
import yaycrawler.dao.domain.CrawlerTask;
import yaycrawler.worker.executor.core.ITaskExecutor;
import yaycrawler.worker.model.ScheduleResult;
import yaycrawler.worker.puller.execution.AbstractExecutionDataPuller;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * @ClassName: ScheduleCallbackTaskExecutor
 * @Description:
 * @Author Abi
 * @Email 380285138@qq.com
 * @Date 2017/6/15 15:42
 */
@Service("crawlerTaskExecutor")
public class CrawlerTaskExecutor implements IScheduleTaskDealMulti<CrawlerRequest> {

    @Resource(name = "crawlerTaskExecutionPuller")
    private AbstractExecutionDataPuller crawlerTaskExecutionPuller;
    @Resource(name = "parseTaskExecutor")
    private ITaskExecutor parseTaskExecutor;

    private static final Logger logger = LoggerFactory.getLogger(CrawlerTaskExecutor.class);

    @Override
    public boolean execute(CrawlerRequest[] tasks, String ownSign) throws Exception {
        logger.info("采集任务开始执行...");
        logger.debug("--------------------------parsetask start-----------------------------------------");
        for (CrawlerRequest task:tasks) {
            logger.debug(task.getHashCode().toString());
        }
        logger.debug("--------------------------parsetask end-------------------------------------------------");
        ScheduleResult result = parseTaskExecutor.execute(Arrays.asList(tasks));
        return result != null && result.getStatus();
    }

    @Override
    public List<CrawlerRequest> selectTasks(String taskParameter, String ownSign, int taskItemNum, List<TaskItemDefine> taskItemList, int eachFetchDataNum) throws Exception {

        List<CrawlerRequest> configs = crawlerTaskExecutionPuller.select(ImmutableMap.of("taskItemList",taskItemList,"taskItemNum",taskItemNum), null, 5L);
        if (configs == null || configs.isEmpty()){
            logger.info("获取采集列表信息,采集列表大小:[{}]", 0);
            return new ArrayList<>(1);
        }
        logger.info("获取采集列表信息,采集列表大小:[{}]", configs.size());
        return configs;
    }

    @Override
    public Comparator<CrawlerRequest> getComparator() {
        return Comparator.comparing(CrawlerRequest::getHashCode);
    }
}
