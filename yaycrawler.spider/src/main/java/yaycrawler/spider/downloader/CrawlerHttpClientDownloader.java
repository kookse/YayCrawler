package yaycrawler.spider.downloader;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.annotation.ThreadSafe;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.downloader.AbstractDownloader;
import us.codecraft.webmagic.downloader.HttpClientGenerator;
import us.codecraft.webmagic.downloader.HttpClientRequestContext;
import us.codecraft.webmagic.downloader.HttpUriRequestConverter;
import us.codecraft.webmagic.proxy.Proxy;
import us.codecraft.webmagic.proxy.ProxyProvider;
import us.codecraft.webmagic.selector.PlainText;
import us.codecraft.webmagic.utils.CharsetUtils;
import us.codecraft.webmagic.utils.HttpClientUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by ucs_yuananyun on 2016/5/23.
 * modify by bill on 2017/3/30
 */
@ThreadSafe
public class CrawlerHttpClientDownloader extends AbstractDownloader {

    private static Pattern UNICODE_PATTERN = Pattern.compile("\\\\u([0-9a-fA-F]{4})");
    private static final Logger logger  = LoggerFactory.getLogger(CrawlerHttpClientDownloader.class);

    private final Map<String, CloseableHttpClient> httpClients = new HashMap<String, CloseableHttpClient>();

    private HttpClientGenerator httpClientGenerator = new HttpClientGenerator();

    private HttpUriRequestConverter httpUriRequestConverter = new HttpUriRequestConverter();

    private ProxyProvider proxyProvider;

    private boolean responseHeader = true;

    public void setHttpUriRequestConverter(HttpUriRequestConverter httpUriRequestConverter) {
        this.httpUriRequestConverter = httpUriRequestConverter;
    }

    public void setProxyProvider(ProxyProvider proxyProvider) {
        this.proxyProvider = proxyProvider;
    }

    private CloseableHttpClient getHttpClient(Site site) {

        if (site == null) {
            return httpClientGenerator.getClient(null);
        }
        String domain = site.getDomain();
        CloseableHttpClient httpClient = httpClients.get(domain);
        if (httpClient == null) {
            synchronized (this) {
                httpClient = httpClients.get(domain);
                if (httpClient == null) {
                    httpClient = httpClientGenerator.getClient(site);
                    httpClients.put(domain, httpClient);
                }
            }
        }
        return httpClient;
    }


    public Page download(Request request, Task task, String cookie) {

        if (task == null || task.getSite() == null) {
            throw new NullPointerException("task or site can not be null");
        }
        Site site = task.getSite();
        CloseableHttpResponse httpResponse = null;

        if (site != null && StringUtils.isNotBlank(cookie)) {
            site.addHeader("Cookie", cookie);
        }
        CloseableHttpClient httpClient = getHttpClient(site);
        Proxy proxy = proxyProvider != null ? proxyProvider.getProxy(task) : null;
//        proxy = new Proxy("127.0.0.1",8888);
        HttpClientRequestContext requestContext = httpUriRequestConverter.convert(request, site, proxy);
        Page page = Page.fail();
        try {
            httpResponse = httpClient.execute(requestContext.getHttpUriRequest(), requestContext.getHttpClientContext());
            page = handleResponse(request, request.getCharset() != null ? request.getCharset() : site.getCharset(), httpResponse, task);
            onSuccess(request);
            logger.info("downloading page success {}", request.getUrl());
            return page;
        } catch (IOException e) {
            logger.warn("download page {} error", request.getUrl(), e);
            onError(request);
            return page;
        } finally {
            if (httpResponse != null) {
                //ensure the connection is released back to pool
                EntityUtils.consumeQuietly(httpResponse.getEntity());
            }
            if (proxyProvider != null && proxy != null) {
                proxyProvider.returnProxy(proxy, page, task);
            }
        }

    }

