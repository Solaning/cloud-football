package com.kinth.football.chat.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 对字符串进行MD5算法加密
 * @author ChenFeng
 *
 */
public class MD5Util {

	public static String getMD5(String val) throws NoSuchAlgorithmException {
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		md5.update(val.getBytes());
		byte[] m = md5.digest();// 加密
		return getString(m);
	}

	private static String getString(byte[] b) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			sb.append(b[i]);
		}
		return sb.toString();
	}
}
