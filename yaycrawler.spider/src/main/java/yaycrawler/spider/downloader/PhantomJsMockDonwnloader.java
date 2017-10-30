package yaycrawler.spider.downloader;

import com.google.common.collect.Sets;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.downloader.AbstractDownloader;
import us.codecraft.webmagic.selector.PlainText;
import yaycrawler.common.utils.CasperjsProgramManager;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Created by ucs_yuananyun on 2016/5/27.
 */
public class PhantomJsMockDonwnloader extends AbstractDownloader {

    private static final Logger logger  = LoggerFactory.getLogger(PhantomJsMockDonwnloader.class);

    private static Pattern UNICODE_PATTERN = Pattern.compile("\\\\u([0-9a-fA-F]{4})");

    public Page download(Request request, Task task,String casperjsDownloadName,String charset,Map paramData,String cookie) {
        Site site = null;
        if (task != null) {
            site = task.getSite();
        }
        Set<Integer> acceptStatCode;
        String domain = "";
        if (site != null) {
            acceptStatCode = site.getAcceptStatCode();
            domain = String.valueOf(site.getDomain());
        } else {
            acceptStatCode = Sets.newHashSet(200, 500);
        }

        logger.debug("downloading page {}", request.getUrl());
        int statusCode = 0;
        String result = null;
        try {

            List<String> paramList = new ArrayList<>();
            paramList.add(String.format("--%s=%s", "pageUrl", request.getUrl()));
            paramList.add(String.format("--%s=%s", "domain", domain));
            cookie = cookie != null ? URLEncoder.encode(cookie.replaceAll(" ", "%20"), "utf-8") : "";
            paramList.add(String.format("--%s=%s", "cookies", cookie));
            paramData.forEach((name,value)->{
                paramList.add(String.format("--%s=%s", name, value));
            });
            StringBuffer buffer = new StringBuffer();
            request.getExtras().forEach((name,value)->{
                if(!StringUtils.equalsIgnoreCase(name,"$pageInfo"))
                    buffer.append(String.format("%s=%s;",name,value));
            });
            paramList.add(String.format("--%s=%s","searchParam",buffer.toString()));
            result = CasperjsProgramManager.launch(casperjsDownloadName, "gbk", paramList);

            statusCode = Integer.parseInt(StringUtils.substringBefore(result, "\r\n").trim());
//            request.putExtra(Request.STATUS_CODE, statusCode);
            if (statusAccept(acceptStatCode, statusCode)) {
                Page page = handleResponse(request, result,charset);
                onSuccess(request);
                return page;
            } else {
                logger.warn("code error {}\t,{}", statusCode, request.getUrl());
                return Page.fail();
            }
        } catch (Exception e) {
            logger.warn("download page {} error {} msg {}", request.getUrl(), e, result);
            if (site.getCycleRetryTimes() > 0) {
//                return addToCycleRetry(request, site);
            }
            onError(request);
            return Page.fail();
        } finally {
//            request.putExtra(Request.STATUS_CODE, statusCode);
        }
    }

    protected boolean statusAccept(Set<Integer> acceptStatCode, int statusCode) {
        return acceptStatCode.contains(statusCode);
    }

    protected Page handleResponse(Request request, String content,String charset) throws IOException {
        Page page = new Page();
        content = StringUtils.substringBetween(content,"$PageStart","$PageEnd");
        byte[] bytes = content.getBytes(charset);
        page.setBytes(bytes);
        if (!request.isBinaryContent()) {
            page.setCharset(charset);
            content = new String(bytes, charset);
            //unicode编码处理
            if (UNICODE_PATTERN.matcher(content).find())
                content = StringEscapeUtils.unescapeJava(content.replace("\"", "\\\""));
            page.setRawText(content);
        }
        page.setUrl(new PlainText(request.getUrl()));
        page.setRequest(request);
        return page;
    }

    @Override
    public Page download(Request request, Task task) {
        return download(request, task, "casperjsDownload.js","gbk",null,null);
    }

    @Override
    public void setThread(int threadNum) {

    }
}
