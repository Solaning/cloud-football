package com.kinth.football.singleton;

import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.kinth.football.CustomApplcation;
import com.kinth.football.R;

public class SingletonBitmapClass {
	private static SingletonBitmapClass instance = new SingletonBitmapClass();
	private Context context;
	private Bitmap bitmap;

	private SingletonBitmapClass() {
		context = CustomApplcation.getInstance();
		loadBcakgroundBitmap();
	}

	public static SingletonBitmapClass getInstance() {
		if (instance == null)
			instance = new SingletonBitmapClass();
		return instance;
	}
	
	public Bitmap getBackgroundBitmap() {
		if (bitmap == null) {
			loadBcakgroundBitmap();
		}
		return bitmap;
	}

	private void loadBcakgroundBitmap() {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.ARGB_8888;
		opt.inInputShareable = true;
		opt.inPurgeable = true;
		// 获取资源图片
		InputStream is = context.getResources().openRawResource(
				R.drawable.bg);
		bitmap = BitmapFactory.decodeStream(is, null, opt);
	}

	/**
	 * 资源回收利用
	 */
	public void recycle() {
		if (bitmap != null) {
			bitmap.recycle();
			bitmap = null;
		}
	}
}
