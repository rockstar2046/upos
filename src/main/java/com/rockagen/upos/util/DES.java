/*
 * %W% %E%
 *
 * Copyright (c) 2013, RIPPLETECH Inc. All rights reserved.
 * RIPPLETECH PROPRIETARY/CONFIDENTIAL
 */
package com.rockagen.upos.util;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

/**
 * DES algorithm (DES/CBC/NoPadding)
 * @author RA
 * @since JDK1.6
 */
public class DES {

	private static final String DES = "DES";
	private static final String DESIN = "DES/CBC/NoPadding";
	
	static {
		 Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());	}

	/**
	 * Encrypt
	 * 
	 * @param plain
	 *            plain bytes
	 * @param key
	 *            key bytes
	 * @return encrypted bytes
	 * @throws NoSuchPaddingException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 * @throws InvalidKeyException
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException
	 * @throws InvalidAlgorithmParameterException
	 */
	public static byte[] encrypt(byte[] plain, byte[] key)
			throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, InvalidKeySpecException,
			IllegalBlockSizeException, BadPaddingException,
			InvalidAlgorithmParameterException {
		Cipher cipher = Cipher.getInstance(DESIN);
		cipher.init(Cipher.ENCRYPT_MODE, getSecKey(key), new IvParameterSpec(
				new byte[8]));
		return cipher.doFinal(plain);
	}

	/**
	 * Decrypt
	 * 
	 * @param signature
	 *            signature bytes
	 * @param key
	 *            key bytes
	 * @return decrypted bytes
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 * @throws InvalidKeySpecException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws InvalidAlgorithmParameterException
	 */
	public static byte[] decrypt(byte[] signature, byte[] key)
			throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, InvalidKeySpecException,
			IllegalBlockSizeException, BadPaddingException,
			InvalidAlgorithmParameterException {
		Cipher cipher = Cipher.getInstance(DESIN);
		cipher.init(Cipher.DECRYPT_MODE, getSecKey(key), new IvParameterSpec(
				new byte[8]));
		return cipher.doFinal(signature);
	}

	/**
	 * 3des(dual length) encryption
	 * 
	 * @param plain
	 * @param key
	 *            must has 16 bytes
	 * @return 3des(dual length) encrypted bytes
	 * @throws InvalidAlgorithmParameterException
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException
	 * @throws InvalidKeySpecException
	 * @throws NoSuchPaddingException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 */
	public static byte[] dualEncrypt(byte[] plain, byte[] key)
			throws InvalidKeyException, NoSuchAlgorithmException,
			NoSuchPaddingException, InvalidKeySpecException,
			IllegalBlockSizeException, BadPaddingException,
			InvalidAlgorithmParameterException {
		if (key.length != 16) {
			return null;
		}
		byte[] lk = Arrays.copyOfRange(key, 0, 8);
		byte[] rk = Arrays.copyOfRange(key, 8, 16);

		byte[] enwlk = encrypt(plain, lk);

		byte[] dewrk = decrypt(enwlk, rk);

		byte[] retval = encrypt(dewrk, lk);
		return retval;

	}

	/**
	 * 3des(dual length) decryption
	 * 
	 * @param plain
	 * @param key
	 *            must has 16 bytes
	 * @return 3des(dual length) decrypted bytes
	 * @throws InvalidAlgorithmParameterException
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException
	 * @throws InvalidKeySpecException
	 * @throws NoSuchPaddingException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 * @throws Exception
	 */
	public static byte[] dualDecrypt(byte[] plain, byte[] key)
			throws InvalidKeyException, NoSuchAlgorithmException,
			NoSuchPaddingException, InvalidKeySpecException,
			IllegalBlockSizeException, BadPaddingException,
			InvalidAlgorithmParameterException {
		if (key.length != 16) {
			return null;
		}
		byte[] lk = Arrays.copyOfRange(key, 0, 8);
		byte[] rk = Arrays.copyOfRange(key, 8, 16);

		byte[] dewrk = decrypt(plain, lk);
		byte[] enwlk = encrypt(dewrk, rk);

		byte[] retval = decrypt(enwlk, lk);
		return retval;

	}

	/**
	 * Get SecretKey
	 * 
	 * @param key
	 *            key bytes
	 * @return SecretKey
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 */
	public static SecretKey getSecKey(byte[] key) throws InvalidKeyException,
			NoSuchAlgorithmException, InvalidKeySpecException {
		DESKeySpec dks = new DESKeySpec(key);
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
		SecretKey securekey = keyFactory.generateSecret(dks);
		return securekey;
	}

}
