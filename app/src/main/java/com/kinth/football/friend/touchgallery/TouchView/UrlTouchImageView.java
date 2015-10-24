/*
 Copyright (c) 2012 Roman Truba

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial
 portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
 THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.kinth.football.friend.touchgallery.TouchView;

import java.io.File;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.kinth.football.R;
import com.kinth.football.config.JConstants;
import com.kinth.football.util.Md5Util;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class UrlTouchImageView extends RelativeLayout {
    protected ProgressBar mProgressBar;
    protected TouchImageView mImageView;

    protected Context mContext;

    public UrlTouchImageView(Context ctx)
    {
        super(ctx);
        mContext = ctx;
        init();
    }
    public UrlTouchImageView(Context ctx, AttributeSet attrs)
    {
        super(ctx, attrs);
        mContext = ctx;
        init();
    }
    public TouchImageView getImageView() { return mImageView; }

    protected void init() {
        mImageView = new TouchImageView(mContext);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mImageView.setLayoutParams(params);
        this.addView(mImageView);
//        mImageView.setVisibility(GONE);

//        mProgressBar = new ProgressBar(mContext, null, android.R.attr.progressBarStyleHorizontal);
//        params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
//        params.addRule(RelativeLayout.CENTER_VERTICAL);
//        params.setMargins(30, 0, 30, 0);
//        mProgressBar.setLayoutParams(params);
//        mProgressBar.setIndeterminate(false);
//        mProgressBar.setMax(100);
//        this.addView(mProgressBar);
    }

    public void setUrl(final String imageUrl)
    {
		final String md5Path = JConstants.IMAGE_PERSISTENT_CACHE + Md5Util.md5s(imageUrl);//取md5后的图片路径
		File imageFile = new File(md5Path);
		if(imageFile.exists()){//存在
			// 加载图片
			ImageLoader.getInstance().displayImage("file:///" + md5Path, mImageView,  new SimpleImageLoadingListener() {
				@Override
				public void onLoadingStarted(String imageUri, View view) {
					mImageView.setImageResource(R.drawable.image_download_loading_icon);
					super.onLoadingStarted(imageUri, view);
				}
			});
		}else{//本地不存在，去下载，然后显示
			HttpUtils http = new HttpUtils();
			HttpHandler<File> handler = http.download(imageUrl,md5Path,
			    true, // 如果目标文件存在，接着未完成的部分继续下载。服务器不支持RANGE时将从新下载。
			    false, // 如果从请求返回信息中获取到文件名，下载完成后自动重命名。
			    new RequestCallBack<File>() {

			        @Override
			        public void onStart() {
			        }

			        @Override
			        public void onLoading(long total, long current, boolean isUploading) {
			        }

			        @Override
			        public void onSuccess(ResponseInfo<File> responseInfo) {
//			            testTextView.setText("downloaded:" + responseInfo.result.getPath());
			        	ImageLoader.getInstance().displayImage("file:///" + md5Path, mImageView, new SimpleImageLoadingListener() {
							@Override
							public void onLoadingStarted(String imageUri, View view) {
								mImageView.setImageResource(R.drawable.image_download_loading_icon);
								super.onLoadingStarted(imageUri, view);
							}
						});
			        }

			        @Override
			        public void onFailure(HttpException error, String msg) {
			        	ImageLoader.getInstance().displayImage(imageUrl, mImageView, new SimpleImageLoadingListener() {
							@Override
							public void onLoadingStarted(String imageUri, View view) {
								mImageView.setImageResource(R.drawable.image_download_loading_icon);
								super.onLoadingStarted(imageUri, view);
							}
						});
			        }
			});
		}
		
//    	ImageLoader.getInstance().displayImage(imageUrl,
//				mImageView, new SimpleImageLoadingListener() {
//					@Override
//					public void onLoadingStarted(String imageUri, View view) {
//						mImageView.setImageResource(R.drawable.image_download_loading_icon);
//						super.onLoadingStarted(imageUri, view);
//					}
//				});
    }
    
    public void setScaleType(ScaleType scaleType) {
        mImageView.setScaleType(scaleType);
    }
    
    //No caching load
//    public class ImageLoadTask extends AsyncTask<String, Integer, Bitmap>
//    {
//        @Override
//        protected Bitmap doInBackground(String... strings) {
//            String url = strings[0];
//            Bitmap bm = null;
//            try {
//                URL aURL = new URL(url);
//                URLConnection conn = aURL.openConnection();
//                conn.connect();
//                InputStream is = conn.getInputStream();
//                int totalLen = conn.getContentLength();
//                InputStreamWrapper bis = new InputStreamWrapper(is, 8192, totalLen);
//                bis.setProgressListener(new InputStreamProgressListener()
//				{					
//					@Override
//					public void onProgress(float progressValue, long bytesLoaded,
//							long bytesTotal)
//					{
//						publishProgress((int)(progressValue * 100));
//					}
//				});
//                bm = BitmapFactory.decodeStream(bis);
//                bis.close();
//                is.close();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return bm;
//        }
//        
//        @Override
//        protected void onPostExecute(Bitmap bitmap) {
//        	if (bitmap == null) 
//        	{
//        		mImageView.setScaleType(ScaleType.CENTER);
//        		bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.no_photo);
//        		mImageView.setImageBitmap(bitmap);
//        	}
//        	else 
//        	{
//        		mImageView.setScaleType(ScaleType.MATRIX);
//	            mImageView.setImageBitmap(bitmap);
//        	}
//            mImageView.setVisibility(VISIBLE);
//            mProgressBar.setVisibility(GONE);
//        }
//
//		@Override
//		protected void onProgressUpdate(Integer... values)
//		{
//			mProgressBar.setProgress(values[0]);
//		}
//    }
}
