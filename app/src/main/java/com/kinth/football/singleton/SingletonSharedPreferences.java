package com.kinth.football.singleton;

import com.kinth.football.CustomApplcation;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * 
 * @author Sola
 *
 */
public class SingletonSharedPreferences {
	private static final String Version_Code = "VersionCode";// 版本号
	private static final String Screen_Width = "ScreenWidth";// 屏幕宽
	private static final String Screen_Height = "ScreenHeight";// 屏幕高
	private static final String First_Load = "FirstLoad";// 第一次运行

	private static SingletonSharedPreferences instance = new SingletonSharedPreferences();
	private Context context = null;
	private static SharedPreferences mainSharedPreferences;
	private static Editor mainEditor;

	public Editor getMainEditor() {
		return mainEditor;
	}

	private SingletonSharedPreferences() {
		context = CustomApplcation.getInstance();
		mainSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		mainEditor = mainSharedPreferences.edit();
	}

	public static SingletonSharedPreferences getInstance() {
		if (instance == null)
			instance = new SingletonSharedPreferences();
		return instance;
	}

	/**
	 * 获得屏幕的宽，并且写入到SharedPrefrenecs
	 */
	public int getScreenWidth() {
		int width = mainSharedPreferences.getInt(Screen_Width, 0);
		if (width != 0) {
			return width;
		}
		// 屏幕分辨率
		DisplayMetrics metric = new DisplayMetrics();
		WindowManager windowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		windowManager.getDefaultDisplay().getMetrics(metric);
		int screenWidth = metric.widthPixels; // 屏幕宽度（PX）
		mainEditor.putInt(Screen_Width, screenWidth).commit();
		return screenWidth;
	}

	/**
	 * 获得屏幕的高，并且写入到SharedPrefrenecs
	 */
	public int getScreenHeight() {
		int height = mainSharedPreferences.getInt(Screen_Height, 0);
		if (height != 0) {
			return height;
		}
		// 屏幕分辨率
		DisplayMetrics metric = new DisplayMetrics();
		WindowManager windowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		windowManager.getDefaultDisplay().getMetrics(metric);
		int screenHeight = metric.heightPixels; // 屏幕宽度（PX）
		mainEditor.putInt(Screen_Height, screenHeight).commit();
		return screenHeight;
	}

	/**
	 * 获取版本号 
	 */
	public int getVersionCode() {
		return mainSharedPreferences.getInt(Version_Code, 0);
	}

	/**
	 * 写入版本号
	 */
	public void setVersionCode(int value) {
		mainEditor.putInt(Version_Code, value).commit();
	}

	/**
	 * 判定是否第一次运行，默认返回true
	 */
	public boolean isFirstLoad() {
		return mainSharedPreferences.getBoolean(First_Load, true);
	}

	/**
	 * 设置非第一次
	 */
	public void setFirestLoad(boolean value) {
		mainEditor.putBoolean(First_Load, value).commit();
	}
	
}
