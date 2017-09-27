package yaycrawler.master.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author bill
 * @create 2017-09-25 11:48
 * @desc 队列生产类
 **/
@Component
public class CrawlerQueueServiceFactory {

    @Autowired(required = false)
    private List<ICrawlerQueueService> crawlerQueueServices;

    public ICrawlerQueueService getCrawlerQueueServiceByDataType(String dataType) {
        if(crawlerQueueServices==null) return null;
        for (ICrawlerQueueService resultPersistentService : crawlerQueueServices) {
            if (resultPersistentService.getSupportedDataType().equals(dataType))
                return resultPersistentService;
        }
        return null;
    }
}
