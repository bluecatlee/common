package com.github.bluecatlee.common.encrypt.rewrite;


import org.apache.commons.codec.binary.Hex;

/**
 * 十六进制编码
 */
public class HexX {


//    Integer
//        numberOfLeadingZeros(int)  返回这个数据的二进制串中从最左边算起连续的"0"的总数量,即返回32位值的前面补零个数。二分查找的思想
//        toUnsignedString0(int val, int shift) shift=4时表示返回int数的16进制字符串形式(每四个二进制数相当于一个16进制数)

//    字符串转十六进制字符串
//          1.首先字符串要转成字节数组(这一步存在编码问题)
//          2.遍历每一个字节，将每个字节转成十六进制(一个字节8位，8位二进制可以转成2位十六进制表示), 并拼接
//          3.是否转换成大写


    public static String str2Hex(String str) {
        return byte2Hex(str.getBytes());
    }

    public static String byte2Hex(byte[] bytes) {
        String c = "";
        StringBuilder sb = new StringBuilder();
        System.out.println(bytes.length);
        for (int n = 0; n < bytes.length; n++) {
            c = Integer.toHexString(bytes[n] & 0xFF);                // 与 1111 1111，作用就是获取低8位
            sb.append((c.length() == 1) ? "0" + c : c);
        }
        return sb.toString().toUpperCase().trim();
    }

    public static String _str2Hex(String str) {

        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder();
        byte[] bytes = str.getBytes();
        int bit;

        for (int i = 0; i < bytes.length; i++) {
            bit = (bytes[i] & 0x0f0) >> 4;          // 获取8位中的高4位
            sb.append(chars[bit]);
            bit = bytes[i] & 0x0f;                  // 获取第4位
            sb.append(chars[bit]);
//            sb.append(' ');
        }
        return sb.toString().trim();
    }

    public static String _hex2Str(String hexStr) {
        String str = "0123456789ABCDEF";
        char[] hexs = hexStr.toCharArray();
        byte[] bytes = new byte[hexStr.length() / 2];
        int n;

        for (int i = 0; i < bytes.length; i++) {
            n = str.indexOf(hexs[2 * i]) * 16;
            n += str.indexOf(hexs[2 * i + 1]);
            bytes[i] = (byte) (n & 0xff);
        }
        return new String(bytes);
    }

    public static void main(String[] args) {
        String str = "中国";
        char[] chars = Hex.encodeHex(str.getBytes(), false);
        String s = new String(chars);
        System.out.println(s);

        String s1 = byte2Hex(str.getBytes());
        System.out.println(s1);

        String s2 = _str2Hex(str);
        System.out.println(s2);

    }

}
