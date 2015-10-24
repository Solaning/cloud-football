package com.kinth.football.ui.mine;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.method.NumberKeyListener;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.kinth.football.R;
import com.kinth.football.bean.User;
import com.kinth.football.config.Action;
import com.kinth.football.eventbus.bean.ModifyPlayerHeightEvent;
import com.kinth.football.eventbus.bean.ModifyPlayerNickNameEvent;
import com.kinth.football.eventbus.bean.ModifyPlayerWeightEvent;
import com.kinth.football.network.NetWorkManager;
import com.kinth.football.network.UserNetworkManager;
import com.kinth.football.singleton.SingletonBitmapClass;
import com.kinth.football.singleton.ViewCompat;
import com.kinth.football.ui.BaseActivity;
import com.kinth.football.util.DateUtil;
import com.kinth.football.util.ErrorCodeUtil;
import com.kinth.football.util.QuitWay;
import com.kinth.football.view.ClearEditText;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import de.greenrobot.event.EventBus;

/**
 * 设置昵称,邮箱 --身高，体重
 * 
 * @author Sola
 *
 */
@ContentView(R.layout.activity_setnick)
public class ModifyInfoActivity extends BaseActivity {
	private String from = "";
	
	@ViewInject(R.id.entire_layout)
	private View entireLayout;
	
	@ViewInject(R.id.edt_nick)
	private ClearEditText edtNick;
	
	@ViewInject(R.id.edt_height_or_weight)
	private ClearEditText edt_height_or_weight;
	
	@ViewInject(R.id.llt_height_or_weight)
	private LinearLayout llt_height_or_weight;
	
	@ViewInject(R.id.txt_height_or_weight)
	private TextView txt_height_or_weight;
	
	@ViewInject(R.id.nav_title)
	private TextView nav_title;

	@ViewInject(R.id.nav_left)
	private ImageButton nav_left;
	
	@ViewInject(R.id.nav_right_btn)
	private Button nav_right_btn;

	@ViewInject(R.id.height_line)
	private View height_line;
	
	@ViewInject(R.id.nick_line)
	private View nick_line;
	
	@OnClick(R.id.nav_left)
	public void fun_1(View v){
		finish();
	}
	
	@OnClick(R.id.nav_right_btn)
	public void fun_2(View v){
		btnOnClick(v);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);

		from = getIntent().getStringExtra("from");
		QuitWay.activityList.add(this);

		// 設置背景
		Bitmap background = SingletonBitmapClass.getInstance()
				.getBackgroundBitmap();
		ViewCompat.setBackground(entireLayout, new BitmapDrawable(
				getResources(), background));

