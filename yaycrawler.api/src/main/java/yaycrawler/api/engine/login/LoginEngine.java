package yaycrawler.api.engine.login;

import com.alibaba.fastjson.JSON;
import com.google.common.io.Files;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import us.codecraft.webmagic.utils.UrlUtils;
import yaycrawler.api.engine.Engine;
import yaycrawler.api.engine.ocr.BinaryEngine;
import yaycrawler.api.engine.ocr.OCREngine;
import yaycrawler.api.process.SpringContextUtil;
import yaycrawler.common.model.BinaryDto;
import yaycrawler.common.model.EngineResult;
import yaycrawler.common.model.LoginParam;
import yaycrawler.common.model.PhantomCookie;
import yaycrawler.common.utils.CasperjsProgramManager;
import yaycrawler.common.utils.HttpUtil;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author bill
 * @create 2017-08-29 15:18
 * @desc 广州公积金登陆引擎
 **/
@Service("loginEngine")
public class LoginEngine implements Engine<LoginParam> {

    private static final Logger logger = LoggerFactory.getLogger(LoginEngine.class);

    @Value("${login.engine.validate:self.location =  \"(.*)\"}")
    private String regexParam;

    @Value("${resolver.worker.address:http://localhost:8086/worker/resolveGeetestSlicePosition}")
    private String resolverAddress;

    @Override
    public EngineResult execute(LoginParam info) {
        EngineResult engineResult = executeEngineWithFailover(info);
        return engineResult;
    }

    @Override
    public List<EngineResult> execute(List<LoginParam> info) {
        return null;
    }

