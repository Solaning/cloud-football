package com.kinth.football.config;

import android.os.Environment;

public class JConstants {
	
	public static final String ACTION_FINISH_MAIN = "ACTION_FINISH_MAIN";//结束MainActivity
	
	public static final int REQUESTCODE_UPLOADAVATAR_CAMERA = 1;
	public static final int REQUESTCODE_UPLOADAVATAR_LOCATION = 2;
	public static final int REQUESTCODE_UPLOADAVATAR_CROP = 3;
	
	public static final int REQUESTCODE_TAKE_CAMERA = 0x000001;
	public static final int REQUESTCODE_TAKE_LOCAL = 0x000002;
	public static final int REQUESTCODE_TAKE_LOCATION = 0x000003;
	public static final String EXTRA_STRING = "extra_string";
	
	public static final String ACTION_REGISTER_SUCCESS_FINISH ="register.success.finish";
	// SD卡目录
	public static final String SDCARD_DIRECTORY = Environment.getExternalStorageDirectory().getPath();
	//程序目录
	public static final String PROGRAM_DIRECTORY = SDCARD_DIRECTORY + "/Kinth/Football";
	//图片缓存目录名称 /Kinth/Football/Image
	public static final String IMAGE_DIRECTORY = PROGRAM_DIRECTORY + "/Image";
	//图片缓存目录，每次上传完需要删除 /Kinth/Football/Image/Cache
	public static final String IMAGE_CACHE = IMAGE_DIRECTORY + "/Cache";
	//图片缓存目录，不清空，持久缓存 /Kinth/Pic/  原来 Config.PIC_DIR
	public static final String IMAGE_PERSISTENT_CACHE = Config.PIC_DIR;//跟聊天阵容那边的图片缓存整合
	
	
	//用于Intent中传输json数据
	public static final String INTENT_JSON_STRING = "INTENT_JSON_STRING";
	
	//最终需要放到intent中跳转的activity
	public static final String INTENT_TARGET_CLASS_NAME = "INTENT_TARGET_CLASS_NAME";
	//来源的activity，用于后退时跳转
	public static final String INTENT_SOURCE_CLASS_NAME = "INTENT_SOURCE_CLASS_NAME";
}
