package com.huiyadan.crypt.mybatis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * AES 加解密
 *
 * @author huiyadanli
 */
public class EncryptUtil {
    private static final Logger log = LoggerFactory.getLogger(EncryptUtil.class);

    private static final String KEY_ALGORITHM = "AES";
    private static final String DEFAULT_CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";//默认的加密算法

    /**
     * 密码
     * 如果你要直接使用本类库,TODO 请修改此内容
     */
    private static final String PASSWORD = "f79d438038acf33397af71bef3610a72";


    /**
     * 字符串加密（若入参已加密，则返回原字符串）
     *
     * @param content
     * @return
     */
    public static String encrypt(String content) {
        try {
            String plaintext = tryDecrypt(content);

            if (plaintext == null) {
                Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);// 创建密码器
                byte[] byteContent = content.getBytes("utf-8");
                cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(PASSWORD));// 初始化为加密模式的密码器
                byte[] result = cipher.doFinal(byteContent);// 加密
                return Base64.getEncoder().encodeToString(result);//通过Base64转码返回
            } else {
                return content;
            }

        } catch (Exception e) {
            log.warn("AES encrypt failed", e);
            return content;
        }

    }

    /**
     * 字符串解密
     *
     * @param content
     * @return
     */
    public static String decrypt(String content) {
        String plaintext = tryDecrypt(content);
        return plaintext == null ? content : plaintext;
    }

    public static String tryDecrypt(String content) {
        try {
            //实例化
            Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
            //使用密钥初始化，设置为解密模式
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey(PASSWORD));
            //执行操作
            byte[] result = cipher.doFinal(Base64.getDecoder().decode(content));
            return new String(result, "utf-8");
        } catch (Exception e) {
            log.debug("AES decrypt failed", e);
            return null;
        }
    }


    /**
     * 生成加密秘钥
     *
     * @param password
     * @return
     */
    private static SecretKeySpec getSecretKey(final String password) {
        //返回生成指定算法密钥生成器的 KeyGenerator 对象
        KeyGenerator kg = null;
        try {
            kg = KeyGenerator.getInstance(KEY_ALGORITHM);
            //AES 要求密钥长度为 128
            kg.init(128, new SecureRandom(password.getBytes()));
            //生成一个密钥
            SecretKey secretKey = kg.generateKey();
            return new SecretKeySpec(secretKey.getEncoded(), KEY_ALGORITHM);// 转换为AES专用密钥
        } catch (NoSuchAlgorithmException e) {
            log.error("AES generate failed", e);
        }
        return null;
    }

}
