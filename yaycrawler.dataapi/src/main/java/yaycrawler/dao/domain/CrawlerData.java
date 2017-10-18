package yaycrawler.dao.domain;

import com.alibaba.fastjson.JSON;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.util.Date;
import java.util.Map;

@Entity
@Table(name = "crawler_data")
@TypeDef(name = "JsonDataUserType", typeClass = JsonDataUserType.class)
public class CrawlerData {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator="s_gen")
    @SequenceGenerator(name="s_gen",sequenceName="crawler_task_id_seq")
    private Integer id;

    /**
     * 任务的标识
     */
    @Column(name = "code" , columnDefinition = "varchar(300)",unique = true)
    private String code;

    @Column(name = "orderId" , columnDefinition = "varchar(300)")
    private String orderId;

    /**
     * 请求的数据（JSON字符串）
     */
    @Column(name = "data",columnDefinition="text")
    @Type(type = "JsonDataUserType")
    private String data;

    @Column(name = "created_time",columnDefinition = "timestamp default (now())")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdTime = new Date();
    @Column(name = "pageUrl" , columnDefinition = "varchar(300)")
    private String pageUrl;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public String getPageUrl() {
        return pageUrl;
    }

    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Map getDataMap() {
        if (this.data != null) {
            try {
                return JSON.parseObject(data, Map.class);
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
        }
        return null;
    }

    public void setData(Map data) {
        if (data != null)
            setData(JSON.toJSONString(data));
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}
