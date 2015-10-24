package com.kinth.football.ui;

import java.io.InputStream;
import java.util.HashMap;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Handler.Callback;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.gui.RegisterPage;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
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
import com.kinth.football.util.ErrorCodeUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import de.greenrobot.event.EventBus;

/**
 * 登陆页面
 * @author Sola
 * 
 */
@ContentView(R.layout.activity_login_1)
public class LoginActivity extends BaseActivity implements Callback {
	// 填写从短信SDK应用后台注册得到的APPKEY
	private static String APPKEY = "6eca8008edc0";

	// 填写从短信SDK应用后台注册得到的APPSECRET
	private static String APPSECRET = "7b1ceaf9a8a265d4be8ab5073ef0d947";
	
	private ProgressDialog progress;
	private boolean hasLogouted = false;

	@ViewInject(R.id.edtPhone)
	private EditText et_username;
	
	@ViewInject(R.id.edtPwd)
	private EditText et_password; // userName,密码
	
	@ViewInject(R.id.btn_login)
	private Button btn_login;
	
	@ViewInject(R.id.tv_foget_password)
	private TextView forgetPassword;//忘记密码
	
	@ViewInject(R.id.register_tv)
	private TextView tv_register;
	
	@ViewInject(R.id.view_group)
	private RelativeLayout viewGroup;
	
	@OnClick(R.id.register_tv)
	public void fun_1(View v){//注册
//		setPassword("广东", "12346");  //测试 
		RegisterPage registerPage = new RegisterPage();
		registerPage.setRegisterCallback(new EventHandler() {
			public void afterEvent(int event, int result, Object data) {
				// 解析注册结果
				if (result == SMSSDK.RESULT_COMPLETE) {
					@SuppressWarnings("unchecked")
					HashMap<String, Object> phoneMap = (HashMap<String, Object>) data;
					String country = (String) phoneMap.get("country");
					String phone = (String) phoneMap.get("phone");
					// 提交用户信息
					setPassword(country, phone);
				}
			}
		});
		registerPage.show(mContext);
	}
	
	@OnClick(R.id.btn_login)
	public void fun_2(View v){//登陆
		boolean isNetConnected = CommonUtils.isNetworkAvailable(this);
		if (!isNetConnected) {
			ShowToast(R.string.network_tips);
			return;
		}
		login();
	}
	
	@OnClick(R.id.tv_foget_password)
	public void fun_3(View v){
		ForgetPasswordPage forgetPasswordPage = new ForgetPasswordPage();
		forgetPasswordPage.show(mContext);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EventBus.getDefault().register(this);
		ViewUtils.inject(this);
		
		//设置背景
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inJustDecodeBounds = false;
		opt.inInputShareable = true;
		opt.inPurgeable = true;
		// 获取资源图片
		InputStream is = getResources().openRawResource(
				R.drawable.bg2);
		Bitmap background = BitmapFactory.decodeStream(is, null, opt);
		ViewCompat.setBackground(viewGroup, new BitmapDrawable(getResources(),
				background));
		
		init();
		initSDK();
		// showNotice();
		progress = new ProgressDialog(LoginActivity.this);
		progress.setMessage("正在登陆...");
		progress.setCanceledOnTouchOutside(false);
	}

	private void setPassword(String country, String phone) {
		Intent intent = new Intent(this, RegistTwoActivity.class);
		intent.putExtra("country", country);
		intent.putExtra("phone", phone);
		startActivity(intent);
	}

	private void initSDK() {
		// 初始化短信SDK
		SMSSDK.initSDK(this, APPKEY, APPSECRET);
		final Handler handler = new Handler(this);
		EventHandler eventHandler = new EventHandler() {
			public void afterEvent(int event, int result, Object data) {
				Message msg = new Message();
				msg.arg1 = event;
				msg.arg2 = result;
				msg.obj = data;
				handler.sendMessage(msg);
			}
		};
		// 注册回调监听接口
		SMSSDK.registerEventHandler(eventHandler);
	}

