package yaycrawler.dao.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import yaycrawler.dao.domain.PageInfo;

import javax.persistence.OrderBy;
import java.util.List;


/**
 * Created by ucs_yuananyun on 2016/5/10.
 */
@Repository
public  interface PageInfoRepository extends CrudRepository<PageInfo, String> {

    @Query(value="select *  from conf_page_info pi where ?  ~  pi.url_rgx ORDER BY url_rgx desc limit 1",nativeQuery = true)
    PageInfo findOneByUrlRgx(String url);

    @OrderBy("createdDate desc ")
    Page<PageInfo> findAllByOrderByCreatedDateDesc(Pageable pageable);

    List<PageInfo> findByCityCodeAndCategory(String cityCode,Integer category);

}
