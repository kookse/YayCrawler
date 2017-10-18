package yaycrawler.master.quartz;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import yaycrawler.common.status.CrawlerStatus;
import yaycrawler.dao.domain.CrawlerData;
import yaycrawler.dao.mapper.CrawlerDataMapper;
import yaycrawler.dao.mapper.CrawlerTaskMapper;
import yaycrawler.dao.repositories.CrawlerDataRepository;
import yaycrawler.dao.repositories.CrawlerTaskRepository;
import yaycrawler.rocketmq.service.MQProduceService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author bill
 * @create 2017-10-11 10:25
 * @desc RocketMQ 定时任务
 **/
public class RocketMQTask {

    private static final String TOPIC = "TP_CRAWLER_RESPONSE_DEV";
    private static final String TAG = "portal";
    private static final Logger logger = LoggerFactory.getLogger(RocketMQTask.class);

    @Autowired
    private MQProduceService produceService;

    @Autowired
    private CrawlerDataMapper crawlerDataMapper;

    @Autowired
    private CrawlerTaskMapper crawlerTaskMapper;

    public void saveRocketMQ() {
        List<String> orderIds = crawlerDataMapper.getOrderIds(CrawlerStatus.SUCCESS.getStatus());
        orderIds.forEach(orderId->{
            List<CrawlerData> crawlerDataList = crawlerDataMapper.findByOrderId(orderId);
            Map regionDataMap = Maps.newHashMap();
            crawlerDataList.forEach(crawlerData -> {
                regionDataMap.putAll(crawlerData.getDataMap());
            });
            Map<String,Object> data = new HashMap<>();
            regionDataMap.remove("_id");
            regionDataMap.remove("pageUrl");
            regionDataMap.remove("timestamp");
            Map loginParams = (Map) regionDataMap.get("loginParams");
            regionDataMap.remove("loginParams");
            data.put("result",regionDataMap);
            data.put("orderId",loginParams.get("orderId"));
            String paramJson = JSON.toJSONString(data);
            boolean flag = produceService.sendMsg(TOPIC, TAG, null, paramJson);
            if(flag)
                crawlerTaskMapper.updateCrawlerTaskByOrderId(CrawlerStatus.EXPORT.getStatus(),orderId);
        });
    }

}
