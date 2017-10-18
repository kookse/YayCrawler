package yaycrawler.api.engine.encrypt;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import yaycrawler.api.engine.Engine;
import yaycrawler.api.process.SpringContextUtil;
import yaycrawler.common.model.BinaryDto;
import yaycrawler.common.model.EngineResult;
import yaycrawler.common.model.LoginParam;
import yaycrawler.common.utils.HttpUtil;
import yaycrawler.common.utils.MD5Util;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author bill
 * @create 2017-08-31 9:53
 * @desc
 **/
@Service("encryptEngine")
public class EncryptEngine implements Engine<Map<String, Object>> {

    private static final Logger logger = LoggerFactory.getLogger(EncryptEngine.class);

    @Value("${yaycrawler.api.src.path:d:/tmp/ocr/}")
    private String srcPath;

    @Value("${yaycrawler.api.dest.path:d:/tmp/ocr/dest}")
    private String destPath;

    @Value("${yaycrawler.api.js.path:d:/tmp/js}")
    private String jsPath;

    private static Pattern INVOKE_PATTERN = Pattern.compile("(\\w+)\\((.*)\\)");

    @Value("${login.engine.validate:self.location =  \"(.*)\"|<script.*window.location='(.*)'.*</script>}")
    private String regexParam;

    @Override
    public EngineResult execute(Map<String, Object> info) {
        EngineResult engineResult = executeEngineWithFailover(info);
        return engineResult;
    }

    @Override
    public List<EngineResult> execute(List<Map<String, Object>> info) {
        return null;
    }