    public EngineResult executeEngineWithFailover(LoginParam loginParam) {

        EngineResult engineResult = new EngineResult();
        HttpUtil httpUtil = HttpUtil.getInstance();
        String loginUrl = loginParam.getLoginUrl();
        Map params = loginParam.getNewParams();
        List<Header> headerList = new ArrayList<>();
        headerList.add(new BasicHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36"));
        headerList.add(new BasicHeader("Cookie", loginParam.getCookie()));
        Pattern pattern = Pattern.compile(loginParam.getValideLogin() != null ?loginParam.getValideLogin():"");
        Matcher matcher;
        Engine encryptEngine = (Engine) SpringContextUtil.getBean("encryptEngine");
        int i = 0;
        while (i < 10) {
            try {
                if (loginParam.getJsStatus()) {
                    String userName = loginParam.getUsername();
                    String password = loginParam.getPassword();
                    if (StringUtils.isBlank(userName)) return null;
                    if (StringUtils.isBlank(password)) return null;

                    String result = null;
                    List<String> paramList = new ArrayList<>();
                    loginParam.getNewParams().forEach((name, value) -> {
                        paramList.add(String.format("--%s=%s", name, value));
                    });
                    paramList.add(String.format("--%s=%s", "loginUrl", loginUrl));
                    paramList.add(String.format("--%s=%s", "resolverAddress", resolverAddress));
                    result = CasperjsProgramManager.launch(loginParam.getLoginJsFileName(), paramList);
                    logger.info(result);
                    assert result != null;
                    if (result.contains("Automatic login success")) {
                        String cookie = StringUtils.substringBetween(result, "$CookieStart", "$CookieEnd");
                        if (StringUtils.isNotEmpty(cookie)) {
                            List<PhantomCookie> phantomCookies = JSON.parseArray(cookie, PhantomCookie.class);
                            StringBuilder cookieBuild = new StringBuilder();
                            phantomCookies.forEach(
                                    phantomCookie -> {
                                        if (phantomCookie.getName().equalsIgnoreCase("Cookie"))
                                            cookieBuild.append(String.format("%s;", phantomCookie.getValue()));
                                        else
                                            cookieBuild.append(String.format("%s=%s;", phantomCookie.getName(), phantomCookie.getValue()));
                                    });
                            headerList.forEach(header -> {
                                if (StringUtils.equalsIgnoreCase(header.getName(), "Cookie")) {
                                    PhantomCookie phantomCookie = new PhantomCookie(header.getName(), header.getValue());
                                    phantomCookies.add(phantomCookie);
                                }
                            });
                            engineResult.setPhantomCookies(phantomCookies);
                            headerList.add(new BasicHeader("Cookie", cookieBuild.toString()));
                            engineResult.setStatus(true);
                            break;
                        }
                    } else {
                        engineResult.setStatus(false);
                    }

                } else {
                    HttpResponse response = httpUtil.doPost(loginUrl, null, params, headerList);
                    String content = EntityUtils.toString(response.getEntity());
                    if (StringUtils.isEmpty(content) && response.getStatusLine().getStatusCode() == 302) {
                        response = httpUtil.doGet(loginUrl, params, headerList);
                        content = EntityUtils.toString(response.getEntity());
                    }
                    matcher = pattern.matcher(content);
                    if (matcher.find()) {
                        Header[] headers;
                        while (response.getStatusLine().getStatusCode() == 302) {
                            String url = response.getFirstHeader("Location").getValue();
                            headers = response.getHeaders("Set-Cookie");
                            for (Header header : headers) {
                                headerList.add(new BasicHeader("Cookie", header.getValue()));
                            }
                            response = httpUtil.doPost(url, null, null, headerList);
                        }
                        if (response.getStatusLine().getStatusCode() == 200) {
                            headers = response.getHeaders("Set-Cookie");
                            for (Header header : headers) {
                                headerList.add(new BasicHeader("Cookie", header.getValue()));
                            }
                        }
                        //System.out.println(EntityUtils.toString(httpUtil.doGet("https://gzgjj.gov.cn:8280/fund/wap/wap!depositQuery.do?rdm=0.5121620276304217",null,headerList).getEntity()));
                        //List<Header> headerList1 = new ArrayList<>();
                        List<PhantomCookie> phantomCookies = new ArrayList<>();
                        headerList.forEach(header -> {
                            if (StringUtils.equalsIgnoreCase(header.getName(), "Cookie")) {
                                PhantomCookie phantomCookie = new PhantomCookie(header.getName(), header.getValue());
                                phantomCookies.add(phantomCookie);
                            }
                        });
                        engineResult.setHeaders(headerList);
                        engineResult.setPhantomCookies(phantomCookies);
                        engineResult.setStatus(Boolean.TRUE);
                        break;
                    } else {
                        engineResult.setStatus(Boolean.FALSE);
                    }
                }
            } catch (Exception e) {
                engineResult = failureCallback(loginParam, e);
                logger.error("pageUrl {} Exception {}", loginUrl, e);
            }
            loginParam.getOldParams().put("$requestUrl", loginParam.getUrl());
            loginParam = encryptEngine.execute(loginParam.getOldParams()).getLoginParam();
            params = loginParam.getNewParams();
            i++;
        }
        if (i >= 10) {
            logger.info("登陆失败！");
        } else {
            try {
                HttpResponse response = httpUtil.doGet(loginParam.getUrl(), null, headerList);
                List<PhantomCookie> phantomCookies = engineResult.getPhantomCookies();
                Header[] headers = response.getHeaders("Set-Cookie");
                for (Header header : headers) {
                    PhantomCookie phantomCookie = new PhantomCookie(header.getName(), header.getValue());
                    phantomCookies.add(phantomCookie);
                    headerList.add(new BasicHeader("Cookie", header.getValue()));
                }
                String content = EntityUtils.toString(response.getEntity());
                pattern = Pattern.compile(regexParam);
                matcher = pattern.matcher(content);
                String url;
                while (matcher.find()) {
                    for (int j = 1; j <= matcher.groupCount(); j++) {
                        url = matcher.group(j);
                        if (StringUtils.isNotEmpty(url)) {
                            if (!StringUtils.startsWithAny(url, "http", "https"))
                                url = StringUtils.substringBeforeLast(loginParam.getUrl(), "/") + "/" + url;
                            response = httpUtil.doGet(url, null, headerList);
                            headers = response.getHeaders("Set-Cookie");
                            for (Header header : headers) {
                                PhantomCookie phantomCookie = new PhantomCookie(header.getName(), header.getValue());
                                phantomCookies.add(phantomCookie);
                                headerList.add(new BasicHeader("Cookie", header.getValue()));
                            }
                            content = EntityUtils.toString(response.getEntity());
                            break;
                        }
                    }
                    matcher = pattern.matcher(content);
                }
                engineResult.setPhantomCookies(phantomCookies);
                engineResult.setResult(content);
            } catch (Exception e) {
                logger.error("pageUrl {} Exception {}", loginUrl, e);
            }

        }
        return engineResult;
    }
}
