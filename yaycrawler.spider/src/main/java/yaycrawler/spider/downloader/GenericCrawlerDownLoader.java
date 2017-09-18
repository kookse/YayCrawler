package yaycrawler.spider.downloader;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.downloader.AbstractDownloader;
import us.codecraft.webmagic.selector.Selectable;
import us.codecraft.webmagic.utils.UrlUtils;
import yaycrawler.api.engine.Engine;
import yaycrawler.api.process.SpringContextUtil;
import yaycrawler.api.resolver.CrawlerExpressionResolver;
import yaycrawler.common.model.CrawlerCookie;
import yaycrawler.common.model.EngineResult;
import yaycrawler.common.model.LoginParam;
import yaycrawler.common.model.PhantomCookie;
import yaycrawler.dao.domain.PageInfo;
import yaycrawler.dao.domain.PageSite;
import yaycrawler.dao.domain.SiteCookie;
import yaycrawler.dao.service.PageCookieService;
import yaycrawler.dao.service.PageParserRuleService;
import yaycrawler.monitor.captcha.CaptchaIdentificationProxy;
import yaycrawler.monitor.login.AutoLoginProxy;
import yaycrawler.spider.cookie.DynamicCookieManager;
import yaycrawler.spider.listener.IPageParseListener;
import yaycrawler.spider.processor.GenericPageProcessor;
import yaycrawler.spider.service.PageSiteService;
import yaycrawler.spider.utils.RequestHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by ucs_yuananyun on 2016/5/27.
 */
@Component
public class GenericCrawlerDownLoader extends AbstractDownloader {

    private static final Logger logger  = LoggerFactory.getLogger(GenericCrawlerDownLoader.class);

    @Autowired
    private PageParserRuleService pageParserRuleService;
    @Autowired
    private PageCookieService pageCookieService;

    @Autowired
    private DynamicCookieManager dynamicCookieManager;

    @Autowired
    private GenericPageProcessor genericPageProcessor;

    private CrawlerHttpClientDownloader httpClientDownloader;
    private PhantomJsMockDonwnloader mockDonwnloader;


    public GenericCrawlerDownLoader() {
        httpClientDownloader = new CrawlerHttpClientDownloader();
        mockDonwnloader = new PhantomJsMockDonwnloader();
    }

    private static Pattern redirectPattern = Pattern.compile("<script.*(?s)setInterval.*location.href\\s*=.*(?s).*</script>");

    @Override
    public Page download(Request request, Task task) {
        PageInfo pageInfo = pageParserRuleService.findOnePageInfoByRgx(request.getUrl());
        if(pageInfo == null && request.getExtra("$pageInfo") != null) {
            pageInfo = (PageInfo) request.getExtra("$pageInfo");
        }
        boolean isJsRendering = pageInfo != null && "1".equals(pageInfo.getIsJsRendering());
        String pageUrl = request.getUrl();
        String loginName = request.getExtra("loginName") != null? request.getExtra("loginName").toString(): RequestHelper.getParam(request.getUrl(),"loginName");
        SiteCookie siteCookie = pageCookieService.getCookieByUrl(pageUrl,loginName);
        String cookie ="";
        while (StringUtils.isNotEmpty(loginName) && siteCookie == null) {
            boolean doRecovery = genericPageProcessor.doAutomaticRecovery(Page.fail(),request,request.getUrl());
            if(doRecovery)
                siteCookie = pageCookieService.getCookieByUrl(pageUrl,loginName);
        }
        if(siteCookie!=null) {
            cookie=siteCookie.getCookie();
            String cookieId = siteCookie.getId();
            request.putExtra("cookieId", cookieId);
        }
        //获取动态的cookies
        List<CrawlerCookie> dynamicCookieList = dynamicCookieManager.getCookiesByDomain(UrlUtils.getDomain(pageUrl));
        if(dynamicCookieList!=null){
            cookie += ";";
            for (CrawlerCookie crawlerCookie : dynamicCookieList) {
                cookie += String.format("%s=%s", crawlerCookie.getName(), crawlerCookie.getValue());
            }
        }
        request = RequestHelper.createRequest(request.getUrl(),request.getMethod(),request.getExtras());
        Page page = !isJsRendering ? httpClientDownloader.download(request, task, cookie) : mockDonwnloader.download(request, task, cookie);
        if (!isJsRendering && (!"post".equalsIgnoreCase(request.getMethod())&&page != null) && page.getRawText() != null && redirectPattern.matcher(page.getRawText()).find())
            page = mockDonwnloader.download(request, task, cookie);
        if(page != null && page.getRawText() == null)
            return null;
        return page;
    }

    @Override
    public void setThread(int threadNum) {
        httpClientDownloader.setThread(threadNum);
        mockDonwnloader.setThread(threadNum);
    }

}
