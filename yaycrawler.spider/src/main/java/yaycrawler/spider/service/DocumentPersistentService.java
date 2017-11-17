package yaycrawler.spider.service;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.utils.UrlUtils;
import yaycrawler.common.model.CrawlerRequest;
import yaycrawler.common.utils.FTPUtils;
import yaycrawler.spider.listener.IPageParseListener;
import yaycrawler.spider.persistent.IResultPersistentService;
import yaycrawler.spider.persistent.PersistentDataType;

import java.util.*;

/**
 * Created by ucs_yuananyun on 2016/5/23.
 */
@Component
public class DocumentPersistentService implements IResultPersistentService {

    private static final Logger logger  = LoggerFactory.getLogger(DocumentPersistentService.class);

    @Autowired(required = false)
    private IPageParseListener pageParseListener;

    @Autowired(required = false)
    private DownloadService downloadService;

    /**
     * param data {id:"",srcList:""}
     */
    @Override
    public boolean saveCrawlerResult(String pageUrl, Map<String, Object> regionDataMap) {
        //List<String> childRequestList = new LinkedList<>();
        try {
            List<String> srcList = new ArrayList<>();
            String id = "";
            for (Object o : regionDataMap.values()) {
                Collection regionData;
                if(o instanceof HashedMap) {
                    regionData = ((Map<String,Object>)o).values();
                } else {
                    regionData = (Collection) o;
                }
                if(regionData==null) continue;
                for (Object src : regionData) {
                    if (src instanceof List)
                        srcList = (List<String>) src;
                    else if(src instanceof HashedMap) {
                        srcList.add(MapUtils.getString((HashedMap)src,"src"));
                    } else {
                        id = String.valueOf(src);
                    }
                }
                CrawlerRequest crawlerRequest = new CrawlerRequest();
                crawlerRequest.setDomain(UrlUtils.getDomain(pageUrl));
                crawlerRequest.setHashCode(DigestUtils.sha1Hex(pageUrl));
                crawlerRequest.setMethod("get");
                crawlerRequest.setUrl(pageUrl + "?$download=pdf");
                crawlerRequest.setExtendMap(ImmutableMap.of("$DOWNLOAD",".pdf","$src",srcList));
                downloadService.startCrawlerDownload(Lists.newArrayList(crawlerRequest));
//                if (srcList == null || srcList.isEmpty())
//                    continue;
//                else{
//                    ftpUtil = new FTPUtils();
//                    ftpUtil.connect(url, port, username, password);
//                }
//
//                for (String src : srcList) {
//                    HttpResponse response =  httpUtil.doGet(src,null,null);
//                    if(response.getStatusLine().getStatusCode() != 200) {
//                        childRequestList.add(src);
//                        continue;
//                    }
//
//                    byte[] bytes = EntityUtils.toByteArray(response.getEntity());
//                    String documentName = StringUtils.substringAfterLast(src,"/");
//                    if (!StringUtils.contains(documentName,".")) {
//                        documentName = documentName + ".pdf";
//                    }
//                    File document = new File(ftpPath + "/" + documentName);
//                    Files.createParentDirs(document);
//                    Files.write(bytes,document);
//
//                    String path = UrlUtils.getDomain(pageUrl) + "/" + DigestUtils.sha1Hex(pageUrl) + "/" + id;
//                    //上传文件
//                    ftpUtil.upLoadByFtp(ftpPath + "/" + documentName, path, documentName);
//                    document.delete();
//                }
//                ftpUtil.disconnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
//            if(childRequestList.size() > 0) {
//                CrawlerRequest crawlerRequest = new CrawlerRequest();
//                crawlerRequest.setDomain(UrlUtils.getDomain(pageUrl));
//                crawlerRequest.setHashCode(DigestUtils.sha1Hex(pageUrl));
//                crawlerRequest.setMethod("get");
//                crawlerRequest.setUrl(pageUrl + "?$download=pdf");
//                crawlerRequest.setExtendMap(ImmutableMap.of("$DOWNLOAD",".pdf","$src",childRequestList));
//                pageParseListener.onSuccess(new Request(pageUrl), Lists.newArrayList(crawlerRequest));
//            }
        }
        return true;
    }

    @Override
    public String getSupportedDataType() {
        return PersistentDataType.DOCMUENT;
    }

    @Override
    public Object getCrawlerResult(String collectionName, String taskId) {
        return null;
    }

}
