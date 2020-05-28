package com.github.bluecatlee.common.third.wechat.bean;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.github.bluecatlee.common.third.wechat.annotation.WeChatBeanFieldAlias;
import com.github.bluecatlee.common.third.wechat.enumeration.WeChatTradeType;

/**
 * 统一下单
 */
public class WeChatUnifiedOrder extends WeChatXml {


    @WeChatBeanFieldAlias(value = "appid", required = true)
    @JacksonXmlProperty(localName = "appid")
    private String appId;

    /**
     * 商户号
     */
    @WeChatBeanFieldAlias(value = "mch_id", required = true)
    @JacksonXmlProperty(localName = "mch_id")
    private String mchId;

    /**
     * 设备号
     */
    @WeChatBeanFieldAlias("device_info")
    @JacksonXmlProperty(localName = "device_info")
    private String deviceInfo;

    /**
     * 随机字符串
     */
    @WeChatBeanFieldAlias(value = "nonce_str", required = true)
    @JacksonXmlProperty(localName = "nonce_str")
    private String nonce;

    /**
     * 签名
     */
    @JacksonXmlProperty(localName = "sign")
    private String sign;

    /**
     * 商品描述
     */
    @WeChatBeanFieldAlias(value = "body", required = true)
    private String body;

    /**
     * 商品详情
     */
    @WeChatBeanFieldAlias("detail")
    private String detail;

    /**
     * 附加数据
     */
    @WeChatBeanFieldAlias("attach")
    private String attach;

    /**
     * 商户订单号
     */
    @WeChatBeanFieldAlias(value = "out_trade_no", required = true)
    private String outTradeNo;

    /**
     * 货币类型
     */
    @WeChatBeanFieldAlias("fee_type")
    private String feeType = "CNY";

    /**
     * 总金额
     */
    @WeChatBeanFieldAlias(value = "total_fee", required = true)
    private Integer totalFee;

    /**
     * 终端IP
     */
    @WeChatBeanFieldAlias(value = "spbill_create_ip", required = true)
    private String spbillCreateIp;

    /**
     * 交易起始时间
     */
    @WeChatBeanFieldAlias("time_start")
    private String timeStart;

    /**
     * 交易结束时间
     */
    @WeChatBeanFieldAlias("time_expire")
    private String timeExpire;

    /**
     * 商品标记
     */
    @WeChatBeanFieldAlias("goods_tag")
    private String goodsTag;

    /**
     * 通知地址
     */
    @WeChatBeanFieldAlias(value = "notify_url", required = true)
    private String notifyUrl;

    /**
     * 交易类型
     */
    @WeChatBeanFieldAlias(value = "trade_type", required = true)
    @JacksonXmlProperty(localName = "trade_type")
    private String tradeType;

    /**
     * 商品ID
     */
    @WeChatBeanFieldAlias("product_id")
    private String productId;

    /**
     * 用户标识
     */
    @WeChatBeanFieldAlias("openid")
    private String openId;

    /**
     * 预支付交易会话标识
     */
    @JacksonXmlProperty(localName = "prepay_id")
    private String prepayId;

    /**
     * 二维码链接
     */
    @JacksonXmlProperty(localName = "code_url")
    private String codeUrl;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getMchId() {
        return mchId;
    }

    public void setMchId(String mchId) {
        this.mchId = mchId;
    }

    public String getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getAttach() {
        return attach;
    }

    public void setAttach(String attach) {
        this.attach = attach;
    }

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    public String getFeeType() {
        return feeType;
    }

    public void setFeeType(String feeType) {
        this.feeType = feeType;
    }

    public Integer getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(Integer totalFee) {
        this.totalFee = totalFee;
    }

    public String getSpbillCreateIp() {
        return spbillCreateIp;
    }

    public void setSpbillCreateIp(String spbillCreateIp) {
        this.spbillCreateIp = spbillCreateIp;
    }

    public String getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(String timeStart) {
        this.timeStart = timeStart;
    }

    public String getTimeExpire() {
        return timeExpire;
    }

    public void setTimeExpire(String timeExpire) {
        this.timeExpire = timeExpire;
    }

    public String getGoodsTag() {
        return goodsTag;
    }

    public void setGoodsTag(String goodsTag) {
        this.goodsTag = goodsTag;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    public String getTradeType() {
        return tradeType;
    }

    public void setTradeType(WeChatTradeType tradeType) {
        this.tradeType = tradeType.value();
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getPrepayId() {
        return prepayId;
    }

    public void setPrepayId(String prepayId) {
        this.prepayId = prepayId;
    }

    public String getCodeUrl() {
        return codeUrl;
    }

    public void setCodeUrl(String codeUrl) {
        this.codeUrl = codeUrl;
    }

    @Override
    public String toString() {
        return "WeChatUnifiedOrder{" +
                "appId='" + appId + '\'' +
                ", mchId='" + mchId + '\'' +
                ", deviceInfo='" + deviceInfo + '\'' +
                ", nonce='" + nonce + '\'' +
                ", sign='" + sign + '\'' +
                ", body='" + body + '\'' +
                ", detail='" + detail + '\'' +
                ", attach='" + attach + '\'' +
                ", outTradeNo='" + outTradeNo + '\'' +
                ", feeType='" + feeType + '\'' +
                ", totalFee=" + totalFee +
                ", spbillCreateIp='" + spbillCreateIp + '\'' +
                ", timeStart='" + timeStart + '\'' +
                ", timeExpire='" + timeExpire + '\'' +
                ", goodsTag='" + goodsTag + '\'' +
                ", notifyUrl='" + notifyUrl + '\'' +
                ", tradeType='" + tradeType + '\'' +
                ", productId='" + productId + '\'' +
                ", openId='" + openId + '\'' +
                ", prepayId='" + prepayId + '\'' +
                ", codeUrl='" + codeUrl + '\'' +
                '}';
    }
}