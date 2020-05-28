package com.github.bluecatlee.common.third.wechat.service;

import com.github.bluecatlee.common.third.wechat.bean.*;
import com.github.bluecatlee.common.third.wechat.enumeration.WeChatTradeType;
import com.github.bluecatlee.common.third.wechat.exception.WeChatException;
import com.github.bluecatlee.common.third.wechat.utils.WeChatUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.github.bluecatlee.common.third.wechat.bean.WeChatUrls.*;


@Service
public class WeChatService {

    /**
     * 获取小程序二维码
     */
    public String createQRCode(final String accessToken, String scene, @Nullable String page, @Nullable Integer width) {
        if (width == null) {
            width = 430; // 二维码的宽度
        }
        if (StringUtils.isBlank(page)) {
            page = "pages/index/index";
        }
        String param = "{\"scene\":\""+ scene + "\",\"page\":\"" + page + "\", \"width\": " + width.intValue() + "}";
        byte[] bytes = WeChatUtils.post2(String.format(URL_CREATEQRCODE_UNLIMIT, accessToken), param.getBytes());
        return "data:image/jpg;base64," + Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * 获取小程序二维码
     */
    public byte[] createQRCode2(final String accessToken, String scene, @Nullable String page, @Nullable Integer width) throws IOException {
        if (width == null) {
            width = 430; // 二维码的宽度
        }
        if (StringUtils.isBlank(page)) {
            page = "pages/goodsdetail/goodsdetail";
        }
        String param = "{\"scene\":\""+ scene + "\",\"page\":\"" + page + "\", \"width\": " + width.intValue() + "}";
        byte[] bytes = WeChatUtils.post2(String.format(URL_CREATEQRCODE_UNLIMIT, accessToken), param.getBytes());
        return bytes;
    }

    /**
     * 小程序获取openid
     */
    public WeChatJSCodeSession getJSCodeSession(final String appId, final String secret, final String code) {
        final String content = WeChatUtils.get(String.format(URL_JSCODE2SESSION, appId, secret, code));
        return WeChatUtils.getBeanFromJson(content, WeChatJSCodeSession.class);
    }

    /**
     * 获取令牌
     */
    public WeChatAccessToken getAccessToken(final String appId, final String secret) {
        final String content = WeChatUtils.get(String.format(URL_ACCESS_TOKEN, appId, secret));
        return WeChatUtils.getBeanFromJson(content, WeChatAccessToken.class);
    }


    /**
     * 发送模版信息
     */
    public WeChatJson sendTempleteMessage(final String accessToken, final String openId, final String templeteId,
                                          final String url, Map<String, Map<String, Object>> body, final String formId) {
        Map<String, Object> params = new HashMap<>();
        params.put("touser", openId);
        params.put("template_id", templeteId);
        params.put("page", url);
        params.put("data", body);
        params.put("form_id", formId);

        final String content;
        try {
            content = WeChatUtils.post(String.format(URL_TEMPLETE_SEND, accessToken), WeChatUtils.beanToJsonString(params).getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new WeChatException(e);
        }
        return WeChatUtils.getBeanFromJson(content, WeChatJson.class);
    }


    /**
     * 生成微信订单号
     * @param ch 订单渠道
     */
    public String genOrderNo(String ch) {
        String base = "0123456789";
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        StringBuilder sb = new StringBuilder("wx00" + ch);
        sb.append(format.format(new Date()));
        Random random = new Random();
        for (int i = 0; i < (32 - ch.length() - sb.length()); ++i) {
            int idx = random.nextInt(base.length());
            sb.append(base.charAt(idx));
        }
        return sb.toString();
    }

    /**
     * 统一下单并且获取支付签名
     *      新增expireTime 超时时间限制 单位min
     */
    public WeChatPay buildPay(final String appId, final String mchId, final String appKey,
                              final WeChatTradeType tradeType, final String openId, final String orderNo, final String productName,
                              final int fee, final String ip, final String notifyUrl, final String attach, final String expireTime) {
        WeChatUnifiedOrder order = new WeChatUnifiedOrder();
        order.setAppId(appId);
        order.setMchId(mchId);
        order.setBody(WeChatUtils.handleLength(productName));
        order.setOutTradeNo(orderNo);
        order.setTotalFee(fee);
        order.setSpbillCreateIp(ip);
        order.setTradeType(tradeType);
        order.setNotifyUrl(notifyUrl);
        order.setAttach(attach);
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        Calendar calendar = Calendar.getInstance();
        order.setTimeStart(format.format(calendar.getTime()));
        if (StringUtils.isBlank(expireTime)) {
            calendar.add(Calendar.DATE, 1);   // 默认订单是1天内支付有效
        } else {
            calendar.add(Calendar.MINUTE, Integer.parseInt(expireTime));
        }
        order.setTimeExpire(format.format(calendar.getTime()));
        if (tradeType == WeChatTradeType.JSAPI) {
            order.setOpenId(openId);
        }
        WeChatUnifiedOrder unifiedOrder = getUnifiedOrder(appKey, order);
        WeChatPay weChatPay = createPay(appId, appKey, unifiedOrder.getPrepayId());
        return weChatPay;
    }

    /**
     * 支付参数
     */
    private WeChatPay createPay(final String appId, final String appKey, final String prepayId) {
        final WeChatPay weChatPay = new WeChatPay();
        weChatPay.setAppId(appId);
        weChatPay.setNonceStr(WeChatUtils.createNonceStr());
        weChatPay.setTimeStamp(WeChatUtils.createTimestamp());
        weChatPay.setPack("prepay_id=" + prepayId);
        weChatPay.setPaySign(WeChatUtils.signWithMd5(weChatPay, appKey));
        return weChatPay;
    }

    /**
     * 退款
     * @param appKey     秘钥
     * @param file       证书
     * @param password   证书密码
     * @param weChatRefund 退款参数
     * @return
     */
    public WeChatRefund refund(final String appKey, File file, final String password,
                               WeChatRefund weChatRefund) {
        try {
            weChatRefund.setNonce(WeChatUtils.createNonceStr());
            Map<String, Object> params = WeChatUtils.getSignMapFromObject(weChatRefund);
            Set<String> keysSet = params.keySet();
            Object[] keys = keysSet.toArray();
            Arrays.sort(keys);

            StringBuilder sb = new StringBuilder();
            StringBuilder xml = new StringBuilder();
            xml.append("<xml>");
            for (Object key : keys) {
                String value = String.valueOf(params.get(key));
                if (!value.equals("null")) {
                    sb.append(key).append("=").append(value).append("&");
                }
                xml.append("<").append(key).append("><![CDATA[").append(value).append("]]></").append(key).append(">");
            }
            sb.append("key=").append(appKey);
            String sign = WeChatUtils.md5(sb.toString()).toUpperCase();
            xml.append("<sign><![CDATA[").append(sign).append("]]></sign>");
            xml.append("</xml>");
            final String content = WeChatUtils.post(URL_REFUND, true, file, password, xml.toString().getBytes());
            weChatRefund = WeChatUtils.getBeanFromXml(content, WeChatRefund.class);
            return weChatRefund;
        } catch (Exception e) {
            throw new WeChatException(e);
        }

    }

    /**
     * 统一下订单
     */
    public WeChatUnifiedOrder getUnifiedOrder(final String appKey, WeChatUnifiedOrder weChatUnifiedOrder) {
        try {
            weChatUnifiedOrder.setNonce(WeChatUtils.createNonceStr());
            Map<String, Object> params = WeChatUtils.getSignMapFromObject(weChatUnifiedOrder);
            Set<String> keysSet = params.keySet();
            Object[] keys = keysSet.toArray();
            Arrays.sort(keys);
            StringBuilder sb = new StringBuilder();
            StringBuilder xml = new StringBuilder();
            xml.append("<xml>");
            for (Object key : keys) {
                String value = String.valueOf(params.get(key));
                if (!value.equals("null")) {
                    sb.append(key).append("=").append(value).append("&");
                    xml.append("<").append(key).append("><![CDATA[").append(value).append("]]></").append(key)
                            .append(">");
                }
            }
            sb.append("key=").append(appKey);
            final String sign = WeChatUtils.md5(sb.toString()).toUpperCase();
            xml.append("<sign><![CDATA[").append(sign).append("]]></sign>");
            xml.append("</xml>");

            final String content = WeChatUtils.post(URL_UNIFIEDORDER, xml.toString().getBytes("utf-8"));
            weChatUnifiedOrder = WeChatUtils.getBeanFromXml(content, WeChatUnifiedOrder.class);
            final String returnCode = weChatUnifiedOrder.getReturnCode();
            if (returnCode == null || !returnCode.equalsIgnoreCase("SUCCESS")) {
                throw new WeChatException(weChatUnifiedOrder.getReturnMsg());
            }
            return weChatUnifiedOrder;
        } catch (Exception e) {
            throw new WeChatException(e);
        }
    }

    /**
     * 微信回调
     */
    public WeChatPayBack parseWechatBack(final String content, final String appKey) {
        WeChatPayBack weChatPayBack = WeChatUtils.getBeanFromXml(content, WeChatPayBack.class);
        String sign = WeChatUtils.signWithMd5(weChatPayBack, appKey);
        if (sign == null || weChatPayBack.getSign() == null || !weChatPayBack.getSign().equals(sign)) {
            throw new WeChatException("sign error " + weChatPayBack.getSign() + " != " + sign);
        }
        return weChatPayBack;
    }

    /**
     * 微信回调(不带验签) 重载方法 方便调用者单独处理验签结果
     * @param content
     */
    public WeChatPayBack parseWechatBack(final String content) {
        WeChatPayBack weChatPayBack = WeChatUtils.getBeanFromXml(content, WeChatPayBack.class);
        return weChatPayBack;
    }

    /**
     * 验证签名
     * @param weChatPayBack
     * @param appKey
     * @return
     */
    public boolean verifySign(final WeChatPayBack weChatPayBack, final String appKey) {
        String sign = WeChatUtils.signWithMd5(weChatPayBack, appKey);
        if (sign == null || weChatPayBack.getSign() == null || !weChatPayBack.getSign().equals(sign)) {
            //throw new WeChatException("sign error " + weChatPayBack.getSign() + " != " + sign);
            return false;
        }
        return true;
    }

}