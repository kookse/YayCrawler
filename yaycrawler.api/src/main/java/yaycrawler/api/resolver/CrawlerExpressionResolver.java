package yaycrawler.api.resolver;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Json;
import us.codecraft.webmagic.selector.PlainText;
import us.codecraft.webmagic.selector.Selectable;
import yaycrawler.api.selector.CrawlerSelectable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by bill on 2016/5/1.
 */
public class CrawlerExpressionResolver {
    private static Logger logger = LoggerFactory.getLogger(CrawlerExpressionResolver.class);
    private static Pattern INVOKE_PATTERN = Pattern.compile("(\\w+)\\((.*)\\)");


    public static <T> T resolve(Request request, Object selector, String expression) {
        if (selector == null) return null;
        String[] invokeArray = expression.split("\\)\\.");
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        MethodType methodType = null;
        MethodHandle methodHandle;
        Class<?> cls = null;
        for (int i = 0; i < invokeArray.length; i++) {
            String invokeStr = invokeArray[i];
            if (!invokeStr.endsWith(")")) invokeStr += ")";
            Matcher matcher = INVOKE_PATTERN.matcher(invokeStr);
            if (matcher.find()) {
                String method = matcher.group(1);
                String param = matcher.group(2);
                String[] paramArray = new String[]{};
                Object[] params = new Object[1];
                Class<?>[] ptypes = new Class[]{};
                if(StringUtils.equalsIgnoreCase(param,"")) {
                    params[0] = selector;
                } else {
                    paramArray = param.split("\\$\\$");
                    params = new Object[paramArray.length+1];
                    params[0] = selector;
                    ptypes = new Class[paramArray.length];
                }

                for (int j = 0; j < paramArray.length; j++) {
                    String p = String.valueOf(paramArray[j]);
                    if (p.startsWith("\""))
                        p = p.substring(1, p.length());
                    if (p.endsWith("\""))
                        p = p.substring(0, p.length() - 1);
                    if (p.equals("$1"))
                        p = selector.toString();
                    try{
                        Object object = Integer.parseInt(p);
                        ptypes[j] = int.class;
                        params[j + 1] = Integer.parseInt(p);
                    } catch (Exception e) {
                        ptypes[j] = String.class;
                        params[j + 1] = p;
                    }
                }

                if(selector instanceof Collection) {
                    selector = new PlainText((List)selector);
                } else if(selector instanceof String) {
                    selector = new PlainText(String.valueOf(selector));
                }
                if(method.equalsIgnoreCase("getCrawler")) {
                    selector = new CrawlerSelectable(((Selectable)selector).all());
                    cls = null;
                } else if(method.equalsIgnoreCase("getJson")) {
                    selector = new Json(((Selectable)selector).all());
                    cls = null;
                } else if(method.equalsIgnoreCase("getHtml")) {
                    StringBuffer buffer = new StringBuffer();
                    ((Selectable)selector).all().forEach(sel -> {
                        buffer.append(sel);
                    });
                    selector = new Html(buffer.toString());
                    cls = null;
                } else if(method.equalsIgnoreCase("all")){
                    selector = ((Selectable)selector).all();
                    cls = null;
                } else if(method.equalsIgnoreCase("nodes")){
                    selector = ((Selectable)selector).nodes();
                    cls = null;
                } else if(method.equalsIgnoreCase("get")){
                    selector = ((Selectable)selector).get();
                    cls = null;
                } else if(selector instanceof CrawlerSelectable) {
                    cls = CrawlerSelectable.class;
                } else if(selector instanceof Selectable) {
                   cls = Selectable.class;
                } else if(selector instanceof Collection) {
                    cls = List.class;
                }
                if(cls!=null && StringUtils.containsAny(cls.getName(),"Selectable","CrawlerSelectable")) {
                    if(ptypes.length == 0)
                        methodType = MethodType.methodType(cls);
                    else
                        methodType = MethodType.methodType(cls,ptypes);
                    try {
                        methodHandle = lookup.findVirtual(cls,method,methodType);
                        selector = methodHandle.invokeWithArguments(params);
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                }
            }
        }
//        logger.info("表达式{}解析完成", expression);
        return (T) selector;
    }

}
