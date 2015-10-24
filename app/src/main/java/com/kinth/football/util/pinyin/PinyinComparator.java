package com.kinth.football.util.pinyin;

import java.util.Comparator;

public class PinyinComparator implements Comparator{

	@Override
	public int compare(Object o1, Object o2) {
		//可能会出错
		String str1 ;
		 String str2;
		try {
			 str1 = PingYinUtil.getPingYin((String) o1);
		} catch (Exception e) {
			str1 = "Z";
		}
		try {
			  str2 = PingYinUtil.getPingYin((String) o2);
		} catch (Exception e) {
			str2 = "Z";
		}
	   
	     return str1.compareTo(str2);
	}

}
