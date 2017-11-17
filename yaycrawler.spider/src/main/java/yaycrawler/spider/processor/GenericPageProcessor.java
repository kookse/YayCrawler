package yaycrawler.spider.processor;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Json;
import us.codecraft.webmagic.selector.Selectable;
import yaycrawler.api.resolver.CrawlerExpressionResolver;
import yaycrawler.common.model.CrawlerRequest;
import yaycrawler.common.status.CrawlerStatus;
import yaycrawler.common.utils.PinYinUtil;
import yaycrawler.dao.domain.*;
import yaycrawler.dao.mapper.CrawlerTaskMapper;

import yaycrawler.dao.service.PageParserRuleService;
import yaycrawler.spider.listener.IPageParseListener;


import java.util.*;

/**
 * Created by yuananyun on 2016/5/1.
 */
@Component(value = "genericPageProcessor")
public class GenericPageProcessor implements PageProcessor {
    private static final Logger logger = LoggerFactory.getLogger(GenericPageProcessor.class);
    private static String DEFAULT_PAGE_SELECTOR = "page";

    @Autowired(required = false)
    private IPageParseListener pageParseListener;
    @Autowired
    private PageParserRuleService pageParserRuleService;

    @Autowired
    private CrawlerTaskMapper crawlerTaskMapper;

    @Override
    public void process(Page page) {
        Request pageRequest = page.getRequest();
        String pageUrl = pageRequest.getUrl();

        //是否正确的页面
        PageInfo pageInfo = pageParserRuleService.findOnePageInfoByRgx(pageUrl);
        if (pageInfo == null) return;
        String pageValidationExpression = pageInfo.getPageValidationRule();
//        if (pageValidated(page, pageValidationExpression)) {
            try {
                List<CrawlerRequest> childRequestList = new LinkedList<>();
                Set<PageParseRegion> regionList = pageParserRuleService.getPageRegions(pageUrl);
                for (PageParseRegion pageParseRegion : regionList) {
                    Object result = parseOneRegion(page, pageParseRegion, childRequestList);
                    if (result != null) {
                        if (result instanceof List)
                            ((List) result).add(pageParseRegion.getDataType());
                        page.putField(PinYinUtil.converterToFirstSpell(pageParseRegion.getName()), result);
                    }
                }
                if (pageParseListener != null) {
                    pageParseListener.onSuccess(pageRequest, childRequestList);
                }
                //crawlerTaskMapper.updateCrawlerTaskStatus(DigestUtils.sha1Hex(pageRequest.getUrl()),"", CrawlerStatus.SUCCESS.getStatus(), CrawlerStatus.SUCCESS.getMsg());
            } catch (Exception ex) {
                logger.error(ex.getMessage());
                if (pageParseListener != null) {
                    pageParseListener.onError(pageRequest, "页面解析失败");
                }
                //crawlerTaskMapper.updateCrawlerTaskStatus(DigestUtils.sha1Hex(getUniqueUrl(pageRequest)),"", CrawlerStatus.FAILURE.getStatus(), CrawlerStatus.FAILURE.getMsg());
            }
//        }
//        else {
//            //页面下载错误，验证码或cookie失效
//            if (pageParseListener != null)
//                pageParseListener.onError(pageRequest, "下载的页面不是我想要的");
//        }
    }

    private String getUniqueUrl(Request request) {
        if (request.getExtras() == null)
            return request.getUrl();
        StringBuilder urlBuilder = new StringBuilder(request.getUrl().trim());
        String random = DigestUtils.sha1Hex(JSON.toJSONString(request.getExtras()));
        urlBuilder.append(String.format("%s%s=%s", urlBuilder.indexOf("?") > 0 ? "&" : "?", "random", random));
        return urlBuilder.toString();
    }

