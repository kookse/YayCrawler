package yaycrawler.spider.service;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.utils.UrlUtils;
import yaycrawler.common.model.CrawlerRequest;
import yaycrawler.common.utils.FTPUtils;
import yaycrawler.common.utils.HttpUtil;
import yaycrawler.spider.listener.IPageParseListener;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by bill on 2017/5/5.
 */
@Component
public class DownloadService {

    private static final Logger logger  = LoggerFactory.getLogger(DownloadService.class);

    private ExecutorService executorService;

    @Value("${ftp.server.url:127.0.0.1}")
    private String url;
    @Value("${ftp.server.port:2121}")
    private int port;
    @Value("${ftp.server.username:admin}")
    private String username;
    @Value("${ftp.server.password:admin}")
    private String password;

    @Value("${ftp.server.path:d/tmp}")
    private String ftpPath;

    private FTPUtils ftpUtil;

    @Autowired(required = false)
    private IPageParseListener pageParseListener;

    public boolean startCrawlerDownload(List<CrawlerRequest> downList) {
        if (executorService == null || executorService.isShutdown())
            executorService = Executors.newFixedThreadPool(downList.size());
        for (CrawlerRequest request : downList) {
            List<String> srcList = (List<String>) MapUtils.getObject(request.getExtendMap(),"$src");
            if (srcList == null || srcList.isEmpty())
                continue;
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    ftpUtil = ftpUtil.getInstance();
                    try {
                        ftpUtil.connect(url, port, username, password);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    HttpUtil httpUtil = HttpUtil.getInstance();
                    List<String> childRequestList = new LinkedList<>();
                    String suffix = MapUtils.getString(request.getExtendMap(), "$DOWNLOAD");
                    for (String src:srcList) {
                        try {
                            HttpResponse response = httpUtil.doGet(src, null, null);
                            if (response.getStatusLine().getStatusCode() != 200) {
                                childRequestList.add(src);
                                continue;
                            }

                            byte[] bytes = EntityUtils.toByteArray(response.getEntity());
                            String documentName = StringUtils.substringAfterLast(src, "/");

                            if (!StringUtils.contains(documentName, ".")) {
                                documentName = documentName + suffix;
                            }
                            File document = new File(ftpPath + "/" + UrlUtils.getDomain(src) + "/" + documentName);
                            Files.createParentDirs(document);
                            Files.write(bytes, document);

                            String path = request.getDomain() + "/" + request.getHashCode() + "/";
                            //上传文件
                            ftpUtil.upLoadByFtp(ftpPath + "/" + documentName, path, documentName);
                            document.delete();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if(childRequestList.size() > 0) {
                        CrawlerRequest crawlerRequest = new CrawlerRequest();
                        crawlerRequest.setDomain(request.getDomain());
                        crawlerRequest.setHashCode(request.getHashCode());
                        crawlerRequest.setMethod("get");
                        crawlerRequest.setUrl(request.getUrl() + "?$download=pdf");
                        crawlerRequest.setExtendMap(ImmutableMap.of("$DOWNLOAD",".pdf","$src",childRequestList));
                        pageParseListener.onSuccess(new Request(request.getUrl()), Lists.newArrayList(crawlerRequest));
                    }
                    try {
                        ftpUtil.disconnect();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });


        }
        return true;
    }
}
