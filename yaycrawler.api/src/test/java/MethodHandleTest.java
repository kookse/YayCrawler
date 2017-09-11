import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Lists;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.junit.Test;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Json;
import us.codecraft.webmagic.selector.PlainText;
import us.codecraft.webmagic.selector.Selectable;
import yaycrawler.api.resolver.CrawlerExpressionResolver;
import yaycrawler.api.selector.CrawlerSelectable;
import yaycrawler.common.utils.HttpUtil;

import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;

/**
 * @author bill
 * @create 2017-09-06 15:21
 * @desc MethodHandler test
 **/
public class MethodHandleTest {

    @Test
    public void css() {
        try {
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            MethodType methodType = MethodType.methodType(Selectable.class, String.class, String.class);
            MethodHandle methodHandle = lookup.findVirtual(Selectable.class, "css", methodType);
            Html html = new Html("!<!DOCTYPE html><html><head><title>test</title></head><body><div id=\"threadList\">test methodHandler</div></body></html>");
            Selectable selectable = (Selectable) methodHandle.invoke(html, "#threadList", "text");
            System.out.println(selectable.get());
            methodType = MethodType.methodType(CrawlerSelectable.class, String.class);
            methodHandle = lookup.findVirtual(CrawlerSelectable.class, "constant", methodType);
            CrawlerSelectable crawlerSelectable = new CrawlerSelectable("test constant");
            selectable = (Selectable) methodHandle.invoke(crawlerSelectable, "555555");
            System.out.println(selectable.get());

            methodType = MethodType.methodType(Selectable.class, String.class, int.class);
            methodHandle = lookup.findVirtual(Selectable.class, "regex", methodType);
            Object[] data = new Object[]{selectable, "test method", 0};

            selectable = (Selectable) methodHandle.invokeWithArguments(data);
            System.out.println(selectable.get());
            methodType = MethodType.methodType(List.class);
            methodHandle = lookup.findVirtual(Selectable.class, "all", methodType);
            List datas = (List) methodHandle.invokeWithArguments(new PlainText("test tt data"));
            System.out.println();
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    @Test
    public void testCrawler() throws Throwable {
        Request request = new Request();
        HttpUtil httpUtil = HttpUtil.getInstance();
        Html html = new Html(EntityUtils.toString(httpUtil.doGet("http://www.qisuu.com/35478.html", null, Lists.newArrayList(new BasicHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.101 Safari/537.36"))).getEntity(), "utf-8"));
        Object selectable = CrawlerExpressionResolver.resolve(request, html, "css(.position).css(a:gt(0)$$allText).all()");
        System.out.println(selectable);
    }

    @Test
    public void testHrssz() throws Throwable {
        Request request = new Request();
        HttpUtil httpUtil = HttpUtil.getInstance();
        Html html = new Html(EntityUtils.toString(httpUtil.doGet("http://localhost:8069/admin/hrssz", null, Lists.newArrayList(new BasicHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.101 Safari/537.36"))).getEntity(), "utf-8"));
        Object selectables = CrawlerExpressionResolver.resolve(request, html, "css(tr:gt(4)$$allText).getCrawler().array(0$$6)");
        if (selectables instanceof Selectable) {
            ((Selectable) selectables).nodes().forEach(selectable -> {
                System.out.println(selectable);
                Object data = null;
                for (int i = 0; i < 13; i++) {
                    try {
                        data = CrawlerExpressionResolver.resolve(request, selectable, String.format("getCrawler().split().index(%s)", i));
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                    System.out.println(data);
                }
            });
        }
    }

    @Test
    public void testQisuu() throws Throwable {
        Request request = new Request();
        HttpUtil httpUtil = HttpUtil.getInstance();
        Html html = new Html(EntityUtils.toString(httpUtil.doGet("http://www.qisuu.com/35478.html", null, Lists.newArrayList(new BasicHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.101 Safari/537.36"))).getEntity(), "utf-8"));
        Object selectables = CrawlerExpressionResolver.resolve(request, html, "css(.detail_right li:not(.link),.detail_right h1)");
        if (selectables instanceof Selectable) {
            ((Selectable) selectables).nodes().forEach(selectable -> {
                Object data = null;
                try {
                    data = CrawlerExpressionResolver.resolve(request, selectable, String.format("getCrawler().regexp(\">(.*)：.*<\"$$\"书籍信息\")"));
                    System.out.println(data);
                    data = CrawlerExpressionResolver.resolve(request, selectable, String.format("getCrawler().regexp(\">.*：(.*)<\"$$\"$1\").regexp(\"class=\"(.*)\"\"$$\"$1\").regexp(\">(.*)<\"$$\"$1\")"));
                    System.out.println(data);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            });
        }
    }

    @Test
    public void testDividend() throws Throwable {
        Request request = new Request();
        HttpUtil httpUtil = HttpUtil.getInstance();
        Html html = new Html(EntityUtils.toString(httpUtil.doGet("http://www.cninfo.com.cn/information/fund/dividend/150008.html", null, Lists.newArrayList(new BasicHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.101 Safari/537.36"))).getEntity(), "gb2312"));
        Object selectables = CrawlerExpressionResolver.resolve(request, html, "css(tr:gt(0)$$allText)");
        if (selectables instanceof Selectable) {
            ((Selectable) selectables).nodes().forEach(selectable -> {
                System.out.println(selectable);
                Object data = null;
                for (int i = 0; i < 4; i++) {
                    try {
                        data = CrawlerExpressionResolver.resolve(request, selectable, String.format("getCrawler().split().index(%s)", i));
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                    System.out.println(data);
                }
            });
        }
    }

    @Test
    public void testShareholders() throws Throwable {
        Request request = new Request();
        HttpUtil httpUtil = HttpUtil.getInstance();
        Html html = new Html(EntityUtils.toString(httpUtil.doGet("http://www.cninfo.com.cn/information/shareholders/000002.html", null, Lists.newArrayList(new BasicHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.101 Safari/537.36"))).getEntity(), "gb2312"));
        Object selectables = CrawlerExpressionResolver.resolve(request, html, "css(.clear tr:gt(0) td:not(.zx_data)$$allText).getCrawler().array(0$$84$$4)");
        if (selectables instanceof Selectable) {
            ((Selectable) selectables).nodes().forEach(selectable -> {
                System.out.println(selectable);
                Object data = null;
                for (int i = 0; i < 4; i++) {
                    try {
                        data = CrawlerExpressionResolver.resolve(request, selectable, String.format("getCrawler().index(%s)", i));
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                    System.out.println(data);
                }
            });
        }
    }

    @Test
    public void testStockstructure() throws Throwable {
        Request request = new Request();
        HttpUtil httpUtil = HttpUtil.getInstance();
        Html html = new Html(EntityUtils.toString(httpUtil.doGet("http://www.cninfo.com.cn/information/stockstructure/szmb000002.html", null, Lists.newArrayList(new BasicHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.101 Safari/537.36"))).getEntity(), "gb2312"));
        Object selectables = CrawlerExpressionResolver.resolve(request, html, "css(\".clear tr td\"$$allText).getCrawler().array(0$$76$$19$$4)");
        if (selectables instanceof Selectable) {
            ((Selectable) selectables).all().forEach(selectable -> {
                System.out.println(selectable);
                Object data = null;
                for (int i = 1; i < 19; i++) {
                    try {
                        data = CrawlerExpressionResolver.resolve(request, selectable, String.format("getCrawler().split().index(%s)", i));
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                    System.out.println(data);
                }
            });
        }
    }

    @Test
    public void testManagement() throws Throwable {
        Request request = new Request();
        HttpUtil httpUtil = HttpUtil.getInstance();
        Html html = new Html(EntityUtils.toString(httpUtil.doGet("http://www.cninfo.com.cn/information/management/szmb000002.html", null, Lists.newArrayList(new BasicHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.101 Safari/537.36"))).getEntity(), "gb2312"));
        Object selectables = CrawlerExpressionResolver.resolve(request, html, "css(tr:gt(0)$$allText)");
        if (selectables instanceof Selectable) {
            ((Selectable) selectables).nodes().forEach(selectable -> {
                System.out.println(selectable);
                Object data = null;
                for (int i = 0; i < 5; i++) {
                    try {
                        data = CrawlerExpressionResolver.resolve(request, selectable, String.format("getCrawler().split().index(%s)", i));
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                    System.out.println(data);
                }
            });
        }
    }

    @Test
    public void testInfoDividend() throws Throwable {
        Request request = new Request();
        HttpUtil httpUtil = HttpUtil.getInstance();
        Html html = new Html(EntityUtils.toString(httpUtil.doGet("http://www.cninfo.com.cn/information/dividend/szmb000002.html", null, Lists.newArrayList(new BasicHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.101 Safari/537.36"))).getEntity(), "gb2312"));
        Object selectables = CrawlerExpressionResolver.resolve(request, html, "css(tr:gt(0)$$allText)");
        if (selectables instanceof Selectable) {
            ((Selectable) selectables).nodes().forEach(selectable -> {
                System.out.println(selectable);
                Object data = null;
                for (int i = 0; i < 5; i++) {
                    try {
                        data = CrawlerExpressionResolver.resolve(request, selectable, String.format("getCrawler().split().index(%s)", i));
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                    System.out.println(data);
                }
            });
        }
    }

    @Test
    public void testInfoIssue() throws Throwable {
        Request request = new Request();
        HttpUtil httpUtil = HttpUtil.getInstance();
        Html html = new Html(EntityUtils.toString(httpUtil.doGet("http://www.cninfo.com.cn/information/issue/szmb000002.html", null, Lists.newArrayList(new BasicHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.101 Safari/537.36"))).getEntity(), "gb2312"));
        Object selectables = CrawlerExpressionResolver.resolve(request, html, "css(.zx_left table tr$$allText)");
        if (selectables instanceof Selectable) {
            ((Selectable) selectables).nodes().forEach(selectable -> {
                System.out.println(selectable);
                Object data = null;
                for (int i = 0; i < 2; i++) {
                    try {
                        data = CrawlerExpressionResolver.resolve(request, selectable, String.format("getCrawler().split().index(%s)", i));
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                    System.out.println(data);
                }
            });
        }
    }

    @Test
    public void testInfoLastest() throws Throwable {
        Request request = new Request();
        HttpUtil httpUtil = HttpUtil.getInstance();
        Html html = new Html(EntityUtils.toString(httpUtil.doGet("http://www.cninfo.com.cn/information/lastest/szmb000002.html", null, Lists.newArrayList(new BasicHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.101 Safari/537.36"))).getEntity(), "gb2312"));
        Object selectables = CrawlerExpressionResolver.resolve(request, html, "css(.zx_left table tr$$allText)");
        if (selectables instanceof Selectable) {
            ((Selectable) selectables).nodes().forEach(selectable -> {
                System.out.println(selectable);
                Object data = null;
                for (int i = 0; i < 2; i++) {
                    try {
                        data = CrawlerExpressionResolver.resolve(request, selectable, String.format("getCrawler().split().index(%s)", i));
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                    System.out.println(data);
                }
            });
        }
    }

    @Test
    public void testFyydrcb() throws Throwable {
        Request request = new Request();
        HttpUtil httpUtil = HttpUtil.getInstance();
        Json json = new Json(EntityUtils.toString(httpUtil.doGet("http://e.fyydrcb.com/f/memberBidInfo/fenyeBidInfo?pageNo=1", null, Lists.newArrayList(new BasicHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.101 Safari/537.36"))).getEntity(), "utf-8"));
        Object selectables = CrawlerExpressionResolver.resolve(request, json, "jsonPath(list).getCrawler().array(0$$10)");
        if (selectables instanceof Selectable) {
            ((Selectable) selectables).nodes().forEach(selectable -> {
                System.out.println(selectable);
                Object data = null;
                for (int i = 0; i < 25; i++) {
                    try {
                        data = CrawlerExpressionResolver.resolve(request, selectable, String.format("getCrawler().index(%s)", i));
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                    System.out.println(data);
                }
            });
        }
    }

    @Test
    public void testFund() throws Throwable {
        Request request = new Request();
        HttpUtil httpUtil = HttpUtil.getInstance();
        Html html = new Html(EntityUtils.toString(httpUtil.doGet("http://www.cninfo.com.cn/information/fund/financialreport/150008.html", null, Lists.newArrayList(new BasicHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.101 Safari/537.36"))).getEntity(), "gb2312"));
        Object selectables = CrawlerExpressionResolver.resolve(request, html, "css(\".clear tr td\"$$allText).getCrawler().array(0$$96$$2)");
        if (selectables instanceof Selectable) {
            ((Selectable) selectables).all().forEach(selectable -> {
                Object data = null;
                try {
                    data = CrawlerExpressionResolver.resolve(request, selectable, String.format("getCrawler().index(0)"));
                    System.out.println(data);
                    data = CrawlerExpressionResolver.resolve(request, selectable, String.format("getCrawler().index(1)"));
                    System.out.println(data);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            });
        }
    }

    @Test
    public void testEastmoney() throws Throwable {
        Request request = new Request();
        HttpUtil httpUtil = HttpUtil.getInstance();
        Html html = new Html(EntityUtils.toString(httpUtil.doGet("http://fund.eastmoney.com/fundguzhi.html", null, Lists.newArrayList(new BasicHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.101 Safari/537.36"))).getEntity(), "gb2312"));
        Object selectables = CrawlerExpressionResolver.resolve(request, html, "css(#oTable tbody tr$$allText)");
        if (selectables instanceof Selectable) {
            ((Selectable) selectables).nodes().forEach(selectable -> {
                System.out.println(selectable);
                Object data = null;
                for (int i = 0; i < 9; i++) {
                    try {
                        data = CrawlerExpressionResolver.resolve(request, selectable, String.format("getCrawler().split().index(%s)", i));
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                    System.out.println(data);
                }
            });
        }
    }

    @Test
    public void testCompanyInfo() throws Throwable {
        Request request = new Request();
        HttpUtil httpUtil = HttpUtil.getInstance();
        Html html = new Html(EntityUtils.toString(httpUtil.doGet("http://www.cninfo.com.cn/cninfo-new/information/companylist", null, Lists.newArrayList(new BasicHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.101 Safari/537.36"))).getEntity(), "gb2312"));
        Object selectables = CrawlerExpressionResolver.resolve(request, html, "links().regex(\\?fulltext\\?(.*))");
        if (selectables instanceof Selectable) {
            ((Selectable) selectables).nodes().forEach(selectable -> {
                System.out.println(selectable);
                Object data = null;
                try {
                    data = CrawlerExpressionResolver.resolve(request, selectable, "getCrawler().format(\"http://www.cninfo.com.cn/information/brief/%s.html\")");
                    System.out.println(data);
                    data = CrawlerExpressionResolver.resolve(request, selectable, "getCrawler().format(\"\"http://www.cninfo.com.cn/information/management/%s.html\")");
                    System.out.println(data);
                    data = CrawlerExpressionResolver.resolve(request, selectable, "getCrawler().format(\"http://www.cninfo.com.cn/information/dividend/%s.html\")");
                    System.out.println(data);
                    data = CrawlerExpressionResolver.resolve(request, selectable, "getCrawler().format(\"http://www.cninfo.com.cn/information/stockstructure/%s.html\")");
                    System.out.println(data);
                    data = CrawlerExpressionResolver.resolve(request, selectable, "getCrawler().format(\"http://www.cninfo.com.cn/information/lastest/%s.html\")");
                    System.out.println(data);
                    data = CrawlerExpressionResolver.resolve(request, selectable, "getCrawler().format(\"http://www.cninfo.com.cn/information/shareholders/%s.html\")");
                    System.out.println(data);
                    data = CrawlerExpressionResolver.resolve(request, selectable, "getCrawler().format(\"http://www.cninfo.com.cn/information/issue/%s.html\")");
                    System.out.println(data);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            });
        }
    }

    @Test
    public void testJisilu() throws Throwable {
        Request request = new Request();
        HttpUtil httpUtil = HttpUtil.getInstance();
        Json json = new Json(EntityUtils.toString(httpUtil.doGet("https://www.jisilu.cn/data/repo/sz_repo_list/?t=1464855341797", null, Lists.newArrayList(new BasicHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.101 Safari/537.36"))).getEntity(), "utf-8"));
        Object selectables = CrawlerExpressionResolver.resolve(request, json, "jsonPath(\"$..cell\")");
        if (selectables instanceof Selectable) {
            ((Selectable) selectables).nodes().forEach(selectable -> {
                System.out.println(selectable);
                Object data = null;
                    try {
                        data = CrawlerExpressionResolver.resolve(request, selectable, String.format("getJson().jsonPath(asset_id)"));
                        System.out.println(data);
                        data = CrawlerExpressionResolver.resolve(request, selectable, String.format("getJson().jsonPath(asset_nm)"));
                        System.out.println(data);
                        data = CrawlerExpressionResolver.resolve(request, selectable, String.format("getJson().jsonPath(price)"));
                        System.out.println(data);
                        data = CrawlerExpressionResolver.resolve(request, selectable, String.format("getJson().jsonPath(increase_rt)"));
                        System.out.println(data);
                        data = CrawlerExpressionResolver.resolve(request, selectable, String.format("getJson().jsonPath(volume)"));
                        System.out.println(data);
                        data = CrawlerExpressionResolver.resolve(request, selectable, String.format("getJson().jsonPath(volume2)"));
                        System.out.println(data);
                        data = CrawlerExpressionResolver.resolve(request, selectable, String.format("getJson().jsonPath(money_earn)"));
                        System.out.println(data);
                        data = CrawlerExpressionResolver.resolve(request, selectable, String.format("getJson().jsonPath(money_ava)"));
                        System.out.println(data);
                        data = CrawlerExpressionResolver.resolve(request, selectable, String.format("getJson().jsonPath(money_ava2)"));
                        System.out.println(data);
                        data = CrawlerExpressionResolver.resolve(request, selectable, String.format("getJson().jsonPath(notes)"));
                        System.out.println(data);
                        data = CrawlerExpressionResolver.resolve(request, selectable, String.format("getJson().jsonPath(occupation_day)"));
                        System.out.println(data);
                        data = CrawlerExpressionResolver.resolve(request, selectable, String.format("getJson().jsonPath(calendar_day)"));
                        System.out.println(data);
                        data = CrawlerExpressionResolver.resolve(request, selectable, String.format("getJson().jsonPath(calendar_day2)"));
                        System.out.println(data);
                        data = CrawlerExpressionResolver.resolve(request, selectable, String.format("getJson().jsonPath(daily_profit)"));
                        System.out.println(data);
                        data = CrawlerExpressionResolver.resolve(request, selectable, String.format("getJson().jsonPath(daily_profit2)"));
                        System.out.println(data);
                        data = CrawlerExpressionResolver.resolve(request, selectable, String.format("getJson().jsonPath(high)"));
                        System.out.println(data);
                        data = CrawlerExpressionResolver.resolve(request, selectable, String.format("getJson().jsonPath(low)"));
                        System.out.println(data);
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
            });
        }
    }

    @Test
    public void testEastmoneyCjsj() throws Throwable {
        Request request = new Request();
        HttpUtil httpUtil = HttpUtil.getInstance();
        Html html = new Html(EntityUtils.toString(httpUtil.doGet("http://data.eastmoney.com/cjsj/reserverequirementratio.aspx?p=1", null, Lists.newArrayList(new BasicHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.101 Safari/537.36"))).getEntity(), "gb2312"));
        Object selectables = CrawlerExpressionResolver.resolve(request, html, "css(tr:gt(1):not(#moretr)$$allText)");
        if (selectables instanceof Selectable) {
            ((Selectable) selectables).nodes().forEach(selectable -> {
                System.out.println(selectable);
                Object data = null;
                for (int i = 0; i < 11; i++) {
                    try {
                        data = CrawlerExpressionResolver.resolve(request, selectable, String.format("getCrawler().split().index(%s)", i));
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                    System.out.println(data);
                }
            });
        }
    }

    @Test
    public void testJiayuan() throws Throwable {
        Request request = new Request();
        HttpUtil httpUtil = HttpUtil.getInstance();
        Json json = new Json(EntityUtils.toString(httpUtil.doGet("http://www.jiayuan.com/dynmatch/ajax/index_new.php?ss=1&sex=f&min_age=0&max_age=100&work_location=44", null, Lists.newArrayList(new BasicHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.101 Safari/537.36"))).getEntity(), "utf-8"));
        Object selectables = CrawlerExpressionResolver.resolve(request, json, "getJson().jsonPath(*)");
        if (selectables instanceof Selectable) {
            ((Selectable) selectables).nodes().forEach(selectable -> {
                System.out.println(selectable);
                Object data = null;
                try {
                    data = CrawlerExpressionResolver.resolve(request, selectable, String.format("getJson().jsonPath(avatar)"));
                    System.out.println(data);
                    data = CrawlerExpressionResolver.resolve(request, selectable, String.format("getJson().jsonPath(privacy)"));
                    System.out.println(data);
                    data = CrawlerExpressionResolver.resolve(request, selectable, String.format("getJson().jsonPath(uid_disp)"));
                    System.out.println(data);
                    data = CrawlerExpressionResolver.resolve(request, selectable, String.format("getJson().jsonPath(nickname)"));
                    System.out.println(data);
                    data = CrawlerExpressionResolver.resolve(request, selectable, String.format("getJson().jsonPath(sex)"));
                    System.out.println(data);
                    data = CrawlerExpressionResolver.resolve(request, selectable, String.format("getJson().jsonPath(age)"));
                    System.out.println(data);
                    data = CrawlerExpressionResolver.resolve(request, selectable, String.format("getJson().jsonPath(gid)"));
                    System.out.println(data);
                    data = CrawlerExpressionResolver.resolve(request, selectable, String.format("getJson().jsonPath(height)"));
                    System.out.println(data);
                    data = CrawlerExpressionResolver.resolve(request, selectable, String.format("getJson().jsonPath(education)"));
                    System.out.println(data);
                    data = CrawlerExpressionResolver.resolve(request, selectable, String.format("getJson().jsonPath(work_location)"));
                    System.out.println(data);
                    data = CrawlerExpressionResolver.resolve(request, selectable, String.format("getJson().jsonPath(income)"));
                    System.out.println(data);
                    data = CrawlerExpressionResolver.resolve(request, selectable, String.format("getJson().jsonPath(shortnote)"));
                    System.out.println(data);
                    data = CrawlerExpressionResolver.resolve(request, selectable, String.format("getJson().jsonPath(avatar_url)"));
                    System.out.println(data);
                    data = CrawlerExpressionResolver.resolve(request, selectable, String.format("getJson().jsonPath(service)"));
                    System.out.println(data);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            });
        }
    }

    @Test
    public void testJiayuanDetail() throws Throwable {
        Request request = new Request();
        HttpUtil httpUtil = HttpUtil.getInstance();
        Html html = new Html(EntityUtils.toString(httpUtil.doGet("http://www.jiayuan.com/4764431", null, Lists.newArrayList(new BasicHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.101 Safari/537.36"),new BasicHeader("Cookie","view_m=1; PHPSESSID=d8f698fad596174b650d2ed3ac65e53c; SESSION_HASH=3427e6a8c2b557bf4c90d923feb4b48740d7afff; user_access=1; save_jy_login_name=13216635314; sl_jumper=%26cou%3D17%26omsg%3D0%26dia%3D0%26lst%3D2016-07-18; last_login_time=1504939113; upt=SiJMpegWj2ZwUfX2aMmGVvqvn0Ahi2K2W1TjChe1FKRD%2AgqNry9jIGPgoVSEWdyofqBT1Y6k4mRtEnBKwKH0Pg..; user_attr=000000; pclog=%7B%22152828725%22%3A%221504939113104%7C1%7C0%22%7D; IM_ID=1; IM_M=%5B%7B%22cmd%22%3A57%2C%22data%22%3A%22123.59.161.3%22%7D%5D; IM_CON=%7B%22IM_TM%22%3A1504939113322%2C%22IM_SN%22%3A1%7D; IM_TK=1504939113652; IM_S=%7B%22IM_CID%22%3A5885788%2C%22IM_SV%22%3A%22123.59.161.3%22%2C%22svc%22%3A%7B%22code%22%3A0%2C%22nps%22%3A0%2C%22unread_count%22%3A%2256%22%2C%22ocu%22%3A0%2C%22ppc%22%3A0%2C%22jpc%22%3A0%2C%22regt%22%3A%221463975828%22%2C%22using%22%3A%22%22%2C%22user_type%22%3A%2210%22%2C%22uid%22%3A152828725%7D%2C%22m%22%3A0%2C%22f%22%3A0%2C%22omc%22%3A0%7D; stadate1=151828725; myloc=11%7C1103; myage=27; PROFILE=152828725%3ATUA%3Am%3Aat3.jyimg.com%2F6b%2Fcd%2Ff146c7606dce5ebf3f9d43316d48%3A1%3A%3A1%3Af146c7606_1_avatar_p.jpg%3A1%3A1%3A50%3A10; mysex=m; myuid=151828725; myincome=50; RAW_HASH=v4UZW20GtBme4zqaMLV12iq5cHag-2PyXIe95n4kJGQYCO01vk5vC4gJnuFUihDvzxkAlzq6ZMi794iPSe7mmuUzX-7ggMMY7H5T1EsITiZliS8.; COMMON_HASH=6bf146c7606dce5ebf3f9d43316d48cd; IM_CS=0"))).getEntity(), "utf-8"));
        Object selectables = CrawlerExpressionResolver.resolve(request, html, "css(.member_info_list li,.member_name$$allText)");
        if (selectables instanceof Selectable) {
            ((Selectable) selectables).nodes().forEach(selectable -> {
                Object data = null;
                try {
                    data = CrawlerExpressionResolver.resolve(request, selectable, String.format("getCrawler().regexp(\"(.*)：.*\"$$\"籍贯信息\")"));
                    System.out.println(data);
                    data = CrawlerExpressionResolver.resolve(request, selectable, String.format("getCrawler().regexp(\".*：(.*)\"$$\"$1\")"));
                    System.out.println(data);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            });
        }
    }

    @Test
    public void testJavbusMovie() throws Throwable {
        Request request = new Request();
        HttpUtil httpUtil = HttpUtil.getInstance();
        Html html = new Html(EntityUtils.toString(httpUtil.doGet("https://www.javbus2.com/MEYD-264", null, Lists.newArrayList(new BasicHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.101 Safari/537.36"))).getEntity(), "utf-8"));
        Object selectables = CrawlerExpressionResolver.resolve(request, html, "css(.col-md-3 p$$allText).getCrawler().split(:).array(0$$20$$2)");
        if (selectables instanceof Selectable) {
            ((Selectable) selectables).nodes().forEach(selectable -> {
                Object data = null;
                try {
                    data = CrawlerExpressionResolver.resolve(request, selectable, String.format("getCrawler().index(0)"));
                    System.out.println(data);
                    data = CrawlerExpressionResolver.resolve(request, selectable, String.format("getCrawler().index(1)"));
                    System.out.println(data);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            });
        }
    }

    @Test
    public void testPa() {
        String[][][] dataStr = new String[][][]{{{"1", "2", "3"}, {"4", "5", "6"}}, {{"7", "8", "9"}, {"10", "11", "12"}}};
        String str = JSON.toJSONString(dataStr);
        System.out.println(str);
        List datas = JSON.parseObject(str, List.class);

        for (Object data : datas) {
            if (data instanceof Collection) {
                for (Object tmp : (List) data) {
                    System.out.println(tmp);
                }
            } else {
                System.out.println(data);
            }
        }
    }

    @Test
    public void index() throws Throwable {
        Request request = new Request();
        HttpUtil httpUtil = HttpUtil.getInstance();
        String[][][] dataStr = new String[][][]{{{"1", "2", "3"}, {"4", "5", "6"}}, {{"7", "8", "9"}, {"10", "11", "12"}}};
        String str = JSON.toJSONString(dataStr);
        CrawlerSelectable crawlerSelectable = new CrawlerSelectable(str);
        Selectable selectable = CrawlerExpressionResolver.resolve(request, crawlerSelectable, "getCrawler().index(1).index(1).index(2)");
        System.out.println(selectable.get());
    }

    @Test
    public void toJson() throws Throwable {
        Request request = new Request();
        Json json = new Json("{\"allCount\":55,\"pageNo\":1,\"list\":[[11000016886,\"经营建材\",3000000,5.7000,170.00,\"FY1709041542441259\",84,1504569600000,\"lilz20170823\",null,16,\"Sep 15, 2017 8:00:00 AM\",\"东泽聚富第49期\",10,0,null,null,1,1504569600000,1504884411000,1,471000.00,\"Sep 5, 2017 8:00:00 AM\",null,\"\"],[11000016116,\"经营建材\",2500000,5.7000,180.00,\"FY170829102507668\",100,1504000800000,\"lilz20170823\",null,19,\"Sep 8, 2017 6:00:00 PM\",\"东泽聚富第48期\",31,0,null,null,1,1504000800000,1504884411000,1,0.00,null,\"Sep 4, 2017 6:46:51 PM\",\"\"],[11000015776,\"经营建材\",3000000,5.9000,330.00,\"FY170824150638150\",100,1503792000000,\"lilz20170823\",null,19,\"Sep 6, 2017 8:00:00 AM\",\"东泽聚富第47期\",31,0,null,null,1,1503792000000,1504884411000,1,0.00,null,\"Aug 30, 2017 5:53:11 PM\",\"\"],[11000015754,\"经营建材\",3000000,5.8000,270.00,\"FY17082317482207\",100,1503568800000,\"lilz20170823\",null,19,\"Sep 3, 2017 6:00:00 PM\",\"东泽聚富第46期\",31,0,null,null,1,1503568800000,1504884411000,1,0.00,null,\"Aug 27, 2017 8:32:00 AM\",\"\"],[11000015702,\"经营建材\",2500000,5.7000,170.00,\"FY17082310171166\",100,1503455700000,\"lilz20170823\",null,19,\"Sep 2, 2017 10:35:00 AM\",\"东泽聚富第45期\",31,0,null,null,1,1503455700000,1504884411000,1,0.00,null,\"Aug 24, 2017 10:03:25 PM\",\"\"],[11000015072,\"经营纸油墨\",2000000,5.9000,340.00,\"FY170818111127208\",100,1503050400000,\"lipei20170718\",null,19,\"Aug 28, 2017 6:00:00 PM\",\"东泽聚富第44期\",31,0,null,null,1,1503050400000,1504884411000,1,0.00,null,\"Aug 20, 2017 6:55:56 AM\",\"\"],[11000014086,\"经营纸油墨\",2000000,5.8000,260.00,\"FY170811180313854\",100,1502785800000,\"lipei20170718\",null,19,\"Aug 25, 2017 4:30:00 PM\",\"东泽聚富第43期\",31,0,null,null,1,1502785800000,1504884411000,1,0.00,null,\"Aug 20, 2017 6:55:25 AM\",\"\"],[11000013852,\"经营纸油墨\",2000000,5.7000,170.00,\"FY170811124943577\",100,1502438400000,\"lipei20170718\",null,19,\"Aug 21, 2017 4:00:00 PM\",\"东泽聚富第42期\",31,0,null,null,1,1502438400000,1504884411000,1,0.00,null,\"Aug 15, 2017 3:38:51 PM\",\"\"],[11000013814,\"经营纸油墨\",2000000,5.9000,330.00,\"FY170810142000388\",100,1502353800000,\"lipei20170718\",null,19,\"Aug 20, 2017 4:30:00 PM\",\"东泽聚富第41期\",31,0,null,null,1,1502353800000,1504884411000,1,0.00,null,\"Aug 12, 2017 10:11:46 PM\",\"\"],[11000013748,\"经营纸油墨\",2000000,5.8000,220.00,\"FY170809142555114\",100,1502267400000,\"lipei20170718\",null,19,\"Aug 19, 2017 4:30:00 PM\",\"东泽聚富第40期\",31,0,null,null,1,1502267400000,1504884411000,1,0.00,null,\"Aug 10, 2017 2:38:37 PM\",\"\"]]}");
        System.out.println(json.jsonPath("$..list"));
        Selectable crawlerSelectable = json.jsonPath("$..list");
        Selectable selectable = CrawlerExpressionResolver.resolve(request, crawlerSelectable, "getCrawler().index(1)");
        System.out.println(selectable.get());
    }

    @Test
    public void testJiaYuan() {
        HttpUtil httpUtil = HttpUtil.getInstance();
        try {
            String content = httpUtil.doPostForString("https://passport.jiayuan.com/dologin.php?pre_url=http://www.jiayuan.com/usercp?name=13216635314&password=123456789&remem_pass=on&_s_x_id=5ce312f2f3f7fe31245fc3d26f3cf2fd&ljg_login=1&m_p_l=1&channel=0&position=0",null);
            System.out.println(content);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
