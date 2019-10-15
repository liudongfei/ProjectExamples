package com.liu.logmsgcommon.util;

/**
 * byte util.
 * @Auther: liudongfei
 * @Date: 2019/2/20 11:00
 * @Description:
 */
public class ByteUtil {
    /**
     * merge two byte[] to one byte[].
     * @param bt1 bt1
     * @param bt2 bt2
     * @return
     */
    public static byte[] byteMerge(byte[] bt1, byte[] bt2) {
        if (null == bt1) {
            return bt2;
        }
        byte[] bt3 = new byte[bt1.length + bt2.length];
        System.arraycopy(bt1, 0, bt3, 0, bt1.length);
        System.arraycopy(bt2, 0, bt3, bt1.length, bt2.length);
        return bt3;
    }

    /**
     * 判断byte[]是否越界.
     * @param inputMessage the input message
     * @param pos the pos
     * @param len the len
     * @return true, if successful
     */
    public static boolean isOutOfBound(byte[] inputMessage, int pos, int len) {
        return (pos + len) > inputMessage.length;
    }

    /**
     * convert byte[] to hex string.
     * @param arrB arrB
     * @return
     */
    public static String bytes2HexStr(byte[] arrB) {
        int iLen = arrB.length;
        StringBuffer sb = new StringBuffer(iLen * 2);
        for (int i = 0; i < iLen; i++) {
            int intTmp = arrB[i];
            while (intTmp < 0) {
                intTmp = intTmp + 256;
            }
            if (intTmp < 16) {
                sb.append("0");
            }
            sb.append(Integer.toString(intTmp, 16));
        }
        return sb.toString();
    }

    /**
     * hex str to byte[].
     * @param str str
     * @return
     */
    public static byte[] hexStr2Bytes(String str) {
        if (str == null || str.trim().equals("")) {
            return new byte[0];
        }
        byte[] bytes = new byte[str.length() / 2];
        for (int i = 0; i < str.length() / 2; i++) {
            String subStr = str.substring(i * 2, i * 2 + 2);
            bytes[i] = (byte) Integer.parseInt(subStr, 16);
        }

        return bytes;
    }
}
