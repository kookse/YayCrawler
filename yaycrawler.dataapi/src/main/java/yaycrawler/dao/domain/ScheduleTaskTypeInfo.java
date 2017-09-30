package yaycrawler.dao.domain;

import com.sun.istack.internal.NotNull;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * @author bill
 * @create 2017-09-30 15:45
 * @desc 调度任务表
 **/

@Entity
@Table(name = "schedule_task_type_info")
public class ScheduleTaskTypeInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator="s_gen")
    @SequenceGenerator(name="s_gen",sequenceName="schedule_task_type_info_id_seq")
    private Integer id;

    @NotNull
    @Column(name = "baseTaskType", nullable = false, length = 64)
    private String baseTaskType;

    @Column(name = "heartBeatRate", columnDefinition = "int")
    private Integer heartBeatRate;
    @Column(name = "judgeDeadInterval", columnDefinition = "int")
    private Integer judgeDeadInterval;

    @Column(name = "sleepTimeNoData", columnDefinition = "int")
    private Integer sleepTimeNoData;
    @Column(name = "sleepTimeInterval", columnDefinition = "int")
    private Integer sleepTimeInterval;

    @Column(name = "fetchDataNumber", columnDefinition = "int")
    private Integer fetchDataNumber;
    @Column(name = "executeNumber", columnDefinition = "int")
    private Integer executeNumber;

    @Column(name = "threadNumber", columnDefinition = "int")
    private Integer threadNumber;
    @Column(name = "processorType", columnDefinition = "varchar(32)")
    private Integer processorType;

    @Column(name = "permitRunStartTime", columnDefinition = "varchar(320) ")
    private String permitRunStartTime;
    @Column(name = "permitRunEndTime", columnDefinition = "varchar(320)")
    private String permitRunEndTime;

    @Column(name = "dealBeanName", columnDefinition = "varchar(1000)")
    private String dealBeanName;

    @Column(name = "taskParameter", columnDefinition = "varchar(1000)")
    private String taskParameter;

    @Column(name = "taskItemsString", columnDefinition = "varchar(1000)")
    private String taskItemsString;

    @Column(name = "ownSign", columnDefinition = "varchar(1000)")
    private String ownSign;

    @Column(name = "scheduleRate", columnDefinition = "varchar(1000)")
    private String scheduleRate;

    @Column(name = "taskSelectTag", columnDefinition = "bool")
    private Boolean taskSelectTag;

    @Column(name = "detailTimeToExecute", columnDefinition = "varchar(32)")
    private String detailTimeToExecute;

    @Column(name = "detailTaskType", columnDefinition = "varchar(32)")
    private String detailTaskType;

    @Column(name = "needRegisterZk", columnDefinition = "bool")
    private Boolean needRegisterZk;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBaseTaskType() {
        return baseTaskType;
    }

    public void setBaseTaskType(String baseTaskType) {
        this.baseTaskType = baseTaskType;
    }

    public Integer getHeartBeatRate() {
        return heartBeatRate;
    }

    public void setHeartBeatRate(Integer heartBeatRate) {
        this.heartBeatRate = heartBeatRate;
    }

    public Integer getJudgeDeadInterval() {
        return judgeDeadInterval;
    }

    public void setJudgeDeadInterval(Integer judgeDeadInterval) {
        this.judgeDeadInterval = judgeDeadInterval;
    }

    public Integer getSleepTimeNoData() {
        return sleepTimeNoData;
    }

    public void setSleepTimeNoData(Integer sleepTimeNoData) {
        this.sleepTimeNoData = sleepTimeNoData;
    }

    public Integer getSleepTimeInterval() {
        return sleepTimeInterval;
    }

    public void setSleepTimeInterval(Integer sleepTimeInterval) {
        this.sleepTimeInterval = sleepTimeInterval;
    }

    public Integer getFetchDataNumber() {
        return fetchDataNumber;
    }

    public void setFetchDataNumber(Integer fetchDataNumber) {
        this.fetchDataNumber = fetchDataNumber;
    }

    public Integer getExecuteNumber() {
        return executeNumber;
    }

    public void setExecuteNumber(Integer executeNumber) {
        this.executeNumber = executeNumber;
    }

    public Integer getThreadNumber() {
        return threadNumber;
    }

    public void setThreadNumber(Integer threadNumber) {
        this.threadNumber = threadNumber;
    }

    public Integer getProcessorType() {
        return processorType;
    }

    public void setProcessorType(Integer processorType) {
        this.processorType = processorType;
    }

    public String getPermitRunStartTime() {
        return permitRunStartTime;
    }

    public void setPermitRunStartTime(String permitRunStartTime) {
        this.permitRunStartTime = permitRunStartTime;
    }

    public String getPermitRunEndTime() {
        return permitRunEndTime;
    }

    public void setPermitRunEndTime(String permitRunEndTime) {
        this.permitRunEndTime = permitRunEndTime;
    }

    public String getDealBeanName() {
        return dealBeanName;
    }

    public void setDealBeanName(String dealBeanName) {
        this.dealBeanName = dealBeanName;
    }

    public String getTaskParameter() {
        return taskParameter;
    }

    public void setTaskParameter(String taskParameter) {
        this.taskParameter = taskParameter;
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

    public String getScheduleRate() {
        return scheduleRate;
    }

    public void setScheduleRate(String scheduleRate) {
        this.scheduleRate = scheduleRate;
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

    public Boolean getNeedRegisterZk() {
        return needRegisterZk;
    }

    public void setNeedRegisterZk(Boolean needRegisterZk) {
        this.needRegisterZk = needRegisterZk;
    }
}
