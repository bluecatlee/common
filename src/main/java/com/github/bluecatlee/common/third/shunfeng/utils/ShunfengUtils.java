package com.github.bluecatlee.common.third.shunfeng.utils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.bluecatlee.common.third.shunfeng.bean.*;
import com.github.bluecatlee.common.third.shunfeng.bean.req.SfOrderReq;
import com.github.bluecatlee.common.third.shunfeng.bean.resp.AccountResp;
import com.github.bluecatlee.common.third.shunfeng.bean.resp.CreateSfOrderResp;
import com.github.bluecatlee.common.third.shunfeng.bean.resp.QuerySfOrderResp;
import com.github.bluecatlee.common.third.shunfeng.bean.resp.SfProductListResp;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 顺丰优选工具类
 */
public class ShunfengUtils {

    private static final String baseUrl = "http://cct.benlaiguofang.com";
    private static final String getAccountUrl = "/Account/GetAccount";
    private static final String getWebSiteListUrl = "/WebSite/GetWebSiteList";
    private static final String getDeskSiteListUrl = "/WebSite/GetDeskSiteList";
    private static final String getProductListUrl = "/ProductThirPart/GetProductList";
    private static final String getProductSingleUrl = "/ProductThirPart/GetProductSingle";
    private static final String createOrderUrl = "/Order/CreateOrder";
    private static final String getOrderUrl = "/Order/GetOrder";
    private static final String cancelOrderUrl = "/Order/CancelOrder";

