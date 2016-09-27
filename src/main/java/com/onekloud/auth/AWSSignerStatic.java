//package com.onekloud.auth;
//
///**
// * AWSSigner object with a local stored AWS secret
// * 
// * @author uriel
// *
// */
//public class AWSSignerStatic implements AWSRemoteSigner {
//	private String secret;
//
//	public AWSSignerStatic(String secret) {
//		this.secret = secret;
//	}
//
//	@Override
//	/**
//	 * usage like:
//	 * 
//	 * this.date = AWSUtils.AWSDate.get().format(now); // yyyyMMdd
//	 * 
//	 * String signingKey = MakeSigne(signing, date, "us-east-1", endpointPrefix, "aws4_request");
//	 * 
//	 * @param singingString
//	 *            like
//	 *            "AWS4-HMAC-SHA256\n20160919T184625Z\n20160919/us-east-1/es/aws4_request\n1dcf0ceb95b5088d6bbce7b94908dcb1d4c7478f6414fa2604002ef75b5708d1"
//	 * @param data
//	 *            like [20160919, us-east-1, es, aws4_request]
//	 * @return data a byte[32] signature block
//	 */
//	public byte[] makeSigne(String singingString, String... data) {
//		if (data.length < 3) {
//			throw new RuntimeException("invalid usage");
//		}
//		if (!"aws4_request".equals(data[data.length])) {
//			throw new RuntimeException("data should end with aws4_request");
//		}
//		if (!singingString.startsWith("AWS4-HMAC-SHA256")) {
//			throw new RuntimeException("singingString should start with AWS4-HMAC-SHA256");
//		}
//		byte[] kSigning = AWSSigneHelper.getAWS4SignatureKey(secret, data);
//		byte[] bytes = AWSSigneHelper.HmacSHA256(singingString, kSigning);
//		return bytes;
//	}
//}
