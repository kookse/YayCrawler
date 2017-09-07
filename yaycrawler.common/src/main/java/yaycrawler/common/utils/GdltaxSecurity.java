package yaycrawler.common.utils;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Files;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

import javax.imageio.ImageIO;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.net.URLEncoder;
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
public class GdltaxSecurity {

    public static void main(String[] args) throws Exception {
        int i = 0, j = 0;
        HttpUtil httpUtil = HttpUtil.getInstance();
        String loginName = "15626241465";
        String loginPassword = "jaB4Gz143AtQ";

        String loginUrl = "http://mtax.gdltax.gov.cn/appserver/security/user/tpLogin.do";
        Map params = new HashMap();
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
                params.put("phonenum",loginName);
                params.put("password",loginPassword);
                HttpResponse response = httpUtil.doGet("http://mtax.gdltax.gov.cn/appserver/security/binduser/captcha.do?t=" + System.currentTimeMillis(), null, headerList);
                if (response.getStatusLine().getStatusCode() != 200) {
                    continue;
                }
                Header[] headers = response.getHeaders("Set-Cookie");
                for(Header header:headers) {
                    headerList.add(new BasicHeader("Cookie",header.getValue()));
                }

                byte[] bytes = EntityUtils.toByteArray(response.getEntity());
                String documentName = "11111" + ".jpg";
                File document = new File("d:/tmp/gdltax/" + documentName);
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
                response = httpUtil.doPost(loginUrl,null,ImmutableMap.of("accountInfoStr", URLEncoder.encode(URLEncoder.encode(JSON.toJSONString(params),"utf-8"),"utf-8"),"callback","jsonp_callback4","time",String.valueOf(System.currentTimeMillis()),"timeOut","100000"),headerList);
                page = EntityUtils.toString(response.getEntity());
                System.out.println(page);
                Map code = JSON.parseObject(StringUtils.substringBetween(page,"jsonp_callback4(",")"));
                if(StringUtils.equalsIgnoreCase(MapUtils.getString(code,"flag"),"ok")) {
                    String url = "http://mtax.gdltax.gov.cn/appserver/zrr/grsds/queryGrsdsNsmx.do";
                    response = httpUtil.doGet(url,ImmutableMap.of("startdate","20130101" ,"callback","jsonp_callback4","enddate","20170930","time",String.valueOf(System.currentTimeMillis()),"timeOut","100000"),headerList);
                    System.out.println(EntityUtils.toString(response.getEntity()));
                    break;
                } else {
                    System.out.println(page);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
