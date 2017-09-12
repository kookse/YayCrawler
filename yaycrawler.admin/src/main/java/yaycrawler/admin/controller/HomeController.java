package yaycrawler.admin.controller;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Files;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import yaycrawler.admin.communication.MasterActor;
import yaycrawler.common.utils.BinaryTest;
import yaycrawler.common.utils.HttpUtil;
import yaycrawler.common.utils.OCRHelper;

import javax.imageio.ImageIO;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yuananyun on 2016/5/3.
 */
@Controller
public class HomeController {

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    private MasterActor masterActor;

    @RequestMapping({"", "/", "/index"})
    public ModelAndView index() {
        return new ModelAndView("index");
    }

    @RequestMapping("/test")
    public ModelAndView test() {
        return new ModelAndView("test");
    }

    @RequestMapping("/social")
    public ModelAndView social() {
        return new ModelAndView("social");
    }

    @RequestMapping("/hrssz")
    public ModelAndView hrssz() {
        return new ModelAndView("hrssz");
    }


    @RequestMapping("/gdltax")
    public ModelAndView gdltax() {
        return new ModelAndView("gdltax");
    }

    @RequestMapping(value = {"/workerList"}, method = RequestMethod.GET)
    public ModelAndView workerList() {
        ModelAndView mv = new ModelAndView("worker_list");
        return mv;
    }

    @RequestMapping(value = {"/queryWorkers"}, method = RequestMethod.GET)
    @ResponseBody
    public Object queryWorkers() {
        return masterActor.retrievedWorkerRegistrations();
    }

    @RequestMapping(value = {"/testFus"}, method = RequestMethod.POST)
    @ResponseBody
    public Object testFus(@RequestParam String address, @RequestParam String username, @RequestParam String pwd) {
        HttpUtil httpUtil = HttpUtil.getInstance();
        String loginUrl = "https://gzgjj.gov.cn:8280/fund/wap/wap!userLogin.do";
        Map params = new HashMap();
        params.put("username", username);
        params.put("pwd", pwd);
        ArrayList<Header> headerList = new ArrayList<>();
        headerList.add(new BasicHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.101 Safari/537.36"));
        int i = 0;
        Map data = new HashMap();
        while (true) {
            try {
                HttpResponse response = httpUtil.doGet("https://gzgjj.gov.cn:8280/fund/wap/wap!getImgCode.do?rnd=" + Math.random(), null, headerList);
                if (response.getStatusLine().getStatusCode() != 302) {
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
                    System.out.println("************************" + i + "***********" + recognizeText + "*******" + username + "********");
                    headerList.add(new BasicHeader("Cookie", "rmbUser=true; username=" + username + ";"));
                    String content = EntityUtils.toString(httpUtil.doGet("https://gzgjj.gov.cn:8280/fund/wap/wap!depositQuery.do?rdm=" + Math.random(), null, headerList).getEntity());
                    System.out.println(content);
                    data.put("data1", content);
                    content = EntityUtils.toString(httpUtil.doGet("https://gzgjj.gov.cn:8280/fund/wap/wap!depositDetails.do?rdm=" + Math.random(), null, headerList).getEntity());
                    data.put("data2", content);
                    System.out.println(content);
                    i++;
                    break;
                } else if (code.get("ret_msg").toString().indexOf("未有该身份") > -1) {
                    System.out.println(JSON.toJSONString(code + "$$$$$$$$$$$$$$$$" + username));
                    data = code;
                    break;
                } else if (code.get("ret_msg").toString().indexOf("密码") > -1) {
                    System.out.println("******************" + username + "*********************" + JSON.toJSONString(code));
                    data = code;
                    break;
                } else {
                    System.out.println(JSON.toJSONString(code));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return data;
    }

    @RequestMapping(value = {"/testSocial"}, method = RequestMethod.POST)
    @ResponseBody
    public Object testSocial(@RequestParam String address, @RequestParam String loginName, @RequestParam String loginPassword) {
        Map data = new HashMap();
        try {
            HttpUtil httpUtil = HttpUtil.getInstance();
            String loginUrl = "http://gzlss.hrssgz.gov.cn/cas/login";
            Map params = new HashMap();
            ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
            ScriptEngine nashorn = scriptEngineManager.getEngineByName("nashorn");
            nashorn.eval(new InputStreamReader(new FileInputStream("C:\\Users\\bill\\Downloads\\security.js"), "utf-8"));
            ArrayList<Header> headerList = new ArrayList<>();
            headerList.add(new BasicHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.101 Safari/537.36"));
            String page = "";//EntityUtils.toString(httpUtil.doGet(loginUrl, null, headerList).getEntity());
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
                    Header header = response.getFirstHeader("Set-Cookie");
                    if (header != null)
                        headerList.add(new BasicHeader("Cookie", header.getValue()));
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
                    String url = response.getFirstHeader("Location").getValue();
                    if (response.getStatusLine().getStatusCode() == 302) {
                        if (url.equalsIgnoreCase("http://gzlss.hrssgz.gov.cn:80/gzlss_web/business/tomain/main.xhtml")) {
                            Header[] headers = response.getHeaders("Set-Cookie");
                            for (Header header1 : headers) {
                                headerList.add(new BasicHeader("Cookie", header1.getValue()));
                            }
                            response = httpUtil.doGet(url, null, headerList);
                            page = EntityUtils.toString(response.getEntity());
                            System.out.println(page);
                            data.put("data", page);
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
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return data;
    }

    @RequestMapping(value = {"/testGdltax"}, method = RequestMethod.POST)
    @ResponseBody
    public Object testGdltax(@RequestParam String address, @RequestParam String loginName, @RequestParam String loginPassword) {
        Map data = new HashMap();
        try {
            HttpUtil httpUtil = HttpUtil.getInstance();
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
                    params.put("phonenum", loginName);
                    params.put("password", loginPassword);
                    HttpResponse response = httpUtil.doGet("http://mtax.gdltax.gov.cn/appserver/security/binduser/captcha.do?t=" + System.currentTimeMillis(), null, headerList);
                    if (response.getStatusLine().getStatusCode() != 200) {
                        continue;
                    }
                    Header[] headers = response.getHeaders("Set-Cookie");
                    for (Header header : headers) {
                        headerList.add(new BasicHeader("Cookie", header.getValue()));
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
                    response = httpUtil.doPost(loginUrl, null, ImmutableMap.of("accountInfoStr", URLEncoder.encode(URLEncoder.encode(JSON.toJSONString(params), "utf-8"), "utf-8"), "callback", "jsonp_callback4", "time", String.valueOf(System.currentTimeMillis()), "timeOut", "100000"), headerList);
                    page = EntityUtils.toString(response.getEntity());
                    System.out.println(page);
                    Map code = JSON.parseObject(StringUtils.substringBetween(page, "jsonp_callback4(", ")"));
                    if (StringUtils.equalsIgnoreCase(MapUtils.getString(code, "flag"), "ok")) {
                        String url = "http://mtax.gdltax.gov.cn/appserver/zrr/grsds/queryGrsdsNsmx.do";
                        response = httpUtil.doGet(url, ImmutableMap.of("startdate", "20130101", "callback", "jsonp_callback4", "enddate", "20170930", "time", String.valueOf(System.currentTimeMillis()), "timeOut", "100000"), headerList);
                        page = EntityUtils.toString(response.getEntity());
                        data = JSON.parseObject(StringUtils.substringBetween(page, "jsonp_callback4(", ")"), Map.class);
                        break;
                    } else {
                        System.out.println(page);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return data;
    }
}