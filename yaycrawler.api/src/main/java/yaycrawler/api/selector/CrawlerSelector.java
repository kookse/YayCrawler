package yaycrawler.api.selector;

import us.codecraft.webmagic.selector.Selector;

import java.util.List;

/**
 * @author bill
 * @create 2017-09-06 12:46
 * @desc 个人定义筛选器
 **/
public class CrawlerSelector implements Selector{


    @Override
    public String select(String text) {
        return text;
    }

    @Override
    public List<String> selectList(String text) {
        return null;
    }
}
