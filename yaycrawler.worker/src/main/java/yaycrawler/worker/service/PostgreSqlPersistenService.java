package yaycrawler.worker.service;

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
        Object orderId = ((Map)regionDataMap.get("loginParams")).get("orderId");
        crawlerData.setOrderId(orderId!=null?orderId.toString():null);
        crawlerDataRepository.save(crawlerData);
        return true;
    }

    @Override
    public String getSupportedDataType() {
        return PersistentDataType.POSTGRESQL;
    }
}
