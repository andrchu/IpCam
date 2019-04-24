
package net.kaicong.ipcam.utils;

public final class BytesUtils {

    /**
     * 二进制字符串转二进制数组,中间要用逗号隔开
     * 只能处理无符号的数值
     * 例如：00111011,01111111都可以处理，如果01111111二进制书中的第一位是1，则会报错
     *
     * @param b
     * @return
     */
    public static byte[] bytesStringToBytes(String b) {
        if (b.length() < 0) {
            return null;
        }
        String[] in = b.split(",");
        byte[] by = new byte[in.length];
        for (int i = 0; i < in.length; i++) {
            by[i] = Byte.parseByte(in[i], 2);
        }
        return by;
    }

    /**
     * 二进制字符串，转十六进制字符串，中间要用逗号隔开
     * 只能处理无符号的数值
     * 例如：00111011,01111111都可以处理，如果01111111二进制书中的第一位是1，则会报错
     */
    public static String bytesStringToHexString(String byteString) {
        if (byteString.length() < 0) {
            return null;
        }
        String[] inputs = byteString.split(",");
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < inputs.length; i++) {
            byte[] b = new byte[1];
            b[0] = Byte.parseByte(inputs[i], 2);
            sb.append(BytesUtils.bytesToHexString(b));
        }
        return sb.toString();
    }

    /**
     * 二进制数组转二进制字符串
     *
     * @param b
     * @return
     */
    public static String bytesToBytesString(byte[] b) {
        StringBuffer sb = new StringBuffer();
        String s = "";
        for (byte bs : b) {
            String sj = Integer.toBinaryString(bs);
            s += sj;
            int i = sj.length();
            if (i < 8) {    //8位不够，前面补零操作
                int in = 8 - i;
                s = addZeroHead(s, in);
                sb.append(s);
                s = "";
            }
            sb.append(" ");
        }
        return sb.toString();
    }

    /**
     * 前补零操作
     * 二进制字符串中，不够八位
     *
     * @return
     */
    public static String addZeroHead(String src, int addZero) {
        String sr = src;
        String s = "";
        for (int f = 0; f < addZero; f++) {
            s += "0";
        }
        return sr = s + sr;
    }

    /**
     * 二进制数组转十六进制字符串<br/>
     *
     * @param b
     * @return String
     */
    public static String bytesToHexString(byte[] b) {
        if (b == null) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            String strHex = Integer.toHexString(b[i]);
            if (strHex.length() > 3) {
                sb.append(strHex.substring(6));
            } else {
                if (strHex.length() < 2) {
                    sb.append("0" + strHex);
                } else {
                    sb.append(strHex);
                }
            }
        }
        return sb.toString();
    }

    /**
     * 二进制字符串，转十六进制字符串
     * 只能处理无符号的数值
     * 例如：00111011,01111111都可以处理，如果01111111二进制书中的第一位是1，则会报错
     */
    public static String hexStringToBytesString(String hexString) {
        if (hexString.length() < 0) {
            return null;
        }
        String[] inputs = hexString.split(",");
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < inputs.length; i++) {
            byte[] b = new byte[1];
            b[0] = Byte.parseByte(inputs[i], 2);
            sb.append(BytesUtils.bytesToHexString(b));
        }
        return sb.toString();
    }

    /**
     * 十六进制字符串转二进制数组<br/>
     *
     * @param s
     * @return
     */
    public static byte[] hexStringToBytes(String s) {
        if (s == null || s.equals("")) {
            return null;
        }
        s = s.toUpperCase();
        int length = s.length() / 2;
        char[] hexChars = s.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    /**
     * 字符转为byte<br/>
     * 把一个字符转成二进制<br/>
     *
     * @param c
     * @return
     */
    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }


    /**
     * int to bytes<br/>
     * 十进制转二进制数组；产生的数据在高<br/>
     *
     * @param i
     * @return
     */
    public static byte[] intToBytes(int i) {
        byte[] b = new byte[4];
        b[0] = (byte) (0xff & i);
        b[1] = (byte) ((0xff00 & i) >> 8);
        b[2] = (byte) ((0xff0000 & i) >> 16);
        b[3] = (byte) ((0xff000000 & i) >> 24);
        return b;
    }

    /**
     * bytes to int  ；产生的int数据在高位<br/>
     * 二进制数组转十进制，数组必须大于4，小于4会出错<br/>
     *
     * @param b
     * @return
     */
    public static int bytesToInt(byte[] b) {
        if (b.length < 4) {
            return 0;
        }
        int n = b[0] & 0xFF;
        n |= ((b[1] << 8) & 0xFF00);
        n |= ((b[2] << 16) & 0xFF0000);
        n |= ((b[3] << 24) & 0xFF000000);
        return n;
    }

    /**
     * 合并两个byte数组  <br/>
     *
     * @param src 合并在前
     * @param des 合并在后
     * @return
     */
    public static byte[] getMergeBytes(byte[] src, byte[] des) {
        int ac = src.length;
        int bc = des.length;
        byte[] b = new byte[ac + bc];
        for (int i = 0; i < ac; i++) {
            b[i] = src[i];
        }
        for (int i = 0; i < bc; i++) {
            b[ac + i] = des[i];
        }
        return b;
    }

    /**
     * 合并三个byte数组  <br/>
     *
     * @param src 合并前
     * @param cen 合并中
     * @param des 合并后
     * @return 字节数组
     */
    public static byte[] getMergeBytes(byte[] src, byte[] cen, byte[] des) {
        int ac = src.length;
        int bc = cen.length;
        int cc = des.length;
        byte[] b = new byte[ac + bc + cc];
        for (int i = 0; i < ac; i++) {
            b[i] = src[i];
        }
        for (int i = 0; i < bc; i++) {
            b[ac + i] = cen[i];
        }
        for (int i = 0; i < cc; i++) {
            b[ac + bc + i] = des[i];
        }
        return b;
    }

    /**
     * 5个byte合并<br/>
     *
     * @param a
     * @param b
     * @param c
     * @param d
     * @param e
     * @return
     */
    public static byte[] getMergeBytesFive(byte[] a, byte[] b, byte[] c, byte[] d, byte[] e) {
        int ia = a.length;
        int ib = b.length;
        int ic = c.length;
        int id = d.length;
        int ie = e.length;
        byte[] arrs = new byte[ia + ib + ic];
        arrs = getMergeBytes(a, b, c);
        byte[] twoArr = new byte[id + ie];
        twoArr = getMergeBytes(d, e);
        byte[] bs = new byte[ia + ib + ic + id + ie];
        bs = getMergeBytes(arrs, twoArr);
        return bs;
    }

}


