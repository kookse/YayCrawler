package yaycrawler.dao.domain;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "v_user")       //liferay实体中User用户
public class User {

    public User() {
        super();
    }


    @Id                                //一定有有主键
    @Column(name = "uid")
    private String uid;

    @Column(name = "email")
    private String email;


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


}