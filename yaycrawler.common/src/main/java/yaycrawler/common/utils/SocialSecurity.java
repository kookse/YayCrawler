package yaycrawler.common.utils;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableMap;
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
        String loginName = "431027197805183119";
        String loginPassword = "g123456";

        String loginUrl = "http://gzlss.hrssgz.gov.cn/cas/login";
        Map params = new HashMap();
        ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        ScriptEngine nashorn = scriptEngineManager.getEngineByName("nashorn");
        nashorn.eval(new FileReader("C:\\Users\\bill\\Downloads\\security.js"));
        ArrayList<Header> headerList = new ArrayList<>();
        headerList.add(new BasicHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.101 Safari/537.36"));
        String page = EntityUtils.toString(httpUtil.doGet(loginUrl, null, headerList).getEntity());
        File testDataDir = new File("d:/tmp/social/tmp1");
        final String destDir = testDataDir.getAbsolutePath() + "/tmp1";
        File destF = new File(destDir);
        if (!destF.exists()) {
            destF.mkdirs();
        }
        while (true) {
            try {
                String username = "";
                String password = "";
                Pattern pattern = Pattern.compile("var modulus=\"(.*)\"");
                Matcher matcher = pattern.matcher(page);
                String modulus = "00a6adde094d3a76cd88df34026e9b034560485c1c0c90fab750c4335de9968532b3ce99503c7f856238c51c9494d069f274cacaa0c918013c08bab250602f6d71f91e60980942ed9b5e6fcc069f78a831d3dd9b3b45a10c8f19d0b29c8c26aa5aff535ecf27ef3ca0b0d0f008ce587f1c6e427e4724f8e8bf5414f286dac64957";
                String radamKey = "010001";
                String lt = "_c6D3C30DA-3C00-364A-C250-7F230605D40C_k1594B468-13B7-D956-4F08-3204DC3A7C22";
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
                String eval = "encryptPassword('" + radamKey + "','" + modulus + "','" + loginName + "');";
                username = nashorn.eval(eval).toString();
                eval = "encryptPassword('" + radamKey + "','" + modulus + "','" + loginPassword + "');";
                password = nashorn.eval(eval).toString();
                params.put("username", username);
                params.put("password", password);
                params.put("usertype", "2");
                params.put("_eventId", "submit");
                params.put("lt", lt);
                HttpResponse response = httpUtil.doGet("http://gzlss.hrssgz.gov.cn/cas/captcha.jpg?Rnd=" + Math.random(), null, headerList);
                if (response.getStatusLine().getStatusCode() != 200) {
                    continue;
                }
                Header[] headers = response.getHeaders("Set-Cookie");
                for(Header header:headers) {
                    headerList.add(new BasicHeader("Cookie",header.getValue()));
                }
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

                if (response.getStatusLine().getStatusCode() == 302) {
                    String url = response.getFirstHeader("Location").getValue();
                    if (url.equalsIgnoreCase("http://gzlss.hrssgz.gov.cn:80/gzlss_web/business/tomain/main.xhtml")) {
                        Header[] headers1 = response.getHeaders("Set-Cookie");
                        for(Header header1:headers1) {
                            headerList.add(new BasicHeader("Cookie",header1.getValue()));
                        }
                        //response = httpUtil.doGet("http://gzlss.hrssgz.gov.cn/gzlss_web/business/front/foundationcentre/viewPage/viewPersonPayHistoryInfo.xhtml?aac001=1009547681&xzType=1&startStr=201704&endStr=201709&querylog=true&businessocde=291QB-GRJFLS&visitterminal=PC",null,headerList);
                        response = httpUtil.doPost(url,null,null,headerList);
                        while (response.getStatusLine().getStatusCode() == 302) {
                            url = response.getFirstHeader("Location").getValue();
                            headers1 = response.getHeaders("Set-Cookie");
                            for(Header header1:headers1) {
                                headerList.add(new BasicHeader("Cookie",header1.getValue()));
                            }
                            response = httpUtil.doPost(url,null,null,headerList);
                        }
                         page = EntityUtils.toString(response.getEntity());
                        System.out.println(page);
                        break;
                    }
                    response = httpUtil.doGet(url, null, headerList);
                    page = EntityUtils.toString(response.getEntity());
                    System.out.println(page);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
