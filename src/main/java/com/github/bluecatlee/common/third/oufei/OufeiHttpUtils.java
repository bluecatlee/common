package com.github.bluecatlee.common.third.oufei;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import okhttp3.OkHttpClient.Builder;

import javax.net.ssl.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.Arrays;

public class OufeiHttpUtils {

	public static final MediaType DEFAULT_MEDIATYPE = MediaType.parse("text/plain");

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	private static OkHttpClient newHttpClient(final boolean isSSL, File file, final String password) {
		try {
			final Builder builder = new Builder();

			if (isSSL) {

				KeyStore keyStore = KeyStore.getInstance("PKCS12");
				FileInputStream instream = new FileInputStream(file);
				keyStore.load(instream, password.toCharArray());
				instream.close();

				KeyManagerFactory keyManagerFactory = KeyManagerFactory
						.getInstance(KeyManagerFactory.getDefaultAlgorithm());
				keyManagerFactory.init(keyStore, password.toCharArray());

				TrustManagerFactory trustManagerFactory = TrustManagerFactory
						.getInstance(TrustManagerFactory.getDefaultAlgorithm());
				trustManagerFactory.init((KeyStore) null);
				TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
				if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
					throw new IllegalStateException(
							"Unexpected default trust managers:" + Arrays.toString(trustManagers));
				}
				X509TrustManager trustManager = (X509TrustManager) trustManagers[0];

				SSLContext sslContext = SSLContext.getInstance("TLS");
				sslContext.init(keyManagerFactory.getKeyManagers(), new TrustManager[] { trustManager },
						new SecureRandom());

				return builder.sslSocketFactory(sslContext.getSocketFactory(), trustManager).build();
			} else {
				return builder.build();
			}

		} catch (Exception e) {
			throw new RuntimeException("newHttpClient", e);
		}

	}

	/**
	 * 请求数据
	 * 
	 * @param url
	 *            地址
	 * @param isSSL
	 *            是否加密
	 * @param file
	 *            密文
	 * @param password
	 *            密码
	 * @param bytes
	 *            内容
	 * @return
	 */
	private static String request(final String url, final boolean isSSL, File file, final String password,
			byte[] bytes) {
		
			// 跳过验证证书
		   /*X509TrustManager xtm = new X509TrustManager() {
	            @Override
	            public void checkClientTrusted(X509Certificate[] chain, String authType) {
	            }

	            @Override
	            public void checkServerTrusted(X509Certificate[] chain, String authType) {
	            }

	            @Override
	            public X509Certificate[] getAcceptedIssuers() {
	                X509Certificate[] x509Certificates = new X509Certificate[0];
	                return x509Certificates;
	            }
	        };

	        SSLContext sslContext = null;
	        try {
	            sslContext = SSLContext.getInstance("SSL");

	            sslContext.init(null, new TrustManager[]{xtm}, new SecureRandom());

	        } catch (NoSuchAlgorithmException e) {
	            e.printStackTrace();
	        } catch (KeyManagementException e) {
	            e.printStackTrace();
	        }
	        HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
	            @Override
	            public boolean verify(String hostname, SSLSession session) {
	                return true;
	            }
	        };
	        OkHttpClient client = new OkHttpClient.Builder()
	                //.addInterceptor(interceptor)
	                .sslSocketFactory(sslContext.getSocketFactory())
	                .hostnameVerifier(DO_NOT_VERIFY)
	                .build();*/
		
		
		OkHttpClient client = newHttpClient(isSSL, file, password);
		Request request = null;
		if (bytes != null) {
			request = new Request.Builder().url(url).post(RequestBody.create(MediaType.parse("application/soap+xml"), bytes))
					.build();
		} else {
			request = new Request.Builder().url(url).build();
		}
		Response response = null;
		try {
			response = client.newCall(request).execute();
			if (!response.isSuccessful()) {
				throw new RuntimeException("Recharge >> Unexpected code" + response);
			}
			return response.body().string();

		} catch (IOException e) {
			throw new RuntimeException("Recharge >> request" + response);
		} finally {
			if (response != null) {
				response.close();
			}
		}
	}

	/**
	 * post 请求
	 * 
	 * @param url
	 *            地址
	 * @param bytes
	 *            内容
	 * @param type
	 *            返回类型
	 * @return
	 */
	public static <E> E post(final String url, byte[] bytes, Class<E> type) {
		return post(url, false, null, null, bytes, type);
	}

	/**
	 * post 请求
	 * 
	 * @param url
	 *            地址
	 * @param isSSL
	 *            是否加密
	 * @param file
	 *            密文
	 * @param password
	 *            密码
	 * @param bytes
	 *            内容
	 * @param type
	 *            返回类型
	 * @return
	 */
	public static <E> E post(final String url, final boolean isSSL, File file, final String password, byte[] bytes,
			Class<E> type) {
		final String body = request(url, isSSL, file, password, bytes);
		if (type == String.class) {
			return (E) body;
		}
		if (body != null) {
            try {
                return OBJECT_MAPPER.readValue(body, type);
            } catch (IOException e) {
                // e.printStackTrace();
            }
        }
		return null;
	}

	/**
	 * get 请求
	 * 
	 * @param url
	 * @param type
	 * @return
	 */
	public static <E> E get(final String url, Class<E> type) {
		final String body = request(url, false, null, null, null);
		if (type == String.class) {
			return (E) body;
		}
		if (body != null) {
            try {
                return OBJECT_MAPPER.readValue(body, type);
            } catch (IOException e) {
                // e.printStackTrace();
            }
		}
		return null;

	}
}
