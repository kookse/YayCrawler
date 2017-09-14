package yaycrawler.admin;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import yaycrawler.admin.mapper.PageInfoMapper;
import yaycrawler.admin.service.CrawlerResultRetrivalService;

import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
public class MapperTest {

    private static final Logger logger = LoggerFactory.getLogger(MapperTest.class);

    @Autowired
    private PageInfoMapper pageInfoMapper;

    @Test
    public void listPageInfo() {
        List<Map> datas = pageInfoMapper.findAllPageInfo();
        datas.forEach(data -> {
            data.forEach((key,value) -> {
               logger.info("{}:{}",key,value);
            });
        });
        System.out.println(datas.size());
    }
}
