package com.kinth.football.ui;

import java.io.File;
import java.io.InputStream;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.ImageView;

import com.kinth.football.R;
import com.kinth.football.bean.User;
import com.kinth.football.config.JConstants;
import com.kinth.football.singleton.SingletonSharedPreferences;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

import com.kinth.football.util.FileUtil;
import com.kinth.football.util.PhotoUtil;
import com.kinth.football.util.UtilFunc;

/**
 * App 入口，有账号则进入，自动登录界面， 没有则进入注册界面
 * 
 * @author Sola
 */
public class EntryActivity extends BaseActivity {

	private boolean showGuide = false;// 是否显示引导页
	private int version;
	private Bitmap biamap1;
	private Bitmap biamap2;
	private ImageView ivSplash;
	private ImageView ivCaption;// 介绍标题

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Fabric.with(this, new Crashlytics());
		setContentView(R.layout.activity_splash);

		biamap1 = PhotoUtil.getDecodeBitmap(mContext, R.drawable.splash1);
		ivSplash = (ImageView) findViewById(R.id.iv_splash);
		ivSplash.setImageBitmap(biamap1);

		biamap2 = PhotoUtil.getDecodeBitmap(mContext, R.drawable.caption1);
		ivCaption = (ImageView) findViewById(R.id.iv_caption);
		ivCaption.setImageBitmap(biamap2);

		// 第一次运行 而且不是旧版本 比较版本号
		int preVersion = SingletonSharedPreferences.getInstance()
				.getVersionCode();// 上一个版本号
		version = UtilFunc.getCurrentVersion(mContext);
		if (version < 3) {// 版本小于3的删除图片缓存目录
			new Thread(new Runnable() {

				@Override
				public void run() {
					File file = new File(JConstants.IMAGE_PERSISTENT_CACHE);
					FileUtil.delete(file);
				}
			}).start();
		}
		if (preVersion == 0) {// 没有上一个的版本记录，说明当前是全新安装或者更早的版本
			showGuide = true;
		} else if (preVersion < version) {
			showGuide = true;
		}
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				if (SingletonSharedPreferences.getInstance().isFirstLoad()
						|| showGuide) {
					SingletonSharedPreferences.getInstance().setFirestLoad(
							false);
					SingletonSharedPreferences.getInstance().setVersionCode(
							version);
					// 显示引导页
					Intent intent = new Intent(EntryActivity.this,
							GuideActivity.class);
					startActivity(intent);
				} else {
					// 没有登录成功
					User user = footBallUserManager.getCurrentUser();
					if (user == null
							|| TextUtils.isEmpty(user.getToken())
							|| user.getPlayer() == null
							|| user.getPassword() == null
							|| TextUtils.isEmpty(user.getPlayer().getUuid())
							|| TextUtils.isEmpty(user.getPlayer()
									.getAccountName())) {
						// 已经注销，到firstload页面
						startActivity(new Intent(EntryActivity.this,
								LoginActivity.class));
					} else {
						// 用户不为空就登录
						Intent intent = new Intent(EntryActivity.this,
								MainActivity.class);
						startActivity(intent);
						overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
					}
				}
				EntryActivity.this.finish();
			}
		}, 2000);
	}

	@Override
	protected void onDestroy() {
		if (biamap1 != null && !biamap1.isRecycled()) {
			biamap1.recycle(); // 回收图片所占的内存
		}
		if (biamap2 != null && !biamap2.isRecycled()) {
			biamap2.recycle(); // 回收图片所占的内存
		}
		super.onDestroy();
	}

//	private void aa(){
//		DisplayMetrics metric = new DisplayMetrics();
//		getWindowManager().getDefaultDisplay().getMetrics(metric);
//		float density = metric.density; // 屏幕密度（0.75 / 1.0 / 1.5）
//		int densityDpi = metric.densityDpi; // 屏幕密度DPI（120 / 160 / 240）
//		Log.e("density", "density = " + density); // 3倍的密度
//		Log.e("densityDpi", "densityDpi = " + densityDpi);// 480dpi
//	}
}