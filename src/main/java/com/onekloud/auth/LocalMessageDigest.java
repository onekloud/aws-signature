package com.onekloud.auth;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
/**
 * Thread caching MessageDigest
 * 
 * @author uriel
 */
public class LocalMessageDigest extends ThreadLocal<MessageDigest> {
	String algo;

	public LocalMessageDigest(String algo) {
		this.algo = algo;
	}

	protected MessageDigest initialValue() {
		try {
			return MessageDigest.getInstance(algo);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Fail creating " + algo + " Digester");
		}
	}
}