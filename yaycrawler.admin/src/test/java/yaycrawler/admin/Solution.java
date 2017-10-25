package yaycrawler.admin;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import org.apache.commons.codec.digest.Md5Crypt;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.junit.Test;
import org.springframework.util.Base64Utils;
import yaycrawler.common.utils.HttpUtil;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Math.min;

/**
 * Created by bill on 2017/3/31.
 */
public class Solution {
    private void SwapFirstK(int stt, int lst, char[] str) {
        while(stt < lst) {
            char tmp = str[stt];
            str[stt] = str[lst];
            str[lst] = tmp;
            stt++; lst--;
        }
    }

    public String reverseStr(String s, int k) {
        int sz = s.length();
        char[] tmp = s.toCharArray();
        for(int i = 0; i < sz; i += k) {
            if(i%(2*k) == 0) {
                SwapFirstK(i, min(i+k-1, sz - 1), tmp);
            }
        }
        return new String(tmp);
    }

    public static void main(String[] args) {

        Solution solution = new Solution();
        String result = solution.reverseStr("abcdefg",2);
        System.out.println(result);
    }

    @Test
    public void toJiaYuanParam() {
        Map param = Maps.newHashMap();
        param.put("$loginUrl","https://passport.jiayuan.com/dologin.php?pre_url=http://www.jiayuan.com/usercp");
        param.put("$valideLogin","top.location.href='(http://login.jiayuan.com/jump.*)'");
        param.put("$name","get(loginName)");
        param.put("$password","get(loginPassword)");
        param.put("remem_pass","on");
        param.put("_s_x_id","5ce312f2f3f7fe31245fc3d26f3cf2fd");
        param.put("ljg_login","1");
        param.put("m_p_l","1");
        param.put("channel","0");
        param.put("position","0");
        System.out.println(JSON.toJSONString(param));
    }

    @Test
    public void toJiaYuanSite() {
        Map param = Maps.newHashMap();
        param.put("loginName","13216635314");
        param.put("loginPassword","123456789");
        System.out.println(JSON.toJSONString(param));
    }

    @Test
    public void toGzlss() {
        Map param = Maps.newHashMap();
        param.put("$loginUrl","http://gzlss.hrssgz.gov.cn/cas/login");
        param.put("$valideLogin","http://gzlss.hrssgz.gov.cn:80/gzlss_web/business/tomain/main.xhtml");
        param.put("$username","get(loginName).rsa(\"var modulus=\"(.*)\"\"$$\"var exponent=\"(.*)\"\"$$\"$1\")");
        param.put("$password","get(loginPassword).rsa(\"var modulus=\"(.*)\"\"$$\"var exponent=\"(.*)\"\"$$\"$1\")");
        param.put("$yzm","downloadEngine(http://gzlss.hrssgz.gov.cn/cas/captcha.jpg).binaryEngine($1$$178$$13$$160).ocrEngine($1$$[0-9a-zA-z]{4})");
        param.put("usertype", "2");
        param.put("_eventId", "submit");
        param.put("$lt", "regex(<input type=\"hidden\" name=\"lt\" value=\"(.*)\" )");
        System.out.println(JSON.toJSONString(param));
    }

    @Test
    public void toGJJ() {
        Map param = Maps.newHashMap();
        param.put("loginName","445281198903100953");
        param.put("loginPassword","890310");
        System.out.println(JSON.toJSONString(param));
    }

    @Test
    public void toGZLSS() {
        Map param = Maps.newHashMap();
        param.put("loginName","431027197805183119");
        param.put("loginPassword","g123456");
        System.out.println(JSON.toJSONString(param));
    }

    @Test
    public void toGdltax() {
        Map param = Maps.newHashMap();
        param.put("$loginUrl","http://mtax.gdltax.gov.cn/appserver/security/user/tpLogin.do");
        param.put("$valideLogin",".*\"flag\":\"ok\"");
        param.put("phonenum","get(loginName)");
        param.put("password","get(loginPassword)");
        param.put("$yzm","downloadEngine(http://mtax.gdltax.gov.cn/appserver/security/binduser/captcha.do).ocrEngine($1$$[0-9a-zA-z]{4})");
        param.put("#accountInfoStr","json(phonenum$$password$$yzm).encode($1).encode($1)");
        param.put("callback", "jsonp_callback4");
        param.put("timeOut", "100000");
        param.put("$time", "currentTimeMillis()");
        System.out.println(JSON.toJSONString(param));
    }

    @Test
    public void toGdltaxSite() {
        Map param = Maps.newHashMap();
        param.put("loginName","15626241465");
        param.put("loginPassword","jaB4Gz143AtQ");
        System.out.println(JSON.toJSONString(param));
    }

