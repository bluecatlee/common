package com.github.bluecatlee.common.third.upyun;

import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 文件上传工具类
 */
public class UploadUtils {

    // 云提交地址
    private static final String UPYUN_URL = "http://v0.api.upyun.com";

    private static final String KEY_PATH = "x-path";

    /**
     * 又拍云上传文件
     *
     * @param bucket   云存储空间名
     * @param user     用户名
     * @param password 密码
     * @param file     文件
     * @param midPath  中间路径 nullable
     * @return
     * @throws Exception
     */
    public static String upload(final String bucket, final String user, final String password, final MultipartFile file, final String midPath, boolean realName)
            throws Exception {
        final byte[] bytes = file.getBytes();
        final String bucketp = "/" + bucket + "/";
        final String uri = path(file, midPath, realName);
        final String date = getGMTDate();
        final String passwordMD5 = md5(password);
        final String signature = signature(user, passwordMD5, bucketp + uri, date, bytes.length);

        final Map<String, String> headerMap = new HashMap<>();
        headerMap.put("mkdir", "true");
        headerMap.put("Authorization", signature);
        headerMap.put("Date", date);
        headerMap.put(KEY_PATH, uri);

        final OkHttpClient.Builder builder = new OkHttpClient.Builder();
        final Request request = new Request.Builder().url(UPYUN_URL + bucketp + uri)
                .put(RequestBody.create(MediaType.parse(""), bytes)).headers(Headers.of(headerMap)).build();

        Response response = null;
        try {
            response = builder.build().newCall(request).execute();
            if (response.isSuccessful()) {
                return response.request().header(KEY_PATH);
            }
            // return response.body().string();
            throw new Exception(response.body().string());
        } finally {
            if (response != null)
                response.close();
        }
    }

    public static String upload(final String bucket, final String user, final String password, final MultipartFile file, final String midPath) throws Exception{
        return upload(bucket, user, password, file, midPath, false);
    }

    /**
     * 上传文件到本地
     * @param savePath 文件保存路径
     * @param file 文件
     * @param midPath 中间目录 nullable
     * @return
     * @throws Exception
     */
    public static String upload(final String savePath, final MultipartFile file, final String midPath) throws Exception {

        String uri = path(file, midPath, false);
        if (!uri.startsWith("/") && !savePath.endsWith("/")) {
            uri = "/" + uri;
        }
        File dest = new File(savePath + uri);
        if (!dest.getParentFile().exists()){
            dest.getParentFile().mkdirs();
        }
        file.transferTo(dest);
        return uri;
    }

    /**
     * 日期目录文件名
     *
     * @throws NoSuchAlgorithmException
     */
    private static String path(MultipartFile file, String midPath, boolean realName) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        String dir = "";
        if (StringUtils.isNotBlank(midPath)) {
            dir = midPath + "/" + new SimpleDateFormat("yyyy/MM/dd").format(new Date()) + "/";
        } else {
            dir = new SimpleDateFormat("yyyy/MM/dd").format(new Date()) + "/";
        }
        String originalFilename = file.getOriginalFilename();
        if (realName) {
            String p = md5("we-." + System.currentTimeMillis());
            String encode1 = URLEncoder.encode(originalFilename, "UTF-8");
            String path = dir + p + "/" + encode1;
            return path;
        } else {
            String fileName = md5("we-." + System.currentTimeMillis());
            String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            String path = dir + fileName + suffix;
            return path;
        }

    }

    /**
     * 时间
     */
    private static String getGMTDate() {
        SimpleDateFormat formater = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss 'GMT'", Locale.US);
        formater.setTimeZone(TimeZone.getTimeZone("GMT"));
        return formater.format(new Date());

    }

    /**
     * 签名
     *
     * @throws NoSuchAlgorithmException
     */
    private static String signature(final String user, final String password, final String uri, final String date,
                                    final long length) throws NoSuchAlgorithmException {
        String sign = "PUT&" + uri + "&" + date + "&" + length + "&" + password;
        return "UpYun " + user + ":" + md5(sign);
    }

    private static String md5(final String str) throws NoSuchAlgorithmException {
        return encode("MD5", str);
    }

    private static String encode(final String algorithm, final String str) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
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

}

