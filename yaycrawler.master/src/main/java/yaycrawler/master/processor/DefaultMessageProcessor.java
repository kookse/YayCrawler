package yaycrawler.master.processor;

import com.alibaba.fastjson.JSON;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;
import yaycrawler.common.model.CrawlerRequest;
import yaycrawler.common.model.YayCrawlerRequest;
import yaycrawler.common.status.CrawlerStatus;
import yaycrawler.dao.domain.CrawlerTask;
import yaycrawler.dao.domain.PageInfo;
import yaycrawler.dao.repositories.CrawlerTaskRepository;
import yaycrawler.dao.service.PageParserRuleService;
import yaycrawler.master.service.CrawlerQueueServiceFactory;
import yaycrawler.master.service.ICrawlerQueueService;
import yaycrawler.rocketmq.processor.IMessageProcessor;

import java.util.List;

/**
 * Created by  yuananyun on 2017/9/4.
 */
@Component("defaultMessageProcessor")
@ConditionalOnMissingBean(name = {"defaultMessageProcessor"})
public class DefaultMessageProcessor implements IMessageProcessor {

    private Logger logger = LoggerFactory.getLogger(DefaultMessageProcessor.class);

    @Autowired
    private PageParserRuleService pageParseRuleService;

    @Autowired
    private CrawlerTaskRepository crawlerTaskRepository;

    @Autowired
    private CrawlerQueueServiceFactory crawlerQueueServiceFactory;

    @Value("${crawler.queue.dataType:redis}")
    private String dataType;

    @Override
    public boolean handleMessage(MessageExt messageExt) {
        logger.info("receive : " + messageExt.toString());
        try {
            if (messageExt.getTopic().equals("TP_CRAWLER_REQUEST")) {
                // 执行TopicTest1的消费逻辑
                if (messageExt.getTags() != null && messageExt.getTags().equals("portal")) {
                    // 执行TagA的消费
                    String message = new String(messageExt.getBody(), "utf-8");
                    YayCrawlerRequest yayCrawlerRequest = JSON.parseObject(message, YayCrawlerRequest.class);
                    List<CrawlerRequest> crawlerRequestList = Lists.newArrayList();
                    List<PageInfo> pageInfos = pageParseRuleService.getPageInfoByCityCodeAndCategory(yayCrawlerRequest.getCityCode(),yayCrawlerRequest.getCategory());
                    if (pageInfos != null) {
                        pageInfos.forEach(pageInfo -> {
                            CrawlerTask crawlerTask = new CrawlerTask();
                            crawlerTask.setUrl(pageInfo.getPageUrl());
                            crawlerTask.setMethod("GET");
                            crawlerTask.setStatus(CrawlerStatus.INIT.getStatus());
                            crawlerTask.setData(ImmutableMap.of("orderId", yayCrawlerRequest.getOrderId() !=null?yayCrawlerRequest.getOrderId():"","loginName", yayCrawlerRequest.getAccount(), "loginPassword", yayCrawlerRequest.getPassword()));
                            crawlerTask.setCode(DigestUtils.sha1Hex(getUniqueUrl(crawlerTask)));
                            crawlerRequestList.add(crawlerTask.convertToCrawlerRequest());
//                            crawlerTaskRepository.save(crawlerTask);
                        });
                        ICrawlerQueueService crawlerQueueService = crawlerQueueServiceFactory.getCrawlerQueueServiceByDataType(dataType);
                        Boolean flag = crawlerQueueService.pushTasksToWaitingQueue(crawlerRequestList,true);
                        return flag;
                    }
                    System.out.println(message);
                }
            }

        } catch (Exception e) {
            logger.error("mq接收失败！，{}", e);
            return false;
        }
        //消费者向mq服务器返回消费成功的消息
        return true;
    }

    private String getUniqueUrl(CrawlerTask crawlerTask) {
        if (StringUtils.isEmpty(crawlerTask.getData()))
            return crawlerTask.getUrl();
        StringBuilder urlBuilder = new StringBuilder(crawlerTask.getUrl().trim());
        String random = DigestUtils.sha1Hex(crawlerTask.getData());
        urlBuilder.append(String.format("%s%s=%s", urlBuilder.indexOf("?") > 0 ? "&" : "?", "random", random));
        return urlBuilder.toString();
    }
}
