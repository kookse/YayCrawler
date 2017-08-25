package yaycrawler.common.utils;
import java.security.InvalidParameterException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import javax.crypto.Cipher;

import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;
/**
 * @author bill
 * @create 2017-08-23 14:33
 * @desc RSA加密算法
 * RSA算法加密/解密工具类。
 *
 * @author Administrator
 *
 */
public class RSA {
    /** 算法名称 */
    private static final String ALGORITHOM = "RSA";

    /** 密钥大小 */
    private static final int KEY_SIZE = 1024;
    /** 默认的安全服务提供者 */
    private static final Provider DEFAULT_PROVIDER = new BouncyCastleProvider();

    private static KeyPairGenerator keyPairGen = null;
    private static KeyFactory keyFactory = null;
    /** 缓存的密钥对。 */
    private static KeyPair oneKeyPair = null;

    private static String radamKey = "";

    static {
        try {
            keyPairGen = KeyPairGenerator.getInstance(ALGORITHOM,
                    DEFAULT_PROVIDER);
            keyFactory = KeyFactory.getInstance(ALGORITHOM, DEFAULT_PROVIDER);
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 生成并返回RSA密钥对。
     */
    private static synchronized KeyPair generateKeyPair() {
        try {
            keyPairGen.initialize(KEY_SIZE,
                    new SecureRandom(radamKey.getBytes()));
            oneKeyPair = keyPairGen.generateKeyPair();
            return oneKeyPair;
        } catch (InvalidParameterException ex) {
            ex.printStackTrace();
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /** 返回已初始化的默认的公钥。 */
    public static RSAPublicKey getDefaultPublicKey() {
        KeyPair keyPair = generateKeyPair();
        if (keyPair != null) {
            return (RSAPublicKey) keyPair.getPublic();
        }
        return null;
    }

    /**
     * 使用指定的私钥解密数据。
     *
     * @param privateKey 给定的私钥。
     * @param data 要解密的数据。
     * @return 原数据。
     */
    public static byte[] decrypt(PrivateKey privateKey, byte[] data) throws Exception {
        Cipher ci = Cipher.getInstance(ALGORITHOM, DEFAULT_PROVIDER);
        ci.init(Cipher.DECRYPT_MODE, privateKey);
        return ci.doFinal(data);
    }

    /**
     * 使用默认的私钥解密给定的字符串。
     * <p />
     * 若{@code encrypttext} 为 {@code null}或空字符串则返回 {@code null}。
     * 私钥不匹配时，返回 {@code null}。
     *
     * @param encrypttext 密文。
     * @return 原文字符串。
     */
    public static String decryptString(String encrypttext) {
        if(StringUtils.isBlank(encrypttext)) {
            return null;
        }
        KeyPair keyPair = generateKeyPair();
        try {
            byte[] en_data = Hex.decode(encrypttext);
            byte[] data = decrypt((RSAPrivateKey)keyPair.getPrivate(), en_data);
            return new String(data);
        } catch(NullPointerException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * 使用默认的私钥解密由JS加密（使用此类提供的公钥加密）的字符串。
     *
     * @param encrypttext 密文。
     * @return {@code encrypttext} 的原文字符串。
     */
    public static String decryptStringByJs(String encrypttext) {
        String text = decryptString(encrypttext);
        if(text == null) {
            return null;
        }
        return StringUtils.reverse(text);
    }

    public static void main(String[] args) {

        //密码种子，一个密码种子生产一组RSA密码
        RSA.radamKey = "010001";

        //获取公钥，分发公钥（e1,n）
        RSAPublicKey publicKey = RSA.getDefaultPublicKey();
        //公钥-系数(n)
        System.out.println("public key modulus:"
                + new String(Hex.encode(publicKey.getModulus().toByteArray())));
        //公钥-指数(e1)
        System.out.println("public key exponent:"
                + new String(Hex.encode(publicKey.getPublicExponent()
                .toByteArray())));

        //JS加密后的字符串
        String pppp = "60de8f4baddf9264df58951e9fa2bd95d2ee7f851e213f5d9694f0e9ce59bc2f09863afb82afa1f66b42d8732a40b2eaa1339b8123822314083938e43e94466044220e2014f70deaebcfd67cdfc09944b97d0bf059eb0bf25b1165f0c182ec3f3da15f86c88f3b7497d74d33d2655811e576167a84045a0a8eb0f3c10b1c1fa3";
        //解密后的字符串
        String kkkk = RSA.decryptStringByJs(pppp);

        System.out.println("解密后文字：" + kkkk);
    }
}
