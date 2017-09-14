package yaycrawler.admin.mapper;

import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface PageInfoMapper {

    @Select("select * from conf_page_info")
    public List<Map> findAllPageInfo();
}
