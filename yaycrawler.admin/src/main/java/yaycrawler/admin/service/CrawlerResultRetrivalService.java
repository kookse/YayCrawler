package yaycrawler.admin.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import yaycrawler.admin.communication.MasterActor;
import yaycrawler.spider.persistent.IResultPersistentService;
import yaycrawler.spider.persistent.PersistentServiceFactory;

import java.util.LinkedHashMap;

/**爬虫结果查询服务类
 * Created by ucs_yuananyun on 2016/5/30.
 */
@Service
public class CrawlerResultRetrivalService {

    private static final Logger logger = LoggerFactory.getLogger(CrawlerResultRetrivalService.class);

    @Autowired(required = false)
    private PersistentServiceFactory persistentServiceFactory;

    @Value("${crawler.result.persistent:mongdb}")
    private String resultPersistent;

    public Object  retrivalByTaskId(String collectionName,String taskId)
    {
        IResultPersistentService persistentService = persistentServiceFactory.getPersistentServiceByDataType(resultPersistent.toLowerCase());
        return persistentService.getCrawlerResult(collectionName, taskId);
    }

}