    public EngineResult executeEngineWithFailover(Map<String, Object> info) {
        EngineResult engineResult = new EngineResult();
        LoginParam loginParam = new LoginParam();
        loginParam.setOldParams(info);
        HttpUtil httpUtil = HttpUtil.getInstance();
        Map newParam = new HashMap();
        String loginUrl = String.valueOf(info.get("$loginUrl"));
        String cookie = info.get("$Cookie") != null ? String.valueOf(info.get("$Cookie")) : "";
        loginParam.setCookie(cookie);
        List<Header> headerList = Lists.newArrayList();
        try {
            if (StringUtils.isNotEmpty(cookie))
                headerList.add(new BasicHeader("Cookie", cookie));
            headerList.add(new BasicHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36"));
            HttpResponse response = httpUtil.doGet(loginUrl, null, headerList);
            Header[] headers = response.getHeaders("Set-Cookie");
            StringBuffer buffer = new StringBuffer(loginParam.getCookie() != null ? loginParam.getCookie() : "");
            for (Header header : headers) {
                headerList.add(new BasicHeader("Cookie", header.getValue()));
                buffer.append(";").append(header.getValue());
            }
            if (headers.length >= 1) {
                loginParam.setCookie(buffer.toString());
                info.put("$Cookie", loginParam.getCookie());
            }
            String content = EntityUtils.toString(response.getEntity());
            Pattern pattern = Pattern.compile(regexParam);
            Matcher matcher = pattern.matcher(content);
            String url = "";
            while (matcher.find()) {
                for (int j = 1; j <= matcher.groupCount(); j++) {
                    url = matcher.group(j);
                    if(StringUtils.isNotEmpty(url)) {
                        if(!StringUtils.startsWithAny(url,"http","https"))
                            url = StringUtils.substringBeforeLast(loginUrl,"/") + "/" +  url;
                        response = httpUtil.doGet(url,null,headerList);
                        headers = response.getHeaders("Set-Cookie");
                        content = EntityUtils.toString(response.getEntity());
                        for (Header header : headers) {
                            headerList.add(new BasicHeader("Cookie", header.getValue()));
                            buffer.append(";").append(header.getValue());
                        }
                        if (headers.length >= 1) {
                            loginParam.setCookie(buffer.toString());
                            info.put("$Cookie", loginParam.getCookie());
                        }
                        break;
                    }
                }
            }
            info.put("$content", content);
            Map oldParm = new HashMap();
            String finalContent = content;
            info.forEach((name, value) -> {
                if (StringUtils.startsWith(name, "$")) {
                    if (name.equalsIgnoreCase("$valideLogin"))
                        loginParam.setValideLogin(String.valueOf(value));
                    else if (name.equalsIgnoreCase("$loginUrl"))
                        loginParam.setLoginUrl(String.valueOf(value));
                    else if (name.equalsIgnoreCase("$requestUrl"))
                        loginParam.setUrl(String.valueOf(value));
                    else if (name.equalsIgnoreCase("$Cookie")) {
//                        info.put("$Cookie", loginParam.getCookie());
                    } else if (name.equalsIgnoreCase("$content")) {
                        loginParam.setContent(finalContent);
                    } else
                        newParam.put(StringUtils.substringAfter(name, "$"), encrypt(String.valueOf(value), info));
                } else if (StringUtils.startsWith(name, "#")) {
                    oldParm.put(StringUtils.substringAfter(name, "#"), value);
                } else {
                    newParam.put(name, value);
                }
            });
            Map oldMap = new HashMap();
            oldMap.putAll(info);
            oldMap.putAll(newParam);
            oldParm.forEach((name, value) -> {
                newParam.put(name, encrypt(String.valueOf(value), oldMap));
            });
            cookie = info.get("$Cookie") != null ? String.valueOf(info.get("$Cookie")) : "";
            loginParam.setCookie(cookie);
            loginParam.setNewParams(newParam);
            loginParam.setLoginUrl(loginUrl);
            engineResult.setStatus(Boolean.TRUE);
            engineResult.setLoginParam(loginParam);
        } catch (Exception e) {
            engineResult = failureCallback(info, e);
            logger.error("pageUrl {} Exception {}", loginUrl, e);
        }
        return engineResult;
    }

    private String encrypt(String username, Map<String, Object> data) {
        String result = username;
        try {
            String[] invokeArray = username.split("\\)\\.");
            ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
            ScriptEngine nashorn = scriptEngineManager.getEngineByName("nashorn");
            Boolean status = Boolean.FALSE;
            String content = MapUtils.getString(data, "$content");
            String cookie = MapUtils.getString(data, "$Cookie");
            for (int i = 0; i < invokeArray.length || !status; i++) {
                i = i % invokeArray.length;
                String invokeStr = invokeArray[i];
                if (!invokeStr.endsWith(")")) invokeStr += ")";
                Matcher matcher = INVOKE_PATTERN.matcher(invokeStr);
                while (matcher.find()) {
                    String method = matcher.group(1);
                    String param = matcher.group(2);
                    String[] paramArray = param.split("\\$\\$");
                    String[] params = new String[paramArray.length];
                    for (int j = 0; j < paramArray.length; j++) {
                        String p = String.valueOf(paramArray[j]);
                        if (p.startsWith("\""))
                            p = p.substring(1, p.length());
                        if (p.endsWith("\""))
                            p = p.substring(0, p.length() - 1);
                        if (p.equals("$1"))
                            p = String.valueOf(result != null ? result : "");
                        params[j] = p;
                    }
                    if (method.equalsIgnoreCase("base64")) {
                        result = Base64Utils.encodeToString(StringUtils.join(params, "").getBytes());
                        status = Boolean.TRUE;
                    } else if (method.equalsIgnoreCase("encode")) {
                        result = URLEncoder.encode(StringUtils.join(params, ""), "utf-8");
                        status = Boolean.TRUE;
                    } else if (method.equalsIgnoreCase("get")) {
                        result = MapUtils.getString(data, StringUtils.join(params, ""));
                        status = Boolean.TRUE;
                    } else if (method.equalsIgnoreCase("currentTimeMillis")) {
                        result = String.valueOf(System.currentTimeMillis());
                        status = Boolean.TRUE;
                    } else if (method.equalsIgnoreCase("json")) {
                        Map ps = new HashMap();
                        for (int j = 0; j < params.length; j++) {
                            ps.put(params[j], data.get(params[j]));
                        }
                        result = JSON.toJSONString(ps);
                        status = Boolean.TRUE;
                    } else if (method.equalsIgnoreCase("rsa")) {
                        nashorn.eval(new InputStreamReader(new FileInputStream(jsPath + "/security.js"), "utf-8"));
                        String modulus = params[0];
                        String radamKey = params[1];
                        Pattern pattern = Pattern.compile(modulus);
                        Matcher parmMatcher = pattern.matcher(content);
                        while (parmMatcher.find()) {
                            modulus = parmMatcher.group(1);
                        }
                        pattern = Pattern.compile(radamKey);
                        parmMatcher = pattern.matcher(content);
                        while (parmMatcher.find()) {
                            radamKey = parmMatcher.group(1);
                        }
                        String eval = "encryptPassword('" + radamKey + "','" + modulus + "','" + params[2] + "');";
                        result = nashorn.eval(eval).toString();
                        status = Boolean.TRUE;
                    } else if (method.equalsIgnoreCase("encrypt3Hex")) {
                        nashorn.eval(new InputStreamReader(new FileInputStream(jsPath + "/Encrypt.js"), "utf-8"));
                        String tmpData = params[0];
                        String firstKey = params[1];
                        String secondKey = params[2];
                        String thirdKey = params[3];
                        String eval = "strEncode('" + tmpData + "','" + firstKey + "','" + secondKey + "','" + thirdKey + "');";
                        result = nashorn.eval(eval).toString();
                        status = Boolean.TRUE;
                    } else if (method.equalsIgnoreCase("md5")) {
                        String tmpData = params[0];
                        result = MD5Util.MD5(tmpData);
                        status = Boolean.TRUE;
                    } else if (method.equalsIgnoreCase("format")) {
                        String tmpData = params[0];
                        result = String.format(tmpData, params[1]);
                        status = Boolean.TRUE;
                    } else if (method.equalsIgnoreCase("regex")) {
                        Pattern pattern = Pattern.compile(StringUtils.join(params, ""));
                        Matcher parmMatcher = pattern.matcher(content);
                        while (parmMatcher.find()) {
                            result = parmMatcher.group(1);
                        }
                        status = Boolean.TRUE;
                    } else if (StringUtils.endsWith(method, "Engine")) {
                        Engine engine = (Engine) SpringContextUtil.getBean(method);
                        BinaryDto binaryDto = new BinaryDto();
                        binaryDto.setImg(StringUtils.join(params, "$$"));
                        binaryDto.setSrc(srcPath);
                        binaryDto.setDest(destPath);
                        binaryDto.setCookie(cookie);
                        EngineResult engineResult = engine.execute(binaryDto);
                        result = engineResult.getResult();
                        status = engineResult.getStatus();
                        StringBuffer buffer = new StringBuffer(cookie);
                        if (engineResult.getHeaders() != null) {
                            engineResult.getHeaders().forEach(header -> {
                                if (StringUtils.equalsIgnoreCase(header.getName(), "Cookie")) {
                                    buffer.append(":").append(header.getValue());
                                }
                            });
                            data.put("$Cookie", buffer.toString());
                        }
                    } else if (method.equalsIgnoreCase("download")) {
                        HttpUtil httpUtil = HttpUtil.getInstance();
                        String url = params[0];
                        Map formParams = params.length > 1 ? JSON.parseObject(params[1], Map.class) : null;
                        result = EntityUtils.toString(httpUtil.doPost(url, null, formParams, null).getEntity());
                    } else if (method.equalsIgnoreCase("substring")) {
                        String src = params[0];
                        Integer start = Integer.parseInt(params[1]);
                        Integer end = params.length > 2 ? Integer.parseInt(params[2]) : src.length();
                        result = StringUtils.substring(src, start, end);
                    } else if (method.equalsIgnoreCase("trim")) {
                        String src = params[0];
                        result = StringUtils.trimToEmpty(src);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
