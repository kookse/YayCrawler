package yaycrawler.dao.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import yaycrawler.dao.datasource.DatabaseType;
import yaycrawler.dao.datasource.MultiDataSource;
import yaycrawler.dao.domain.CrawlerData;

import java.util.List;

/**
 * @author bill
 * @create 2017-10-11 11:43
 * @desc 数据存储
 **/

@Repository
@MultiDataSource(DatabaseType.primary)
public interface CrawlerDataMapper {

    @Select("SELECT data1.order_id FROM " +
            "( SELECT order_id, COUNT (1) FROM crawler_task where order_id != null GROUP BY order_id ) AS data1," +
            " ( SELECT order_id, COUNT (1) FROM crawler_task WHERE status = #{status} and order_id != null GROUP BY order_id ) AS data2 " +
            "where data1.order_id = data2.order_id and data1.count = data2.count")
    List<String> getOrderIds(@Param("status") Integer status);

    @Select("select id as id,code as code," +
            "created_time as createdTime,data as data," +
            "page_url as pageUrl," +
            "order_id as orderId from crawler_data where order_id = #{orderId}")
    List<CrawlerData> findByOrderId(@Param("orderId") String orderId);


}
