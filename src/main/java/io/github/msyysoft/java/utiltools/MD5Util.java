package io.github.msyysoft.java.utiltools;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Title: MD5加密和校验的工具类<br>
 * Description: 通过该工具类可以对字符串进行16Bit和32Bit的MD5加密和校验<br>
 * Copyright (c) 2018 dyfc <br>
 * 
 * @version 1.0
 * @author 
 */
public class MD5Util
{
	private MD5Util()
	{
	}
	/**
	 * 校验未加密的字符串和经过16位加密后的字符串是否一致。
	 * 
	 * @param originalString，未MD5加密的数据
	 * @param encString，加密后的字符串
	 * @return boolean 如果未加密的字符串和经过16位MD5加密的字符串对应返回真否则返回假
	 * 
	 * @author: 
	 * @CreateTime:2012-8-2
	 * @ModifyTime: 2012-8-2
	 * */
	public static boolean validate16BitMd5WithOriginalString(
			String originalString, String encString)
	{
		String str = get16BitMd5EncString(originalString);
		if (str.equals(encString))
			return true;
		else
			return false;
	}

	/**
	 * 校验未加密的字符串和经过32位加密后的字符串是否一致。
	 * 
	 * @param originalString，未MD5加密的数据
	 * @param encString，加密后的字符串
	 * @return boolean 如果未加密的字符串和经过32位MD5加密的字符串对应返回真否则返回假
	 * 
	 * @author: 
	 * @CreateTime:2012-8-2
	 * @ModifyTime: 2012-8-2
	 * */
	public static boolean validate32BitMd5WithOriginalString(
			String originalString, String encString)
	{
		String str = get32BitMd5EncString(originalString);
		if (str.equals(encString))
			return true;
		else
			return false;
	}

	/**
	 * 对字符串进行16Bit的MD5加密
	 * 
	 * @param plainText，需要进行加密的字符串
	 * @return String 返回经过16位MD5加密后的字符串
	 * 
	 * @author: 
	 * @CreateTime:2012-8-2
	 * @ModifyTime: 2012-8-2
	 * */
	public static String get16BitMd5EncString(String plainText)
	{
		String result = null;
		try
		{
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(plainText.getBytes());
			byte b[] = md.digest();
			int i;
			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++)
			{
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			result = buf.toString().substring(8, 24);
		} catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 对字符串进行32Bit的MD5加密
	 * 
	 * @param plainText，需要进行加密的字符串
	 * @return String 返回经过32位MD5加密后的字符串
	 * 
	 * @author: 
	 * @CreateTime:2012-8-2
	 * @ModifyTime: 2012-8-2
	 * */
	public static String get32BitMd5EncString(String plainText)
	{
		String result = null;
		try
		{
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(plainText.getBytes());
			byte b[] = md.digest();
			int i;
			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++)
			{
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			result = buf.toString(); // md5 32bit
			// result = buf.toString().substring(8, 24))); //md5 16bit
			// result = buf.toString().substring(8, 24);
			// System.out.println("mdt 16bit: " + buf.toString().substring(8,
			// 24));
			// System.out.println("md5 32bit: " + buf.toString());
		} catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}
		return result;
	}
}
