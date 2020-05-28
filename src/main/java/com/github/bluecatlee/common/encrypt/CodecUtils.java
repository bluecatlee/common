package com.github.bluecatlee.common.encrypt;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CodecUtils {

	public static String encode(final String algorithm, final String str) throws NoSuchAlgorithmException {
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

	public static String md5(final String str) throws NoSuchAlgorithmException {
		return encode("MD5", str);
	}

	public static String sha1(final String str) throws NoSuchAlgorithmException {
		return encode("SHA1", str);
	}

}
