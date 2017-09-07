package yaycrawler.api.selector;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import us.codecraft.webmagic.selector.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author bill
 * @create 2017-09-06 11:44
 * @desc 文档抽取器
 **/
public class CrawlerSelectable extends AbstractSelectable {

    protected List<String> sourceTexts;

    public CrawlerSelectable(String text) {
        this.sourceTexts = new ArrayList<String>();
        sourceTexts.add(text);
    }

    public CrawlerSelectable(List<String> sourceTexts) {
        this.sourceTexts = sourceTexts;
    }

    public CrawlerSelectable constant(String text) {
        return new CrawlerSelectable(text);
    }

    public CrawlerSelectable constant(int text) {
        return new CrawlerSelectable(String.valueOf(text));
    }

    public CrawlerSelectable paging(String url, String pageName, int start, int end, int step) {
        List<String> urls = Lists.newArrayList();
        for (int i = start; i <= end; i = i + step) {
            urls.add(url.replace(pageName + "=?", pageName + "=" + i).replace(pageName + "/?", pageName + "/" + i));
        }
        return new CrawlerSelectable(urls);
    }

    public CrawlerSelectable format(String selector, String text) {
        List<String> datas = all();
        List<String> tmps = Lists.newArrayList();
        datas.forEach(data ->{
            tmps.add(String.format(data,text));
        });
        return new CrawlerSelectable(tmps);
    }

    public CrawlerSelectable format(String selector, int text) {
        return format(selector, String.valueOf(text));
    }

    public CrawlerSelectable split(String separator) {
        return null;
    }

    public CrawlerSelectable index(int index) {
        List datas = all();
        if(datas.size() == 1) {
            datas = JSON.parseArray(get(), List.class);
        }
        Object data = null;
        data = datas.get(index);
        if (data instanceof Collection) {
            return new CrawlerSelectable((List) data);
        } else {
            return new CrawlerSelectable(String.valueOf(data != null ? data : ""));
        }
    }

    public CrawlerSelectable add(String param) {
        BigDecimal num1 = new BigDecimal(get());
        BigDecimal num2 = new BigDecimal(param);
        return new CrawlerSelectable(num1.add(num2).toString());
    }

    public CrawlerSelectable add(int param) {
        return add(String.valueOf(param));
    }

    public CrawlerSelectable divide(String param) {
        BigDecimal num1 = new BigDecimal(get());
        BigDecimal num2 = new BigDecimal(param);
        return new CrawlerSelectable(num1.divide(num2).toString());
    }

    public CrawlerSelectable divide(int param) {
        return divide(String.valueOf(param));
    }

    public CrawlerSelectable divide(int param,int roundingMode) {
        return divide(String.valueOf(param),roundingMode);
    }

    public CrawlerSelectable divide(int param,int scale,int roundingMode) {
        return divide(String.valueOf(param),scale,roundingMode);
    }

    public CrawlerSelectable divide(String param,int scale,int roundingMode) {
        BigDecimal num1 = new BigDecimal(get());
        BigDecimal num2 = new BigDecimal(param);
        num1.divide(num2,1);
        return new CrawlerSelectable(num1.divide(num2,scale,roundingMode).toString());
    }

    public CrawlerSelectable divide(String param,int roundingMode) {
        return divide(param, 1,roundingMode);
    }

    public CrawlerSelectable subtract(String param) {
        BigDecimal num1 = new BigDecimal(get());
        BigDecimal num2 = new BigDecimal(param);
        return new CrawlerSelectable(num1.subtract(num2).toString());
    }

    public CrawlerSelectable subtract(int param) {
        return subtract(String.valueOf(param));
    }

    public CrawlerSelectable multiply(String param) {
        BigDecimal num1 = new BigDecimal(get());
        BigDecimal num2 = new BigDecimal(param);
        return new CrawlerSelectable(num1.multiply(num2).toString());
    }

    public CrawlerSelectable multiply(int param) {
        return multiply(String.valueOf(param));
    }

    public CrawlerSelectable paging(String url, String pageName, int start, int end) {
        return paging(url, pageName, start, end, 1);
    }

    public CrawlerSelectable regex(int regex) {
        RegexSelector regexSelector = Selectors.regex(String.valueOf(regex));
        return selectList(regexSelector, getSourceTexts());
    }

    public CrawlerSelectable regex(int regex, int group) {
        RegexSelector regexSelector = Selectors.regex(String.valueOf(regex), group);
        return selectList(regexSelector, getSourceTexts());
    }

    public CrawlerSelectable replace(int regex, String replacement) {
        ReplaceSelector replaceSelector = new ReplaceSelector(String.valueOf(regex), replacement);
        return select(replaceSelector, getSourceTexts());
    }

    public CrawlerSelectable replace(int regex, int replacement) {
        ReplaceSelector replaceSelector = new ReplaceSelector(String.valueOf(regex), String.valueOf(replacement));
        return select(replaceSelector, getSourceTexts());
    }

    public CrawlerSelectable replace(String regex, int replacement) {
        ReplaceSelector replaceSelector = new ReplaceSelector(regex, String.valueOf(replacement));
        return select(replaceSelector, getSourceTexts());
    }

    @Override
    protected CrawlerSelectable select(Selector selector, List<String> strings) {
        List<String> results = new ArrayList<String>();
        for (String string : strings) {
            String result = selector.select(string);
            if (result != null) {
                results.add(result);
            }
        }
        return new CrawlerSelectable(results);
    }

    @Override
    protected CrawlerSelectable selectList(Selector selector, List<String> strings) {
        List<String> results = new ArrayList<String>();
        for (String string : strings) {
            List<String> result = selector.selectList(string);
            results.addAll(result);
        }
        return new CrawlerSelectable(results);
    }

    @Override
    protected List<String> getSourceTexts() {
        return sourceTexts;
    }

    @Override
    public CrawlerSelectable xpath(String xpath) {
        return null;
    }

    @Override
    public CrawlerSelectable $(String selector) {
        return null;
    }

    @Override
    public CrawlerSelectable $(String selector, String attrName) {
        return null;
    }

    @Override
    public CrawlerSelectable smartContent() {
        return null;
    }

    @Override
    public CrawlerSelectable links() {
        return null;
    }

    @Override
    public List<Selectable> nodes() {
        return null;
    }
}
