package yaycrawler.dao.status;

/**
 * @author bill
 * @create 2017-09-30 11:23
 * @desc 采集链接状态
 **/
public enum CrawlerStatus {

    //状态，未开始：0；等待执行：1；运行中：2；执行中：3；失败：4；成功；5：超时；

    // 未开始
    INIT(0,"初始化"),
    // 准备完成,等待执行
    READY(1,"等待采集"),
    // 正在执行
    DEALING(2,"采集中"),
    // 终止
    FAILURE(3,"采集失败"),
    // 完成
    SUCCESS(5,"采集成功"),
    // 工单异常
    EXCEPTION(6,"采集超时");

    private Integer status;
    private String msg;

    CrawlerStatus(Integer status, String msg){
        this.status = status;
        this.msg = msg;
    }

    public Integer getStatus(){
        return status;
    }

    public String getMsg() {
        return msg;
    }
}
