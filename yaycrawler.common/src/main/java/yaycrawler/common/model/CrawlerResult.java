package yaycrawler.common.model;

import java.util.List;

/**
 * Created by Administrator on 2016/5/13.
 */
public class CrawlerResult {

    private String key;
    private boolean isSuccess;
    private List<CrawlerRequest> crawlerRequestList;
    private String message;
    private Integer status;

    public CrawlerResult() {
    }

    public CrawlerResult(boolean isSuccess, String key, List<CrawlerRequest> crawlerRequestList,String message) {
        this.isSuccess = isSuccess;
        this.key = key;
        this.crawlerRequestList = crawlerRequestList;
        this.message = message;
    }

    public CrawlerResult(String key, boolean isSuccess, List<CrawlerRequest> crawlerRequestList, String message, Integer status) {
        this.key = key;
        this.isSuccess = isSuccess;
        this.crawlerRequestList = crawlerRequestList;
        this.message = message;
        this.status = status;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public List<CrawlerRequest> getCrawlerRequestList() {
        return crawlerRequestList;
    }

    public void setCrawlerRequestList(List<CrawlerRequest> crawlerRequestList) {
        this.crawlerRequestList = crawlerRequestList;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "CrawlerResult{" +
                "key='" + key + '\'' +
                ", isSuccess=" + isSuccess +
                ", crawlerRequestList=" + crawlerRequestList +
                ", message='" + message + '\'' +
                ", status=" + status +
                '}';
    }
}
