package yaycrawler.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import yaycrawler.common.interceptor.SignatureSecurityInterceptor;

import java.util.Random;

/**
 * Created by ucs_yuananyun on 2016/5/19.
 */
public class CharacterUtils {

    private static final Logger logger  = LoggerFactory.getLogger(CharacterUtils.class);

    public static String getRandomString(int length) {
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; ++i) {
            int number = random.nextInt(3);
            long result = 0;

            switch (number) {
                case 0:
                    result = Math.round(Math.random() * 25 + 65);
                    sb.append(String.valueOf((char) result));
                    break;
                case 1:
                    result = Math.round(Math.random() * 25 + 97);
                    sb.append(String.valueOf((char) result));
                    break;
                case 2:
                    sb.append(String.valueOf(new Random().nextInt(10)));
                    break;
                default:
                    break;
            }
        }
        return sb.toString();
    }

//    public static void main(String[] args) {
//        System.out.println(getRandomString(10));
//    }
}
