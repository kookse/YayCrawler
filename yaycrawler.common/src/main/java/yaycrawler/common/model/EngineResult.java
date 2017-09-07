package yaycrawler.common.model;

import org.apache.http.Header;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName: EngineResult
 * @Description:
 */
public class EngineResult implements Serializable{

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
    private List<Header> headers;
    private LoginParam loginParam;
    private String result;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public LoginParam getLoginParam() {
        return loginParam;
    }

    public void setLoginParam(LoginParam loginParam) {
        this.loginParam = loginParam;
    }

    public List<Header> getHeaders() {
        return headers;
    }

    public void setHeaders(List<Header> headers) {
        this.headers = headers;
    }

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
        return "EngineResult{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", exceptions=" + exceptions +
                ", index=" + index +
                ", code='" + code + '\'' +
                ", headers=" + headers +
                '}';
    }
}
