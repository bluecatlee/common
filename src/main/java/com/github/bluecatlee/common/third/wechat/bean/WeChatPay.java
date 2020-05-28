package com.github.bluecatlee.common.third.wechat.bean;

import com.github.bluecatlee.common.third.wechat.annotation.WeChatBeanFieldAlias;

/**
 * 支付
 */
public class WeChatPay extends WeChatJson {

    /**
     * 公众号名称，由商户传入
     */
    @WeChatBeanFieldAlias("appId")
    private String appId;

    /**
     * 时间戳，自1970年以来的秒数
     */
    @WeChatBeanFieldAlias("timeStamp")
    private String timeStamp;

    /**
     * 随机串
     */
    @WeChatBeanFieldAlias("nonceStr")
    private String nonceStr;

    /**
     * 订单详情扩展字符串
     */
    @WeChatBeanFieldAlias("package")
    private String pack;

    /**
     * 微信签名方式
     */
    @WeChatBeanFieldAlias("signType")
    private String signType = "MD5";

    /**
     * 微信签名方式
     */
    @WeChatBeanFieldAlias("signType")
    private String prepare_code = "MD5";

    /**
     * 微信签名
     */
    private String paySign;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getNonceStr() {
        return nonceStr;
    }

    public void setNonceStr(String nonceStr) {
        this.nonceStr = nonceStr;
    }

    public String getPack() {
        return pack;
    }

    public void setPack(String pack) {
        this.pack = pack;
    }

    public String getSignType() {
        return signType;
    }

    public void setSignType(String signType) {
        this.signType = signType;
    }

    public String getPaySign() {
        return paySign;
    }

    public void setPaySign(String paySign) {
        this.paySign = paySign;
    }

    @Override
    public String toString() {
        return "WeChatPay{" +
                "appId='" + appId + '\'' +
                ", timeStamp='" + timeStamp + '\'' +
                ", nonceStr='" + nonceStr + '\'' +
                ", pack='" + pack + '\'' +
                ", signType='" + signType + '\'' +
                ", paySign='" + paySign + '\'' +
                '}';
    }
}
