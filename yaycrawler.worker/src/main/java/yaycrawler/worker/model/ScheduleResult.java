package yaycrawler.worker.model;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName: ScheduleResult
 * @Description:
 * @Author Abi
 * @Email 380285138@qq.com
 * @Date 2017/6/20 17:42
 */
public class ScheduleResult implements Serializable{

    private static final long serialVersionUID = 5589882948052875071L;
    // 状态
    private Boolean status;
    // 信息
    private String message;
    //
    private List<Exception> exceptions;
    private Integer index;

    // 状态码
    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Exception> getExceptions() {
        return exceptions;
    }

    public void setExceptions(List<Exception> exceptions) {
        this.exceptions = exceptions;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    @Override
    public String toString() {
        return "ScheduleResult{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", exceptions=" + exceptions +
                ", index=" + index +
                '}';
    }
}
