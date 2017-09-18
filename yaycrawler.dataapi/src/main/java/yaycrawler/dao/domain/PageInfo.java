package yaycrawler.dao.domain;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.istack.internal.NotNull;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * 表示一个待抓取的页面测试信息
 * Created by ucs_yuananyun on 2016/5/10.
 */
@Entity
@Table(name = "conf_page_info",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"urlRgx"})})
public class PageInfo {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    private String id;

    @NotNull
    @Column(name = "pageUrl")
    private String pageUrl;

    @NotNull
    @Column(name = "urlRgx", columnDefinition = "varchar(1000)")
    private String urlRgx;

    @NotNull
    @Column(name = "method", columnDefinition = "varchar(10) default 'GET' ")
    private String method;

    @Column(name = "paramsJson", columnDefinition = "text")
    private String paramsJson;

    @NotNull
    @Column(name = "isJsRendering", columnDefinition = "char default '0' ")
    private String isJsRendering;

    @Column(name = "createdDate", columnDefinition = "timestamp default (now())")
    private Date createdDate = new Date();;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "pageId", insertable = false, updatable = false)
    private Set<PageParseRegion> pageParseRegions;

    @Column(name = "pageValidationRule",columnDefinition = "varchar(500)")
    private String pageValidationRule;

    public String getPageValidationRule() {
        return pageValidationRule;
    }

    public void setPageValidationRule(String pageValidationRule) {
        this.pageValidationRule = pageValidationRule;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getParamsJson() {
        return paramsJson;
    }

    public void setParamsJson(String paramsJson) {
        this.paramsJson = paramsJson;
    }

    public Map<String, Object> getParamsMap() {
        try {
            return JSON.parseObject(paramsJson, Map.class);
        } catch (Exception ex) {
            return null;
        }
    }

    @DateTimeFormat(pattern="yyyy-MM-dd")
    @JsonFormat(pattern="yyyy-MM-dd",timezone = "GMT+8")
    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getPageUrl() {
        return pageUrl;
    }

    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }

    public Set<PageParseRegion> getPageParseRegions() {
        return pageParseRegions;
    }

    public void setPageParseRegions(Set<PageParseRegion> pageParseRegions) {
        this.pageParseRegions = pageParseRegions;
    }


    public String getUrlRgx() {
        return urlRgx;
    }

    public void setUrlRgx(String urlRgx) {
        this.urlRgx = urlRgx;
    }

    public String getIsJsRendering() {
        return isJsRendering;
    }

    public void setIsJsRendering(String isJsRendering) {
        this.isJsRendering = isJsRendering;
    }
}