    @SuppressWarnings("all")
    public Object parseOneRegion(Page page, PageParseRegion pageParseRegion, List<CrawlerRequest> childRequestList) {
        Request request = page.getRequest();
        String selectExpression = pageParseRegion.getSelectExpression();

        Selectable context = getPageRegionContext(page, request, selectExpression);
        if (context == null) return null;

        Set<UrlParseRule> urlParseRules = pageParseRegion.getUrlParseRules();
        if (urlParseRules != null && urlParseRules.size() > 0) {
            childRequestList.addAll(parseUrlRules(context, request, urlParseRules));
        }

        Set<FieldParseRule> fieldParseRules = pageParseRegion.getFieldParseRules();
        if (fieldParseRules != null && fieldParseRules.size() > 0) {
            return parseFieldRules(context, request, fieldParseRules, pageParseRegion.getDataType());
        }

        return null;
    }

    /**
     * 获取一个region的上下文
     *
     * @param page
     * @param request
     * @param regionSelectExpression
     * @return
     */
    public Selectable getPageRegionContext(Page page, Request request, String regionSelectExpression) {
        Selectable context;
        if (StringUtils.isBlank(regionSelectExpression) || DEFAULT_PAGE_SELECTOR.equals(regionSelectExpression))
            context = page.getHtml();
        else if (regionSelectExpression.toLowerCase().contains("getjson()") || regionSelectExpression.toLowerCase().contains("jsonpath"))
            context = CrawlerExpressionResolver.resolve(request, page.getJson(), regionSelectExpression);
        else
            context = CrawlerExpressionResolver.resolve(request, page.getHtml(), regionSelectExpression);
        return context;
    }

