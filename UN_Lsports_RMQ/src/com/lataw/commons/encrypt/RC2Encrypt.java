/**
 * 
 */
package com.lataw.commons.encrypt;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import sun.misc.BASE64Decoder;

/**
 * @author RICHARD
 * 
 */
public class RC2Encrypt extends Algorithm
{
	/** log * */
	Log log = LogFactory.getLog(RC2Encrypt.class);

	public String decrypting(String source, String key)
	{
		log.debug("Begin decryption, source: " + source + " key: " + key);
		try
		{
			BASE64Decoder base64De = new BASE64Decoder();
			byte[] byteMi = base64De.decodeBuffer(source);
			Cipher cipher = Cipher.getInstance("RC2/CBC/PKCS5Padding");
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
					+ key + " error reason: " + e.getMessage());
		}
		return null;
	}

	public String encrypting(String source, String key)
	{
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
			// RC2KeySpec desKeySpec = new RC2KeySpec(key.getBytes("UTF-8"));
			if (false)
			{
				SecretKeyFactory keyFactory = SecretKeyFactory
						.getInstance("RC2/CBC/PKCS5Padding");
			}
			secretKey = new SecretKeySpec(key.getBytes("UTF-8"), "RC2");// keyFactory.generateSecret(desKeySpec);
		}
		catch (Exception e)
		{
			log.error("Error in get key. key: " + key + " Error reason: "
					+ e.getMessage());
		}
		return secretKey;
	}
}
