package com.liu.java.base.security;

import com.liu.java.base.security.rsa.RSA;
import com.liu.java.base.security.sm2.SM2SignVO;
import com.liu.java.base.security.sm2.SM2SignVerUtils;
import org.bouncycastle.util.encoders.Base64;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;


/**
 * 签名验签工具类.
 * @Auther: liudongfei
 * @Date: 2019/10/28 14:23
 * @Description:
 */
public class SecurityUtil {

    /**
     * sha256withRsa签名.
     * @param privateKey privateKey
     * @param sourceData sourceData
     * @return
     */
    public static String signSha256WithRsa(byte[] privateKey, String sourceData) {
        return Util.getHexString(Base64.encode(Util.hexStringToBytes(RSA.sign(privateKey, sourceData))));
    }

    /**
     * sha256withRsa验签.
     * @param publicKey publicKey
     * @param sourceData sourceData
     * @param signed signed
     * @return
     */
    public static boolean verifySignSha256WithRsa(byte[] publicKey, String sourceData, byte[] signed) {
        return RSA.verifySign(publicKey, sourceData, signed);
    }

    /**
     * sm3WithSm2签名.
     * @param privateKey 签名私钥
     * @param sourceData 明文
     * @return
     */
    public static String signSm3WithSm2(byte[] privateKey, byte[] sourceData) {
        try {
            SM2SignVO sm2SignVO = SM2SignVerUtils.sign2SM2(privateKey, sourceData);
            System.out.println("before:\t" + sm2SignVO.getSignR() + sm2SignVO.getSignS());
            return Util.getHexString(Base64.encode(Util.hexStringToBytes(sm2SignVO.getSignR() + sm2SignVO.getSignS())));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * sm3WithSm2验签.
     * @param publicKey publicKey
     * @param sourceData sourceData
     * @param signed signed
     * @return
     */
    public static boolean verifySignSm3WithSm2(byte[] publicKey, byte[] sourceData, byte[] signed) {
        SM2SignVO sm2SignVO = SM2SignVerUtils.verifySignSM2(publicKey, sourceData, signed);
        return sm2SignVO.isVerify;
    }


    /**
     * 生成RSA密钥对.
     */
    public static void rsaKeyPairGenerator() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(1024);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            RSAPublicKey pubKey = (RSAPublicKey) keyPair.getPublic();
            RSAPrivateKey priKey = (RSAPrivateKey) keyPair.getPrivate();
            System.out.println("pubKey:\t" + Util.byteToHex(pubKey.getEncoded()));
            System.out.println("priKey:\t" + Util.byteToHex(priKey.getEncoded()));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }


    /**
     * .
     * @param args args
     */
    public static void main(String[] args) {
        String text = "这是一段明文";
        byte [] sourceData = text.getBytes();
        // sm2 密钥对
        String publicKey = "04BB34D657EE7E8490E66EF577E6B3CEA28B739511E787FB4F71B7F38F241D8"
                + "7F18A5A93DF74E90FF94F4EB907F271A36B295B851F971DA5418F4915E2C1A23D6E";
        String privatekey = "0B1CE43098BC21B8E82B5C065EDB534CB86532B1900A49D49F3C53762D2997FA";

        // rsa密钥对
        String publicKey1 = "30819f300d06092a864886f70d010101050003818d0030818902818100ab1e55c4696b20ca25db22b98c214"
                + "0278073e530f7307fdfea6e68eb3c32fa483cf47b4ad4c882a993b490388111e79acdf3b11c465cee42a538cd6b4b2265"
                + "fd26f88d1ac6a136cc2cc4d1114250a9ca21a8f5789005a40ff78f8c1483afc45c0d9354e627f4b8b07ac91fc8fb854c5"
                + "802dba714cc8ac84021f02de278a2baa10203010001";
        String privatekey1 = "30820276020100300d06092a864886f70d0101010500048202603082025c02010002818100ab1e55c4696b"
                + "20ca25db22b98c2140278073e530f7307fdfea6e68eb3c32fa483cf47b4ad4c882a993b490388111e79acdf3b11c465ce"
                + "e42a538cd6b4b2265fd26f88d1ac6a136cc2cc4d1114250a9ca21a8f5789005a40ff78f8c1483afc45c0d9354e627f4b8"
                + "b07ac91fc8fb854c5802dba714cc8ac84021f02de278a2baa1020301000102818000dd3f5ba598a7eabd7434e1b1067df"
                + "7751ba0bdb591e6237060ac60fbc5da23c1430ed85ae8c4582ce9cd8cab8236a43e840b91f3fd062b0a00967103492230"
                + "b9fcd06916d4f1824de08ffebf6c92cc1359a452934340413e65be9f69efd6d12218a42db4c55d46a402e38cb537ca858"
                + "dbd96f9515d5d6088b073efb4165eed024100f4abe104085477f6826257997af9f2cf7ba9a61d0deecda64c9406589311"
                + "5af7d0e34b0560a9694674091f24dea00f29ff9b8330afa21152c62e465e8a62c9ff024100b30a9fc9fcebce1c855d28c"
                + "afbaeb67a25d4407ea1eefe3f66285d64e280705fa9e9d50465041eaf69566267316447bdaa327087d09628b287142d00"
                + "6e943b5f024100bb5a824b9c355247294034e40a0f2ba745827af8f49a504f6f8449f7b96628ca6ae221a854846560d5a"
                + "9c6776d22137a8d887fc4e038b21b3836671c2dcbc7f102405177eb1fa11bb76cd8ee7c0a691da5e0cce7d8f5064056a6"
                + "8898a36ead761e2c1987d22b2ec0022a75371e8db036de421a78db25a1af4213824d96f0946094e7024010cb929e9c895"
                + "c0de995b18984fb049bcfc48df551a178c2172dc674db1a4f7477f43715a4e006a68dfc238af382c3d4356e8ba0336f9c"
                + "2400c81bba3f3894dc";

        // rsa签名
        String signSha256WithRsa = signSha256WithRsa(Util.hexStringToBytes(privatekey1), text);
        System.out.println("signSha256WithRsa:\t" + signSha256WithRsa);

        // rsa验签
        byte[] signed1 = Base64.decode(Util.hexStringToBytes(signSha256WithRsa));
        boolean verifySignSha256WithRsa = verifySignSha256WithRsa(Util.hexStringToBytes(publicKey1), text, signed1);
        System.out.println("verifySignSha256WithRsa:\t" + verifySignSha256WithRsa);

        // sm2签名
        String signSm3WithSm2 = signSm3WithSm2(Util.hexStringToBytes(privatekey), sourceData);
        System.out.println("signSm3WithSm2:\t" + signSm3WithSm2);


        // sm2验签
        byte[] signed = Base64.decode(Util.hexStringToBytes(signSm3WithSm2));
        boolean verifySignSm3WithSm2 = verifySignSm3WithSm2(Util.hexStringToBytes(publicKey), sourceData, signed);
        System.out.println("verifySignSm3WithSm2:\t" + verifySignSm3WithSm2);


    }
}
