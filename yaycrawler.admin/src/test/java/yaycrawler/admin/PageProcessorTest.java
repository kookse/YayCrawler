package yaycrawler.admin;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import yaycrawler.spider.processor.GenericPageProcessor;

/**
 * @author bill
 * @create 2017-08-29 14:53
 * @desc 测试登陆引擎
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
public class PageProcessorTest {

    @Autowired
    private GenericPageProcessor pageProcessor;

    @Test
    public void testGZGJJProcess() {
        Page page = new Page();
        Request request = new Request("https://gzgjj.gov.cn:8280/fund/wap/wap!depositQuery.do?rdm=0.5121620276304217");
        request.putExtra("$loginUrl","https://gzgjj.gov.cn:8280/fund/wap/wap!userLogin.do");
        request.putExtra("$valideLogin",".*\"ret_code\":[0|2|3]");
        request.putExtra("username","445281198903100953");
        request.putExtra("$pwd","base64(890310)");
        request.putExtra("$code","downloadEngine(https://gzgjj.gov.cn:8280/fund/wap/wap!getImgCode.do).ocrEngine($1$$[0-9a-zA-z]{4})");
        page.setRequest(request);
        pageProcessor.process(page);
    }

    @Test
    public void testGZGJJProcessSite() {
        Page page = new Page();
        Request request = new Request("https://gzgjj.gov.cn:8280/fund/wap/wap!depositQuery.do?rdm=0.5121620276304217");
        request.putExtra("loginName","445281198903100953");
//        request.putExtra("#username","loginName");
        request.putExtra("loginPassword","890310");
//        request.putExtra("#pwd","base64(loginPassword)");
//        request.putExtra("$code","downloadEngine(https://gzgjj.gov.cn:8280/fund/wap/wap!getImgCode.do).ocrEngine($1$$[0-9a-zA-z]{4})");
        page.setRequest(request);
        pageProcessor.process(page);
    }

    @Test
    public void testGZLSSProcess() {

        Page page = new Page();
        Request request = new Request("http://gzlss.hrssgz.gov.cn/gzlss_web/business/tomain/main.xhtml");
        request.putExtra("$loginUrl","http://gzlss.hrssgz.gov.cn/cas/login");
        request.putExtra("$valideLogin","http://gzlss.hrssgz.gov.cn:80/gzlss_web/business/tomain/main.xhtml");
        request.putExtra("$username","rsa(\"var modulus=\"(.*)\"\"$$\"var exponent=\"(.*)\"\"$$431027197805183119)");
        request.putExtra("$password","rsa(\"var modulus=\"(.*)\"\"$$\"var exponent=\"(.*)\"\"$$g123456)");
        request.putExtra("$yzm","downloadEngine(http://gzlss.hrssgz.gov.cn/cas/captcha.jpg).binaryEngine($1$$178$$13$$160).ocrEngine($1$$[0-9a-zA-z]{4})");
        request.putExtra("usertype", "2");
        request.putExtra("_eventId", "submit");
        request.putExtra("$lt", "regex(<input type=\"hidden\" name=\"lt\" value=\"(.*)\")");
        page.setRequest(request);
        pageProcessor.process(page);
    }

    @Test
    public void testGZLSSProcessSite() {
        Page page = new Page();
        Request request = new Request("http://gzlss.hrssgz.gov.cn/gzlss_web/business/tomain/main.xhtml");
        request.putExtra("loginName","431027197805183119");
        request.putExtra("loginPassword","g123456");
        page.setRequest(request);
        pageProcessor.process(page);
    }

    @Test
    public void testGdltaxProcess() {
        Page page = new Page();
        Request request = new Request("http://mtax.gdltax.gov.cn/appserver/zrr/grsds/queryGrsdsNsmx.do");
        request.putExtra("$loginUrl","http://mtax.gdltax.gov.cn/appserver/security/user/tpLogin.do");
        request.putExtra("$valideLogin",".*\"flag\":\"ok\"");
        request.putExtra("phonenum","15626241465");
        request.putExtra("password","jaB4Gz143AtQ");
        request.putExtra("$yzm","downloadEngine(http://mtax.gdltax.gov.cn/appserver/security/binduser/captcha.do).ocrEngine($1$$[0-9a-zA-z]{4})");
        request.putExtra("#accountInfoStr","json(phonenum$$password$$yzm).encode($1).encode($1)");
        request.putExtra("callback", "jsonp_callback4");
        request.putExtra("timeOut", "100000");
        request.putExtra("$time", "currentTimeMillis()");
        page.setRequest(request);
        pageProcessor.process(page);
    }
}
