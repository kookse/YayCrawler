package yaycrawler.dao.domain;

import com.sun.istack.internal.NotNull;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * @author bill
 * @create 2017-09-30 15:46
 * @desc 调度类型表
 **/
@Entity
@Table(name = "schedule_strategy_info")
public class ScheduleStrategyInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator="s_gen")
    @SequenceGenerator(name="s_gen",sequenceName="schedule_strategy_info_id_seq")
    private Integer id;

    @NotNull
    @Column(name = "strategyName", nullable = false, length = 64)
    private String strategyName;
    @Column(name = "ipListString", columnDefinition = "varchar(320) ")
    private String ipListString;
    @Column(name = "numOfSingleServer", columnDefinition = "int")
    private Integer numOfSingleServer;
    @Column(name = "assignNum", columnDefinition = "int default 1")
    private int assignNum;
    @Column(name = "taskName", columnDefinition = "varchar(1000)")
    private String taskName;
    @Column(name = "taskTypeId", columnDefinition = "int")
    private int taskTypeId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStrategyName() {
        return strategyName;
    }

    public void setStrategyName(String strategyName) {
        this.strategyName = strategyName;
    }

    public String getIpListString() {
        return ipListString;
    }

    public void setIpListString(String ipListString) {
        this.ipListString = ipListString;
    }

    public Integer getNumOfSingleServer() {
        return numOfSingleServer;
    }

    public void setNumOfSingleServer(Integer numOfSingleServer) {
        this.numOfSingleServer = numOfSingleServer;
    }

    public int getAssignNum() {
        return assignNum;
    }

    public void setAssignNum(int assignNum) {
        this.assignNum = assignNum;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public int getTaskTypeId() {
        return taskTypeId;
    }

    public void setTaskTypeId(int taskTypeId) {
        this.taskTypeId = taskTypeId;
    }
}
