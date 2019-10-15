package com.liu.acp.security;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 * 加密.
 * @Auther: liudongfei
 * @Date: 2019/9/6 14:22
 * @Description:
 */
public class EncryptionAES {
    /**
     * 加密.
     * @param entryptContent  加密的内容
     * @param key  加密的钥匙
     * @return
     */
    @SuppressWarnings("restriction")
    public static String entrypt(String entryptContent, String key) throws NoSuchAlgorithmException,
            UnsupportedEncodingException, NoSuchPaddingException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException {
        String strPassword = key;
        if (null == entryptContent || entryptContent.trim().length() < 1) {
            return null;
        }
        strPassword = generateKey(strPassword);
        byte[] raw = strPassword.getBytes("UTF8");
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(entryptContent.getBytes());
        return (new sun.misc.BASE64Encoder()).encode(encrypted);
    }

    /**
     * BASE64编码.
     * @param str str
     * @return
     */
    @SuppressWarnings("restriction")
    private static String generateKey(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        if (null == str) {
            str = "defaultpassword";
        } else if (str.length() < 1) {
            str = "emptypassword";
        }
        java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
        md.update(str.getBytes("UTF8"));
        String strret = (new sun.misc.BASE64Encoder()).encode(md.digest());
        while (strret.length() < 16) {
            strret += "%";
        }
        if (strret.length() > 16) {
            int nbegin = (strret.length() - 16) / 2;
            strret = strret.substring(nbegin, nbegin + 16);
        }
        return strret;
    }

    /**
     * main.
     * @param args args
     */
    public static void main(String[] args) {
        try {
            String entrypt = EncryptionAES.entrypt("aaa111", "secret");
            System.out.println(entrypt);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
