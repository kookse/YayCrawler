package yaycrawler.common.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import yaycrawler.common.interceptor.SignatureSecurityInterceptor;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * 管理Casperjs的启动和执行
 * Created by ucs_yuananyun on 2016/6/23.
 */
public class CasperjsProgramManager {

    private static final Logger logger  = LoggerFactory.getLogger(CasperjsProgramManager.class);

    public static String launch(String jsFileName, List<String> params) {
        return launch(jsFileName, null, params);
    }
    public static String launch(String jsFileName, String pageCharset,List<String> params) {
        if (StringUtils.isBlank(jsFileName)) {
            logger.error("待执行的js文件名不能为空！");
            return null;
        }
        BufferedReader br = null;
        try {
            if(pageCharset==null) pageCharset = "utf-8";
            File pathFile = new File(jsFileName);
//            String path = CasperjsProgramManager.class.getResource("/").getPath();
//            path = path.substring(1, path.lastIndexOf("/") + 1);
//            String os = System.getProperties().getProperty("os.name");
//            String casperJsPath = "casperjs";
//            String phantomJsPath = "";
//            if (StringUtils.startsWithIgnoreCase(os, "win")) {
//                casperJsPath = path + "casperjs/bin/casperjs.exe";
//                phantomJsPath = path + "phantomjs/window/phantomjs.exe";
//            } else {
//                path = "/" + path;
//                casperJsPath = path + "casperjs/bin/casperjs";
//                phantomJsPath = path + "phantomjs/linux/phantomjs";
//            }
//            logger.info("CasperJs程序地址:{}", casperJsPath);

            List<String> cmd = new ArrayList<String>();
            cmd.add("casperjs");
            cmd.add("");
            cmd.add(new File(jsFileName).getName());
            cmd.addAll(params);
            cmd.add("--output-encoding="+pageCharset);
            cmd.add("--web-security=no");
            cmd.add("--ignore-ssl-errors=true");
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.directory(new File(jsFileName).getParentFile());
            processBuilder.command(cmd);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            int w = process.waitFor();
            InputStream is = process.getInputStream();
            br = new BufferedReader(new InputStreamReader(is, pageCharset));
            StringBuffer sbf = new StringBuffer();
            String tmp = "";
            while ((tmp = br.readLine()) != null) {
                sbf.append(tmp).append("\r\n");
            }
            br.close();
            process.destroy();
            return sbf.toString();
        } catch (Exception ex) {
            logger.error("{}",ex);
            return null;
        } finally {
            if(br != null)
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

}