	private void init() {
		if (footBallUserManager.getCurrentUser() != null) {
			String phone = footBallUserManager.getCurrentUser().getPlayer().getPhone();
			if (!TextUtils.isEmpty(phone)) {
				et_username.setText(phone);
				et_password.requestFocus();
			}
		}
		SpannableString ss = new SpannableString("点击此处注册");
		ss.setSpan(new UnderlineSpan(), 0, 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		tv_register.setText(ss);
		
		SpannableString sss = new SpannableString("忘记密码？");
		sss.setSpan(new UnderlineSpan(), 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		forgetPassword.setText(sss);
	}

	private void login() {
		final String phone = et_username.getText().toString();
		final String password = et_password.getText().toString();

		if (TextUtils.isEmpty(phone)) {
			ShowToast(R.string.toast_error_username_null);
			return;
		}
		if (TextUtils.isEmpty(password)) {
			ShowToast(R.string.toast_error_password_null);
			return;
		}
		if (!hasLogouted) {
			progress.show();
		}
		NetWorkManager.getInstance(getApplicationContext()).loginAction(phone,
				password, new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Gson gson = new Gson();
						LoginResponse loginResponse = gson.fromJson(response.toString(), new TypeToken<LoginResponse>(){}.getType());
						if(loginResponse == null){
							DialogUtil.dialogDismiss(progress);
							ShowToast("登录失败");
							return;
						}
						Player player = new Player();
						player.setPhone(phone);
						User user = UserManager.getInstance(mContext).getCurrentUser();
						if(user == null){
							user = new User();
						}
						user.setPassword(password);
						user.setPlayer(player);
						user.setToken(loginResponse.getAccess_token());
						footBallUserManager.clearCurrentUser(); // 清除旧用户信息
						footBallUserManager.setCurrentUser(user);
						footBallUserManager.saveCurrentUser(user); // 保存新用户信息
						if (hasLogouted) {//已经注销过一次，直接获取用户信息
							getUserInfo(loginResponse.getAccess_token());
						} else {//调用一次注销接口
							logoutFirst();
						}
					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						DialogUtil.dialogDismiss(progress);
						if (error.networkResponse == null) {
							ShowToast("登录失败，请稍后重试");
						} else if (error.networkResponse.statusCode == 400) {
							ShowToast("用户名或密码错误");
						}
					}
				});
	}

	/**
	 * 获取用户信息
	 * @param token
	 */
	void getUserInfo(String token) {
		NetWorkManager.getInstance(getApplicationContext()).getPlayerInfo(
				token, new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Gson gson = new Gson();
						Player player = gson.fromJson(response.toString(), new TypeToken<Player>(){}.getType());
						if(player == null){
							DialogUtil.dialogDismiss(progress);
							ShowToast("登陆失败，请稍后重试");
							return;
						}
						User user = footBallUserManager.getCurrentUser();
						user.setPlayer(player);
						footBallUserManager.saveCurrentUser(user);
						// 2.如果昵称或者头像为空，则跳转到设置昵称头像界面
						if (TextUtils.isEmpty(player.getName())) {
							DialogUtil.dialogDismiss(progress);
							startAnimActivity(SetIconAndNickActivity.class);
							LoginActivity.this.finish();
							return;
						}
						DialogUtil.dialogDismiss(progress);
						startAnimActivity(MainActivity.class);
						finish();
						overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
//						ShowToast("获取用户信息失败，请重新尝试");
					}
				});
	}
	
	/**
	 * 关闭当前页面的事件
	 * @param finishLoginActivityEvent
	 */
	public void onEventMainThread(FinishLoginActivityEvent finishLoginActivityEvent){
		finish();
	}

	@Override
	protected void onDestroy() {
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}

	@Override
	public boolean handleMessage(Message arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * 第一次先注销
	 */
	public void logoutFirst() {
		NetWorkManager.getInstance(getApplicationContext()).logoutAction(
				UserManager.getInstance(mContext).getCurrentUser().getToken(),
				new Listener<String>() {
					@Override
					public void onResponse(String response) {
						User user = UserManager.getInstance(
										getApplicationContext())
										.getCurrentUser();
						user.setToken("");
						UserManager.getInstance(mContext)
								.clearCurrentUser();
						hasLogouted = true;
						login();
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						DialogUtil.dialogDismiss(progress);
						if (error.networkResponse == null) {
							ShowToast("登录失败，请稍后重试");
						} else if (error.networkResponse.statusCode == 401) {
							ErrorCodeUtil.ErrorCode401(mContext);
							return;
						}
						UserManager.getInstance(mContext).clearCurrentUser();
					}
				});
	}

	@Override
	public void onBackPressed() {
		finish();
		// 程序完全退出
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		android.os.Process.killProcess(android.os.Process.myPid());
	}

}
