package yaycrawler.monitor.captcha.geetest;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import yaycrawler.common.utils.CasperjsProgramManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 极验验证码识别
 * Created by ucs_yuananyun on 2016/6/14.
 */
public class GeetestCaptchaIdentification {
    private static final Logger logger = LoggerFactory.getLogger(GeetestCaptchaIdentification.class);

    /**
     * 验证码自动识别     *
     *
     * @param pageUrl             包含验证码的页面url
     * @param jsFileName        要执行的js文件的名称
     * @param deltaResolveAddress 能够解析验证码移动位移的服务地址
     * @return
     */
    public static boolean process(String pageUrl,String domain,String cookies,String jsFileName, String deltaResolveAddress) {
        logger.info("滑块位置服务：" + deltaResolveAddress);
        if (pageUrl == null) return false;
        boolean flag = startIdentification(pageUrl,domain,cookies,jsFileName, deltaResolveAddress);
        if (!flag) {
            int i = 0;
            while (i++ < 1) {
                flag = startIdentification(pageUrl,domain,cookies,jsFileName, deltaResolveAddress);
                logger.info("第{}次重试！", i);
                if (flag) break;
            }
        }
        return flag;
    }

    private static boolean startIdentification(String pageUrl,String domain,String cookies,String jsFileName, String deltaResolveAddress) {
        List<String> paramList = new ArrayList<>();
        paramList.add(String.format("--pageUrl=%s",pageUrl));
        paramList.add(String.format("--deltaResolveAddress=%s",deltaResolveAddress));
        paramList.add(String.format("--domain=%s",domain));
        paramList.add(String.format("--cookies=%s",cookies));
        paramList.add(String.format("--searchword=%s","小米"));
        String result = CasperjsProgramManager.launch(jsFileName, paramList);
        logger.info("验证码识别结果：\r\n" + result);
        return result != null && (result.contains("验证通过") || result.contains("不存在极验验证码"));
    }

    public static void main(String[] args) throws InterruptedException {
        int totalCount = 20;
        int successCount = 0;
        int retryCount = 0;
//        String pageUrl = "http://www.qichacha.com/company_getinfos?unique=9cce0780ab7644008b73bc2120479d31&companyname=%E5%B0%8F%E7%B1%B3%E7%A7%91%E6%8A%80%E6%9C%89%E9%99%90%E8%B4%A3%E4%BB%BB%E5%85%AC%E5%8F%B8&tab=susong";
        String pageUrl = "http://www.gsxt.gov.cn/corp-query-homepage.html";
        String deltaResolveAddress = "http://localhost:8086/worker/resolveGeetestSlicePosition";
        StopWatch stopWatch = new StopWatch();
        for (int i = 0; i < totalCount; i++) {
            stopWatch.reset();
            stopWatch.start();
            if (startIdentification(pageUrl,null,null,"d:/tmp/js/geetest_refresh.js", deltaResolveAddress))
                successCount++;
            else {
                int t = retryCount;
                while (t > 0) {
                    System.out.println("重试一次");
                    if (startIdentification(pageUrl, null,null,"d:/tmp/js/geetest_refresh.js",deltaResolveAddress)) {
                        successCount++;
                        break;
                    }
                    t--;
                }
            }
            stopWatch.stop();
            System.out.println("本次调用耗时：(毫秒)" + stopWatch.getTime());
        }
        System.out.println("调用" + totalCount + "次，失败重试" + retryCount + "次的情况下，共成功" + successCount + "次");
    }
}
