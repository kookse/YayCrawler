package yaycrawler.worker.service;

import com.google.common.collect.Lists;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;

import yaycrawler.common.model.CrawlerRequest;
import yaycrawler.common.thread.DynamicThreadPoolExecutorMaintainer;
import yaycrawler.dao.service.PageParserRuleService;
import yaycrawler.spider.crawler.YaySpider;
import yaycrawler.spider.downloader.GenericCrawlerDownLoader;
import yaycrawler.spider.listener.IPageParseListener;
import yaycrawler.spider.pipeline.GenericPipeline;
import yaycrawler.spider.processor.GenericPageProcessor;
import yaycrawler.spider.scheduler.CrawlerQueueScheduler;
import yaycrawler.spider.service.DownloadService;
import yaycrawler.spider.service.PageSiteService;
import yaycrawler.worker.listener.TaskDownloadFailureListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by ucs_yuananyun on 2016/5/13.
 */
@Service
public class TaskScheduleService {
    private static final Logger logger = LoggerFactory.getLogger(TaskScheduleService.class);

    @Autowired
    private PageSiteService pageSiteService;

    @Autowired
    private PageParserRuleService pageParserRuleService;

    @Autowired
    private GenericPageProcessor pageProcessor;
    @Autowired
    private GenericPipeline pipeline;

    @Autowired
    private GenericCrawlerDownLoader genericCrawlerDownLoader;

    @Autowired
    private TaskDownloadFailureListener downloadFailureListener;

    @Autowired
    private IPageParseListener pageParseListener;

    @Value("${worker.spider.threadCount:10}")
    private int spiderThreadCount;

    private Map<String, YaySpider> spiderMap = new HashMap<>();

    @Autowired(required = false)
    private DownloadService downloadService;

    private final ThreadPoolExecutor TASK_EXECUTOR_POOL = DynamicThreadPoolExecutorMaintainer.get(TaskScheduleService.class.getName(),32);

    public TaskScheduleService() {
    }

    public void refreshSpiderSite(String domain) {
        YaySpider spider = spiderMap.get(domain);
        if (spider == null) return;
        Site newSite = pageSiteService.getSite(domain);
        spider.setSite(newSite);
    }


    public Integer getRunningTaskCount() {
        int count = 0;
        for (Map.Entry<String, YaySpider> entry : spiderMap.entrySet()) {
            CrawlerQueueScheduler crawlerQueueScheduler = (CrawlerQueueScheduler) entry.getValue().getScheduler();
            count += crawlerQueueScheduler.getLeftRequestsCount(null);
        }
        logger.info("worker还有{}个运行中任务", count);
        return count;
    }

    public Boolean doSchedule(List<CrawlerRequest> taskList) {
        logger.info("worker接收到{}个任务", taskList.size());
//        TASK_EXECUTOR_POOL.execute(() -> {
            try {
                List<CrawlerRequest> downList = Lists.newArrayList();
                for (CrawlerRequest crawlerRequest : taskList) {
                    if(crawlerRequest==null) continue;
                    //如果查找不到与url相关的解析规则，则该任务不能执行
                    if(MapUtils.getString(crawlerRequest.getExtendMap(),"$DOWNLOAD") != null) {
                        downList.add(crawlerRequest);
                    }
                    if (pageParserRuleService.findOnePageInfoByRgx(crawlerRequest.getUrl()) == null) {
                        logger.info("查找不到与{}匹配的解析规则，该任务失败！", crawlerRequest.getUrl());
                        pageParseListener.onError(convertCrawlerRequestToSpiderRequest(crawlerRequest), "查找不到匹配的页面解析规则！");
                        continue;
                    }
                    String domain = crawlerRequest.getDomain();
                    YaySpider spider = spiderMap.get(domain);
                    if (spider == null)
                        spider = createSpider(domain);
                    spider.addRequest(convertCrawlerRequestToSpiderRequest(crawlerRequest));
                    if (spider.getStatus() != Spider.Status.Running)
                        spider.runAsync();
                }
                if(downList.size() > 0 ) {
                    downloadService.startCrawlerDownload(downList);
                }
                logger.info("worker任务分配完成！");
            } catch (Exception ex) {
                logger.error(ex.getMessage(),ex);
                return Boolean.FALSE;
            }
            return Boolean.TRUE;
//        });
    }

    private Request convertCrawlerRequestToSpiderRequest(CrawlerRequest crawlerRequest) {
        Request request = new Request();
        request.setUrl(crawlerRequest.getUrl());
        request.setExtras(crawlerRequest.getData());
        request.setMethod(crawlerRequest.getMethod());
        return request;
    }

    private YaySpider createSpider(String domain) {
        YaySpider spider = new YaySpider(domain, pageSiteService, pageProcessor);
        spider.setScheduler(new CrawlerQueueScheduler());
        spider.thread(spiderThreadCount);
        spider.addPipeline(pipeline);
        spider.setDownloader(genericCrawlerDownLoader);
        spider.getSpiderListeners().add(downloadFailureListener);
        spiderMap.put(domain, spider);
        return spider;
    }


    /**
     * 中断Worker的所有任务
     */
    public void interruptAllTasks() {
        logger.info("Worker开始停止所有的爬虫……");
        for (YaySpider spider : spiderMap.values()) {
            try {
                spider.stop();
                spider.close();
            } catch (Exception ex) {
                logger.error(ex.getMessage());
            }
        }
        logger.info("Worker的所有爬虫停止完成");
    }
}
