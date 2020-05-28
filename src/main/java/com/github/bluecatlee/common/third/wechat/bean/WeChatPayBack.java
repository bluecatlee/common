package com.github.bluecatlee.common.third.wechat.bean;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.github.bluecatlee.common.third.wechat.annotation.WeChatBeanFieldAlias;

public class WeChatPayBack extends WeChatXml {

    @WeChatBeanFieldAlias(value = "appid")
    @JacksonXmlProperty(localName = "appid")
    private String appId;

    /**
     * 商户号
     */
    @WeChatBeanFieldAlias(value = "mch_id")
    @JacksonXmlProperty(localName = "mch_id")
    private String mchId;

    /**
     * 设备号
     */
    @WeChatBeanFieldAlias(value = "device_info")
    @JacksonXmlProperty(localName = "device_info")
    private String deviceInfo;

    /**
     * 随机字符串
     */
    @WeChatBeanFieldAlias(value = "nonce_str")
    @JacksonXmlProperty(localName = "nonce_str")
    private String nonce;

    /**
     * 签名
     */
    @JacksonXmlProperty(localName = "sign")
    private String sign;

    /**
     * 业务结果
     */
    @WeChatBeanFieldAlias(value = "result_code")
    @JacksonXmlProperty(localName = "result_code")
    private String resultCode;

    @WeChatBeanFieldAlias(value = "return_code")
    @JacksonXmlProperty(localName = "return_code")
    private String returnCode;

    /**
     * 错误代码
     */
    @WeChatBeanFieldAlias(value = "err_code")
    @JacksonXmlProperty(localName = "err_code")
    private String errCode;

    /**
     * 错误代码描述
     */
    @WeChatBeanFieldAlias(value = "err_code_des")
    @JacksonXmlProperty(localName = "err_code_des")
    private String errCodeDes;

    /**
     * 用户标识
     */
    @WeChatBeanFieldAlias(value = "openid")
    @JacksonXmlProperty(localName = "openid")
    private String openId;

    /**
     * 是否关注公众账号
     */
    @WeChatBeanFieldAlias(value = "is_subscribe")
    @JacksonXmlProperty(localName = "is_subscribe")
    private String isSubscribe;

    /**
     * 交易类型
     */
    @WeChatBeanFieldAlias(value = "trade_type")
    @JacksonXmlProperty(localName = "trade_type")
    private String tradeType;

    /**
     * 付款银行
     */
    @WeChatBeanFieldAlias(value = "bank_type")
    @JacksonXmlProperty(localName = "bank_type")
    private String bankType;

    /**
     * 订单金额
     */
    @WeChatBeanFieldAlias(value = "total_fee")
    @JacksonXmlProperty(localName = "total_fee")
    private Integer totalFee;

    /**
     * 货币种类
     */
    @WeChatBeanFieldAlias(value = "fee_type")
    @JacksonXmlProperty(localName = "fee_type")
    private String feeType;

    /**
     * 现金支付金额
     */
    @WeChatBeanFieldAlias(value = "cash_fee")
    @JacksonXmlProperty(localName = "cash_fee")
    private String cashFee;

    /**
     * 现金支付货币类型
     */
    @WeChatBeanFieldAlias(value = "cash_fee_type")
    @JacksonXmlProperty(localName = "cash_fee_type")
    private String cashFeeType;

    /**
     * 代金券金额
     */
    @JacksonXmlProperty(localName = "coupon_fee")
    private String couponFee;

    /**
     * 代金券使用数量
     */
    @JacksonXmlProperty(localName = "coupon_count")
    private String couponCount;

    /**
     * 微信支付订单号
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
     * 商家数据包
     */
    @WeChatBeanFieldAlias(value = "attach")
    @JacksonXmlProperty(localName = "attach")
    private String attach;

    /**
     * 支付完成时间
     */
    @WeChatBeanFieldAlias(value = "time_end")
    @JacksonXmlProperty(localName = "time_end")
    private String timeEnd;

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

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    @Override
    public String getReturnCode() {
        return returnCode;
    }

    @Override
    public void setReturnCode(String returnCode) {
        this.returnCode = returnCode;
    }

    @Override
    public String getErrCode() {
        return errCode;
    }

    @Override
    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }

    public String getErrCodeDes() {
        return errCodeDes;
    }

    public void setErrCodeDes(String errCodeDes) {
        this.errCodeDes = errCodeDes;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getIsSubscribe() {
        return isSubscribe;
    }

    public void setIsSubscribe(String isSubscribe) {
        this.isSubscribe = isSubscribe;
    }

    public String getTradeType() {
        return tradeType;
    }

    public void setTradeType(String tradeType) {
        this.tradeType = tradeType;
    }

    public String getBankType() {
        return bankType;
    }

    public void setBankType(String bankType) {
        this.bankType = bankType;
    }

    public Integer getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(Integer totalFee) {
        this.totalFee = totalFee;
    }

    public String getFeeType() {
        return feeType;
    }

    public void setFeeType(String feeType) {
        this.feeType = feeType;
    }

    public String getCashFee() {
        return cashFee;
    }

    public void setCashFee(String cashFee) {
        this.cashFee = cashFee;
    }

    public String getCashFeeType() {
        return cashFeeType;
    }

    public void setCashFeeType(String cashFeeType) {
        this.cashFeeType = cashFeeType;
    }

    public String getCouponFee() {
        return couponFee;
    }

    public void setCouponFee(String couponFee) {
        this.couponFee = couponFee;
    }

    public String getCouponCount() {
        return couponCount;
    }

    public void setCouponCount(String couponCount) {
        this.couponCount = couponCount;
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

    public String getAttach() {
        return attach;
    }

    public void setAttach(String attach) {
        this.attach = attach;
    }

    public String getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(String timeEnd) {
        this.timeEnd = timeEnd;
    }

    @Override
    public String toString() {
        return "WeChatPayBack{" +
                "appId='" + appId + '\'' +
                ", mchId='" + mchId + '\'' +
                ", deviceInfo='" + deviceInfo + '\'' +
                ", nonce='" + nonce + '\'' +
                ", sign='" + sign + '\'' +
                ", resultCode='" + resultCode + '\'' +
                ", returnCode='" + returnCode + '\'' +
                ", errCode='" + errCode + '\'' +
                ", errCodeDes='" + errCodeDes + '\'' +
                ", openId='" + openId + '\'' +
                ", isSubscribe='" + isSubscribe + '\'' +
                ", tradeType='" + tradeType + '\'' +
                ", bankType='" + bankType + '\'' +
                ", totalFee=" + totalFee +
                ", feeType='" + feeType + '\'' +
                ", cashFee='" + cashFee + '\'' +
                ", cashFeeType='" + cashFeeType + '\'' +
                ", couponFee='" + couponFee + '\'' +
                ", couponCount='" + couponCount + '\'' +
                ", transactionId='" + transactionId + '\'' +
                ", outTradeNo='" + outTradeNo + '\'' +
                ", attach='" + attach + '\'' +
                ", timeEnd='" + timeEnd + '\'' +
                '}';
    }
}