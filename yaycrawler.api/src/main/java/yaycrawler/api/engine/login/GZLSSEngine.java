package yaycrawler.api.engine.login;

import com.google.common.io.Files;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import yaycrawler.api.engine.Engine;
import yaycrawler.common.model.EngineResult;
import yaycrawler.common.model.LoginParam;
import yaycrawler.common.utils.BinaryTest;
import yaycrawler.common.utils.HttpUtil;
import yaycrawler.common.utils.OCRHelper;

import javax.imageio.ImageIO;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author bill
 * @create 2017-08-29 16:54
 * @desc 广州社保的登陆引擎
 **/
public class GZLSSEngine implements Engine<LoginParam>{

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
        try {
            HttpUtil httpUtil = HttpUtil.getInstance();
            String loginName = info.getUsername();
            String loginPassword = info.getPassword();

            String loginUrl = info.getLoginUrl();
            Map params = new HashMap();
            ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
            ScriptEngine nashorn = scriptEngineManager.getEngineByName("nashorn");
            nashorn.eval(new InputStreamReader(new FileInputStream("C:\\Users\\bill\\Downloads\\security.js"),"utf-8"));;
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
                            engineResult.setHeaders( Arrays.asList(response.getHeaders("Set-Cookie")));
                            engineResult.setStatus(Boolean.TRUE);
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
        } catch (Exception e) {
            e.printStackTrace();
        }

        return engineResult;
    }
}
