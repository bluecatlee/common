package com.github.bluecatlee.common.third.wechat.bean;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.github.bluecatlee.common.third.wechat.annotation.WeChatBeanFieldAlias;

/**
 * 退款
 */
public class WeChatRefund extends WeChatXml {

    /**
     * 返回code 用来判断退款请求是否成功
     */
    @JacksonXmlProperty(localName = "result_code")
    private String resultCode;

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
     * 操作员
     */
    @Deprecated
    @WeChatBeanFieldAlias(value = "op_user_id")
    private String opUserId;

    /**
     * 微信订单号
     */
    @WeChatBeanFieldAlias(value = "transaction_id")
    @JacksonXmlProperty(localName = "transaction_id")
    private String transactionId;

    /**
     * 商户订单号
     */
    @WeChatBeanFieldAlias(value = "out_trade_no")
    @JacksonXmlProperty(localName = "out_trade_no")
    private String outTradeNo;

    /**
     * 商户退款单号
     */
    @WeChatBeanFieldAlias(value = "out_refund_no", required = true)
    @JacksonXmlProperty(localName = "out_refund_no")
    private String outRefundNo;

    /**
     * 退款渠道
     */
    @JacksonXmlProperty(localName = "refund_channel")
    private String refundChannel;

    /**
     * 退款金额
     */
    @WeChatBeanFieldAlias(value = "refund_fee", required = true)
    @JacksonXmlProperty(localName = "refund_fee")
    private Integer refundFee;

    /**
     * 总金额
     */
    @WeChatBeanFieldAlias(value = "total_fee", required = true)
    @JacksonXmlProperty(localName = "totalFee")
    private Integer totalFee;

    /**
     * 微信退款单号
     */
    @JacksonXmlProperty(localName = "refund_id")
    private String refundId;

    /**
     * 现金支付金额
     */
    @JacksonXmlProperty(localName = "cash_fee")
    private Integer cashFee;

    /**
     * 现金退款金额 cash_refund_fee
     */
    @JacksonXmlProperty(localName = "cash_refund_fee")
    private Integer cashRefundFee;

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

    public String getOpUserId() {
        return opUserId;
    }

    public void setOpUserId(String opUserId) {
        this.opUserId = opUserId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    public String getOutRefundNo() {
        return outRefundNo;
    }

    public void setOutRefundNo(String outRefundNo) {
        this.outRefundNo = outRefundNo;
    }

    public String getRefundChannel() {
        return refundChannel;
    }

    public void setRefundChannel(String refundChannel) {
        this.refundChannel = refundChannel;
    }

    public Integer getRefundFee() {
        return refundFee;
    }

    public void setRefundFee(Integer refundFee) {
        this.refundFee = refundFee;
    }

    public Integer getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(Integer totalFee) {
        this.totalFee = totalFee;
    }

    public String getRefundId() {
        return refundId;
    }

    public void setRefundId(String refundId) {
        this.refundId = refundId;
    }

    public Integer getCashFee() {
        return cashFee;
    }

    public void setCashFee(Integer cashFee) {
        this.cashFee = cashFee;
    }

    public Integer getCashRefundFee() {
        return cashRefundFee;
    }

    public void setCashRefundFee(Integer cashRefundFee) {
        this.cashRefundFee = cashRefundFee;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    @Override
    public String toString() {
        return "WeChatRefund{" +
                "resultCode='" + resultCode + '\'' +
                ",appId='" + appId + '\'' +
                ", mchId='" + mchId + '\'' +
                ", deviceInfo='" + deviceInfo + '\'' +
                ", nonce='" + nonce + '\'' +
                ", sign='" + sign + '\'' +
                ", opUserId='" + opUserId + '\'' +
                ", transactionId='" + transactionId + '\'' +
                ", outTradeNo='" + outTradeNo + '\'' +
                ", outRefundNo='" + outRefundNo + '\'' +
                ", refundChannel='" + refundChannel + '\'' +
                ", refundFee=" + refundFee +
                ", totalFee=" + totalFee +
                ", refundId='" + refundId + '\'' +
                ", cashFee=" + cashFee +
                ", cashRefundFee=" + cashRefundFee +
                '}';
    }

}