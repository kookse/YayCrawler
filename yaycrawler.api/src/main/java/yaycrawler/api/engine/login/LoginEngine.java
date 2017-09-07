package yaycrawler.api.engine.login;

import com.alibaba.fastjson.JSON;
import com.google.common.io.Files;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import yaycrawler.api.engine.Engine;
import yaycrawler.api.engine.ocr.BinaryEngine;
import yaycrawler.api.engine.ocr.OCREngine;
import yaycrawler.api.process.SpringContextUtil;
import yaycrawler.common.model.BinaryDto;
import yaycrawler.common.model.EngineResult;
import yaycrawler.common.model.LoginParam;
import yaycrawler.common.utils.HttpUtil;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author bill
 * @create 2017-08-29 15:18
 * @desc 广州公积金登陆引擎
 **/
@Service("loginEngine")
public class LoginEngine implements Engine<LoginParam> {

    @Override
    public EngineResult execute(LoginParam info) {
        EngineResult engineResult = executeEngineWithFailover(info);
        return engineResult;
    }

    @Override
    public List<EngineResult> execute(List<LoginParam> info) {
        return null;
    }

    public EngineResult executeEngineWithFailover(LoginParam info) {

        EngineResult engineResult = new EngineResult();
        HttpUtil httpUtil = HttpUtil.getInstance();
        String loginUrl = info.getLoginUrl();
        Map params = info.getNewParams();
        List<Header> headerList = new ArrayList<>();
        headerList.add(new BasicHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.101 Safari/537.36"));
        headerList.add(new BasicHeader("Cookie", info.getCookie()));
        Pattern pattern = Pattern.compile(info.getValideLogin());
        Matcher matcher;
        Engine encryptEngine = (Engine) SpringContextUtil.getBean("encryptEngine");
        while (true) {
            try {
                HttpResponse response = httpUtil.doPost(loginUrl, null, params, headerList);
                String content = EntityUtils.toString(response.getEntity());
                matcher = pattern.matcher(content);

                if (matcher.find()) {
                    while (response.getStatusLine().getStatusCode() == 302) {
                        String url = response.getFirstHeader("Location").getValue();
                        Header[] headers = response.getHeaders("Set-Cookie");
                        for(Header header:headers) {
                            headerList.add(new BasicHeader("Cookie",header.getValue()));
                        }
                        response = httpUtil.doPost(url,null,null,headerList);
                    }
                    List<Header> headerList1 = new ArrayList<>();
                    headerList.forEach(header -> {
                        if(StringUtils.equalsIgnoreCase(header.getName(),"Cookie")) {
                            headerList1.add(header);
                        }
                    });
                    engineResult.setHeaders(headerList1);
                    engineResult.setStatus(Boolean.TRUE);
                    break;
                } else {
                    engineResult.setStatus(Boolean.FALSE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            info = encryptEngine.execute(info.getOldParams()).getLoginParam();
            params = info.getNewParams();
        }
        return engineResult;
    }
}
