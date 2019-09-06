package com.github.bluecatlee.common.http;

public class RefererUtils {

    String regex = "^((https|http|ftp|rtsp|mms)?://)"
            // ftp的user@
            + "?(([0-9a-z_!~*'().&=+$%-]+: )?[0-9a-z_!~*'().&=+$%-]+@)?"
            // IP形式的URL- 199.194.52.184
            // 允许IP和DOMAIN（域名）
            + "(([0-9]{1,3}\\.){3}[0-9]{1,3}" + "|"
            // 域名- www.
            + "([0-9a-z_!~*'()-]+\\.)*"
            // 二级域名
            + "([0-9a-z][0-9a-z-]{0,61})?[0-9a-z]\\."
            // first level domain- .com or .museum
            + "[a-z]{2,6}|"
            // 测试用 : 本地localhost.
            + "([0-9a-z][0-9a-z-]{0,61}))"
            // 端口- :80
            + "(:[0-9]{1,4})?"
            // 项目名称
            // request.getContextPath() + "/";
            + "/";

    // todo

}
