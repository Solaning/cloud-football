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

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.ImageView.ScaleType;

import com.kinth.football.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class FileTouchImageView extends RelativeLayout {
	protected TouchImageView mImageView;

	protected Context mContext;

	public FileTouchImageView(Context ctx) {
		super(ctx);
		mContext = ctx;
		init();
	}

	public FileTouchImageView(Context ctx, AttributeSet attrs) {
		super(ctx, attrs);
		mContext = ctx;
		init();
	}

	protected void init() {
		mImageView = new TouchImageView(mContext);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		mImageView.setLayoutParams(params);
		this.addView(mImageView);
	}

	public TouchImageView getImageView() {
		return mImageView;
	}

	public void setScaleType(ScaleType scaleType) {
		mImageView.setScaleType(scaleType);
	}

	public void setUrl(String imagePath) {
		mImageView.setVisibility(VISIBLE);
		ImageLoader.getInstance().displayImage("file:///" + imagePath,
				mImageView, new SimpleImageLoadingListener() {
					@Override
					public void onLoadingStarted(String imageUri, View view) {
						mImageView.setImageResource(R.drawable.image_download_loading_icon);
						super.onLoadingStarted(imageUri, view);
					}
				});
	}
}
