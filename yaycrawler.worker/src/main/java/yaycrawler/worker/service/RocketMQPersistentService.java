package yaycrawler.worker.service;

import com.alibaba.fastjson.JSON;
import com.smartdata360.rocketmq.service.MQProduceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import yaycrawler.spider.persistent.IResultPersistentService;
import yaycrawler.spider.persistent.PersistentDataType;

import java.util.Map;

/**
 * @author bill
 * @create 2017-09-26 11:13
 * @desc rocket mq持久化
 **/
@Component
public class RocketMQPersistentService implements IResultPersistentService {

    private static final String TOPIC = "TP_CRAWLER_RESPONSE";
    private static final String TAG = "portal";
    private static final Logger logger = LoggerFactory.getLogger(RocketMQPersistentService.class);

    @Autowired
    private MQProduceService produceService;

    @Override
    public boolean saveCrawlerResult(String pageUrl, Map<String, Object> regionDataMap) {
        String paramJson = JSON.toJSONString(regionDataMap);
        boolean flag = produceService.sendMsg(TOPIC, TAG, null, paramJson);
        return flag;
    }

    @Override
    public String getSupportedDataType() {
        return PersistentDataType.ROCKETMQ;
    }
}
