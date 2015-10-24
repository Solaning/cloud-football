package com.kinth.football.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;

import com.kinth.football.R;
import com.kinth.football.config.JConstants;
import com.kinth.football.listener.MomentImageLoadingListener;
import com.kinth.football.threadpool.SaveBitmapTask;
import com.kinth.football.threadpool.TaskManager;
import com.kinth.football.ui.ImageBrowserActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.core.download.ImageDownloader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.utils.IoUtils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore.Images.Media;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class PictureUtil {

	/**
	 * 把bitmap转换成String
	 * 
	 * @param filePath
	 * @return
	 */
	public static String bitmapToString(String filePath) {

		Bitmap bm = getSmallBitmap(filePath);
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.JPEG, 40, baos);
		byte[] b = baos.toByteArray();
		
		return Base64.encodeToString(b, Base64.DEFAULT);
		
	}

	/**
	 * 计算图片的缩放值
	 * 
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	@Deprecated
	public static int calculateInSampleSize_2(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			// Calculate ratios of height and width to requested height and
			// width
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);

			// Choose the smallest ratio as inSampleSize value, this will
			// guarantee
			// a final image with both dimensions larger than or equal to the
			// requested height and width.
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}

		return inSampleSize;
	}
	
	/**
	 * 根据路径获得图片并压缩返回bitmap用于显示
	 * 
	 * @param imagesrc
	 * @return
	 */
	public static Bitmap getSmallBitmap(String filePath) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize_2(options, 480, 800);
		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;

		return BitmapFactory.decodeFile(filePath, options);
	}

	/**
	 * 根据路径删除图片
	 * 
	 * @param path
	 */
	public static void deleteTempFile(String path) {
		File file = new File(path);
		if (file.exists()) {
			file.delete();
		}
	}

	/**
	 * 添加到图库
	 */
	public static void galleryAddPic(Context context, String path) {
		Intent mediaScanIntent = new Intent(
				Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		File f = new File(path);
		Uri contentUri = Uri.fromFile(f);
		mediaScanIntent.setData(contentUri);
		context.sendBroadcast(mediaScanIntent);
	}

	//指定宽，高按比例计算
	public static String getThumbUploadPath(String oldPath, int bitmapMaxWidth)
			throws Exception {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(oldPath, options);
		int height = options.outHeight;
		int width = options.outWidth;
		int reqHeight = 0;
		int reqWidth = bitmapMaxWidth;
		reqHeight = (reqWidth * height) / width;
		// 在内存中创建bitmap对象，这个对象按照缩放大小创建的
		options.inSampleSize = calculateInSampleSize(options, bitmapMaxWidth,
				reqHeight);
		options.inJustDecodeBounds = false;
		Bitmap bitmap = BitmapFactory.decodeFile(oldPath, options);
		Bitmap bbb = compressImage(Bitmap.createScaledBitmap(bitmap,
				bitmapMaxWidth, reqHeight, false));
		String timeStamp = DateUtil.getStringDate() + oldPath + Math.random()*1000;//当前时间戳 + 图片路径 + 1000内的随机数
		return saveImg(bbb, Md5Util.md5s(timeStamp));
	}
	
	private static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			if (width > height) {
				inSampleSize = Math.round((float) height / (float) reqHeight);
			} else {
				inSampleSize = Math.round((float) width / (float) reqWidth);
			}
		}
		return inSampleSize;
	}
	
	/**
	 * 压缩图片
	 * @param image
	 * @return
	 */
	private static Bitmap compressImage(Bitmap image) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while (baos.toByteArray().length / 1024 > 200) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
			options -= 10;// 每次都减少10
			baos.reset();// 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
		return bitmap;
	}
	
	/**
	 * @param b Bitmap
	 * @return 图片存储的位置
	 * @throws IOException
	 */
	public static String saveImg(Bitmap b, String name) throws IOException  {
		File mediaFile = new File(JConstants.IMAGE_CACHE + File.separator + name);
		if (mediaFile.exists()) {
			mediaFile.delete();
		}
		if (!new File(JConstants.IMAGE_CACHE).exists()) {
			new File(JConstants.IMAGE_CACHE).mkdirs();
		}
		mediaFile.createNewFile();
		FileOutputStream fos = new FileOutputStream(mediaFile);
		b.compress(Bitmap.CompressFormat.PNG, 100, fos);
		fos.flush();
		fos.close();
		b.recycle();
		b = null;
		System.gc();
		return mediaFile.getPath();
	}
	
	public static String getRealPath(ContentResolver cr, long image_id) {
		String[] projection = { Media._ID, Media.DATA };
		Cursor cursor = cr.query(Media.EXTERNAL_CONTENT_URI, projection,
				Media._ID + "=" + image_id, null, null);
		String realPath = null;
		try {
			if (cursor != null) {
				cursor.moveToFirst();
				realPath = cursor.getString(cursor.getColumnIndex(Media.DATA));
			}
		}catch(Exception e){
			
		}finally{
			if(cursor != null){
				cursor.close();
			}
		}
		return realPath;
	}
	
	public static void getMd5PathByUrl(final String url, final ImageView imageView){
		getMd5PathByUrl(url, imageView, null, null);
	}
	
	public static void getMd5PathByUrl(final String url, final ImageView imageView, final DisplayImageOptions options){
		getMd5PathByUrl(url, imageView, options, null);
	}
	
	public static void getMd5PathByUrl(final String url, final ImageView imageView, final ImageLoadingListener listener){
		getMd5PathByUrl(url, imageView, null, listener);
	}
	
	/**
	 * 把图片url转换为本地的md5路径
	 */
	public static void getMd5PathByUrl(final String url, final ImageView imageView, final DisplayImageOptions options, final ImageLoadingListener listener){
		if(TextUtils.isEmpty(url)){
			ImageLoader.getInstance().displayImage(null, imageView, options, listener); //先显示默认的图片
			return;
		}
		ImageLoader.getInstance().displayImage(url, imageView, options, listener); //先显示默认的图片
	}
	
	/**
	 * 查看大图
	 */
	public static void viewLargerImage(Context mContext,ArrayList<String> photos){
		Intent intent = new Intent(mContext, ImageBrowserActivity.class);
		intent.putStringArrayListExtra("photos", photos);
		intent.putExtra("position", 0);
		intent.putExtra("flag", 1);
		mContext.startActivity(intent);
	}
	
	/**
	 * 是否一个文件是否为图片
	 * @param path
	 * @return
	 */
	public static boolean isImage(String path){
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(path, options);
		if (options.outWidth != -1 && options.outHeight != -1) {
		    // This is an image file.
			return true;
		} else {
		    // This is not an image file.
			return false;
		}
	}

	public static void UILShowImage(Context mContext, ImageView imageView, String imageUrl) {
		UILShowImage(mContext, imageView, imageUrl, null, null, null);
	}
	
	public static void UILShowImage(Context mContext, ImageView imageView, String imageUrl, DisplayImageOptions options) {
		UILShowImage(mContext, imageView, imageUrl, options, null, null);
	}
	
	public static void UILShowImage(Context mContext, ImageView imageView, String imageUrl, ImageLoadingListener listener) {
		UILShowImage(mContext, imageView, imageUrl, null, listener, null);
	}
	
	public static void UILShowImage(Context mContext, ImageView imageView, String imageUrl, MomentImageLoadingListener momentListener) {
		UILShowImage(mContext, imageView, imageUrl, null, null, momentListener);
	}
	
	public static void UILShowImage(Context mContext, ImageView imageView, String imageUrl, DisplayImageOptions options, MomentImageLoadingListener momentListener) {
		UILShowImage(mContext, imageView, imageUrl, options, null, momentListener);
	}
	
	public static void UILShowImage(Context mContext, ImageView imageView, String imageUrl, DisplayImageOptions options, ImageLoadingListener listener){
		UILShowImage(mContext, imageView, imageUrl, options, listener, null);
	}
	
	public static void UILShowImage(Context mContext, ImageView imageView, String imageUrl, ImageLoadingListener listener, MomentImageLoadingListener momentListener){
		UILShowImage(mContext, imageView, imageUrl, null, listener, momentListener);
	}
	
	/**
	 * 通过url下载图片，图片保存到本地
	 * @param mContext
	 * @param imageUrl
	 * @param targetPath
	 * @throws IOException
	 */
	public static void UILShowImage(Context mContext, final ImageView imageView, String imageUrl, final DisplayImageOptions options, final ImageLoadingListener listener, final MomentImageLoadingListener momentListener) {
		final String md5Path = JConstants.IMAGE_PERSISTENT_CACHE  + Md5Util.md5s(StringUtils.defaultIfEmpty(imageUrl, ""));//取md5后的图片路径
		final File imageFile = new File(md5Path);
		if(imageFile.isFile() && imageFile.exists()){//存在
			// 加载本地图片
			if(momentListener == null){//为空代表是朋友圈多图
				ImageLoader.getInstance().displayImage("file:///" + md5Path, imageView, options, listener);
			}else{//回调不为空，是单图显示的回调
				momentListener.onMomentImageExit(md5Path);
			}
		} else {// 本地不存在，去下载，然后显示
	        ImageLoader.getInstance().loadImage(imageUrl, options,  new ImageLoadingListener() {
				
				@Override
				public void onLoadingStarted(String imageUri, View view) {
					imageView.setImageResource(R.drawable.image_download_loading_icon);
				}
				
				@Override
				public void onLoadingFailed(String imageUri, View view,
						FailReason failReason) {
					imageView.setImageResource(R.drawable.image_download_fail_icon);
				}
				
				@Override
				public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
					TaskManager.getInstance().addDownloadTask(new SaveBitmapTask(imageUri, md5Path + ".temp", loadedImage)); //保存到本地
					
					if(momentListener != null){//朋友圈里单图需要显示
						momentListener.onMomentImageDownloadComplete(md5Path, loadedImage);
					}else{//朋友圈按多图显示
//						ImageLoader.getInstance().displayImage("file:///" + md5Path, imageView, options, listener);
						imageView.setImageBitmap(loadedImage);
					}
				}
				
				@Override
				public void onLoadingCancelled(String imageUri, View view) {
				}
			});
		}
	}
	
	/**
	 * 第一种下载图片的方法
	 * @param mContext
	 * @param imageUrl
	 * @param targetPath
	 * @throws IOException
	 */
	public static void download1(Context mContext, String imageUrl, String targetPath, MomentImageLoadingListener momentListener) throws IOException{
		File fileForImage = new File(targetPath);

		InputStream sourceStream;
		File cachedImage = ImageLoader.getInstance().getDiskCache()
				.get(imageUrl);
		if (cachedImage != null && cachedImage.exists()) { // if image was cached by UIL
			sourceStream = new FileInputStream(cachedImage);
		} else { // otherwise - download image
			ImageDownloader downloader = new BaseImageDownloader(mContext);
			sourceStream = downloader.getStream(imageUrl, null);
		}

		if (sourceStream != null) {
			try {
				OutputStream targetStream = new FileOutputStream(fileForImage);
				try {
					IoUtils.copyStream(sourceStream, targetStream, null);
				} finally {
					targetStream.close();
				}
			} finally {
				sourceStream.close();
			}
		}
	}
}
