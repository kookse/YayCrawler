package yaycrawler.common.utils;

import com.github.stuxuhai.jpinyin.PinyinException;
import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PinYinUtil {

    //输入中文名,获取对应每个文字的首字母大写,其他字符不变
    public static String converterToFirstSpell(String chineseName) {
        int strLen = chineseName.length();
        int i = 0;
        StringBuffer buffer = new StringBuffer();
        while (i < strLen) {
            String word = null;
            try {
                word = PinyinHelper.convertToPinyinString(String.valueOf(chineseName.charAt(i)), "", PinyinFormat.WITHOUT_TONE);
            } catch (PinyinException e) {
                e.printStackTrace();
            }
            char[] ch = word.toCharArray();
            if (ch[0] >= 'a' && ch[0] <= 'z') {
                ch[0] = (char) (ch[0] - 32);
            }
            buffer.append(String.valueOf(ch));
            i++;
        }
        String regEx ="[^a-zA-Z0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(buffer.toString());
        return m.replaceAll("").trim();
    }
}
