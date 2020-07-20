package com.liu.java.base.security.sm2;

import com.liu.java.base.security.Util;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.crypto.digests.SM3Digest;
import org.bouncycastle.math.ec.ECPoint;

import java.math.BigInteger;

/**
 * 国密算法的签名、验签.
 */
public class SM2SignVerUtils {

    /**
     * 默认USERID.
     */
    public static String USER_ID = "1234567812345678";

    /**
     * 私钥签名
     * 使用SM3进行对明文数据计算一个摘要值.
     * @param privatekey 私钥
     * @param sourceData 明文数据
     * @return 签名后的值
     * @throws Exception e
     */
    public static SM2SignVO sign2SM2(byte[] privatekey,byte[] sourceData) throws Exception {
        SM2SignVO sm2SignVO = new SM2SignVO();
        sm2SignVO.setSm2Type("sign");
        SM2Factory factory = SM2Factory.getInstance();
        BigInteger userD = new  BigInteger(privatekey);
        //System.out.println("userD:"+userD.toString(16));
        sm2SignVO.setSm2Userd(userD.toString(16));

        ECPoint userKey = factory.eccPointG.multiply(userD);
        //System.out.println("椭圆曲线点X: "+ userKey.getXCoord().toBigInteger().toString(16));
        //System.out.println("椭圆曲线点Y: "+ userKey.getYCoord().toBigInteger().toString(16));

        SM3Digest sm3Digest = new SM3Digest();
        byte [] z = factory.sm2GetZ(USER_ID.getBytes(), userKey);
        //System.out.println("SM3摘要Z: " + Util.getHexString(z));
        //System.out.println("被加密数据的16进制: " + Util.getHexString(sourceData));
        sm2SignVO.setSm3Z(Util.getHexString(z));
        sm2SignVO.setSignExpress(Util.getHexString(sourceData));

        sm3Digest.update(z, 0, z.length);
        sm3Digest.update(sourceData,0,sourceData.length);
        byte [] md = new byte[32];
        sm3Digest.doFinal(md, 0);
        System.out.println("SM3摘要值: " + Util.getHexString(md));
        sm2SignVO.setSm3Digest(Util.getHexString(md));

        SM2Result sm2Result = new SM2Result();
        factory.sm2Sign(md, userD, userKey, sm2Result);
        System.out.println(sm2Result.r);
        System.out.println("r: " + sm2Result.r.toString(16));
        System.out.println(new BigInteger(sm2Result.r.toString(16), 16));
        //System.out.println("s: " + sm2Result.s.toString(16));
        sm2SignVO.setSignR(sm2Result.r.toString(16));
        sm2SignVO.setSignS(sm2Result.s.toString(16));

        ASN1Integer d_r = new ASN1Integer(sm2Result.r);
        ASN1Integer d_s = new ASN1Integer(sm2Result.s);
        ASN1EncodableVector v2 = new ASN1EncodableVector();
        v2.add(d_r);
        v2.add(d_s);
        DERSequence sign = new DERSequence(v2);
        String result = Util.byteToHex(sign.getEncoded());
        sm2SignVO.setSm2Sign(result);
        return sm2SignVO;
    }

    /**
     * 验证签名.
     * @param publicKey 公钥信息
     * @param sourceData 密文信息
     * @param signData 签名信息
     * @return 验签的对象 包含了相关参数和验签结果
     */
    @SuppressWarnings("unchecked")
    public static SM2SignVO verifySignSM2(byte[] publicKey,byte[] sourceData,byte[] signData) {
        try {
            byte[] formatedPubKey;
            SM2SignVO verifyVo = new SM2SignVO();
            verifyVo.setSm2Type("verify");
            if (publicKey.length == 64) {
                // 添加一字节标识，用于ECPoint解析
                formatedPubKey = new byte[65];
                formatedPubKey[0] = 0x04;
                System.arraycopy(publicKey, 0, formatedPubKey, 1, publicKey.length);
            } else {
                formatedPubKey = publicKey;
            }
            SM2Factory factory = SM2Factory.getInstance();
            ECPoint userKey = factory.eccCurve.decodePoint(formatedPubKey);

            SM3Digest sm3Digest = new SM3Digest();
            byte [] z = factory.sm2GetZ(USER_ID.getBytes(), userKey);
            //System.out.println("SM3摘要Z: " + Util.getHexString(z));
            verifyVo.setSm3Z(Util.getHexString(z));
            sm3Digest.update(z,0,z.length);
            sm3Digest.update(sourceData,0,sourceData.length);
            byte [] md = new byte[32];
            sm3Digest.doFinal(md, 0);
            //System.out.println("SM3摘要值: " + Util.getHexString(md));
            verifyVo.setSm3Digest(Util.getHexString(md));
            //ByteArrayInputStream bis = new ByteArrayInputStream(signData);
            //ASN1InputStream dis = new ASN1InputStream(bis);
            SM2Result sm2Result = null;
            //ASN1Primitive derObj = dis.readObject();
            byte[] sBytes = new byte[32];
            byte[] rBytes = new byte[32];
            System.arraycopy(signData, 0, rBytes, 0, 32);
            System.arraycopy(signData, 32, sBytes, 0, 32);
            //Enumeration<ASN1Integer> e = ((ASN1Sequence)derObj).getObjects();
            //BigInteger r = ((ASN1Integer) e.nextElement()).getValue();
            //BigInteger s = ((ASN1Integer) e.nextElement()).getValue();
            sm2Result = new SM2Result();
            sm2Result.r = new BigInteger(Util.byteToHex(rBytes), 16);
            sm2Result.s = new BigInteger(Util.byteToHex(sBytes), 16);
            //System.out.println("vr: " + sm2Result.r.toString(16));
            //System.out.println("vs: " + sm2Result.s.toString(16));
            verifyVo.setVerifyR(sm2Result.r.toString(16));
            verifyVo.setVerify_s(sm2Result.s.toString(16));
            factory.sm2Verify(md, userKey, sm2Result.r, sm2Result.s, sm2Result);
            boolean verifyFlag = sm2Result.r.equals(sm2Result.R);
            verifyVo.setVerify(verifyFlag);
            return  verifyVo;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
