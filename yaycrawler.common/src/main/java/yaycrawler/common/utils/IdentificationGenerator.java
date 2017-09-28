package yaycrawler.common.utils;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * 用于生成唯一标识的类
 * Created by  yuananyun on 2017/7/3.
 */
public class IdentificationGenerator {

    /**
     * 根据多个key生成一个唯一hash
     * @param keys
     * @return
     */
    public static String fromHash(String ... keys){
        StringBuilder seedBuilder=new StringBuilder();
        for (String key : keys) {
            seedBuilder.append(String.valueOf(key)).append("@");
        }
        return DigestUtils.md5Hex(seedBuilder.toString());
    }
}
