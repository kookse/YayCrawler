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
import org.springframework.util.Base64Utils;
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

/**
 * @author bill
 * @create 2017-08-29 15:18
 * @desc 广州公积金登陆引擎
 **/
@Service("gzgjjEngine")
public class GZGJJEngine implements Engine<LoginParam>{

    @Value("${yaycrawler.api.src.path:d:/tmp/ocr/}")
    private String srcPath;

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
        String username = info.getUsername();
        String pwd = info.getPassword();
        String loginUrl = info.getLoginUrl();
        Map params = new HashMap();
        params.put("username", username);
        params.put("pwd", pwd);
        ArrayList<Header> headerList = new ArrayList<>();
        headerList.add(new BasicHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.101 Safari/537.36"));
        OCREngine ocrEngine = (OCREngine) SpringContextUtil.getBean(info.getOcrEngine());
        BinaryEngine binaryEngine = null;
        while (true) {
            try {
                HttpResponse response = httpUtil.doGet("https://gzgjj.gov.cn:8280/fund/wap/wap!getImgCode.do?rnd=" + Math.random(), null, headerList);
                if (response.getStatusLine().getStatusCode() != 200) {
                    continue;
                }
                Header header = response.getFirstHeader("Set-Cookie");
                if (header != null)
                    headerList.add(new BasicHeader("Cookie", header.getValue()));
                byte[] bytes = EntityUtils.toByteArray(response.getEntity());
                String documentName = username + ".jpg";
                File document = new File(srcPath + "/" + documentName);
                Files.createParentDirs(document);
                Files.write(bytes, document);
                BinaryDto binaryDto = new BinaryDto(srcPath + "/",documentName,"eng");
                String recognizeText = ocrEngine.execute(binaryDto).getCode()
                        .replaceAll("\\|", "i")
                        .replaceAll("v\\\\", "w")
                        .replaceAll("([a-z0-9A-z]){3}cl","$1d")
                        .replaceAll(":>","o")
                        .replaceAll("v\\\\","w")
                        .replaceAll("vo.","w")
                        .replaceAll("n‘","m")
                        .replaceAll("c!","a")
                        .replaceAll("r'r","m")
                        .replaceAll("_]","j")
                        .replaceAll("l\\\\/","m").trim().toLowerCase();

                params.put("code", recognizeText);
                response = httpUtil.doPost(loginUrl,null,params,headerList);
                Map code = JSON.parseObject(EntityUtils.toString(response.getEntity()), Map.class);
                if (StringUtils.containsAny(code.get("ret_code").toString(),"0","2","3")) {
                    headerList.add(new BasicHeader("Cookie", "rmbUser=true; username=" + username + ";"));
                    List<Header> headers = Arrays.asList(response.getHeaders("Set-Cookie"));
                    List<Header> finalHeaders = new ArrayList<>();
                    finalHeaders.addAll(headers);
                    headerList.forEach(header1 -> {
                        if(header1.getName().equalsIgnoreCase("Cookie"))
                            finalHeaders.add(header1);
                    });
                    engineResult.setHeaders(finalHeaders);
                    engineResult.setStatus(Boolean.TRUE);
                    break;
                } else if(code.get("ret_msg").toString().indexOf("未有该身份") > -1 || code.get("ret_msg").toString().indexOf("密码") > -1){
                    engineResult.setStatus(Boolean.FALSE);
                    break;
                } else {
                    engineResult.setStatus(Boolean.FALSE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return engineResult;
    }
}
