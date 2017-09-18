package yaycrawler.spider.utils;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.utils.HttpConstant;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ucs_yuananyun on 2016/5/12.
 */
public class RequestHelper {

    public static Request createRequest(String url, String method, Map<String, Object> paramsMap) {
        if (StringUtils.isBlank(url)) return null;

        Request request = new Request(url);
        String requestMethod = method.toUpperCase();
        request.setMethod(requestMethod);
        if (HttpConstant.Method.POST.equals(requestMethod))
            request.putExtra("nameValuePair", paramsMap);
        else if (paramsMap != null && paramsMap.size() > 0) {
            StringBuilder urlBuilder = new StringBuilder(url);
            for (Map.Entry<String, Object> entry : paramsMap.entrySet()) {
                try {
                    if(StringUtils.isEmpty(entry.getKey())) {
                        urlBuilder.append(String.format("%s/%s/%s", "/", entry.getKey(), URLEncoder.encode(String.valueOf(entry.getValue()), "utf-8")));
                    } else
                        urlBuilder.append(String.format("%s%s=%s",  urlBuilder.indexOf("?") > 0 ? "&" : "?", entry.getKey(), URLEncoder.encode(String.valueOf(entry.getValue()), "utf-8")));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            request.setUrl(urlBuilder.toString());
        }
        return request;
    }

    public static String getParam(String url,String param) {
        Pattern pattern = Pattern.compile(String.format("%s=(.*?)&|/%s/(.*?)/",param,param));
        Matcher matcher = pattern.matcher(url);
        String value = "";
        while (matcher.find()) {
            value = matcher.group(1) != null ? matcher.group(1):matcher.group(2);
        }
        return value;
    }

    public static Map<String,Object> getParams( String url) {
        Map<String,Object> data = Maps.newHashMap();
        if(url.indexOf("?") > 0) {
            String[] params = StringUtils.split(StringUtils.split(url,"?")[1],"&");
            for (String param:params) {
                data.put(StringUtils.substringBefore(param,"="),StringUtils.substringAfter(param,"="));
            }
        }
        return data;
    }
}
