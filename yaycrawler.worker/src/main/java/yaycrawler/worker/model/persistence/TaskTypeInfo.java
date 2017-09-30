package yaycrawler.worker.model.persistence;

import com.taobao.pamirs.schedule.taskmanager.ScheduleTaskType;

/**
 * @ClassName: TaskTypeInfo
 * @Description:    调度任务类型信息
 *                    @see com.taobao.pamirs.schedule.taskmanager.ScheduleTaskType
 *                    对照数据库表 schedule_task_type
 * @Author Abi
 * @Email 380285138@qq.com
 * @Date 2017/6/1 14:32
 */
public class TaskTypeInfo extends ScheduleTaskType {

    private static final long serialVersionUID = -3261528800660775800L;
    private Integer id;
    // 任务项字符串
    private String taskItemsString = "0:{DEFAULT=1}";
    // 当前环境
    private String ownSign = "BASE";
    // 执行cron,推荐permitStartTime为null
    private String scheduleRate;
    // 任务获取标识
    private Boolean taskSelectTag;
    // 具体在哪个时间执行
    private String detailTimeToExecute;
    // 具体的任务类型,sql shell commons
    private String detailTaskType;
    // 是否需要注册到ZK
    private Boolean needRegisterZk;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTaskItemsString() {
        return taskItemsString;
    }

    public void setTaskItemsString(String taskItemsString) {
        this.taskItemsString = taskItemsString;
    }

    public String getOwnSign() {
        return ownSign;
    }

    public void setOwnSign(String ownSign) {
        this.ownSign = ownSign;
    }

    public Boolean getTaskSelectTag() {
        return taskSelectTag;
    }

    public void setTaskSelectTag(Boolean taskSelectTag) {
        this.taskSelectTag = taskSelectTag;
    }

    public String getDetailTimeToExecute() {
        return detailTimeToExecute;
    }

    public void setDetailTimeToExecute(String detailTimeToExecute) {
        this.detailTimeToExecute = detailTimeToExecute;
    }

    public String getDetailTaskType() {
        return detailTaskType;
    }

    public void setDetailTaskType(String detailTaskType) {
        this.detailTaskType = detailTaskType;
    }

    public String getScheduleRate() {
        return scheduleRate;
    }

    public void setScheduleRate(String scheduleRate) {
        this.scheduleRate = scheduleRate;
    }

    public Boolean getNeedRegisterZk() {
        return needRegisterZk;
    }

    public void setNeedRegisterZk(Boolean needRegisterZk) {
        this.needRegisterZk = needRegisterZk;
    }

    @Override
    public String toString() {
        return "TaskTypeInfo{" +
                "id=" + id +
                ", taskItemsString='" + taskItemsString + '\'' +
                ", ownSign='" + ownSign + '\'' +
                ", scheduleRate='" + scheduleRate + '\'' +
                ", taskSelectTag=" + taskSelectTag +
                ", detailTimeToExecute='" + detailTimeToExecute + '\'' +
                ", detailTaskType='" + detailTaskType + '\'' +
                ", needRegisterZk=" + needRegisterZk +
                "} " + super.toString();
    }
}
