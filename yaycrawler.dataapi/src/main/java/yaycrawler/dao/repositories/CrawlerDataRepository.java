package yaycrawler.dao.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import yaycrawler.dao.domain.CrawlerData;
import yaycrawler.dao.domain.CrawlerTask;

/**
 * Created by  yuananyun on 2017/3/24.
 */
@Repository
public interface CrawlerDataRepository extends PagingAndSortingRepository<CrawlerData, String> {

}
