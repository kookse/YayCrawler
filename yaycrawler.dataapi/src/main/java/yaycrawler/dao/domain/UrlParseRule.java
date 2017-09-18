package yaycrawler.dao.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sun.istack.internal.NotNull;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

/**
 * Created by yuananyun on 2016/5/1.
 */
@Entity
@Table(name = "conf_url_rule")
public class UrlParseRule implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    private String id;

    @NotNull
    @Column(columnDefinition = "varchar(38) not null")
    private String regionId;

    @NotNull
    private String rule;
    @NotNull
    private String method;


    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "urlRuleId", insertable = false, updatable = false)
    private Set<UrlRuleParam> urlRuleParams;

    @Column(name = "createdDate", columnDefinition = "timestamp default (now())")
    private Date createdDate = new Date();

    public UrlParseRule() {
        method = "GET";
    }

    public UrlParseRule(String rule) {
        this();
        this.rule = rule;
    }


    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRegionId() {
        return regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }

    public Set<UrlRuleParam> getUrlRuleParams() {
        return urlRuleParams;
    }

    public void setUrlRuleParams(Set<UrlRuleParam> urlRuleParams) {
        this.urlRuleParams = urlRuleParams;
    }

    @DateTimeFormat(pattern="yyyy-MM-dd")
    @JsonFormat(pattern="yyyy-MM-dd",timezone = "GMT+8")
    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
}