    /**
     * 解析一个字段抽取规则
     *
     * @param context
     * @param request
     * @param fieldParseRuleList
     * @return
     */
    private Object parseFieldRules(Selectable context, Request request, Collection<FieldParseRule> fieldParseRuleList, String dataType) {
        Map resultMap = new HashedMap();
        List<Map> resultData = Lists.newArrayList();
        List<Selectable> nodes = getNodes(context);
        for (Selectable node : nodes) {
            Map childMap = new HashedMap();
            Object label = null;
            Object value = null;
            for (FieldParseRule fieldParseRule : fieldParseRuleList) {
                if (StringUtils.equalsIgnoreCase(fieldParseRule.getFieldName(), "label")) {
                    label = CrawlerExpressionResolver.resolve(request, node, fieldParseRule.getRule());
                } else if (StringUtils.equalsIgnoreCase(fieldParseRule.getFieldName(), "value")) {
                    value = CrawlerExpressionResolver.resolve(request, node, fieldParseRule.getRule());
                } else {
                    childMap.put(fieldParseRule.getFieldName(), StringUtils.trimToEmpty(CrawlerExpressionResolver.resolve(request, node, fieldParseRule.getRule()).toString()));
                }
            }
            if (StringUtils.equalsIgnoreCase(dataType, "autoField")) {
                try {
                    if (label == null && value != null && value instanceof Collection) {
                        for (Object val : (Collection) value) {
                            childMap = new HashMap();
                            childMap.put("value", StringUtils.trimToEmpty(val.toString()));
                            resultData.add(childMap);
                        }
                    } else if (label != null && value != null && value instanceof Collection) {
                        int i = 0;
                        if (resultData.size() == 0) {
                            for (Object val : (Collection) value) {
                                resultData.add(Maps.newHashMap());
                            }
                        }
                        for (Object val : (Collection) value) {
                            childMap = resultData.get(i++);
                            childMap.put(PinYinUtil.converterToFirstSpell(label.toString()), StringUtils.trimToEmpty(val.toString()));
                        }
                    } else if (label != null && value == null) {
                        childMap.put("label", label);
                        resultData.add(childMap);
                    } else if (label == null && value != null) {
                        childMap.put("value", StringUtils.trimToEmpty(value.toString()));
                        resultData.add(childMap);
                    } else if (label != null && value != null) {
                        label = PinYinUtil.converterToFirstSpell(StringUtils.trimToEmpty(String.valueOf(label)));
                        if(StringUtils.isNotEmpty(label.toString()))
                            resultMap.put(label,StringUtils.trimToEmpty(value.toString()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                resultData.add(childMap);
            }
        }
        if ((nodes.size() >= 1 || StringUtils.equalsIgnoreCase(dataType, "autoField")) && resultData.size() > 0)
            return resultData;
        else {
            resultData.add(resultMap);
            return resultData;
        }
    }


    /**
     * 解析一个Url抽取规则
     *
     * @param context
     * @param request
     * @param urlParseRuleList
     * @return
     */
    private List<CrawlerRequest> parseUrlRules(Selectable context, Request request, Collection<UrlParseRule> urlParseRuleList) {
        List<CrawlerRequest> childRequestList = new LinkedList<>();
        List<Selectable> nodes = getNodes(context);

        for (Selectable node : nodes) {
            if (node == null) continue;

            for (UrlParseRule urlParseRule : urlParseRuleList) {
                //解析url
                Object u = CrawlerExpressionResolver.resolve(request, node, urlParseRule.getRule());
                //解析Url的参数
                Map<String, Object> urlParamMap = new HashMap<>();
                if (urlParseRule.getUrlRuleParams() != null)
                    for (UrlRuleParam ruleParam : urlParseRule.getUrlRuleParams()) {
                        urlParamMap.put(ruleParam.getParamName(), CrawlerExpressionResolver.resolve(request, node, ruleParam.getExpression()));
                    }
                //组装成完整的URL
                if (u instanceof Collection) {
                    Collection<String> urlList = (Collection<String>) u;
                    if (urlList.size() > 0)
                        for (String url : urlList)
                            childRequestList.add(new CrawlerRequest(url, urlParseRule.getMethod(), urlParamMap));
                } else
                    childRequestList.add(new CrawlerRequest(String.valueOf(u), urlParseRule.getMethod(), urlParamMap));
            }
        }
        return childRequestList;
    }


    private List<Selectable> getNodes(Selectable context) {
        List<Selectable> nodes = new LinkedList<>();

        if (context instanceof Json) {
            nodes.add(context);
        } else nodes.addAll(context.nodes());
        return nodes;
    }


    @Override
    public Site getSite() {
        return Site.me();
    }

    /**
     * 页面自动恢复
     *
     * @param page
     * @param pageRequest
     * @param pageUrl
     */
//    private boolean doAutomaticRecovery(Page page, Request pageRequest, String pageUrl,int status) {
//        boolean doRecovery = false;
//        PageSite pageSite = pageSiteService.getPageSiteByUrl(pageUrl);
//        if (pageSite != null) {
//            String loginJudgeExpression = pageSite.getLoginJudgeExpression();
//            String captchaJudgeExpression = pageSite.getCaptchaJudgeExpression();
//            String loginJsFileName = pageSite.getLoginJsFileName();
//            String captchaJsFileName = pageSite.getCaptchaJsFileName();
//            String oldCookieId = (String) pageRequest.getExtra("cookieId");
//
//            Selectable judgeContext = StringUtils.isNotBlank(loginJsFileName) ? getPageRegionContext(page, pageRequest, loginJudgeExpression) : null;
//            if (judgeContext != null && judgeContext.match()) {
//                doRecovery = true;
//                //需要登录了
//                autoLoginProxy.login(pageUrl, loginJsFileName, page.getRawText(), oldCookieId);
//                //重新加入队列
//                page.addTargetRequest(pageRequest);
//            } else {
//                judgeContext = StringUtils.isNotBlank(captchaJsFileName) ? getPageRegionContext(page, pageRequest, captchaJudgeExpression) : null;
//                if (judgeContext != null && judgeContext.match()) {
//                    doRecovery = true;
//                    //需要刷新验证码了
//                    captchaIdentificationProxy.recognition(pageUrl, captchaJsFileName, page.getRawText(), oldCookieId);
//                }
//            }
//        }
//        return doRecovery;
//    }



}
