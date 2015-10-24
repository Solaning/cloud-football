package com.kinth.football.ui;

import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kinth.football.R;
import com.kinth.football.bean.LoginResponse;
import com.kinth.football.bean.User;
import com.kinth.football.dao.Player;
import com.kinth.football.eventbus.bean.FinishLoginActivityEvent;
import com.kinth.football.manager.UserManager;
import com.kinth.football.network.NetWorkManager;
import com.kinth.football.singleton.ViewCompat;
import com.kinth.football.ui.user.SetIconAndNickActivity;
import com.kinth.football.util.CommonUtils;
import com.kinth.football.util.DialogUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;

import de.greenrobot.event.EventBus;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

/**
 * 输入密码
 * @author admin
 */
@ContentView(R.layout.activity_register_two)
public class RegistTwoActivity extends BaseActivity	{
	
	@ViewInject(R.id.view_group)
	private View viewGroup;
	
	private String phone = "";
	
	@ViewInject(R.id.edtPwd)
	private EditText edtPwd;
	
	@ViewInject(R.id.edtPwd2)
	private EditText edtPwd2;
	
	private ProgressDialog progress =null;   //进度条
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.ARGB_8888;
		opt.inInputShareable = true;
		opt.inPurgeable = true;
		// 获取资源图片
		InputStream is = getResources().openRawResource(
				R.drawable.bg2);
		Bitmap bitmap = BitmapFactory.decodeStream(is, null, opt);
		ViewCompat.setBackground(viewGroup, new BitmapDrawable(getResources(),
				bitmap));
		
		phone = getIntent().getStringExtra("phone");
		initView();
	}
	
	private void initView() {
		initTopBarForLeft("设置密码");
	}
	
	/**
	 * 登录按钮的方法
	 * @param v
	 */
	public void btnOnClick(View v) {
		register();
	}
	
	private void register(){
		final String password = edtPwd.getText().toString();
		final String password2 = edtPwd2.getText().toString();
		//1.判断手机号是否为空
		if (TextUtils.isEmpty(phone)) {
			ShowToast("请重新输入手机号");
			return;
		}
		//1.判断密码是否为空
		if (TextUtils.isEmpty(password)) {
			ShowToast("请输入密码");
			return;
		}
		if (TextUtils.isEmpty(password2)) {
			ShowToast("请再次输入密码");
			return;
		}
		if (!password.trim().equals(password2.trim())) {
			ShowToast("2次密码不一样！！请重新输入");
			return;
		}
		boolean isNetConnected = CommonUtils.isNetworkAvailable(this);
		if(!isNetConnected){
			ShowToast(R.string.network_tips);
			return;
		}
		progress = new ProgressDialog(RegistTwoActivity.this);
		progress.setMessage("正在注册...");
		progress.setCanceledOnTouchOutside(false);
		progress.show();
		//足球app的註冊
		NetWorkManager.getInstance(getApplicationContext()).
			registerAction(phone, password, new Listener<JSONObject>() {
				@Override
				public void onResponse(JSONObject response) {
					String id = null ,accountName = null;
					try {
						id = response.getString("uuid");
						accountName = response.getString("accountName");
					} catch (JSONException e) {
						e.printStackTrace();
						DialogUtil.dialogDismiss(progress);
						return;
					}
					Player player = new Player();
					player.setUuid(id);
					player.setAccountName(accountName);
					User user = new User();
					user.setPlayer(player);
					user.setPassword(password);
					//设置当前用户为user
					footBallUserManager.saveCurrentUser(user);
					footBallUserManager.setCurrentUser(user);
					loginAction();
					//注册成功后，跳转到设置头像，昵称界面
				}
			}, new ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {
					// TODO Auto-generated method stub
					ShowToast("注册失败(账号已经注册)");
					DialogUtil.dialogDismiss(progress);
				}
			});
	}
	
	/**
	 * 注册之后登录
	 */
	private void loginAction() {
		NetWorkManager.getInstance(getApplicationContext()).loginAction(phone, edtPwd.getText().toString(), new Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				DialogUtil.dialogDismiss(progress);
				Gson gson = new Gson();
				LoginResponse loginResponse = gson.fromJson(response.toString(), new TypeToken<LoginResponse>(){}.getType());
				if(loginResponse == null){
					ShowToast("登录失败");
					return;
				}
				User user = UserManager.getInstance(mContext).getCurrentUser();
				user.getPlayer().setPhone(phone);
				user.setToken(loginResponse.getAccess_token());
				footBallUserManager.saveCurrentUser(user); // 保存新用户信息
				EventBus.getDefault().post(new FinishLoginActivityEvent());//关闭登录页面，跳转到设置头像
				startAnimActivity(SetIconAndNickActivity.class);
				RegistTwoActivity.this.finish();
			}
			
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				DialogUtil.dialogDismiss(progress);
				ShowToast("登陆失败");
			}
		});
	}
}
