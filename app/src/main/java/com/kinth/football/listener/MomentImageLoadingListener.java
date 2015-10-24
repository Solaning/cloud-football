package com.kinth.football.listener;

import android.graphics.Bitmap;

/**
 * 朋友圈加载图片时
 * @author Sola
 *
 */
public interface MomentImageLoadingListener {
	void onMomentImageExit(String path);//本地存在图片缓存
	void onMomentImageDownloadFailed();//朋友圈图片下载失败
	void onMomentImageDownloadComplete(String path, Bitmap loadedImage);//朋友圈图片下载完成
}
