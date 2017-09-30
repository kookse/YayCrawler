package yaycrawler.worker.model.persistence;

import com.taobao.pamirs.schedule.strategy.ScheduleStrategy;

import java.io.Serializable;

/**
 * @ClassName: ScheduleStrategyInfo
 * @Description:       调度策略信息
 *                       @see com.taobao.pamirs.schedule.strategy.ScheduleStrategy
 *                       对照数据库表 schedule_strategy_info
 * @Author Abi
 * @Email 380285138@qq.com
 * @Date 2017/6/1 14:33
 */
public class ScheduleStrategyInfo extends ScheduleStrategy implements Serializable{

    private static final long serialVersionUID = 6422580939709437670L;
    private Integer id;
    // IP列表字符串
    private String ipListString;
    // taskTypeId
    private Integer taskTypeId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIpListString() {
        return ipListString;
    }

    public void setIpListString(String ipListString) {
        this.ipListString = ipListString;
    }

    public Integer getTaskTypeId() {
        return taskTypeId;
    }

    public void setTaskTypeId(Integer taskTypeId) {
        this.taskTypeId = taskTypeId;
    }

    @Override
    public String toString() {
        return "ScheduleStrategyInfo{" +
                "id=" + id +
                ", ipListString='" + ipListString + '\'' +
                ", taskTypeId=" + taskTypeId +
                "} " + super.toString();
    }
}
