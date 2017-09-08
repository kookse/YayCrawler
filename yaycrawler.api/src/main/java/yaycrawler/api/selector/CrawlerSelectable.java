package yaycrawler.api.selector;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import us.codecraft.webmagic.selector.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author bill
 * @create 2017-09-06 11:44
 * @desc 文档抽取器
 **/
public class CrawlerSelectable<T> extends AbstractSelectable {

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

    public CrawlerSelectable split() {
        return split(" ");
    }
    public CrawlerSelectable split(String separator) {
        String[] datas = StringUtils.split(get(),separator);
        return new CrawlerSelectable(Arrays.asList(datas));
    }

    public CrawlerSelectable array(int start,int end) {
        return array(start, end,1);
    }
    public CrawlerSelectable array(int start,int end,int step) {return array(start, end,step,1);}
    public CrawlerSelectable array(int start,int end,int step,int jump) {
        List<String> datas = Lists.newArrayList();
        List<String> tmps = all();
        if(end < 0) {
            end += tmps.size();
        }
        for (int i = 0; i <end /step ; i++) {

        }
        for (int i = start; i <end / step ; i++) {
            if (step == 1) {
                datas.add(tmps.get(i));
            } else {
                List tmp = Lists.newArrayList();
                for (int j = 0; j < step; j++) {
                    tmp.add(tmps.get(j * jump + i));
                }
                datas.add(JSON.toJSONString(tmp));
            }
        }
        return new CrawlerSelectable(datas);
    }

    public CrawlerSelectable index(int index) {
        List datas = all();
        if(datas.size() == 1) {
            datas = JSON.parseObject(get(), List.class);
        }
        Object data = null;
        data = datas.get(index);
        if (data instanceof Collection) {
            return new CrawlerSelectable((List) data);
        } else {
            return new CrawlerSelectable(String.valueOf(data != null ? data : ""));
        }
    }

    public CrawlerSelectable regexp(String regex,int result) {
        return regexp(regex, String.valueOf(result));
    }
    public CrawlerSelectable regexp(String regex,String result) {
        RegexSelector regexSelector = Selectors.regex(String.valueOf(regex));
        String data = regexSelector.select(get());
        if(StringUtils.isEmpty(data)) {
            return new CrawlerSelectable(result);
        } else {
            return new CrawlerSelectable(data);
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
        List<Selectable> nodes = new ArrayList<Selectable>(getSourceTexts().size());
        for (String string : getSourceTexts()) {
            nodes.add(PlainText.create(string));
        }
        return nodes;
    }


}
