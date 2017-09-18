package yaycrawler.dao.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sun.istack.internal.NotNull;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;

/**
 * Created by ucs_yuananyun on 2016/5/10.
 */
@Entity
@Table(name = "res_site_cookie")
public class SiteCookie implements Serializable {

    public SiteCookie() {
    }

    public SiteCookie(String siteId,String domain, String cookie) {
        this.siteId = siteId;
        this.domain = domain;
        this.cookie = cookie;
    }

    public SiteCookie(String cookie, String siteId, String domain, String loginName) {
        this.cookie = cookie;
        this.siteId = siteId;
        this.domain = domain;
        this.loginName = loginName;
    }

    public SiteCookie(String domain, String cookie) {
        this.domain = domain;
        this.cookie = cookie;
    }

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    private String id;

    @NotNull
    @Column(name = "cookie", columnDefinition = "text")
    private String cookie;

    @NotNull
    @Column(columnDefinition = "varchar(38)")
    private String siteId;

    @NotNull
    private String domain;

    @NotNull
    @Column(name = "available",insertable = false,columnDefinition = "char default '1'")
    private String  available;

    @Column(name = "createdDate", columnDefinition = "timestamp default now()")
    private Date createdDate;

    @Column(name = "lastUpdatedDate", columnDefinition = "timestamp default now()")
    private Date lastUpdatedDate;

    @NotNull
    @Column(name = "login_name",columnDefinition = "varchar(500)")
    private String loginName;

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }


    public String getAvailable() {
        return available;
    }

    public void setAvailable(String available) {
        this.available = available;
    }

    @DateTimeFormat(pattern="yyyy-MM-dd")
    @JsonFormat(pattern="yyyy-MM-dd",timezone = "GMT+8")
    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(Date lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }
}
