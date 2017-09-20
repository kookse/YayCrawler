package yaycrawler.worker;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import yaycrawler.dao.domain.PageInfo;
import yaycrawler.dao.repositories.PageInfoRepository;
import yaycrawler.dao.service.PageParserRuleService;
import yaycrawler.spider.pipeline.GenericPipeline;
import yaycrawler.spider.service.PageSiteService;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
public class ApplicationTests {

	@Autowired
	private GenericPipeline genericPipeline;

	@Autowired
	private PageParserRuleService parserRuleService;
	@Autowired
	private PageSiteService pageSiteService;

	@Autowired
	private PageInfoRepository pageInfoRepository;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Test
	public void testMongoDB()
	{
		Map<String, Object> data = new HashMap<>();
		data.put("name", "zhangsan");
		data.put("age", 28);
		mongoTemplate.save(data, "yay");
	}


	@Test
	public void testSpider()
	{
//		String seedUrl = "http://floor.0731fdc.com/jggs.php";
//		GenericPageProcessor pageProcessor = new GenericPageProcessor();
//
//		Downloader downloader = new HttpClientDownloader();
//		Scheduler scheduler =new QueueScheduler();
//
//		Site site = pageSiteService.getSite("floor.0731fdc.com");
	}

	@Test
	public void testFile() {
		final String path = "d:/";
		File dst = new File("dataTmp");
		if(dst != null && dst.isDirectory()) {
			File[] dstfiles = dst.listFiles();
			if(dstfiles!= null && dstfiles.length == 0) {
				for (File file :dstfiles) {
					if(file.isDirectory()) {
						File[] files = file.listFiles();
						if(files != null && files.length > 20) {
							System.out.println(file.getName());
						}
					}
				}
			}
		}

	}

	@Test
	public void findOnePageInfoByRgx() {
		PageInfo pageInfo = pageInfoRepository.findOneByUrlRgx("http://mtax.gdltax.gov.cn/appserver/zrr/grsds/queryGrsdsNsmx.do?startdate=20170601&enddate=20170930&callback=jsonp_callback4&time=1505464152982&timeOut=60000");
		System.out.println(pageInfo);
	}

}
