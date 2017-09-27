package yaycrawler.master.quartz;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import yaycrawler.master.service.CrawlerQueueServiceFactory;
import yaycrawler.master.service.ICrawlerQueueService;

/**
 * Created by ucs_yuananyun on 2016/5/31.
 */
public class BreakedQueueRefreshTask {
    private static final Logger logger = LoggerFactory.getLogger(BreakedQueueRefreshTask.class);
    private Long queueTimeOut;
    @Autowired
    private CrawlerQueueServiceFactory crawlerQueueServiceFactory;
    @Value("${crawler.queue.dataType:redis}")
    private String dataType;

    public void refreshBreakedQueue() {
        ICrawlerQueueService crawlerQueueService = crawlerQueueServiceFactory.getCrawlerQueueServiceByDataType(dataType);
        logger.info("开始刷新中断任务队列……");
        crawlerQueueService.refreshBreakedQueue(queueTimeOut);
    }

    public void setQueueTimeOut(Long queueTimeOut) {
        this.queueTimeOut = queueTimeOut;
    }
}
