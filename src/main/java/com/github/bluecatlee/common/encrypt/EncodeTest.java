package com.github.bluecatlee.common.encrypt;

import org.apache.commons.codec.binary.Hex;
import org.jline.terminal.impl.jna.linux.LinuxNativePty;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Arrays;

public class EncodeTest {

    public static void main(String[] args) throws Exception {
//        String x = "中文";
//        byte[] bytes0 = x.getBytes();
//        byte[] bytes1 = x.getBytes("UTF-8");
//        byte[] bytes2 = x.getBytes("GBK");
//        byte[] bytes3 = x.getBytes("iso-8859-1");
//        byte[] bytes4 = x.getBytes("unicode");
//        byte[] bytes5 = x.getBytes("UTF-16");
//
//        char[] chars0 = Hex.encodeHex(bytes0);
//        char[] chars1 = Hex.encodeHex(bytes1);
//        char[] chars2 = Hex.encodeHex(bytes2);
//        char[] chars3 = Hex.encodeHex(bytes3);
//        char[] chars4 = Hex.encodeHex(bytes4);
//        char[] chars5 = Hex.encodeHex(bytes5);
//
//        System.out.println(System.getProperty("file.encoding"));    // UTF-8
//        System.out.println(Charset.defaultCharset());               // UTF-8
//        System.out.println(Arrays.toString(bytes0) + "======" + Arrays.toString(chars0));  // [e, 4, b, 8, a, d, e, 6, 9, 6, 8, 7]
//        System.out.println(Arrays.toString(bytes1) + "======" + Arrays.toString(chars1));  // [e, 4, b, 8, a, d, e, 6, 9, 6, 8, 7]
//        System.out.println(Arrays.toString(bytes2) + "======" + Arrays.toString(chars2));  // [d, 6, d, 0, c, e, c, 4]
//        System.out.println(Arrays.toString(bytes3) + "======" + Arrays.toString(chars3));  // [3, f, 3, f]
//        System.out.println(Arrays.toString(bytes4) + "======" + Arrays.toString(chars4));  // [f, e, f, f, 4, e, 2, d, 6, 5, 8, 7]
//        System.out.println(Arrays.toString(bytes5) + "======" + Arrays.toString(chars5));  // [f, e, f, f, 4, e, 2, d, 6, 5, 8, 7]

        // 几个点：
        // 0.Charset.defaultCharset()是默认的平台编码，new String(bytes) getBytes(str) 都是采用的平台编码集。 建议指定编码集
        // 1.当前java文件的编码格式看System.getProperty("file.encoding")
        // 2.java文件生成的class文件的编码格式为unicode(java中默认的unicode实现为UTF-16)
        // 3.jvm加载class文件中的字符串到内存也是unicode编码形式的
        // 4.UTF-8与UTF-16并不兼容的，UTF-8与GBK不兼容
        // 5.unicode包含汉字字符集 因此虽然UTF-8能正确展示中文 不代表其兼容GBK
        // 6.兼容的概念是 new String(bytes, "GB2312") getBytes(str, "gbk")
        // 7.如果用错误的字符集编码(字符集并不包含这种字符)的话，即使再用这个字符集解码，也会异常



        // 字符集兼容性问题
//        for (int i = 0; i < arr.length; i++) {
//            testCompatibility(arr[i], arr[0]);
//            testCompatibility(arr[i], arr[1]);
//            testCompatibility(arr[i], arr[2]);
//        }
//        testCompatibility("gbk", "utf-8");

        // url转义的问题
        // 转义不安全字符 w3c建议要使用UTF-8编码再进行转义
        url();

        // todo base64

    }

    private static void url() throws Exception{
        String body = "q=中国";
        String body2 = new String(body.getBytes(), "iso-8859-1");
//        String encode = URLEncoder.encode(body);                    // 默认应该是系统编码集
        String encode1 = URLEncoder.encode(body, "UTF-8");
        String encode2 = URLEncoder.encode(body, "gbk");
        String encode3 = URLEncoder.encode(body, "iso-8859-1");
        String encode4 = URLEncoder.encode(body2, "iso-8859-1");
//        System.out.println(encode);
        System.out.println(encode1);
        System.out.println(encode2);
        System.out.println(encode3);
        System.out.println(encode4);

    }

    private static String[] arr = {"gb2312", "gbk", "gb18030"};
    private static boolean testCompatibility(String charset1, String charset2) throws Exception {
        String x = "中国";
        byte[] bytes = x.getBytes(charset1);        // 编码
        String s = new String(bytes, charset2);     // 解码
//        System.out.println(s);

        boolean equals = x.equals(s);

        String a = "兼容";
        if (!equals) {
            a = "不" + a;
        }

        System.out.println(charset2 + a + charset1);
        return equals;
    }

}
