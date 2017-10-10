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
        CrawlerData crawlerData = new CrawlerData();
        String _id = DigestUtils.sha1Hex(pageUrl);
        crawlerData.setCode(_id);
        String paramJson = JSON.toJSONString(regionDataMap);
        crawlerData.setData(paramJson);
        crawlerData.setPageUrl(pageUrl);
        crawlerDataRepository.save(crawlerData);
        return true;
    }

    @Override
    public String getSupportedDataType() {
        return PersistentDataType.POSTGRESQL;
    }
}
