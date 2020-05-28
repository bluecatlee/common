package com.github.bluecatlee.common.third.wechat.bean;

/**
 * WeChat urls
 */
public final class WeChatUrls {

    // 调用接口
    public static final String URL_ACCESS_TOKEN = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s";

    // 分享凭证
    public static final String URL_JSAPI_TICKET = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=%s&type=jsapi";

    // public static final String URL_USER_INFO =
    // "https://api.weixin.qq.com/cgi-bin/user/info?access_token=%s&openid=%s&lang=zh_CN";
    public static final String URL_USER_INFO = "https://api.weixin.qq.com/sns/userinfo?access_token=%s&openid=%s&lang=zh_CN";

    public static final String URL_MEDIA = "http://file.api.weixin.qq.com/cgi-bin/media/get?access_token=%s&media_id=%s";

    // 授权
    public static final String URL_AUTH = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=%s&state=%s#wechat_redirect";

    // 网页授权
    public static final String URL_ACCESS_TOKEN_BY_WEB = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code";

    // 统一下单
    public static final String URL_UNIFIEDORDER = "https://api.mch.weixin.qq.com/pay/unifiedorder";

    // 退款
    public static final String URL_REFUND = "https://api.mch.weixin.qq.com/secapi/pay/refund";

    // 发送消息
    public static final String URL_TEMPLETE_SEND = "https://api.weixin.qq.com/cgi-bin/message/wxopen/template/send?access_token=%s";

    // 小程序获取openid
    public static final String URL_JSCODE2SESSION = "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code";

    // 生成小程序二维码
    public static final String URL_CREATEQRCODE = "https://api.weixin.qq.com/cgi-bin/wxaapp/createwxaqrcode?access_token=%s";

    // 生成小程序二维码(不限制次数)
    public static final String URL_CREATEQRCODE_UNLIMIT = "https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token=%s";

}