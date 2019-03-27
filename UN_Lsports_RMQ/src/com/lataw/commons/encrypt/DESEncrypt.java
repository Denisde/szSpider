package com.lataw.commons.encrypt;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * DES encrypt implement
 * 
 * @author RICHARD
 * 
 */
public class DESEncrypt extends Algorithm
{
	/** log * */
	Log log = LogFactory.getLog(DESEncrypt.class);

	protected DESEncrypt()
	{

	}

	/**
	 * Use DES algorithm specified key decryption string
	 * 
	 * @param source
	 *            to decryption a string
	 * @param key
	 *            specified key
	 * @return
	 */
	public String decrypting(String source, String key)
	{
		log.debug("Begin decryption, source: " + source + " key: " + key);
		try
		{
			BASE64Decoder base64De = new BASE64Decoder();
			byte[] byteMi = base64De.decodeBuffer(source);
			Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
			IvParameterSpec iv = new IvParameterSpec(Constant._IV
					.getBytes("UTF-8"));
			cipher.init(Cipher.DECRYPT_MODE, getKey(key), iv);
			byte[] byteFina = cipher.doFinal(byteMi);
			log.debug("Successed decryption, source: " + source + " key: "
					+ key);
			return new String(byteFina, "UTF8");
		}
		catch (Exception e)
		{
			log.error("Error in decryption. source: " + source + ", key: "
					+ key);
		}
		return null;
	}

	/**
	 * Use DES algorithm specified key encryption string
	 * 
	 * @param source
	 *            to encrypt a string
	 * @param key
	 *            specified key
	 * @return
	 */
	public String encrypting(String source, String key)
	{
		log.debug("Begin encryption, source: " + source + " key: " + key);
		try
		{
			BASE64Encoder base64en = new BASE64Encoder();
			byte[] byteMi = source.getBytes("UTF8");
			Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
			IvParameterSpec iv = new IvParameterSpec(Constant._IV
					.getBytes("UTF-8"));
			cipher.init(Cipher.ENCRYPT_MODE, getKey(key), iv);
			byte[] byteFina = cipher.doFinal(byteMi);
			log.debug("Successed encryption, source: " + source + " key: "
					+ key);
			return base64en.encode(byteFina);
		}
		catch (Exception e)
		{
			log.error("Error in decryption. source: " + source + ", key: "
					+ key);
		}
		return null;
	}

	/**
	 * produce DES key
	 * 
	 * @param strKey
	 * @return key
	 */
	public Key getKey(String key)
	{
		SecretKey secretKey = null;
		try
		{
			DESKeySpec desKeySpec = new DESKeySpec(key.getBytes("UTF-8"));
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			secretKey = keyFactory.generateSecret(desKeySpec);
		}
		catch (Exception e)
		{
			log.error("Error in get key. key: " + key);
		}
		return secretKey;
	}
}