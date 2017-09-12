package yaycrawler.common.utils;

import com.google.common.io.Files;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

/**
 * @author bill
 * @create 2017-08-24 12:46
 * @desc
 **/
public class ImageDownload {

    private static final Logger logger  = LoggerFactory.getLogger(ImageDownload.class);

    public static void main(String[] args) throws IOException, URISyntaxException {
        HttpUtil httpUtil = HttpUtil.getInstance();
        ArrayList<Header> headerList = new ArrayList<>();
        headerList.add(new BasicHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.101 Safari/537.36"));
        for (int i = 0; i < 200; i++) {
            HttpResponse response = httpUtil.doGet("http://gzlss.hrssgz.gov.cn/cas/captcha.jpg?Rnd=" + Math.random(), null, headerList);
            if (response.getStatusLine().getStatusCode() != 200) {
                continue;
            }
            Header header = response.getFirstHeader("Set-Cookie");
            if (header != null)
                headerList.add(new BasicHeader("Cookie", header.getValue()));
            byte[] bytes = EntityUtils.toByteArray(response.getEntity());
            String documentName = i + ".jpg";
            System.out.println("****************** " + documentName + " **********");
            File document = new File("d:/tmp/social/" + documentName);
            Files.createParentDirs(document);
            Files.write(bytes, document);
        }
    }
}