    private static final Logger LOGGER = LoggerFactory.getLogger(ShunfengUtils.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static String appKey = "";
    private static String appSecret = "";
    private static String merchantId = "";

    static {
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // 初始化配置信息
        Properties configProperties = getConfigProperties();
        if (configProperties != null) {
            appKey = (String) configProperties.get("shunfeng.appKey");
            appSecret = (String) configProperties.get("shunfeng.appSecret");
            merchantId = (String) configProperties.get("shunfeng.merchantId");
        }
        if (StringUtils.isBlank(appKey)) {
            appKey = "588b4612b36a51e42fca90e280df6ae3";
        }
        if (StringUtils.isBlank(appSecret)) {
            appSecret = "Benlai123!@#_Yegou";
        }
        if (StringUtils.isBlank(merchantId)) {
            merchantId = "2020365142902";
        }
    }

    private static Properties getConfigProperties() {
        Properties properties = new Properties();
        try {
            properties.load(ShunfengUtils.class.getClassLoader().getResourceAsStream("com/ningpai/web/config/config.properties"));
        } catch (Exception e) {
            return null;
        }
        return properties;
    }

    /**
     * 获取账户信息
     */
    public static AccountResp getAccount() {
        // AccountReq accountReq = new AccountReq();
        Map<String, Object> params = new HashMap<>();
        Map<String, Object> requestMap = initCommonParams(params);
        try {
            // String s = OBJECT_MAPPER.writeValueAsString(accountReq);
            String s = OBJECT_MAPPER.writeValueAsString(requestMap);
            String result = ShunfengHttpUtils.post(baseUrl + getAccountUrl, s.getBytes("UTF-8"), String.class);
            SfResult<AccountResp> sfResult = OBJECT_MAPPER.readValue(result, new TypeReference<SfResult<AccountResp>>() {
            });
            AccountResp data = sfResult.getData();
            return data;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取站点列表
     */
    public static List<WebSite> getWebSiteList() {
        // WebSiteReq webSiteReq = new WebSiteReq();
        Map<String, Object> params = new HashMap<>();
        Map<String, Object> requestMap = initCommonParams(params);
        String s = null;
        try {
            // s = OBJECT_MAPPER.writeValueAsString(webSiteReq);
            s = OBJECT_MAPPER.writeValueAsString(requestMap);
            String result = ShunfengHttpUtils.post(baseUrl + getWebSiteListUrl, s.getBytes("UTF-8"), String.class);
            SfResult<List<WebSite>> sfResult = OBJECT_MAPPER.readValue(result, new TypeReference<SfResult<List<WebSite>>>() {
            });
            return sfResult.getData();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据站点编号获取有效小站列表
     * @param siteNo  大站编号
     */
    public static List<DeskSite> getDeskSiteList(String siteNo) {
        Map<String, Object> params = new HashMap<>();
        params.put("siteNo", siteNo);
        Map<String, Object> requestMap = initCommonParams(params);
        String s = null;
        try {
            s = OBJECT_MAPPER.writeValueAsString(requestMap);
            String result = ShunfengHttpUtils.post(baseUrl + getDeskSiteListUrl, s.getBytes("UTF-8"), String.class);
            SfResult<List<DeskSite>> sfResult = OBJECT_MAPPER.readValue(result, new TypeReference<SfResult<List<DeskSite>>>() {
            });
            return sfResult.getData();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取这个站点类别下的所有上架商品
     * @param webSiteDeskNo 小站编号
     * @param c1No 运营大类
     * @param c2No 运营中类
     * @param c3No 运营小类
     */
    public static SfProductListResp getProductList(String webSiteDeskNo, Integer c1No, Integer c2No, Integer c3No, Integer offset, Integer pageSize) {
        Map<String, Object> params = new HashMap<>();
        params.put("webSiteDeskNo", webSiteDeskNo);
        params = initCommonParams(params);
        // 以下参数不参与签名
        // params.put("keyWord", keyWord);   // 查询关键字和类别编号中至少有一项有值
        params.put("c1No", c1No);         // 运营类别编号大类
        params.put("c2No", c2No);
        params.put("c3No", c3No);
        // params.put("imgType", imgType);   // 图片规格枚举 默认2:300*300
        params.put("offset", offset);
        params.put("pageSize", pageSize);

        try {
            String s = OBJECT_MAPPER.writeValueAsString(params);
            String result = ShunfengHttpUtils.post(baseUrl + getProductListUrl, s.getBytes("UTF-8"), String.class);
            SfResult<SfProductListResp> sfResult = OBJECT_MAPPER.readValue(result, new TypeReference<SfResult<SfProductListResp>>() {
            });
            if (sfResult != null) {
                return sfResult.getData();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据商品ID获取商品
     * @param webSiteDeskNo 小站编号
     * @param productId 商品编号
     */
    public static SfProductDetail getProduct(String webSiteDeskNo, String productId) {
        Map<String, Object> params = new HashMap<>();
        params.put("webSiteDeskNo", webSiteDeskNo);
        params.put("productId", productId);
        params = initCommonParams(params);
        params.put("imgType", 3);   // 图片规格枚举 默认2:300*300

        try {
            String s = OBJECT_MAPPER.writeValueAsString(params);
            String result = ShunfengHttpUtils.post(baseUrl + getProductSingleUrl, s.getBytes("UTF-8"), String.class);
            SfResult<SfProductDetail> sfResult = OBJECT_MAPPER.readValue(result, new TypeReference<SfResult<SfProductDetail>>() {
            });
            if (sfResult != null) {
                return sfResult.getData();
            }
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

    /**
     * 创建订单
     * @param sfOrder
     * @return
     */
    public static CreateSfOrderResp createOrder(SfOrder sfOrder) {
        SfOrderReq sfOrderReq = new SfOrderReq();
        sfOrderReq.setSfOrder(sfOrder);

        SfCommonReqParams sfCommonReqParams = initCommonParams(sfOrderReq);
        try {
            String request = OBJECT_MAPPER.writeValueAsString(sfCommonReqParams);
            String result = ShunfengHttpUtils.post(baseUrl + createOrderUrl, request.getBytes("UTF-8"), String.class);
            SfResult<CreateSfOrderResp> sfResult = OBJECT_MAPPER.readValue(result,  new TypeReference<SfResult<CreateSfOrderResp>>() {
            });
            return sfResult.getData();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查询订单
     * @param orderId 订单id
     */
    public static QuerySfOrderResp querOrder(String orderId) {
        Map<String, Object> params = new HashMap<>();
        params.put("orderId", orderId);
        params = initCommonParams(params);

        try {
            String s = OBJECT_MAPPER.writeValueAsString(params);
            String result = ShunfengHttpUtils.post(baseUrl + getOrderUrl, s.getBytes("UTF-8"), String.class);
            SfResult<QuerySfOrderResp> sfResult = OBJECT_MAPPER.readValue(result, new TypeReference<SfResult<QuerySfOrderResp>>() {
            });
            return sfResult.getData();
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 取消订单
     *      取消一个已经取消的订单 会返回错误
     * @param orderId 订单id
     * @return 返回被取消的订单号
     */
    public static String cancelOrder(String orderId) {
        Map<String, Object> params = new HashMap<>();
        params.put("orderId", orderId);
        params = initCommonParams(params);

        try {
            String s = OBJECT_MAPPER.writeValueAsString(params);
            String result = ShunfengHttpUtils.post(baseUrl + cancelOrderUrl, s.getBytes("UTF-8"), String.class);
            SfResult<String> sfResult = OBJECT_MAPPER.readValue(result, new TypeReference<SfResult<String>>() {
            });
            if (sfResult != null && "200".equals(sfResult.getStatus())) {
                return sfResult.getData();
            }
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 初始化公共参数
     * @return
     */
    private static Map<String, Object> initCommonParams(Map<String, Object> params) {
        // HashMap<String, Object> params = new HashMap<>();
        params.put("appKey", appKey);
        params.put("timestamp", DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now()));
        params.put("merchantId", merchantId);
        // 生成签名
        String sign = sign(params);
        params.put("sign", sign);
        return params;
    }

    /**
     * 初始化公共参数
     * @param commonReqParams
     * @return
     */
    private static SfCommonReqParams initCommonParams(SfCommonReqParams commonReqParams) {
        commonReqParams.setAppKey(appKey);
        commonReqParams.setTimestamp(DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now()));
        commonReqParams.setMerchantId(merchantId);

        // 生成签名
        Map<String, Object> params = new HashMap<>();
        params.put("appKey", commonReqParams.getAppKey());
        params.put("timestamp", commonReqParams.getTimestamp());
        params.put("merchantId", commonReqParams.getMerchantId());
        // if (commonReqParams instanceof SfOrderReq) {
        //     SfOrderReq req = (SfOrderReq)commonReqParams;
        //     params.put("deskSiteNo", req.getSfOrder().getDeskSiteNo());
        //     params.put("customerAddressNo", req.getSfOrder().getCustomerAddressNo());
        //     params.put("deliveryType", req.getSfOrder().getDeliveryType());
        //     params.put("deliveryTime", req.getSfOrder().getDeliveryTime());
        // }
        String sign = sign(params);

        commonReqParams.setSign(sign);
        return commonReqParams;
    }

    /**
     * 生成签名
     * @param map
     * @return
     */
    private static String sign(Map map) {
        String signStr = sortAndConstructSignStr(map) + appSecret;
        String sign = null;
        try {
            sign = md5(signStr.toUpperCase()).toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return sign;
    }

    private static String sign(String original) {
        String signStr = original + appSecret;
        String sign = null;
        try {
            sign = md5(signStr.toUpperCase()).toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return sign;
    }

    /**
     * 对参数进行排序并生成签名串
     * @param params
     * @return
     */
    private static String sortAndConstructSignStr(Map<String, Object> params){

        List<Map.Entry<String, Object>> list = new ArrayList<Map.Entry<String, Object>>(params.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Object>>() {
            @Override
            public int compare(Map.Entry<String, Object> arg0, Map.Entry<String, Object> arg1) {
                return (arg0.getKey()).compareTo(arg1.getKey());
            }
        });

        String ret = "";

        for (Map.Entry<String, Object> entry : list) {
            if (entry.getValue() == null) {
                continue;
            }
            if (entry.getValue().getClass().equals(Collection.class)) {
                continue;
            }
            // ret += entry.getKey();
            // ret += "=";
            ret += entry.getValue();
            // ret += "&";
        }
        // ret = ret.substring(0, ret.length() - 1);
        return ret;
    }

    private static String md5(final String str) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        final byte[] buff = messageDigest.digest(str.getBytes());
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < buff.length; ++i) {
            final String hexStr = Integer.toHexString(0xFF & buff[i]);
            if (hexStr.length() == 1) {
                sb.append("0").append(hexStr);
            } else {
                sb.append(hexStr);
            }
        }
        return sb.toString();
    }

    @SneakyThrows
    public static void main(String[] args) {
        // String str = "588b4612b36a51e42fca90e280df6ae31100000df20200101131535Benlai123!@#_Yegou";
        // System.out.println(str.toUpperCase());
        // String s = YGCodecUtils.md5(str.toUpperCase()).toUpperCase();
        // System.out.println(s);

        // String str = "588b4612b36a51e42fca90e280df6ae3202036514290220200615110000Benlai123!@#_Yegou";
        // String s = YGCodecUtils.md5(str.toUpperCase()).toUpperCase();
        // System.out.println(s);

        // getAccount();
        // getWebSiteList();
        // getDeskSiteList("1");
        // getProductList("3", 1, null, null, 1, 20);
        // getProduct("3", "137");

        // SfOrder sfOrder = new SfOrder();
        // sfOrder.setDeskSiteNo(3);
        // sfOrder.setDeliveryType(13);
        // sfOrder.setDeliveryTime(1);
        // sfOrder.setCustomerAddressNo(35);
        // SfOrderGoods sfOrderGoods = new SfOrderGoods();
        // sfOrderGoods.setSkuId("0308010003F");
        // sfOrderGoods.setCount(2);
        // SfOrderGoods sfOrderGoods2 = new SfOrderGoods();
        // sfOrderGoods2.setSkuId("0304010001F");
        // sfOrderGoods2.setCount(3);
        // ArrayList<SfOrderGoods> ordergoods = new ArrayList<>();
        // ordergoods.add(sfOrderGoods);
        // ordergoods.add(sfOrderGoods2);
        // sfOrder.setSkuList(ordergoods);
        // createOrder(sfOrder);

        // querOrder("1000000537");

        cancelOrder("1000000537");

    }

}
