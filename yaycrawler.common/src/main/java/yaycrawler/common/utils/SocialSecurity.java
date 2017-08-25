package yaycrawler.common.utils;

import com.alibaba.fastjson.JSON;
import com.google.common.io.Files;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

import javax.imageio.ImageIO;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author bill
 * @create 2017-08-17 11:20
 * @desc 训练脚本
 **/
public class SocialSecurity {

    public static void main(String[] args) throws Exception {
        int i = 0, j = 0;
        HttpUtil httpUtil = HttpUtil.getInstance();
        String username = "431027197805183119";
        String pwd = "g123456";//"941129";
        String loginUrl = "http://gzlss.hrssgz.gov.cn/cas/login";
        Map params = new HashMap();
        ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        ScriptEngine nashorn = scriptEngineManager.getEngineByName("nashorn");
        nashorn.eval(new FileReader("C:\\Users\\bill\\Downloads\\security.js"));
        ArrayList<Header> headerList = new ArrayList<>();
        headerList.add(new BasicHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.101 Safari/537.36"));
        headerList.add(new BasicHeader("Origin","http://gzlss.hrssgz.gov.cn"));
        headerList.add(new BasicHeader("Upgrade-Insecure-Requests","1"));
        headerList.add(new BasicHeader("Referer","http://gzlss.hrssgz.gov.cn/cas/login"));
//        headerList.add(new BasicHeader("",""));
//        headerList.add(new BasicHeader("",""));
//        headerList.add(new BasicHeader("",""));
//        headerList.add(new BasicHeader("",""));
//        headerList.add(new BasicHeader("",""));
        String page = EntityUtils.toString(httpUtil.doGet(loginUrl, null, headerList).getEntity());
        Pattern pattern = Pattern.compile("var modulus=\"(.*)\"");
        Matcher matcher = pattern.matcher(page);
        String modulus = "00ce0999652fd6a8fe7a51b040eb4d7536efe2e70d44e6fc96c8c6e778484436d2b4abe35ea2de6d723bae45d1329bd9afa337f8aeb238aff98fa9912eead16f51";
        String radamKey = "010001";
        String lt = "_c1DAE47C7-D6F6-E5D8-820F-74FAEC3C2DC0_k2B873250-396F-38B1-0AD4-72582014C418";
        while (matcher.find()) {
            modulus = matcher.group(1);
        }
        pattern = Pattern.compile("var exponent=\"(.*)\"");
        matcher = pattern.matcher(page);
        while (matcher.find()) {
            radamKey = matcher.group(1);
        }
        pattern = Pattern.compile("<input type=\"hidden\" name=\"lt\" value=\"(.*)\"");
        matcher = pattern.matcher(page);
        while (matcher.find()) {
            lt = matcher.group(1);
        }
        String eval = "encryptPassword('"+radamKey+"','"+modulus+"','"+username+"');";
        username = nashorn.eval(eval).toString();
        eval = "encryptPassword('"+radamKey+"','"+modulus+"','"+pwd+"');";
        pwd = nashorn.eval(eval).toString();
        params.put("username", username);
        params.put("pwd", pwd);
        params.put("usertype", "2");
        params.put("_eventId","submit");
        params.put("lt",lt);
        File testDataDir = new File("d:/tmp/social/tmp1");
        final String destDir = testDataDir.getAbsolutePath()+"/tmp1";

        File destF = new File(destDir);
        if (!destF.exists())
        {
            destF.mkdirs();
        }
        while (true) {
            try {
                HttpResponse response = httpUtil.doGet("http://gzlss.hrssgz.gov.cn/cas/captcha.jpg?Rnd=" + Math.random(), null, headerList);
                if (response.getStatusLine().getStatusCode() != 200) {
                    continue;
                }
                Header header = response.getFirstHeader("Set-Cookie");
                if (header != null)
                    headerList.add(new BasicHeader("Cookie", header.getValue().split(";")[0]));
                byte[] bytes = EntityUtils.toByteArray(response.getEntity());
                String documentName = "11111" + ".jpg";
                File document = new File("d:/tmp/social/" + documentName);
                Files.createParentDirs(document);
                Files.write(bytes, document);
                BufferedImage bi = ImageIO.read(document);//通过imageio将图像载入
                int h = bi.getHeight();//获取图像的高
                int w = bi.getWidth();//获取图像的宽
                int rgb = bi.getRGB(0, 0);//获取指定坐标的ARGB的像素值
                BufferedImage nbi = BinaryTest.getBufferedImage(bi, h, w);
                BinaryTest.printGrayRGB(h, w, nbi);
                ImageIO.write(nbi, "jpg", new File(destDir, document.getName()));
                String recognizeText = new OCRHelper().recognizeText(new File(destDir, document.getName())).trim().toLowerCase();
                params.put("yzm", recognizeText);
                response = httpUtil.doPost(loginUrl, null, params, headerList);
                page = EntityUtils.toString(response.getEntity());
                System.out.println(page);
                pattern = Pattern.compile("<p>It's now at <a href=\"http://gzlss.hrssgz.gov.cn/gzlss_web/business/tomain/main.xhtml\"");
                matcher = pattern.matcher(page);
                String url = "";
                if(response.getStatusLine().getStatusCode() == 302) {
                    while (matcher.find()) {
                        url = "http://gzlss.hrssgz.gov.cn/gzlss_web/business/tomain/main.xhtml";
                        response = httpUtil.doGet(url,null,headerList);
                        String content = EntityUtils.toString(response.getEntity());
                        System.out.println(content);
                    }
                }


//                if (code.get("ret_code").toString().equalsIgnoreCase("0")) {
//                    System.out.println("************************" + i + "***********" + recognizeText + "*******" + username + "********");
//                    headerList.add(new BasicHeader("Cookie", "rmbUser=true; username=" + username + ";"));
//                    String content = EntityUtils.toString(httpUtil.doGet("https://gzgjj.gov.cn:8280/fund/wap/wap!depositQuery.do?rdm=" + Math.random(), null, headerList).getEntity());
//                    System.out.println(content);
//                    content = EntityUtils.toString(httpUtil.doGet("https://gzgjj.gov.cn:8280/fund/wap/wap!depositDetails.do?rdm=" + Math.random(), null, headerList).getEntity());
//                    System.out.println(content);
//                    i++;
//                    break;
//                } else if (code.get("ret_msg").toString().indexOf("未有该身份") > -1) {
//                    System.out.println(JSON.toJSONString(code + "$$$$$$$$$$$$$$$$" + username));
//                    break;
//                } else if (code.get("ret_msg").toString().indexOf("密码") > -1) {
//                    System.out.println("******************" + username + "*********************" + JSON.toJSONString(code));
//                    break;
//                } else {
//                    System.out.println(JSON.toJSONString(code));
//                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
