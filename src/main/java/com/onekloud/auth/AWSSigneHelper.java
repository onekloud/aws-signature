package com.onekloud.auth;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

/**
 * AWS basic helper.
 * 
 * contains the most important utils function to process AWS signature calls.
 * 
 * @author uriel
 *
 */
public class AWSSigneHelper {
	private final static String HMAC_SHA256_ALGORITHM = "HmacSHA256";
	private final static Base64 base64 = new Base64(512, new byte[0]);
	/**
	 * get a SimpleDateFormat for AWS datetime
	 */
	public final static ThreadLocal<SimpleDateFormat> AWSDateTime = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
			sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
			return sdf;
		}
	};

	/**
	 * get a SimpleDateFormat for AWS date
	 */
	public final static ThreadLocal<SimpleDateFormat> AWSDate = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
			return sdf;
		}
	};

	/**
	 * lower case Hex Charset
	 */
	static char[] hexa = "0123456789abcdef".toCharArray();

	/**
	 * return bytes array as Hexa string long as 2x bytes array size
	 */
	public static String encodeHex(byte[] bytes) {
		char[] result = new char[bytes.length * 2];
		for (int i = 0; i < bytes.length; i++) {
			result[i * 2] = hexa[(bytes[i] & 0xF0) >> 4];
			result[i * 2 + 1] = hexa[bytes[i] & 0x0F];
		}
		return new String(result);
	}

	/**
	 * return base64 encoded String
	 */
	public static String encodeBase64(byte[] bytes) {
		return base64.encodeToString(bytes);
	}

	/**
	 * return bytes array for an Hexa string
	 */
	public static byte[] decodeHex(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
		}
		return data;
	}

	public static byte[] decodeBase64(String s) {
		return Base64.decodeBase64(s);
	}

	/**
	 * For debug purpose this interface allow to use a fake Date in signature
	 * process
	 */
	private static Date dt = null;

	public static Date getDate() {
		if (dt != null)
			return dt;
		return new Date();
	}

	public static void setDebugTime(String time) {
		if (time == null) {
			dt = null;
			return;
		}
		if (time.length() == 16) {
			try {
				dt = AWSDateTime.get().parse(time);
			} catch (ParseException e) {
			}
		}
	}

	public final static Charset utf8 = Charset.forName("UTF8");
	private final static ThreadLocal<MessageDigest> SHA256_DIGEST = new LocalMessageDigest("SHA-256");
	private final static ThreadLocal<MessageDigest> SHA1_DIGEST = new LocalMessageDigest("SHA-1");

	/**
	 * Return a 64 char long SHA256 String
	 */
	public static String digestSha256(byte[] data) {
		MessageDigest sha256 = SHA256_DIGEST.get();
		sha256.reset();
		byte[] digest = sha256.digest(data);
		return encodeHex(digest);
	}

	/**
	 * Return a 64 char long SHA1 String
	 */
	public static String digestSha1(byte[] data) {
		MessageDigest sha256 = SHA1_DIGEST.get();
		sha256.reset();
		byte[] digest = sha256.digest(data);
		return encodeHex(digest);
	}

	/**
	 * Return a 64 char long SHA256 String
	 */
	public static String digestSha256(String data) {
		return digestSha256(data.getBytes(utf8));
	}

	/**
	 * Return a 64 char long SHA1 String
	 */
	public static String digestSha1(String data) {
		return digestSha1(data.getBytes(utf8));
	}

	/**
	 * get the signature key for the aws request
	 * @param data: date, region, service, "aws4_request"
	 */
	public static byte[] getAWS4SignatureKey(String secretKey, String... data) {
		try {
			byte[] kSecret = ("AWS4".concat(secretKey)).getBytes("UTF8");
			for (String X : data) {
				kSecret = HmacSHA256(X, kSecret);
			}
			return kSecret;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static byte[] HmacSHA256(String data, byte[] key) {
		try {
			SecretKeySpec signingKey = new SecretKeySpec(key, HMAC_SHA256_ALGORITHM);
			Mac mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
			mac.init(signingKey);
			return mac.doFinal(data.getBytes(utf8));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static byte[] HmacSHA1(String data, byte[] key) {
		try {
			String algorithm = "HmacSHA1";
			Mac mac = Mac.getInstance(algorithm);
			mac.init(new SecretKeySpec(key, algorithm));
			return mac.doFinal(data.getBytes(utf8));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
