package io.github.msyysoft.java.utiltools;


import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.security.Key;

/**
 * 3DES加密工具类
 *
 * @author liufeng
 * @date 2012-10-11
 */
public class Des3Util {
    private final static String encoding = "utf-8";
    private static String c0 = "@";
    private static String c1 = "0";
    private static String c2 = "1";
    private static String c3 = "c";
    private static String c4 = "d";
    private static String c5 = "f";
    private static String c6 = "y";

    /**
     * 3DES加密
     *
     * @param plainText 普通文本
     * @return
     * @throws Exception
     */
    public static String encode(String plainText, String secretKey, String iv) throws Exception {
        Key deskey = null;
        DESedeKeySpec spec = new DESedeKeySpec(secretKey.getBytes());
        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
        deskey = keyfactory.generateSecret(spec);

        Cipher cipher = Cipher.getInstance("desede/CBC/PKCS5Padding");
        IvParameterSpec ips = new IvParameterSpec(iv.getBytes());
        cipher.init(Cipher.ENCRYPT_MODE, deskey, ips);
        byte[] encryptData = cipher.doFinal(plainText.getBytes(encoding));
        return Base64.encodeBase64String(encryptData);
    }

    public static String encode(String plainText) throws Exception {
        return encode(plainText, defSecretKey(), defIv());
    }

    /**
     * 3DES解密
     *
     * @param encryptText 加密文本
     * @return
     * @throws Exception
     */
    public static String decode(String encryptText, String secretKey, String iv) throws Exception {
        Key deskey = null;
        DESedeKeySpec spec = new DESedeKeySpec(secretKey.getBytes());
        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
        deskey = keyfactory.generateSecret(spec);
        Cipher cipher = Cipher.getInstance("desede/CBC/PKCS5Padding");
        IvParameterSpec ips = new IvParameterSpec(iv.getBytes());
        cipher.init(Cipher.DECRYPT_MODE, deskey, ips);

        byte[] decryptData = cipher.doFinal(Base64.decodeBase64(encryptText));

        return new String(decryptData, encoding);
    }

    public static String decode(String encryptText) throws Exception {
        return decode(encryptText, defSecretKey(), defIv());
    }

    private static String defSecretKey() {
        return c4 + c6 + c5 + c3 + c0 + c2 + c1 + c1 + c4 + c6 + c5 + c3 + c0 + c2 + c1 + c1 + c4 + c6 + c5 + c3 + c0 + c2 + c1 + c1;
    }

    private static String defIv() {
        return c4 + c6 + c5 + c3 + c0 + c2 + c1 + c1;
    }

    public static void setC0(String c0) {
        Des3Util.c0 = c0;
    }

    public static void setC1(String c1) {
        Des3Util.c1 = c1;
    }

    public static void setC2(String c2) {
        Des3Util.c2 = c2;
    }

    public static void setC3(String c3) {
        Des3Util.c3 = c3;
    }

    public static void setC4(String c4) {
        Des3Util.c4 = c4;
    }

    public static void setC5(String c5) {
        Des3Util.c5 = c5;
    }

    public static void setC6(String c6) {
        Des3Util.c6 = c6;
    }

    public static void main(String args[]) throws Exception {
        String plainText = "";
        String encode = encode(plainText);
        String decode = decode(encode);
        System.out.println(plainText.equals(decode));
        System.out.println(encode);
        System.out.println(decode);
    }
}  