package yaycrawler.master.service.impl;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yaycrawler.common.model.CrawlerRequest;
import yaycrawler.common.model.CrawlerResult;
import yaycrawler.common.model.QueueQueryParam;
import yaycrawler.common.model.QueueQueryResult;
import yaycrawler.master.service.CrawlerQueueDataType;
import yaycrawler.master.service.ICrawlerQueueService;

import java.util.List;

/**
 * @author bill
 * @create 2017-09-25 11:44
 * @desc mq队列
 **/
@Service(value = "rocketMQCrawlerQueueService")
@Transactional
public class RocketMQCrawlerQueueService  implements ICrawlerQueueService {

    private static String TOPIC = "TP_CRAWLER_REQUEST";
    private static String TAG = "portal";

    @Override
    public boolean pushTasksToWaitingQueue(List<CrawlerRequest> crawlerRequests, boolean removeDuplicated) {
        return false;
    }

    @Override
    public List<CrawlerRequest> fetchTasksFromWaitingQueue(long taskCount) {
        return null;
    }

    @Override
    public boolean moveWaitingTaskToRunningQueue(String workerId, List<CrawlerRequest> crawlerRequests) {
        return false;
    }

    @Override
    public boolean moveRunningTaskToFailQueue(String taskCode, String message) {
        return false;
    }

    @Override
    public boolean moveRunningTaskToSuccessQueue(CrawlerResult crawlerResult) {
        return false;
    }

    @Override
    public void refreshBreakedQueue(Long timeout) {

    }

    @Override
    public QueueQueryResult queryWaitingQueues(QueueQueryParam queryParam) {
        return null;
    }

    @Override
    public QueueQueryResult queryRunningQueues(QueueQueryParam queryParam) {
        return null;
    }

    @Override
    public QueueQueryResult queryFailQueues(QueueQueryParam queryParam) {
        return null;
    }

    @Override
    public QueueQueryResult querySuccessQueues(QueueQueryParam queryParam) {
        return null;
    }

    @Override
    public String getSupportedDataType() {
        return CrawlerQueueDataType.MQ;
    }
}
