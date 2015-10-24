package com.kinth.football.ui.mine;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.kinth.football.R;
import com.kinth.football.bean.User;
import com.kinth.football.chat.XmppManager;
import com.kinth.football.config.JConstants;
import com.kinth.football.manager.UserManager;
import com.kinth.football.network.NetWorkManager;
import com.kinth.football.singleton.SingletonBitmapClass;
import com.kinth.football.singleton.ViewCompat;
import com.kinth.football.ui.BaseActivity;
import com.kinth.football.ui.LoginActivity;
import com.kinth.football.util.CheckVersionUtil;
import com.kinth.football.util.QuitWay;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.ImageLoader;

import eu.inmite.android.lib.dialogs.ISimpleDialogListener;
import eu.inmite.android.lib.dialogs.SimpleDialogFragment;

@ContentView(R.layout.fragment_set)
public class SettingActivity extends BaseActivity implements ISimpleDialogListener{
	
	@ViewInject(R.id.setting_lin)
	private LinearLayout setting_lin;
	
	@ViewInject(R.id.current_version)  
	private TextView current_version;  //当前版本号
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		
		//将SettingActivity加入到QuitWay中的activity列表中
		QuitWay.activityList.add(SettingActivity.this);
		
		// 設置背景
		Bitmap background = SingletonBitmapClass.getInstance()
				.getBackgroundBitmap();
		ViewCompat.setBackground(setting_lin, new BitmapDrawable(getResources(),
				background));
		
		initView();
	}

	private void initView() {
		initTopBarForLeft("设置");
		setTheme(R.style.DefaultLightTheme);
		
		current_version.setText("v " + CheckVersionUtil.getCurrentVersion(mContext));
	}
	
	/**
	 * 注销账号
	 * @param v
	 */
	public void logoutOnClik(View v) {
		SimpleDialogFragment
				.createBuilder(SettingActivity.this,
						getSupportFragmentManager())
				.setTitle(R.string.logout_title)
				.setMessage(R.string.logout_tip)
				.setPositiveButtonText(R.string.logout_positive_button)
				.setNegativeButtonText(R.string.logout_negative_button)
				.setRequestCode(42).setTag("custom-tag").show();

	}
	
	//检查新版本
	public void updataVersionOnClick(View v){
		CheckVersionUtil.updataVersion(mContext, true);  //true表示在检查更新的时候弹出提示框
	}
	
	/**
	 * 重设密码
	 * @param v
	 */
	public void resetPassword(View v) {
		startAnimActivity(ResetPasswordActivity.class);
	}
	
	@Override
	public void onPositiveButtonClicked(int requestCode) {
		//注销
		if (requestCode==42) {
			User user = UserManager.getInstance(mContext).getCurrentUser();
			NetWorkManager.getInstance(mContext).logoutAction(user == null ? "" : user.getToken(), new Listener<String>() {
				@Override
				public void onResponse(String response) {
					ShowToast("注销成功");
					
					ImageLoader.getInstance().clearMemoryCache();
					ImageLoader.getInstance().clearDiskCache();
					UserManager.getInstance(mContext).clearCurrentUser();
					XmppManager.getInstance(mContext).disconnect();
					sendBroadcast(new Intent(JConstants.ACTION_FINISH_MAIN));
					startAnimActivity(LoginActivity.class);
					SettingActivity.this.finish();
				}
			}, new ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {//无论如何清空数据
					XmppManager.getInstance(mContext).disconnect();
					UserManager.getInstance(mContext).clearCurrentUser();
					ImageLoader.getInstance().clearMemoryCache();
					sendBroadcast(new Intent(JConstants.ACTION_FINISH_MAIN));
					startAnimActivity(LoginActivity.class);
					SettingActivity.this.finish();
				}
			});
		}
	}
	
	@Override
	public void onNegativeButtonClicked(int requestCode) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onNeutralButtonClicked(int requestCode) {
		// TODO Auto-generated method stub
	}
}
