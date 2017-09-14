package yaycrawler.admin.mapper;

import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import yaycrawler.dao.domain.User;

import java.util.List;
import java.util.Map;

/**
 * @author bill
 * @create 2017-09-14 18:00
 * @desc
 **/

@Repository
public interface UsereMapper {

    @Select("select * from v_user")
    public List<User> findAllUser();
}
