package com.liu.java.base.security.sm2;
/**
 * SM2签名所计算的值 可以根据实际情况增加删除字段属性
 */
public class SM2SignVO {
    //16进制的私钥
    public String sm2Userd;
    //椭圆曲线点X
    public String xCoord;
    //椭圆曲线点Y
    public String yCoord;
    //SM3摘要Z
    public String sm3Z;
    //明文数据16进制
    public String signExpress;
    //SM3摘要值
    public String sm3Digest;
    //R
    public String signR;
    //S
    public String signS;
    //R
    public String verifyR;
    //S
    public String verifyS;
    //签名值
    public String sm2Sign;
    //sign 签名  verfiy验签
    public String sm2Type;
    //是否验签成功  true false
    public boolean isVerify;
    public String getXCoord() {
        return xCoord;
    }
    public void setXCoord(String xCoord) {
        this.xCoord = xCoord;
    }
    public String getYCoord() {
        return yCoord;
    }
    public void setYCoord(String yCoord) {
        this.yCoord = yCoord;
    }
    public String getSm3Z() {
        return sm3Z;
    }
    public void setSm3Z(String sm3Z) {
        this.sm3Z = sm3Z;
    }
    public String getSm3Digest() {
        return sm3Digest;
    }
    public void setSm3Digest(String sm3Digest) {
        this.sm3Digest = sm3Digest;
    }
    public String getSm2SignForSoft() {
        return sm2Sign;
    }

    public String getSm2_signForHard() {
        //System.out.println("R:"+getSign_r());
        //System.out.println("s:"+getSign_s());
        return getSignR() + getSignS();
    }
    public void setSm2Sign(String sm2Sign) {
        this.sm2Sign = sm2Sign;
    }
    public String getSignExpress() {
        return signExpress;
    }
    public void setSignExpress(String signExpress) {
        this.signExpress = signExpress;
    }
    public String getSm2Userd() {
        return sm2Userd;
    }
    public void setSm2Userd(String sm2Userd) {
        this.sm2Userd = sm2Userd;
    }
    public String getSm2Type() {
        return sm2Type;
    }
    public void setSm2Type(String sm2Type) {
        this.sm2Type = sm2Type;
    }
    public boolean isVerify() {
        return isVerify;
    }
    public void setVerify(boolean isVerify) {
        this.isVerify = isVerify;
    }
    public String getSignR() {
        return signR;
    }
    public void setSignR(String signR) {
        this.signR = signR;
    }
    public String getSignS() {
        return signS;
    }
    public void setSignS(String signS) {
        this.signS = signS;
    }
    public String getVerifyR() {
        return verifyR;
    }
    public void setVerifyR(String verifyR) {
        this.verifyR = verifyR;
    }
    public String getVerifyS() {
        return verifyS;
    }
    public void setVerify_s(String verifyS) {
        this.verifyS = verifyS;
    }
}
