package com.github.bluecatlee.common.third.jianzhou;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * 短信工具类
 */
@Component
public class SMSSendUtil {
    /**
     * 日志
     * */
    private static final Logger LOGGER = LoggerFactory.getLogger(SMSSendUtil.class);

    private static String loginName;

    private static String password;

    private static String httpUrl;   // 普通提交 支持多手机号相同内容的批量提交

    private static String httpUrl2;  // 支持多手机号不同内容的批量提交

    @Value("${jianzhou.sms_name:}")
    public void setLoginName(String loginName) {
        SMSSendUtil.loginName = loginName;
    }

    @Value("${jianzhou.sms_pass:}")
    public void setPassword(String password) {
        SMSSendUtil.password = password;
    }

    @Value("${jianzhou.sms_url:}")
    public void setHttpUrl(String httpUrl) {
        SMSSendUtil.httpUrl = httpUrl;
    }

    @Value("${jianzhou.sms_url2:}")
    public void setHttpUrl2(String httpUrl2) {
        SMSSendUtil.httpUrl2 = httpUrl2;
    }

    /**
     * 建周普通短信提交（sendBatchMessage接口）
     *      1.中文字符统一采用utf-8编码
            2.提交必须为POST方式
            3.返回结果为纯文本方式
            4.所有输入参数都大小写敏感
     * @return
     */
    public static Boolean sendMsm(String phone, String content) {
        try {
            RestTemplate rt = new RestTemplate();
            String sendContent = content.replaceAll("<br/>", " ");// 发送内容

            MultiValueMap<String, String> requestEntity = new LinkedMultiValueMap<>();
            requestEntity.add("account", loginName);                                                    //登录账号
            requestEntity.add("password", password);                                                    //登录密码，MD5加密
            requestEntity.add("destmobile", phone);                                                     //目标手机号，多个手机号码用;分割，建议一次最多提交3000左右的号码
            requestEntity.add("msgText", new String(sendContent.getBytes(),"UTF-8"));     //短消息内容+签名
            String result = rt.postForObject(httpUrl, requestEntity, String.class);

            LOGGER.debug("短信返回结果：{}", result);
            int resultCode = Integer.parseInt(result);
            if (resultCode > 0) {
                // >0 表示提交成功，该数字为本批次的任务ID，如有需要提交成功后请自行保存发送记录。
                return true;
            } else {
                logError(resultCode);
                return false;
            }
        } catch (Exception e) {
            LOGGER.error("短信发送失败", e);
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 建周个性化短信提交（sendPersonalMessages接口）
     * @return
     */
    public static Boolean batchSendMsm(String[] phones, String[] contents) {
        if (phones == null || contents == null || phones.length <= 0 || contents.length <= 0 || phones.length != contents.length) {
            LOGGER.debug("批量提交短信失败: 参数不正确");
            return false;
        }
        if (phones.length > 3000) {
            LOGGER.debug("批量提交短信失败: 一次最多3000个手机号码");
        }

        try {
            String destMobiles = StringUtils.join(phones, "||");
            String msgContents = StringUtils.join(contents, "||").replaceAll("<br/>", " ");

            RestTemplate rt = new RestTemplate();

            MultiValueMap<String, String> requestEntity = new LinkedMultiValueMap<>();
            requestEntity.add("account", loginName);                                                    //登录账号
            requestEntity.add("password", password);                                                    //登录密码，MD5加密
            requestEntity.add("destMobiles", destMobiles);                                                     //目标手机号，多个手机号码用||分割，建议一次最多提交3000左右的号码
            requestEntity.add("msgContents", new String(msgContents.getBytes(),"UTF-8"));     //短消息内容+签名
            String result = rt.postForObject(httpUrl2, requestEntity, String.class);

            LOGGER.debug("批量提交短信返回结果：{}", result);
            int resultCode = Integer.parseInt(result);
            if (resultCode > 0) {
                // >0 表示提交成功，该数字为本批次的任务ID，如有需要提交成功后请自行保存发送记录。
                return true;
            } else {
                logError(resultCode);
                return false;
            }
        } catch (Exception e) {
            LOGGER.error("短信批量发送失败", e);
            e.printStackTrace();
        }

        return false;
    }

    private static void logError(int code) {
        //LOGGER.debug("短信发送失败，请查看返回码");
        if (code == -1) {
            LOGGER.debug("短信发送失败：余额不足");
        } else if (code == -2) {
            LOGGER.debug("短信发送失败：帐号或密码错误");
        } else if (code == -3) {
            LOGGER.debug("短信发送失败：连接服务商失败");
        } else if (code == -5) {
            LOGGER.debug("短信发送失败：其他错误，一般为网络问题，IP受限等");
        } else if (code == -6) {
            LOGGER.debug("短信发送失败：短信内容为空");
        } else if (code == -7) {
            LOGGER.debug("短信发送失败：目标号码为空");
        } else if (code == -9) {
            LOGGER.debug("短信发送失败：捕获未知异常");
        } else if (code == -10) {
            LOGGER.debug("短信发送失败：超过最大定时时间限制");
        } else if (code == -13) {
            LOGGER.debug("短信发送失败：没有权限使用该网关");
        } else if (code == -17) {
            LOGGER.debug("短信发送失败：没有提交权限，客户端帐号无法使用接口提交。或非绑定IP提交");
        } else if (code == -18) {
            LOGGER.debug("短信发送失败：提交参数名称不正确或确少参数");
        } else if (code == -19) {
            LOGGER.debug("短信发送失败：必须为POST提交");
        } else if (code == -20) {
            LOGGER.debug("短信发送失败：超速提交(非验证码类短信一般为每秒一次提交)");
        } else if (code == -21) {
            LOGGER.debug("短信发送失败：扩展参数不正确");
        } else if (code == -22) {
            LOGGER.debug("短信发送失败：Ip 被封停");
        } else if (code == -23) {
            LOGGER.debug("短信发送失败：国际短信号码必须“00”开头");
        } else if (code == -24) {
            LOGGER.debug("短信发送失败：userTaskID错误");
        } else {
            LOGGER.debug("短信发送失败：未知原因,返回码(" + code + ")");
        }
    }

}
