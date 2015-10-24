package com.kinth.football.config;

import android.os.Environment;

/** 
  * @ClassName: Config
  * @Description: TODO
  * @author smile
  */
public class Config {
	
	public static String applicationId = "ade3cab4567089acacf29eeb3a8bcf74";
	public static final boolean DEBUG_MODE = false;
	
	public static final String VOICE_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Kinth/Football/Voice/";
	public static final String PIC_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Kinth/Football/Pic/";
	public static final String FORMATION_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Kinth/Football/Formation/";
	
	public static final String HOME_TEAM = "HOME_TEAM";
	public static final String AWAY_TEAM = "AWAY_TEAM";
	
	public static final int PAGE_SIZE = 10; //分页大小
	public static final int DEFAULT_NUM_OF_ACTIVE = 10;// 朋友圈默认的分页大小
	public static final String THUMBNAIL_POSTFIX = "Q03.jpg";//缩略图后缀
}
