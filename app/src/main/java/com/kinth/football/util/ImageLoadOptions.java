package com.kinth.football.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.DisplayImageOptions.Builder;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

public class ImageLoadOptions {

	/** 新闻列表中用到的图片加载配置 */
	public static DisplayImageOptions getOptions() {
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				// // 设置图片在下载期间显示的图片
				// .showImageOnLoading(R.drawable.small_image_holder_listpage)
				// // 设置图片Uri为空或是错误的时候显示的图片
				// .showImageForEmptyUri(R.drawable.small_image_holder_listpage)
				// // 设置图片加载/解码过程中错误时候显示的图片
				// .showImageOnFail(R.drawable.small_image_holder_listpage)
				.cacheInMemory(true)
				// 设置下载的图片是否缓存在内存中
				.cacheOnDisk(true)
				// 设置下载的图片是否缓存在SD卡中
				.considerExifParams(true)
				.imageScaleType(ImageScaleType.EXACTLY)// 设置图片以如何的编码方式显示
				.bitmapConfig(Bitmap.Config.RGB_565)// 设置图片的解码类型
				// .decodingOptions(android.graphics.BitmapFactory.Options
				// decodingOptions)//设置图片的解码配置
				.considerExifParams(true)
				// 设置图片下载前的延迟
				// .delayBeforeLoading(int delayInMillis)//int
				// delayInMillis为你设置的延迟时间
				// 设置图片加入缓存前，对bitmap进行设置
				// 。preProcessor(BitmapProcessor preProcessor)
				.resetViewBeforeLoading(true)// 设置图片在下载前是否重置，复位
				// .displayer(new RoundedBitmapDisplayer(20))//是否设置为圆角，弧度为多少
				.displayer(new FadeInBitmapDisplayer(100))// 淡入
				.build();
		
		return options;
	}

	public static Options decodingOptions(){
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		options.inInputShareable = true;
		options.inPurgeable = true;
		return options;
	}
	
	public static Builder getDisplayDrawableBuilder() {
		Builder builder = new DisplayImageOptions.Builder()
				.cacheInMemory(true)
				.cacheOnDisk(true)
				.imageScaleType(ImageScaleType.EXACTLY)
				// 设置图片以如何的编码方式显示
				.bitmapConfig(Bitmap.Config.RGB_565)
				// 设置图片的解码类型
				.decodingOptions(ImageLoadOptions.decodingOptions())
		// 设置图片加入缓存前，对bitmap进行设置
		// .preProcessor(BitmapProcessor preProcessor)
		;
		return builder;
	}
}
