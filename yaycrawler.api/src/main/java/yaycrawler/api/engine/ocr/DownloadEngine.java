package yaycrawler.api.engine.ocr;

import com.google.common.io.Files;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import yaycrawler.api.engine.Engine;
import yaycrawler.common.model.BinaryDto;
import yaycrawler.common.model.EngineResult;
import yaycrawler.common.utils.HttpUtil;
import yaycrawler.common.utils.OCRHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author bill
 * @create 2017-08-29 11:35
 * @desc 验证码识别引擎
 **/

@Service("downloadEngine")
public class DownloadEngine implements Engine<BinaryDto> {

    private static final Logger logger = LoggerFactory.getLogger(DownloadEngine.class);

    @Override
    public EngineResult execute(BinaryDto info) {
        EngineResult engineResult = executeEngineWithFailover(info);
        return engineResult;
    }

    @Override
    public List<EngineResult> execute(List<BinaryDto> info) {
        return null;
    }

    public EngineResult executeEngineWithFailover(BinaryDto info) {
        EngineResult engineResult = new EngineResult();
        ArrayList<Header> headerList = new ArrayList<>();
        headerList.add(new BasicHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36"));
        headerList.add(new BasicHeader("Cookie",info.getCookie() != null ?info.getCookie(): ""));
        HttpUtil httpUtil = HttpUtil.getInstance();
        try {
            String[] paramArray = info.getImg().split("\\$\\$");
            info.setSuffix(paramArray.length > 1 ? paramArray[1]:null);
            while (true) {
                HttpResponse response = httpUtil.doGet(paramArray[0], null, headerList);
                if (response.getStatusLine().getStatusCode() != 200) {
                    continue;
                }
                Header[] headers = response.getHeaders("Set-Cookie");
                for(Header header:headers) {
                    headerList.add(new BasicHeader("Cookie",header.getValue()));
                }
                if (headers.length >= 1) {
                    engineResult.setHeaders(headerList);
                }
                if(StringUtils.isEmpty(info.getSuffix())) {
                    byte[] bytes = EntityUtils.toByteArray(response.getEntity());
                    String documentName = DigestUtils.sha1Hex(info.getImg()) + ".jpg";
                    File document = new File(info.getSrc() + "/" + documentName);
                    Files.createParentDirs(document);
                    Files.write(bytes, document);
                    engineResult.setResult(documentName);
                } else if(StringUtils.equalsIgnoreCase(info.getSuffix(),"content")){
                    engineResult.setResult(EntityUtils.toString(response.getEntity()));
                } else {
                    engineResult.setResult(EntityUtils.toString(response.getEntity()));
                }
                engineResult.setStatus(Boolean.TRUE);
                break;
            }
        }catch (Exception e) {
            engineResult = failureCallback(info,e);
            e.printStackTrace();
        }
        return engineResult;
    }
}
