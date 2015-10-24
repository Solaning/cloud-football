package com.kinth.football.ui.team.formation;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.kinth.football.chat.util.FileUtil;

public class ScreenshotUtil {

	private Context context;

	public ScreenshotUtil(Context context) {
		this.context = context;
	}

	public Bitmap getScreenshot(int cutHeight, ScreenshotStateListener screenshotStateListener) {
		// 获取屏幕宽高
	    DisplayMetrics dm = new DisplayMetrics();
	    ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(dm);
	    int width = dm.widthPixels;    //得到宽度
	    int height = dm.heightPixels;  //得到高度
		// 构建Bitmap
		Bitmap bmp = null;
		try{
			bmp = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		}catch(OutOfMemoryError e){
			screenshotStateListener.screenshotFail();
		}

		// 获取屏幕
		View decorview = ((Activity) context).getWindow().getDecorView();
		decorview.setDrawingCacheEnabled(true);
		if (cutHeight == -1)
			bmp = cutBitmap(decorview.getDrawingCache(), 0, (int) (height / 9), width, height
					- (int) (height / 9), screenshotStateListener);
		else
			bmp = cutBitmap(decorview.getDrawingCache(), 0, cutHeight, width, height
					- cutHeight, screenshotStateListener);
		return bmp;
	}

	public void saveScreenshot(String dirPath, String fileName,
			final ScreenshotStateListener screenshotStateListener, int cutHeight) {
		try {
			// 判断文件目录是否存在，若不存在，则创建该目录
			File dir = new File(dirPath);
			if (!dir.exists()) {
				dir.mkdirs();
			}

			// 判断文件是否存在，若不存在，则创建新的文件
			String filepath = dirPath + fileName;
			Log.e("filepath", filepath);
			File file = new File(filepath);
			if (!file.exists()) {
				file.createNewFile();
			}

			FileOutputStream fos = null;
			fos = new FileOutputStream(file);
			if (null != fos) {
				Bitmap bmp = getScreenshot(cutHeight, screenshotStateListener);
				bmp.compress(Bitmap.CompressFormat.JPEG, 50, fos);
				fos.flush();
				fos.close();
				fos = null;

				screenshotStateListener.screenshotSuccess(filepath);

				if (bmp != null && !bmp.isRecycled())
					bmp.recycle();
				    bmp = null; 
			}

			FileUtil.isExitNoMedia(dirPath);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取SDCard的目录路径功能
	 * 
	 * @return
	 */
	private String getSDCardPath() {
		File sdcardDir = null;
		// 判断SDCard是否存在
		boolean sdcardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
		if (sdcardExist)
			sdcardDir = Environment.getExternalStorageDirectory();

		return sdcardDir.toString();
	}

	public Bitmap cutBitmap(Bitmap bitmap, int x, int y, int width, int height, ScreenshotStateListener screenshotStateListener) {
		Bitmap bmp = null;
		try {

			/**
			 * Bitmap.createBitmap会报内存溢出错误，所以要进行压缩处理
			 */
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
			int options = 10; // 每次用压缩的数值，尽量设比较小，这样每次压缩的跨度就比较小，更容易接近100kb
			while (baos.toByteArray().length / 1024 > 600) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
				baos.reset();// 重置baos即清空baos
				bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
				// options -= 20;// 每次都减少20
			}
			ByteArrayInputStream isBm = new ByteArrayInputStream(
					baos.toByteArray());
			
			BitmapFactory.Options newOpts = new BitmapFactory.Options();
			// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
			newOpts.inJustDecodeBounds = true;
			Bitmap compressedBitmap = BitmapFactory.decodeStream(isBm, null,
					newOpts);
			newOpts.inJustDecodeBounds = false;
			newOpts.inSampleSize = 1;// 设置缩放比例
			// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
			isBm = new ByteArrayInputStream(baos.toByteArray());
			compressedBitmap = BitmapFactory.decodeStream(isBm, null, newOpts);

			bmp = Bitmap.createBitmap(compressedBitmap, x, y, width, height);
		} catch (OutOfMemoryError e) {
//			Toast.makeText(context, "内存溢出", Toast.LENGTH_LONG).show();
			System.gc();
			screenshotStateListener.screenshotFail();
		} catch (Exception e){
			screenshotStateListener.screenshotFail();
		}
		return bmp;
	}

	public interface ScreenshotStateListener {
		public void screenshotSuccess(String filepath);
		public void screenshotFail();
	}
}
