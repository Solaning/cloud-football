package com.kinth.football.ui;

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher.OnPhotoTapListener;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.kinth.football.R;
import com.kinth.football.chat.DownloadManager;
import com.kinth.football.chat.listener.DownloadListener;
import com.kinth.football.chat.util.MD5Util;
import com.kinth.football.config.Config;
import com.kinth.football.view.CustomViewPager;
import com.nostra13.universalimageloader.core.ImageLoader;

import eu.inmite.android.lib.dialogs.IListDialogListener;
import eu.inmite.android.lib.dialogs.ListDialogFragment;

/**
 * 图片浏览
 * 
 * @ClassName: ImageBrowserActivity
 * @Description: TODO
 * @author smile
 * @date 2014-6-19 下午8:22:49
 */
public class ImageBrowserActivity extends ActivityBase implements
		OnPageChangeListener, IListDialogListener {

	private CustomViewPager mSvpPager;
	private ImageBrowserAdapter mAdapter;
	LinearLayout layout_image;
	private int mPosition;
	private String imgUrl;
	private String localFilePath;
	private ProgressBar progress;
	private PhotoView photoView;

	private ArrayList<String> mPhotos;

	private int flag;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_showpicture);
		initTopBarForLeft("查看大图");
		init();
		initViews();
	}

	private void init() {
		mPhotos = getIntent().getStringArrayListExtra("photos");
		mPosition = getIntent().getIntExtra("position", 0);
		flag = getIntent().getIntExtra("flag", 0); // 判断从哪里来 1--其它，0--聊天
		setTheme(R.style.DefaultLightTheme);
	}

	protected void initViews() {
		mSvpPager = (CustomViewPager) findViewById(R.id.pagerview);
		mAdapter = new ImageBrowserAdapter(this);
		mSvpPager.setAdapter(mAdapter);
		mSvpPager.setCurrentItem(mPosition, false);
		mSvpPager.setOnPageChangeListener(this);
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int arg0) {
		mPosition = arg0;
	}

	private class ImageBrowserAdapter extends PagerAdapter {

		private LayoutInflater inflater;

		public ImageBrowserAdapter(Context context) {
			this.inflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mPhotos.size();
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			// TODO Auto-generated method stub
			return view == object;
		}

		@Override
		public View instantiateItem(ViewGroup container, int position) {

			View imageLayout = inflater.inflate(R.layout.item_show_picture,
					container, false);
			photoView = (PhotoView) imageLayout.findViewById(R.id.photoview);
			photoView.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(View v) {
					// TODO 自动生成的方法存根
					ListDialogFragment
							.createBuilder(ImageBrowserActivity.this,
									getSupportFragmentManager())
							.setItems(new String[] { "共享到" })
							.hideDefaultButton(true).show();
					return false;
				}
			});
			photoView.setOnPhotoTapListener(new OnPhotoTapListener() {
				
				@Override
				public void onPhotoTap(View arg0, float arg1, float arg2) {
					finish();
				}
			});
			progress = (ProgressBar) imageLayout.findViewById(R.id.progress);

			imgUrl = mPhotos.get(position);
			if (imgUrl.startsWith("http")) {
				try {
					localFilePath = Config.PIC_DIR
							+ MD5Util.getMD5(imgUrl.trim())+".jpg";
					if (new File(localFilePath).exists()){
						ImageLoader.getInstance().displayImage(
								"file:///" + localFilePath, photoView);
					}else{
						ImageLoader.getInstance().displayImage(imgUrl.trim(),
								photoView);
						downloadImage();
					}
				} catch (NoSuchAlgorithmException e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				}
			} else {
				localFilePath = imgUrl.replace("file:///", "");
				ImageLoader.getInstance().displayImage(imgUrl, photoView);
			}

			container.addView(imageLayout, 0);
			return imageLayout;
		}

		private void downloadImage() {
			// TODO 自动生成的方法存根
			DownloadManager downloadTask = new DownloadManager(
					new DownloadListener() {

						@Override
						public void onStart() {
							// TODO Auto-generated method stub
							progress.setVisibility(View.VISIBLE);
						}

						@Override
						public void onSuccess() {
							// TODO Auto-generated method stub
							progress.setVisibility(View.GONE);
						}

						@Override
						public void onError(String error) {
							// TODO Auto-generated method stub
							progress.setVisibility(View.GONE);
						}
					});
			downloadTask.execute(imgUrl, localFilePath);
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

	}

	@Override
	public void onListItemSelected(String value, int number) {
		// TODO 自动生成的方法存根
		if (number == 0)
			share();
	}

	private void share() {
		Intent intent = new Intent(Intent.ACTION_SEND);
		File f = new File(localFilePath);
		if (f != null && f.exists() && f.isFile()) {
			intent.setType("image/*");
			Uri u = Uri.fromFile(f);
			intent.putExtra(Intent.EXTRA_STREAM, u);
		}
		intent.putExtra(Intent.EXTRA_SUBJECT, "图片分享");
		intent.putExtra(Intent.EXTRA_TEXT, "我觉得这张图片真不错,推荐给你哦~");
		intent.putExtra("picLocalPath", imgUrl);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(Intent.createChooser(intent, "图片分享"));
	}

	@Override
	public void onBackPressed() {
		// TODO 自动生成的方法存根
		super.onBackPressed();
		finish();
	}
	
}
