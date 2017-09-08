import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Lists;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.junit.Test;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.PlainText;
import us.codecraft.webmagic.selector.Selectable;
import yaycrawler.api.resolver.CrawlerExpressionResolver;
import yaycrawler.api.selector.CrawlerSelectable;
import yaycrawler.common.utils.HttpUtil;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
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
            MethodType methodType = MethodType.methodType(Selectable.class,String.class,String.class);
            MethodHandle methodHandle = lookup.findVirtual(Selectable.class,"css",methodType);
            Html html = new Html("!<!DOCTYPE html><html><head><title>test</title></head><body><div id=\"threadList\">test methodHandler</div></body></html>");
            Selectable selectable = (Selectable) methodHandle.invoke(html,"#threadList","text");
            System.out.println(selectable.get());
            methodType = MethodType.methodType(CrawlerSelectable.class,String.class);
            methodHandle = lookup.findVirtual(CrawlerSelectable.class,"constant",methodType);
            CrawlerSelectable crawlerSelectable = new CrawlerSelectable("test constant");
            selectable = (Selectable) methodHandle.invoke(crawlerSelectable,"555555");
            System.out.println(selectable.get());

            methodType = MethodType.methodType(Selectable.class,String.class,int.class);
            methodHandle = lookup.findVirtual(Selectable.class,"regex",methodType);
            Object[] data = new Object[]{selectable,"test method",0};

            selectable = (Selectable) methodHandle.invokeWithArguments(data);
            System.out.println(selectable.get());
            methodType = MethodType.methodType(List.class);
            methodHandle = lookup.findVirtual(Selectable.class,"all",methodType);
            List datas = (List)methodHandle.invokeWithArguments(new PlainText("test tt data"));
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
        Html html = new Html(EntityUtils.toString(httpUtil.doGet("http://www.qisuu.com/35478.html",null, Lists.newArrayList(new BasicHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.101 Safari/537.36"))).getEntity(),"utf-8"));
        Object selectable = CrawlerExpressionResolver.resolve(request,html,"css(.position).css(a:gt(0)$$allText).all()");
        System.out.println(selectable);
    }

    @Test
    public void testHrssz() throws Throwable {
        Request request = new Request();
        HttpUtil httpUtil = HttpUtil.getInstance();
        Html html = new Html(EntityUtils.toString(httpUtil.doGet("http://localhost:8069/admin/hrssz",null, Lists.newArrayList(new BasicHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.101 Safari/537.36"))).getEntity(),"utf-8"));
        Object selectables = CrawlerExpressionResolver.resolve(request,html,"css(tr:gt(4)$$allText).getCrawler().array(0$$6)");
        if( selectables instanceof Selectable) {
            ((Selectable)selectables).nodes().forEach(selectable -> {
                System.out.println(selectable);
                Object data = null;
                for (int i = 0; i < 13; i++) {
                    try {
                        data = CrawlerExpressionResolver.resolve(request,selectable,String.format("getCrawler().split().index(%s)",i));
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
        Html html = new Html(EntityUtils.toString(httpUtil.doGet("http://www.qisuu.com/35478.html",null, Lists.newArrayList(new BasicHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.101 Safari/537.36"))).getEntity(),"utf-8"));
        Object selectables = CrawlerExpressionResolver.resolve(request,html,"css(.detail_right li:not(.link),.detail_right h1)");
        if( selectables instanceof Selectable) {
            ((Selectable)selectables).nodes().forEach(selectable -> {
                Object data = null;
                    try {
                        data = CrawlerExpressionResolver.resolve(request,selectable,String.format("getCrawler().regexp(\">(.*)：.*<\"$$\"书籍信息\")"));
                        System.out.println(data);
                        data = CrawlerExpressionResolver.resolve(request,selectable,String.format("getCrawler().regexp(\">.*：(.*)<\"$$\"$1\").regexp(\"class=\"(.*)\"\"$$\"$1\").regexp(\">(.*)<\"$$\"$1\")"));
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
        Html html = new Html(EntityUtils.toString(httpUtil.doGet("http://www.cninfo.com.cn/information/fund/dividend/150008.html",null, Lists.newArrayList(new BasicHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.101 Safari/537.36"))).getEntity(),"gb2312"));
        Object selectables = CrawlerExpressionResolver.resolve(request,html,"css(tr:gt(0)$$allText)");
        if( selectables instanceof Selectable) {
            ((Selectable)selectables).nodes().forEach(selectable -> {
                System.out.println(selectable);
                Object data = null;
                for (int i = 0; i < 4; i++) {
                    try {
                        data = CrawlerExpressionResolver.resolve(request,selectable,String.format("getCrawler().split().index(%s)",i));
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
        Html html = new Html(EntityUtils.toString(httpUtil.doGet("http://www.cninfo.com.cn/information/shareholders/000002.html",null, Lists.newArrayList(new BasicHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.101 Safari/537.36"))).getEntity(),"gb2312"));
        Object selectables = CrawlerExpressionResolver.resolve(request,html,"css(.clear tr:gt(0)$$allText)");
        if( selectables instanceof Selectable) {
            ((Selectable)selectables).nodes().forEach(selectable -> {
                System.out.println(selectable);
                Object data = null;
                for (int i = 0; i < 4; i++) {
                    try {
                        data = CrawlerExpressionResolver.resolve(request,selectable,String.format("getCrawler().split().index(%s)",i));
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
        Html html = new Html(EntityUtils.toString(httpUtil.doGet("http://www.cninfo.com.cn/information/stockstructure/szmb000002.html",null, Lists.newArrayList(new BasicHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.101 Safari/537.36"))).getEntity(),"gb2312"));
        Object selectables = CrawlerExpressionResolver.resolve(request,html,"css(\".clear tr td\"$$allText).getCrawler().array(0$$76$$19$$4)");
        if( selectables instanceof Selectable) {
            ((Selectable)selectables).all().forEach(selectable -> {
                System.out.println(selectable);
                Object data = null;
                for (int i = 1; i < 19; i++) {
                    try {
                        data = CrawlerExpressionResolver.resolve(request,selectable,String.format("getCrawler().split().index(%s)",i));
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
        Html html = new Html(EntityUtils.toString(httpUtil.doGet("http://www.cninfo.com.cn/information/fund/financialreport/150008.html",null, Lists.newArrayList(new BasicHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.101 Safari/537.36"))).getEntity(),"gb2312"));
        Object selectables = CrawlerExpressionResolver.resolve(request,html,"css(\".clear tr td\"$$allText).getCrawler().array(0$$96$$2)");
        if( selectables instanceof Selectable) {
            ((Selectable)selectables).all().forEach(selectable -> {
                Object data = null;
                try {
                    data = CrawlerExpressionResolver.resolve(request,selectable,String.format("getCrawler().index(0)"));
                    System.out.println(data);
                    data = CrawlerExpressionResolver.resolve(request,selectable,String.format("getCrawler().index(1)"));
                    System.out.println(data);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            });
        }
    }
    @Test
    public void testPa() {
        String[][][] dataStr = new String[][][]{{{"1","2","3"},{"4","5","6"}},{{"7","8","9"},{"10","11","12"}}};
        String str = JSON.toJSONString(dataStr);
        System.out.println(str);
        List datas = JSON.parseObject(str,List.class);

        for(Object data:datas) {
            if(data instanceof Collection) {
                for(Object tmp:(List)data) {
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
        String[][][] dataStr = new String[][][]{{{"1","2","3"},{"4","5","6"}},{{"7","8","9"},{"10","11","12"}}};
        String str = JSON.toJSONString(dataStr);
        CrawlerSelectable crawlerSelectable = new CrawlerSelectable(str);
        Selectable selectable = CrawlerExpressionResolver.resolve(request,crawlerSelectable,"getCrawler().index(1).index(1).index(2)");
        System.out.println(selectable.get());
    }

    @Test
    public void toJson() throws Throwable {
        Request request = new Request();
        HttpUtil httpUtil = HttpUtil.getInstance();
        Html html = new Html(EntityUtils.toString(httpUtil.doGet("http://www.qisuu.com/35478.html",null, Lists.newArrayList(new BasicHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.101 Safari/537.36"))).getEntity(),"utf-8"));
        Object selectable = CrawlerExpressionResolver.resolve(request,html,"css(.position).css(a:gt(2)$$allText).get()");
        System.out.println(selectable);
    }
}
