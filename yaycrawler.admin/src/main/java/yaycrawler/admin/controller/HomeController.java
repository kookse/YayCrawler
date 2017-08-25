package yaycrawler.admin.controller;

import com.alibaba.fastjson.JSON;
import com.google.common.io.Files;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import yaycrawler.admin.communication.MasterActor;
import yaycrawler.common.utils.HttpUtil;
import yaycrawler.common.utils.OCRHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yuananyun on 2016/5/3.
 */
@Controller
public class HomeController {
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
    @RequestMapping(value = {"/workerList"}, method = RequestMethod.GET)
    public ModelAndView workerList() {
        ModelAndView mv = new ModelAndView("worker_list");
        return mv;
    }
    @RequestMapping(value = {"/queryWorkers"}, method = RequestMethod.GET)
    @ResponseBody
    public Object queryWorkers()
    {
        return masterActor.retrievedWorkerRegistrations();
    }

    @RequestMapping(value = {"/testFus"}, method = RequestMethod.POST)
    @ResponseBody
    public Object testFus(@RequestParam String address,@RequestParam String username,@RequestParam String pwd)
    {
        HttpUtil httpUtil = HttpUtil.getInstance();
        String loginUrl = "https://gzgjj.gov.cn:8280/fund/wap/wap!userLogin.do";
        Map params = new HashMap();
        params.put("username", username);
        params.put("pwd", pwd);
        ArrayList<Header> headerList = new ArrayList<>();
        headerList.add(new BasicHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.101 Safari/537.36"));
        int i =0;
        Map data = new HashMap();
        while (true) {
            try {
                HttpResponse response = httpUtil.doGet("https://gzgjj.gov.cn:8280/fund/wap/wap!getImgCode.do?rnd=" + Math.random(), null, headerList);
                if(response.getStatusLine().getStatusCode() != 302) {
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
                    System.out.println("************************" + i + "***********" + recognizeText + "*******"+username+"********");
                    headerList.add(new BasicHeader("Cookie", "rmbUser=true; username=" + username + ";"));
                    String content = EntityUtils.toString(httpUtil.doGet("https://gzgjj.gov.cn:8280/fund/wap/wap!depositQuery.do?rdm=" + Math.random(), null, headerList).getEntity());
                    System.out.println(content);
                    data.put("data1",content);
                    content = EntityUtils.toString(httpUtil.doGet("https://gzgjj.gov.cn:8280/fund/wap/wap!depositDetails.do?rdm=" + Math.random(), null, headerList).getEntity());
                    data.put("data2",content);
                    System.out.println(content);
                    i++;
                    break;
                } else if(code.get("ret_msg").toString().indexOf("未有该身份") > -1){
                    System.out.println(JSON.toJSONString(code + "$$$$$$$$$$$$$$$$" + username));
                    data = code;
                    break;
                } else if(code.get("ret_msg").toString().indexOf("密码") > -1){
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

}
