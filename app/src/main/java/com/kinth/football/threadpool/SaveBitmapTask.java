package com.kinth.football.threadpool;

import java.io.File;

import com.kinth.football.util.PhotoUtil;

import android.graphics.Bitmap;
import android.util.Log;

/**
 * 线程池保存图片到本地
 * @author Sola
 *
 */
public class SaveBitmapTask extends ParentTask{
	
	private String path;
	private Bitmap bitmap;

	public SaveBitmapTask(String key, String path, Bitmap bitmap) {
		super(key);
		this.path = path;
		this.bitmap = bitmap;
	}

	@Override
	public void run() {
		if(bitmap == null)
			return;
		PhotoUtil.saveBitmap(path, bitmap, true);//保存到本地
		int position = path.lastIndexOf(".temp");
		if(position == -1){
			return;
		}
		String realName = path.substring(0, position);
		File file = new File(path);
		File realNameFile = new File(realName);
		synchronized (file) {
			if(file.exists()){
				file.renameTo(realNameFile);
			}
		}
	}

}
