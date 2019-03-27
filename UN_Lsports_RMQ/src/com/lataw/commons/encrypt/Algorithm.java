package com.lataw.commons.encrypt;

import org.apache.commons.lang.StringUtils;

/**
 * 
 * Algorithm
 * 
 * @author RICHARD
 * 
 */
public abstract class Algorithm
{
	/**
	 * Use encryption algorithm specified by key encryption string
	 * 
	 * @param source
	 *            to encrypt a string
	 * @return encrypt result
	 */
	public abstract String encrypting(String source, String key);

	/**
	 * Intended use of symmetric encryption algorithm, a random key decrypted
	 * string
	 * 
	 * @param source
	 *            to decrypt a string
	 * @return
	 */
	public abstract String decrypting(String source, String key);

	/**
	 * get instance of algorithm
	 * 
	 * @param encryptType
	 * @return
	 * @throws Exception
	 */
	public static Algorithm getInstance(String encryptType) throws Exception
	{
		if (!StringUtils.isNumeric(encryptType))
		{
			throw new Exception("Exception whil encrypt type.");
		}
		int type = Integer.valueOf(encryptType);
		Algorithm algorithm = null;
		switch (type)
		{
		case 1:
			algorithm = new DESEncrypt();
			break;
		case 2:
			algorithm = null;
			break;
		case 3:
			algorithm = null;
			break;
		case 4:
			algorithm = null;
			break;
		case 5:
			algorithm = null;
			break;
		case 6:
			algorithm = null;
			break;
		case 7:
			algorithm = null;
			break;
		case 8:
			algorithm = null;
			break;
		default:
			throw new Exception("Exception whil encrypt type.");
		}
		return algorithm;
	}
}