    @Override
    protected void onSuccess(Request request) {
        super.onSuccess(request);
    }

    @Override
    protected void onError(Request request) {
        super.onError(request);
    }

    @Override
    public Page download(Request request, Task task) {
        return download(request, task, null);
    }

    @Override
    public void setThread(int thread) {
        httpClientGenerator.setPoolSize(thread);
    }

    protected Page handleResponse(Request request, String charset, HttpResponse httpResponse, Task task) throws IOException {
        byte[] bytes = IOUtils.toByteArray(httpResponse.getEntity().getContent());
        String contentType = httpResponse.getEntity().getContentType() == null ? "" : httpResponse.getEntity().getContentType().getValue();
        Page page = new Page();
        page.setBytes(bytes);
        if (!request.isBinaryContent()) {
            if (charset == null) {
                charset = getHtmlCharset(contentType, bytes);
            }
            page.setCharset(charset);
            String content = new String(bytes, charset);
            //unicode编码处理
            if (UNICODE_PATTERN.matcher(content).find())
                content = StringEscapeUtils.unescapeJava(content.replace("\"", "\\\""));
            page.setRawText(content);
        }
        page.setUrl(new PlainText(request.getUrl()));
        page.setRequest(request);
        page.setStatusCode(httpResponse.getStatusLine().getStatusCode());
        page.setDownloadSuccess(true);
        if (responseHeader) {
            page.setHeaders(HttpClientUtils.convertHeaders(httpResponse.getAllHeaders()));
        }
        return page;
    }

