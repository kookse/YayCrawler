package yaycrawler.spider.service;

import com.alibaba.fastjson.JSON;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import yaycrawler.dao.domain.CrawlerData;
import yaycrawler.dao.repositories.CrawlerDataRepository;
import yaycrawler.spider.persistent.IResultPersistentService;
import yaycrawler.spider.persistent.PersistentDataType;

import java.util.Map;

@Service
public class PostgreSqlPersistenService implements IResultPersistentService {

    @Autowired
    private CrawlerDataRepository crawlerDataRepository;

    @Override
    public boolean saveCrawlerResult(String pageUrl, Map<String, Object> regionDataMap) {
        String _id = DigestUtils.sha1Hex(pageUrl);
        CrawlerData crawlerData = crawlerDataRepository.findByCode(_id);
        if(crawlerData == null)
            crawlerData = new CrawlerData();
        crawlerData.setCode(_id);
        String paramJson = JSON.toJSONString(regionDataMap);
        crawlerData.setData(paramJson);
        crawlerData.setPageUrl(pageUrl);
        crawlerData.setOrderId(_id);
        Map loginParams = (Map) regionDataMap.get("loginParams");
        if(loginParams != null)
            crawlerData.setOrderId(loginParams.get("orderId") !=null ?loginParams.get("orderId").toString():null);
        crawlerDataRepository.save(crawlerData);
        return true;
    }

    @Override
    public String getSupportedDataType() {
        return PersistentDataType.POSTGRESQL;
    }

    @Override
    public Object getCrawlerResult(String collectionName, String taskId) {
        CrawlerData crawlerData = crawlerDataRepository.findByCode(taskId);
        if(crawlerData == null)
            return null;
        else {
            return crawlerData.getData();
        }
    }
}