		initView();
	}

	private void initView() {
		nav_right_btn.setText("保存");
		edt_height_or_weight.setKeyListener(new NumberKeyListener() {
			@Override
			protected char[] getAcceptedChars() {
				return new char[] { '1', '2', '3', '4', '5', '6', '7', '8',
						'9', '0' };
			}

			@Override
			public int getInputType() {
				// TODO Auto-generated method stub
				return android.text.InputType.TYPE_CLASS_PHONE;
			}
		});
		
		if ("nick".equals(from)) {
			edtNick.setVisibility(View.VISIBLE);
			llt_height_or_weight.setVisibility(View.GONE);
			height_line.setVisibility(View.GONE);
			nav_title.setText("修改昵称");
			if (footBallUserManager.getCurrentUser() != null
					&& footBallUserManager.getCurrentUser().getPlayer()
							.getName() != null) {
				edtNick.setText(footBallUserManager.getCurrentUser()
						.getPlayer().getName());
				edtNick.setSelection(footBallUserManager.getCurrentUser()
						.getPlayer().getName().trim().length());
			}
			return;
		} 
		if ("email".equals(from)) {
			edtNick.setVisibility(View.VISIBLE);
			llt_height_or_weight.setVisibility(View.GONE);
			height_line.setVisibility(View.GONE);
			nav_title.setText("修改邮箱");

			if (footBallUserManager.getCurrentUser() != null
					&& footBallUserManager.getCurrentUser().getPlayer()
							.getEmail() != null) {
				edtNick.setText(footBallUserManager.getCurrentUser()
						.getPlayer().getEmail());
				edtNick.setSelection(footBallUserManager.getCurrentUser()
						.getPlayer().getEmail().trim().length());
			}
			return;
		} 
		if ("height".equals(from)) {
			edtNick.setVisibility(View.GONE);
			nick_line.setVisibility(View.GONE);
			llt_height_or_weight.setVisibility(View.VISIBLE);
			txt_height_or_weight.setText("CM");
			nav_title.setText("修改身高");
			if (footBallUserManager.getCurrentUser() != null
					&& footBallUserManager.getCurrentUser().getPlayer()
							.getHeight() != null) {
				edt_height_or_weight.setText((footBallUserManager
						.getCurrentUser().getPlayer().getHeight() + "").trim());
				edt_height_or_weight.setSelection((footBallUserManager
						.getCurrentUser().getPlayer().getHeight() + "").trim()
						.length());
			}
			return;
		} 
		if ("weight".equals(from)) {
			edtNick.setVisibility(View.GONE);
			nick_line.setVisibility(View.GONE);
			llt_height_or_weight.setVisibility(View.VISIBLE);
			txt_height_or_weight.setText("KG");
			nav_title.setText("修改体重");

			if (footBallUserManager.getCurrentUser() != null
					&& footBallUserManager.getCurrentUser().getPlayer()
							.getWeight() != null) {
				edt_height_or_weight.setText((footBallUserManager
						.getCurrentUser().getPlayer().getWeight() + "").trim());
				edt_height_or_weight.setSelection((footBallUserManager
						.getCurrentUser().getPlayer().getWeight() + "").trim()
						.length());
			}else{
				
			}
			return;
		} 
		if ("age".equals(from)) {
			edtNick.setVisibility(View.GONE);
			nick_line.setVisibility(View.GONE);
			llt_height_or_weight.setVisibility(View.VISIBLE);
			txt_height_or_weight.setVisibility(View.GONE);
			nav_title.setText("修改年龄");

			if (footBallUserManager.getCurrentUser() != null
					&& footBallUserManager.getCurrentUser().getPlayer()
							.getBirthday() != null) {
				int age = DateUtil.calcAgeByBirthday(footBallUserManager
						.getCurrentUser().getPlayer().getBirthday());
				edt_height_or_weight.setText(String.valueOf(age));
				edt_height_or_weight.setSelection(String.valueOf(age).length());
			}
			return;
		}
	}

	public void btnOnClick(View v) {
		String str1 = edtNick.getText().toString();
		String str2 = edt_height_or_weight.getText().toString();

		if (from.equals("height")) {
			if (str2.equals("") || str2.equals("0")) {
				ShowToast("请输入身高");
			} else {
				try {
					float height = Float.parseFloat(str2);
					monifyUserInfo_height((int) height);
				} catch (Exception e) {
					// 如果抛出异常，返回False
					ShowToast("请输入正确的身重");
				}
			}
			return;
		} 
		if (from.equals("weight")) {
			if (str2.equals("") || str2.equals("0")) {
				ShowToast("请输入体重");
			} else {
				try {
					float weight = Float.parseFloat(str2);
					monifyUserInfo_weight((int) weight);
				} catch (Exception e) {
					// 如果抛出异常，返回False
					ShowToast("请输入正确的体重");
				}
			}
			return;
		} 
		 if (from.equals("nick")) { // 修改昵称
			if (str1.equals("")) {
				ShowToast("昵称不能为空");
			} else {
				String token = footBallUserManager.getCurrentUser().getToken();
				modifyNickData(from, str1, token);
			}
			return;
		} 
		if (from.equals("email")) { // 修改邮箱
			if (str1.equals("") || str1.length() == 0) {
				ShowToast("请输入邮箱");
			} else if (!isEmail(str1)) {
				ShowToast("邮箱格式不正确");
			} else {
				String token = footBallUserManager.getCurrentUser().getToken();
				modifyNickData("email", str1, token);
			}
			return;
		}
	}

	private ProgressDialog progress = null; // 进度

	private void modifyNickData(String flag, final String name, String token) {
		progress = new ProgressDialog(ModifyInfoActivity.this);
		progress.setMessage("正在提交...");
		progress.setCanceledOnTouchOutside(false);
		progress.setCancelable(false);
		progress.show();

		UserNetworkManager.getInstance(getApplicationContext())
				.modifyUserNameOrEmail(flag, name, token, new Listener<Void>() {
					@Override
					public void onResponse(Void response) {
						ShowToast("修改成功");
						dismissProgress();
						User user = footBallUserManager.getCurrentUser();
						if (from.equals("nick")) {
							user.getPlayer().setName(name);

							EventBus.getDefault().post(new ModifyPlayerNickNameEvent(name));
						} else {
							user.getPlayer().setEmail(name);
						}
						footBallUserManager.saveCurrentUser(user);
						ModifyInfoActivity.this.finish();
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						dismissProgress();
						if (!NetWorkManager.getInstance(mContext)
								.isNetConnected()) {
							ShowToast("没有可用的网络");
							return;
						} 
						if (error.networkResponse == null) {
							ShowToast("修改失败，请稍后再试");
							return;
						} 
						if (error.networkResponse.statusCode == 401) { // access token无效
							ErrorCodeUtil.ErrorCode401(mContext);
							return;
						}
					}
				});
	}

	private void monifyUserInfo_height(final int height) {
		progress = new ProgressDialog(ModifyInfoActivity.this);
		progress.setMessage("正在修改...");
		progress.setCanceledOnTouchOutside(false);
		progress.setCancelable(false);
		progress.show();

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("height", height);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		NetWorkManager.getInstance(getApplicationContext()).monifyUserInfo(
				footBallUserManager.getCurrentUser().getToken(), jsonObject,
				new Listener<Void>() {
					@Override
					public void onResponse(Void response) {
						ShowToast("修改成功");
						dismissProgress();
						User user = footBallUserManager.getCurrentUser();
						user.getPlayer().setHeight(height);
						footBallUserManager.saveCurrentUser(user);
						EventBus.getDefault().post(new ModifyPlayerHeightEvent(height));
						ModifyInfoActivity.this.finish();
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						dismissProgress();
						if (!NetWorkManager.getInstance(mContext)
								.isNetConnected()) {
							ShowToast("没有可用的网络");
							return;
						} 
						if (error.networkResponse == null) {
							ShowToast("修改失败，请稍后再试");
							return;
						} 
						if (error.networkResponse.statusCode == 401) { // access token无效
							ErrorCodeUtil.ErrorCode401(mContext);
							return;
						}
					}
				});
	}

	private void monifyUserInfo_weight(final int weight) {
		progress = new ProgressDialog(ModifyInfoActivity.this);
		progress.setMessage("正在修改...");
		progress.setCanceledOnTouchOutside(false);
		progress.setCancelable(false);
		progress.show();

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("weight", weight);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		NetWorkManager.getInstance(getApplicationContext()).monifyUserInfo(
				footBallUserManager.getCurrentUser().getToken(), jsonObject,
				new Listener<Void>() {
					@Override
					public void onResponse(Void response) {
						ShowToast("修改成功");
						dismissProgress();
						User user = footBallUserManager.getCurrentUser();
						user.getPlayer().setWeight(weight);
						footBallUserManager.saveCurrentUser(user);
						EventBus.getDefault().post(new ModifyPlayerWeightEvent(weight));
						ModifyInfoActivity.this.finish();
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						dismissProgress();
						if (!NetWorkManager.getInstance(mContext)
								.isNetConnected()) {
							ShowToast("没有可用的网络");
							return;
						} 
						if (error.networkResponse == null) {
							ShowToast("修改失败，请稍后再试");
							return;
						} 
						if (error.networkResponse.statusCode == 401) { // access token无效
							ErrorCodeUtil.ErrorCode401(mContext);
							return;
						}
					}
				});
	}

	private void dismissProgress() {
		if (progress != null && progress.isShowing()) {
			progress.dismiss();
		}
	}

	// 判断email格式是否正确
	public boolean isEmail(String email) {
		String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
		Pattern p = Pattern.compile(str);
		Matcher m = p.matcher(email);
		return m.matches();
	}

}