    private String getHtmlCharset(String contentType, byte[] contentBytes) throws IOException {
        String charset = CharsetUtils.detectCharset(contentType, contentBytes);
        if (charset == null) {
            charset = Charset.defaultCharset().name();
            logger.warn("Charset autodetect failed, use {} as charset. Please specify charset in Site.setCharset()", Charset.defaultCharset());
        }
        return charset;
    }
//    protected boolean statusAccept(Set<Integer> acceptStatCode, int statusCode) {
//        return acceptStatCode.contains(statusCode);
//    }
//
//    protected HttpUriRequest getHttpUriRequest(Request request, Site site, Map<String, String> headers,HttpHost proxy) {
//        RequestBuilder requestBuilder = selectRequestMethod(request).setUri(request.getUrl());
//        if (headers != null) {
//            for (Map.Entry<String, String> headerEntry : headers.entrySet()) {
//                requestBuilder.addHeader(headerEntry.getKey(), headerEntry.getValue());
//            }
//        }
//        RequestConfig.Builder requestConfigBuilder = RequestConfig.custom()
//                .setConnectionRequestTimeout(site.getTimeOut())
//                .setSocketTimeout(site.getTimeOut())
//                .setConnectTimeout(site.getTimeOut())
//                .setCookieSpec(CookieSpecs.BEST_MATCH);
//        if (proxy !=null) {
//            requestConfigBuilder.setProxy(proxy);
//            request.putExtra(Request.PROXY, proxy);
//        }
//        requestBuilder.setConfig(requestConfigBuilder.build());
//        return requestBuilder.build();
//    }
//
//    protected RequestBuilder selectRequestMethod(Request request) {
//        String method = request.getMethod();
//        if (method == null || method.equalsIgnoreCase(HttpConstant.Method.GET)) {
//            //default get
//            return RequestBuilder.get();
//        } else if (method.equalsIgnoreCase(HttpConstant.Method.POST)) {
//            RequestBuilder requestBuilder = RequestBuilder.post();
//
//            List<NameValuePair> parameters=new ArrayList<>();
//            Map<String,Object> paramsMap= (Map<String, Object>) request.getExtra("nameValuePair");
//            if(paramsMap!=null) {
//                for (Map.Entry<String, Object> entry : paramsMap.entrySet()) {
//                    //对中文编码
//                    String value = String.valueOf(entry.getValue());
////                    try {
//////                        value =
////                                URLEncoder.encode(value, "utf-8");
////                    } catch (UnsupportedEncodingException e) {
////                        logger.error(e.getMessage());
////                    }
//                    parameters.add(new BasicNameValuePair(entry.getKey(), value));
//                }
//            }
//            requestBuilder.setEntity(new UrlEncodedFormEntity(parameters, Charset.forName("utf-8")));
//            return requestBuilder;
//        } else if (method.equalsIgnoreCase(HttpConstant.Method.HEAD)) {
//            return RequestBuilder.head();
//        } else if (method.equalsIgnoreCase(HttpConstant.Method.PUT)) {
//            return RequestBuilder.put();
//        } else if (method.equalsIgnoreCase(HttpConstant.Method.DELETE)) {
//            return RequestBuilder.delete();
//        } else if (method.equalsIgnoreCase(HttpConstant.Method.TRACE)) {
//            return RequestBuilder.trace();
//        }
//        throw new IllegalArgumentException("Illegal HTTP Method " + method);
//    }
//
//    protected Page handleResponse(Request request, String charset, HttpResponse httpResponse, Task task) throws IOException {
//        String content = getContent(charset, httpResponse);
//        Page page = new Page();
//        page.setRawText(content);
//        page.setUrl(new PlainText(request.getUrl()));
//        page.setRequest(request);
//        page.setStatusCode(httpResponse.getStatusLine().getStatusCode());
//        return page;
//    }
//
//    protected String getContent(String charset, HttpResponse httpResponse) throws IOException {
//        String content = null;
//        if (charset == null) {
//            byte[] contentBytes = IOUtils.toByteArray(httpResponse.getEntity().getContent());
//            String htmlCharset = getHtmlCharset(httpResponse, contentBytes);
//            if (htmlCharset != null) {
//                content = new String(contentBytes, htmlCharset);
//            } else {
//                logger.warn("Charset autodetect failed, use {} as charset. Please specify charset in Site.setCharset()", Charset.defaultCharset());
//                content = new String(contentBytes,"utf-8");
//            }
//        } else {
//            content = IOUtils.toString(httpResponse.getEntity().getContent(), charset);
//        }
//        //unicode编码处理
//        if (UNICODE_PATTERN.matcher(content).find())
//            return StringEscapeUtils.unescapeJava(content.replace("\"","\\\""));
//        return content;
//    }
//
//    protected String getHtmlCharset(HttpResponse httpResponse, byte[] contentBytes) throws IOException {
//        String charset;
//        // charset
//        // 1、encoding in http header Content-Type
//        String value = httpResponse.getEntity().getContentType().getValue();
//        charset = UrlUtils.getCharset(value);
//        if (StringUtils.isNotBlank(charset)) {
//            logger.debug("Auto get charset: {}", charset);
//            return charset;
//        }
//        // use default charset to decode first time
//        Charset defaultCharset = Charset.defaultCharset();
//        String content = new String(contentBytes, defaultCharset.name());
//        // 2、charset in meta
//        if (StringUtils.isNotEmpty(content)) {
//            Document document = Jsoup.parse(content);
//            Elements links = document.select("meta");
//            for (Element link : links) {
//                // 2.1、html4.01 <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
//                String metaContent = link.attr("content");
//                String metaCharset = link.attr("charset");
//                if (metaContent.indexOf("charset") != -1) {
//                    metaContent = metaContent.substring(metaContent.indexOf("charset"), metaContent.length());
//                    charset = metaContent.split("=")[1];
//                    break;
//                }
//                // 2.2、html5 <meta charset="UTF-8" />
//                else if (StringUtils.isNotEmpty(metaCharset)) {
//                    charset = metaCharset;
//                    break;
//                }
//            }
//        }
//        logger.debug("Auto get charset: {}", charset);
//        // 3、todo use tools as cpdetector for content decode
//        return charset;
//    }
}
