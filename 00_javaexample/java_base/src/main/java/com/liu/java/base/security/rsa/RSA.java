package com.liu.java.base.security.rsa;

import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * RSA算法签名及验签.
 * @Auther: liudongfei
 * @Date: 2019/10/28 14:28
 * @Description:
 */
public class RSA {

    public static final String SIGNATURE_ALGORITHM = "SHA256withRSA";
    public static final String ENCODE_ALGORITHM = "SHA-256";

    /**
     * SHA256WithRSA签名.
     * @param privateKey privateKey
     * @param plainText plainText
     * @return
     */
    public static String sign(byte[] privateKey, String plainText) {
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(privateKey);

        MessageDigest messageDigest;
        byte[] signed = null;
        try {
            KeyFactory factory = KeyFactory.getInstance("RSA");
            PrivateKey key = factory.generatePrivate(pkcs8EncodedKeySpec);
            messageDigest = MessageDigest.getInstance(ENCODE_ALGORITHM);
            messageDigest.update(plainText.getBytes());
            byte[] outputDigest_sign = messageDigest.digest();
            Signature sign = Signature.getInstance(SIGNATURE_ALGORITHM);
            sign.initSign(key);
            sign.update(outputDigest_sign);
            signed = sign.sign();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bytesToHexString(signed);
    }

    /**
     * SHA256WithRSA验签.
     * @param publicKey publicKey
     * @param plainText plainText
     * @param signed signed
     * @return
     */
    public static boolean verifySign(byte[] publicKey, String plainText, byte[] signed) {
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(publicKey);

        MessageDigest messageDigest;
        boolean signedSuccess = false;
        try {
            KeyFactory factory = KeyFactory.getInstance("RSA");
            PublicKey key = factory.generatePublic(x509EncodedKeySpec);
            messageDigest = MessageDigest.getInstance(ENCODE_ALGORITHM);
            messageDigest.update(plainText.getBytes());
            byte[] outputDigest_verify = messageDigest.digest();
            Signature verifySign = Signature.getInstance(SIGNATURE_ALGORITHM);
            verifySign.initVerify(key);
            verifySign.update(outputDigest_verify);
            signedSuccess = verifySign.verify(signed);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return signedSuccess;
    }

    /**
     * bytes[]换成16进制字符串.
     * @param src src
     * @return
     */
    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }
}
