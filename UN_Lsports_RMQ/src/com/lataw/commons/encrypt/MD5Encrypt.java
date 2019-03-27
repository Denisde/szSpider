package com.lataw.commons.encrypt;

import java.security.MessageDigest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MD5Encrypt extends Algorithm
{
	private Log log = LogFactory.getLog(MD5Encrypt.class);

	/* This array is used to convert from bytes to hexadecimal numbers */
	private final static String[] hexDigits =
	{ "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d",
			"e", "f" };

	public String decrypting(String source, String key)
	{
		return null;
	}

	public String encrypting(String source, String key)
	{
		try
		{
			MessageDigest md = MessageDigest.getInstance("MD5");
			return byteArrayToHexString(md.digest(source.getBytes()));
		}
		catch (Exception e)
		{
			log.error("" + e.getMessage());
		}
		return null;
	}

	public static String byteArrayToHexString(byte[] b)
	{
		StringBuffer resultSb = new StringBuffer();
		for (int i = 0; i < b.length; i++)
		{
			resultSb.append(byteToHexString(b[i]));
		}
		return resultSb.toString();
	}

	private static String byteToHexString(byte b)
	{
		int n = b;
		if (n < 0)
		{
			n = 256 + n;
		}
		int d1 = n / 16;
		int d2 = n % 16;
		return hexDigits[d1] + hexDigits[d2];
	}

}
