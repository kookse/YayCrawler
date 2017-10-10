package yaycrawler.master.service;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import yaycrawler.common.model.CrawlerRequest;
import yaycrawler.common.model.CrawlerResult;
import yaycrawler.common.model.QueueQueryParam;
import yaycrawler.common.model.QueueQueryResult;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author bill
 * @create 2017-10-10 14:37
 * @desc 链接的抽象类
 **/
public class AbstractICrawlerQueueService implements ICrawlerQueueService{

    @Override
    public boolean pushTasksToWaitingQueue(List<CrawlerRequest> crawlerRequests, boolean removeDuplicated) {
        return false;
    }

    @Override
    public List<CrawlerRequest> fetchTasksFromWaitingQueue(long taskCount, List<Integer> taskItemIds) {
        return null;
    }

    @Override
    public boolean moveWaitingTaskToRunningQueue(String workerId, List<CrawlerRequest> crawlerRequests) {
        return false;
    }

    @Override
    public boolean moveRunningTaskToFailQueue(String taskCode, String message) {
        return false;
    }

    @Override
    public boolean moveRunningTaskToSuccessQueue(CrawlerResult crawlerResult) {
        return false;
    }

    @Override
    public void refreshBreakedQueue(Long timeout) {

    }

    @Override
    public QueueQueryResult queryWaitingQueues(QueueQueryParam queryParam) {
        return null;
    }

    @Override
    public QueueQueryResult queryRunningQueues(QueueQueryParam queryParam) {
        return null;
    }

    @Override
    public QueueQueryResult queryFailQueues(QueueQueryParam queryParam) {
        return null;
    }

    @Override
    public QueueQueryResult querySuccessQueues(QueueQueryParam queryParam) {
        return null;
    }

    @Override
    public String getSupportedDataType() {
        return null;
    }

    @Override
    public Integer moveWaitingTaskToReadyQueue(List<?> ids) {
        return null;
    }

    @Override
    public Integer moveReadyTaskToRunningQueue(List<?> ids) {
        return null;
    }

    @Override
    public Integer moveRunningTaskToFailureQueue(List<?> ids) {
        return null;
    }

    /**
     * 通过迪卡尔积生成多个参数批量任务
     * @param requestTmps
     * @param crawlerRequest
     * @param datas
     */
    private void transformRequest(List<CrawlerRequest> requestTmps, CrawlerRequest crawlerRequest, List datas) {
        Set<List<ImmutableMap<String, String>>> parameterSets = Sets.cartesianProduct(datas);
        for (List<ImmutableMap<String, String>> parameters : parameterSets) {
            CrawlerRequest request = new CrawlerRequest();
            BeanUtils.copyProperties(crawlerRequest, request);
            Map<Object, Object> tmp = Maps.newHashMap();
            for (Map data : parameters) {
                if (data != null)
                    tmp.put(data.keySet().iterator().next(), data.values().iterator().next());
            }
            request.setData(tmp);
            requestTmps.add(request);
        }
    }

    /**
     * 通过批量参数转换成批量任务
     * @param entry
     * @param datas
     */
    private void transformParameters(Map.Entry<String, Object> entry, List datas) {
        List<Object> arrayTmps;
        Map pagination;
        if (StringUtils.startsWith(entry.getKey(), "$array_")) {
            arrayTmps = JSON.parseObject(String.valueOf(entry.getValue()), List.class);
            List<Map<String, Object>> tmpData = Lists.newArrayList();
            String tmpParam = StringUtils.substringAfter(entry.getKey(), "$array_");
            for (Object tmp : arrayTmps) {
                tmpData.add(ImmutableMap.of(tmpParam, tmp));
            }

            datas.add(ImmutableSet.copyOf(tmpData));
        } else if (StringUtils.startsWith(entry.getKey(), "$pagination_")) {
            pagination = JSON.parseObject(String.valueOf(entry.getValue()), Map.class);
            List<Map> tmpData = Lists.newArrayList();
            int start = Integer.parseInt(pagination.get("START").toString());
            int end = Integer.parseInt(pagination.get("END").toString());
            int step = Integer.parseInt(pagination.get("STEP").toString());
            String tmpParam = StringUtils.substringAfter(entry.getKey(), "$pagination_");
            for (int i = start; i <= end; i = i + step) {
                tmpData.add(ImmutableMap.of(tmpParam, i));
            }
            datas.add(ImmutableSet.copyOf(tmpData));
        } else {
            datas.add(ImmutableSet.of(ImmutableMap.of(entry.getKey(), entry.getValue())));
        }
    }

    protected List<CrawlerRequest> transCrawlerQueue(List<CrawlerRequest> crawlerRequests) {
        List<CrawlerRequest> crawlerRequestList = Lists.newArrayList();
        for (CrawlerRequest crawlerRequest : crawlerRequests) {
            if(crawlerRequest.getUrl() == null)
                continue;
            Map<String, Object> parameter = crawlerRequest.getData();
            List datas = Lists.newArrayList();
            if (parameter == null || parameter.size() == 0) {
                crawlerRequestList.add(crawlerRequest);
            } else {
                for (Map.Entry<String, Object> entry : parameter.entrySet()) {
                    transformParameters(entry, datas);
                }
                if (datas != null && datas.size() > 0) {
                    transformRequest(crawlerRequestList, crawlerRequest, datas);
                }
            }
        }
        return crawlerRequestList;
    }
}
