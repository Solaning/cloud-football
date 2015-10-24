package com.kinth.football.ui.mine;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.kinth.football.R;
import com.kinth.football.bean.User;
import com.kinth.football.manager.UserManager;
import com.kinth.football.network.NetWorkManager;
import com.kinth.football.singleton.SingletonBitmapClass;
import com.kinth.football.singleton.ViewCompat;
import com.kinth.football.ui.BaseActivity;
import com.kinth.football.util.ErrorCodeUtil;

public class ResetPasswordActivity extends BaseActivity {
	private EditText edtOldPwd,edtNewPwd,edtNewPwd2;
//	private 
	private ProgressDialog progress =null;   //进度
	
	private RelativeLayout AppWidget;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reset_pwd);
	
		AppWidget = (RelativeLayout)this.findViewById(R.id.AppWidget);
		// 設置背景
		Bitmap background = SingletonBitmapClass.getInstance()
				.getBackgroundBitmap();
		ViewCompat.setBackground(AppWidget, new BitmapDrawable(getResources(),
				background));
		
		initView();
	}

	public void initView() {
		initTopBarForLeft("修改密码");
		edtOldPwd = (EditText)findViewById(R.id.edtOldPwd);
	
		edtNewPwd = (EditText) findViewById(R.id.edtPwd);
		edtNewPwd2 = (EditText)findViewById(R.id.edtPwd2);
	}

	public void btnOnClick(View v) {
		String oldpwd = edtOldPwd.getText().toString();
		if (TextUtils.isEmpty(oldpwd.trim())) {
			ShowToast("请输入旧密码");
			return;
		}		
		String pwd = edtNewPwd.getText().toString();
		if (TextUtils.isEmpty(pwd.trim())) {
			ShowToast("请输入新密码");
			return;
		}
		String pwd2 = edtNewPwd2.getText().toString();

		if (TextUtils.isEmpty(pwd2.trim())) {
			ShowToast("请再次输入新密码");
			return;
		}
		if (!pwd2.trim().equals(pwd.trim())) {
			ShowToast("2次密码不一致，请重新输入");
			edtOldPwd.setText("");
			edtNewPwd.setText("");
			edtNewPwd2.setText("");
			return;
		}
		resetPassword(oldpwd,pwd2);
	}

	private void resetPassword(final String oldPwd,final String newPwd) {
		progress = new ProgressDialog(ResetPasswordActivity.this);
		progress.setMessage("正在修改密码...");
		progress.setCanceledOnTouchOutside(false);
		progress.setCancelable(false);
		progress.show();
		
		User user = UserManager.getInstance(getApplicationContext()).getCurrentUser();
		NetWorkManager.getInstance(getApplicationContext()).modifyPwdAction(user.getToken(),oldPwd,
		 newPwd, new Listener<String>() {

			@Override
			public void onResponse(String response) {
				User userTemp = UserManager.getInstance(getApplicationContext()).getCurrentUser();
				userTemp.setPassword(newPwd);
				UserManager.getInstance(getApplicationContext()).saveCurrentUser(userTemp);
				
				runOnUiThread(new Runnable() {
					public void run() {
						dismissProgress(progress);
						ShowToast("修改成功");
						ResetPasswordActivity.this.finish();
					}
				});
			}
			 
		}, new ErrorListener() {
				
			@Override
			public void onErrorResponse(final VolleyError error) {

				runOnUiThread(new Runnable() {
					public void run() {
						dismissProgress(progress);
						if(!NetWorkManager.getInstance(mContext).isNetConnected()){
							ShowToast("没有可用的网络");
						}else if(error.networkResponse == null){
//							ShowToast("ResetPasswordActivity-resetPassword-服务器连接错误");
						}else if(error.networkResponse.statusCode == 401){  //access token无效
								ErrorCodeUtil.ErrorCode401(mContext);
						}else if(error.networkResponse.statusCode == 403){  //旧密码错误
							ShowToast("旧密码错误！！");
						}else if(error.networkResponse.statusCode == 400){  //旧密码错误
							ShowToast("旧密码或者新密码为空！！");
						}
//						ShowToast("修改失败");
					}
				});
				
				
			}
			
			
		});
	}
	
	private void dismissProgress(ProgressDialog progress) {
		if (progress==null) {
			return;
		}
		if (progress.isShowing()) {
			progress.dismiss();
		}
	}
	
	
}
