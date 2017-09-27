package yaycrawler.common.model;

import java.util.Map;

/**
 * @author bill
 * @create 2017-08-29 15:20
 * @desc 登陆参数
 **/
public class LoginParam {

    private String username;
    private String password;
    private String code;
    private String loginUrl;
    private String binaryEngine;
    private String ocrEngine;
    private Map<String,Object> oldParams;
    private Map<String,Object> newParams;
    private String cookie;
    private String valideLogin;
    private String content;
    private String url;

    /**
     *
     * @param username
     * @param password
     * @param code
     * @param loginUrl
     * @param binaryEngine
     * @param ocrEngine
     */
    public LoginParam(String username, String password, String code, String loginUrl, String binaryEngine, String ocrEngine) {
        this.username = username;
        this.password = password;
        this.code = code;
        this.loginUrl = loginUrl;
        this.binaryEngine = binaryEngine;
        this.ocrEngine = ocrEngine;
    }

    public LoginParam () {

    }

    public LoginParam(String username, String password, String code, String loginUrl, String binaryEngine, String ocrEngine, Map<String, Object> oldParams, Map<String, Object> newParams, String cookie, String valideLogin) {
        this.username = username;
        this.password = password;
        this.code = code;
        this.loginUrl = loginUrl;
        this.binaryEngine = binaryEngine;
        this.ocrEngine = ocrEngine;
        this.oldParams = oldParams;
        this.newParams = newParams;
        this.cookie = cookie;
        this.valideLogin = valideLogin;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBinaryEngine() {
        return binaryEngine;
    }

    public void setBinaryEngine(String binaryEngine) {
        this.binaryEngine = binaryEngine;
    }

    public String getOcrEngine() {
        return ocrEngine;
    }

    public void setOcrEngine(String ocrEngine) {
        this.ocrEngine = ocrEngine;
    }

    public String getLoginUrl() {
        return loginUrl;
    }

    public void setLoginUrl(String loginUrl) {
        this.loginUrl = loginUrl;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Map<String, Object> getOldParams() {
        return oldParams;
    }

    public void setOldParams(Map<String, Object> oldParams) {
        this.oldParams = oldParams;
    }

    public Map<String, Object> getNewParams() {
        return newParams;
    }

    public void setNewParams(Map<String, Object> newParams) {
        this.newParams = newParams;
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public String getValideLogin() {
        return valideLogin;
    }

    public void setValideLogin(String valideLogin) {
        this.valideLogin = valideLogin;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "LoginParam{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", code='" + code + '\'' +
                ", loginUrl='" + loginUrl + '\'' +
                ", binaryEngine='" + binaryEngine + '\'' +
                ", ocrEngine='" + ocrEngine + '\'' +
                ", oldParams=" + oldParams +
                ", newParams=" + newParams +
                ", cookie='" + cookie + '\'' +
                ", valideLogin='" + valideLogin + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
