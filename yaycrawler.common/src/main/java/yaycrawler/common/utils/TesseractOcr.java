package yaycrawler.common.utils;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.Files;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.util.*;

/**
 * @author bill
 * @create 2017-08-17 11:20
 * @desc 训练脚本
 **/
public class TesseractOcr {

    public static void main(String[] args) {

        HttpUtil httpUtil = HttpUtil.getInstance();
        String username = "15626241465";
        String pwd = "890310";
        String loginUrl = "https://gzgjj.gov.cn:8280/fund/wap/wap!userLogin.do";
        int i = 0;
        Map params = new HashMap();
        params.put("username",username);
        params.put("pwd",pwd);
        ArrayList<Header> headerList = new ArrayList<>();
        headerList.add(new BasicHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.101 Safari/537.36"));
//        headerList.add(new BasicHeader("Cookie","rmbUser=true; username=15626241465; JSESSIONID=64087F3E57857B86EBF50EE8F8846933"));
//        headerList.add(new BasicHeader("",""));
//        headerList.add(new BasicHeader("",""));
//        headerList.add(new BasicHeader("",""));
//        headerList.add(new BasicHeader("",""));
//        headerList.add(new BasicHeader("",""));
//        headerList.add(new BasicHeader("",""));
//        headerList.add(new BasicHeader("",""));
        while (true) {
            try {
                HttpResponse response = httpUtil.doGet("https://gzgjj.gov.cn:8280/fund/wap/wap!getImgCode.do?rnd="+Math.random(), null, headerList);
                if (response.getStatusLine().getStatusCode() != 200) {
                    continue;
                }
                headerList.add(new BasicHeader("Cookie",response.getFirstHeader("Set-Cookie").getValue()));
                byte[] bytes = EntityUtils.toByteArray(response.getEntity());
                String documentName = username + ".jpg";
                File document = new File("d:/tmp/ocr/" + documentName);
                Files.createParentDirs(document);
                Files.write(bytes, document);
                String recognizeText = new OCRHelper().recognizeText(document).trim().toLowerCase();
                params.put("code",recognizeText);
                Map code = httpUtil.doPostForMap(loginUrl,null,params,headerList);
                System.out.println("************************" + i + "***********"+ recognizeText +"***************");
                if(code.get("ret_code").toString().equalsIgnoreCase("0")) {
                    headerList.add(new BasicHeader("Cookie","rmbUser=true; username=15626241465;"));
                    String content = EntityUtils.toString(httpUtil.doGet("https://gzgjj.gov.cn:8280/fund/wap/wap!depositQuery.do?rdm=" + Math.random(),null,headerList).getEntity());
                    System.out.println(content);
                }
                i++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
