package com.github.bluecatlee.common.pay.utils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

public class RSA {

	public static final String SIGN_TYPE_RSA = "RSA";
	public static final String SIGN_TYPE_RSA2 = "RSA2";
	public static final String SIGN_ALGORITHMS = "SHA1WithRSA";
	public static final String SIGN_SHA256RSA_ALGORITHMS = "SHA256WithRSA";

	/**
	 * sha1WithRsa 加签
	 * 
	 * @param content
	 * @param privateKey
	 * @param charset
	 * @return
	 * @throws Exception
	 */
	public static String rsaSign(String content, String privateKey, String charset) throws Exception {
		try {
			PrivateKey priKey = getPrivateKeyFromPKCS8(SIGN_TYPE_RSA, new ByteArrayInputStream(privateKey.getBytes()));

			java.security.Signature signature = java.security.Signature.getInstance(SIGN_ALGORITHMS);

			signature.initSign(priKey);

			if (StringUtils.isEmpty(charset)) {
				signature.update(content.getBytes());
			} else {
				signature.update(content.getBytes(charset));
			}

			byte[] signed = signature.sign();

			return new String(Base64.encodeBase64(signed));
		} catch (InvalidKeySpecException ie) {
			throw new Exception("RSA私钥格式不正确，请检查是否正确配置了PKCS8格式的私钥", ie);
		} catch (Exception e) {
			throw new Exception("RSAcontent = " + content + "; charset = " + charset, e);
		}
	}

	/**
	 * sha256WithRsa 加签
	 * 
	 * @param content
	 * @param privateKey
	 * @param charset
	 * @return
	 * @throws Exception
	 * @throws Exception
	 */
	public static String rsa256Sign(String content, String privateKey, String charset) throws Exception {

		try {
			PrivateKey priKey = getPrivateKeyFromPKCS8(SIGN_TYPE_RSA, new ByteArrayInputStream(privateKey.getBytes()));

			java.security.Signature signature = java.security.Signature.getInstance(SIGN_SHA256RSA_ALGORITHMS);

			signature.initSign(priKey);

			if (StringUtils.isEmpty(charset)) {
				signature.update(content.getBytes());
			} else {
				signature.update(content.getBytes(charset));
			}

			byte[] signed = signature.sign();

			return new String(Base64.encodeBase64(signed));
		} catch (Exception e) {
			throw new Exception("rsa2生成签名错误");
		}

	}
	
	/**
	 * 对map签名数据验签
	 * 
	 * @param params
	 * @param publicKey
	 * @param charset
	 * @param signType
	 * @return
	 * @throws Exception
	 */
	public static boolean rsaCheckV1(Map<String, String> params, String publicKey,
		String charset,String signType) throws Exception {
		String sign = params.get("sign");
		String content = getSignCheckContentV1(params);
		
		return rsaCheck(content, sign, publicKey, charset,signType);
	}
	
	/**
	 * 验签内容转成字符串
	 * 
	 * @param params
	 * @return
	 */
	public static String getSignCheckContentV1(Map<String, String> params) {
        if (params == null) {
            return null;
        }

        params.remove("sign");
        params.remove("sign_type");

        StringBuffer content = new StringBuffer();
        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);

        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key);
            content.append((i == 0 ? "" : "&") + key + "=" + value);
        }

        return content.toString();
    }


	/**
	 * 验签
	 * 
	 * @param content
	 * @param sign
	 * @param publicKey
	 * @param charset
	 * @param signType
	 * @return
	 * @throws Exception
	 */
	public static boolean rsaCheck(String content, String sign, String publicKey, String charset, String signType)
			throws Exception {

		if (SIGN_TYPE_RSA.equals(signType)) {

			return rsaCheckContent(content, sign, publicKey, charset);

		} else if (SIGN_TYPE_RSA2.equals(signType)) {

			return rsa256CheckContent(content, sign, publicKey, charset);

		} else {

			throw new Exception("Sign Type is Not Support : signType=" + signType);
		}

	}

	/**
	 * sha1WithRsa 验签
	 * 
	 * @param content
	 * @param sign
	 * @param publicKey
	 * @param charset
	 * @return
	 * @throws Exception
	 */
	public static boolean rsaCheckContent(String content, String sign, String publicKey, String charset)
			throws Exception {
		try {
			PublicKey pubKey = getPublicKeyFromX509("RSA", new ByteArrayInputStream(publicKey.getBytes()));

			java.security.Signature signature = java.security.Signature.getInstance(SIGN_ALGORITHMS);

			signature.initVerify(pubKey);

			if (StringUtils.isEmpty(charset)) {
				signature.update(content.getBytes());
			} else {
				signature.update(content.getBytes(charset));
			}

			return signature.verify(Base64.decodeBase64(sign.getBytes()));
		} catch (Exception e) {
			throw new Exception("RSAcontent = " + content + ",sign=" + sign + ",charset = " + charset, e);
		}
	}

	/**
	 * sha256WithRsa 验签
	 * 
	 * @param content
	 * @param sign
	 * @param publicKey
	 * @param charset
	 * @return
	 * @throws Exception
	 */
	public static boolean rsa256CheckContent(String content, String sign, String publicKey, String charset)
			throws Exception {
		try {
			PublicKey pubKey = getPublicKeyFromX509("RSA", new ByteArrayInputStream(publicKey.getBytes()));

			java.security.Signature signature = java.security.Signature.getInstance(SIGN_SHA256RSA_ALGORITHMS);

			signature.initVerify(pubKey);

			if (StringUtils.isEmpty(charset)) {
				signature.update(content.getBytes());
			} else {
				signature.update(content.getBytes(charset));
			}

			return signature.verify(Base64.decodeBase64(sign.getBytes()));
		} catch (Exception e) {
			throw new Exception("RSAcontent = " + content + ",sign=" + sign + ",charset = " + charset, e);
		}
	}

	/**
	 * 获取私钥
	 * 
	 * @param algorithm
	 * @param ins
	 * @return
	 * @throws Exception
	 */
	public static PrivateKey getPrivateKeyFromPKCS8(String algorithm, InputStream ins) throws Exception {
		if (ins == null || StringUtils.isEmpty(algorithm)) {
			return null;
		}

		KeyFactory keyFactory = KeyFactory.getInstance(algorithm);

		byte[] encodedKey = StreamUtil.readText(ins).getBytes();

		encodedKey = Base64.decodeBase64(encodedKey);

		return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(encodedKey));
	}

	/**
	 * 获取公钥
	 * 
	 * @param algorithm
	 * @param ins
	 * @return
	 * @throws Exception
	 */
	public static PublicKey getPublicKeyFromX509(String algorithm, InputStream ins) throws Exception {
		KeyFactory keyFactory = KeyFactory.getInstance(algorithm);

		StringWriter writer = new StringWriter();
		StreamUtil.io(new InputStreamReader(ins), writer);

		byte[] encodedKey = writer.toString().getBytes();

		encodedKey = Base64.decodeBase64(encodedKey);

		return keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
	}
}