    @Test
    public void toBjgjj() {
        Map param = Maps.newHashMap();
        param.put("$loginUrl","http://www.bjgjj.gov.cn/wsyw/wscx/gjjcx-choice.jsp");
        param.put("#lk","downloadEngine(http://www.bjgjj.gov.cn/wsyw/wscx/asdwqnasmdnams.jsp$$content).trim($1).substring(\"$1\"$$\"4\")");
        param.put("lb",1);
        param.put("$valideLogin","<span class=\"tittle1\">");
        param.put("$bh1","get(loginName)");
        param.put("$mm1","get(loginPassword)");
        param.put("$bh","get(loginName).encrypt3Hex($1$$pdcss123$$css11q1a$$co1qacq11)");
        param.put("$mm","get(loginPassword).encrypt3Hex($1$$pdcss123$$css11q1a$$co1qacq11)");
        param.put("$gjjcxjjmyhpppp","downloadEngine(http://www.bjgjj.gov.cn/wsyw/servlet/PicCheckCode1?v=1).binaryEngine($1$$178$$13$$160).ocrEngine($1$$[0-9a-zA-z]{4})");
        param.put("#gjjcxjjmyhpppp1","get(gjjcxjjmyhpppp)");
        System.out.println(JSON.toJSONString(param));
    }

    @Test
    public void toBjgjjSite() {
        Map param = Maps.newHashMap();
        param.put("loginName","120107198207113912");
        param.put("loginPassword","031107");
        System.out.println(JSON.toJSONString(param));
    }

    @Test
    public void toShgjj() {
        Map param = Maps.newHashMap();
        param.put("$loginUrl","https://persons.shgjj.com/MainServlet");
        param.put("$valideLogin","<div align=\"center\">您的个人账户情况</div>");
        param.put("$username","get(loginName)");
        param.put("$password","get(loginPassword)");
        param.put("SUBMIT.x","25");
        param.put("SUBMIT.y","15");
        param.put("ID","0");
        param.put("$password_md5","get(loginPassword).md5($1)");
        param.put("$imagecode","downloadEngine(https://persons.shgjj.com/VerifyImageServlet).ocrEngine($1$$[0-9]{4})");
        System.out.println(JSON.toJSONString(param));
    }

    @Test
    public void toShgjjSite() {
        Map param = Maps.newHashMap();
        param.put("loginName","28324978");
        param.put("loginPassword","19097041");
        System.out.println(JSON.toJSONString(param));
    }

    @Test
    public void toSzsi() {
        Map param = Maps.newHashMap();
        param.put("$loginUrl","https://e.szsi.gov.cn/siservice/LoginAction.do");
        param.put("$valideLogin","<img border=\"0\" name='image1' src=\"ShowImage\" width=\"94\" height=\"122\" alt=\"\">");
        param.put("$AAC002","get(loginName)");
        param.put("$CAC222","get(loginPassword).base64($1)");
        param.put("$pid","regex(<input type=\"hidden\" name=\"pid\" value=\"(.*)\">)");
        param.put("Method","P");
        param.put("$PSINPUT","regex(<img border=0 src=\"(.*)\" alt=\"\").format(\"https://e.szsi.gov.cn/siservice/%s\"$$\"$1\").downloadEngine($1).ocrEngine($1$$[0-9]{4})");
        System.out.println(JSON.toJSONString(param));
    }

    @Test
    public void toSzsiSite() {
        Map param = Maps.newHashMap();
        param.put("loginName","fei891223");
        param.put("loginPassword","Fei12345");
        System.out.println(Base64Utils.encodeToString("Fei12345".getBytes()));
        System.out.println(JSON.toJSONString(param));
    }

    @Test
    public void toQichachaSite() {
        Map param = Maps.newHashMap();
        param.put("loginName","13175315644");
        param.put("loginPassword","123456789");
        System.out.println(JSON.toJSONString(param));
    }

    @Test
    public void testParam() {
        String param = "grsds";
        String url = "http://mtax.gdltax.gov.cn/appserver/zrr/grsds/queryGrsdsNsmx.do?startdate=20170601&enddate=20170930&callback=jsonp_callback4&time=1505464152982&timeOut=60000&loginName=15626241465&loginPassword=123432435";
        Pattern pattern = Pattern.compile(String.format("%s=(.*)&|%s=(.*)|/%s/(.*)/|/%s/(.*)",param,param,param,param));
        Matcher matcher = pattern.matcher(url);
        String value = "";
        while (matcher.find()) {
            for (int i = 1; i <= matcher.groupCount(); i++) {
                value = matcher.group(i);
                if(StringUtils.isNotEmpty(value)) {
                    break;
                }
            }
        }
        System.out.println(value);
    }

    @Test
    public void testData() throws Exception{
        String url = "http://www.bjgjj.gov.cn/wsyw/wscx/gjjcx-login.jsp";
        HttpUtil httpUtil = HttpUtil.getInstance();
        HttpResponse response = httpUtil.doGet(url,null,null);
        System.out.println(EntityUtils.toString(response.getEntity()));
    }
}