package com.kinth.football.ui;

import static cn.smssdk.framework.utils.R.dipToPx;
import static cn.smssdk.framework.utils.R.getColorRes;
import static cn.smssdk.framework.utils.R.getIdRes;
import static cn.smssdk.framework.utils.R.getLayoutRes;
import static cn.smssdk.framework.utils.R.getStringRes;
import static cn.smssdk.framework.utils.R.getStyleRes;

import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kinth.football.bean.ErrorDescription;
import com.kinth.football.network.NetWorkManager;
import com.kinth.football.util.DialogUtil;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Color;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.smssdk.framework.FakeActivity;
import cn.smssdk.gui.CommonDialog;

/** 验证码输入页面 */
public class ForgetPasswordIdentifyNumPage extends FakeActivity implements OnClickListener {

	private static final int RETRY_INTERVAL = 60;
	private String phone;
	private String code;// 地区码
	private String formatedPhone;
	private int time = RETRY_INTERVAL;
	private Dialog pd;

	private EditText etIdentifyNum;
	private TextView tvTitle;
	private TextView tvPhone;
	private TextView tvIdentifyNotify;
	private TextView tvUnreceiveIdentify;
	private ImageView ivClear, ivClear1, ivClear2;
	private Button btnSubmit;
	private EditText edtPwd;
	private EditText edtPwd2;
	private int SHOWDIALOGTYPE = 1;

	public void setPhone(String phone, String code, String formatedPhone) {
		this.phone = phone;
		this.code = code;
		this.formatedPhone = formatedPhone;
	}

	public void onCreate() {
		int resId = getLayoutRes(activity, "smssdk_input_identify_num_and_password_page");
		if (resId > 0) {
			activity.setContentView(resId);
			resId = getIdRes(activity, "ll_back");
			activity.findViewById(resId).setOnClickListener(this);
			resId = getIdRes(activity, "btn_submit");
			btnSubmit = (Button) activity.findViewById(resId);
			btnSubmit.setOnClickListener(this);
			btnSubmit.setClickable(false);
			btnSubmit.setEnabled(false);
			btnSubmit.setTextColor(Color.GRAY);

			resId = getIdRes(activity, "tv_title");
			tvTitle = (TextView) activity.findViewById(resId);
			resId = getStringRes(activity, "smssdk_write_identify_code");
			if (resId > 0) {
				tvTitle.setText(resId);
			}
			resId = getIdRes(activity, "et_put_identify");
			etIdentifyNum = (EditText) activity.findViewById(resId);
			etIdentifyNum.addTextChangedListener(new TextWatcher() {
				
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					// 如果输入框木有，就隐藏delbtn
					if (s.length() > 0) {
						btnSubmit.setEnabled(true);
						btnSubmit.setClickable(true);
						ivClear.setVisibility(View.VISIBLE);
						btnSubmit.setTextColor(Color.WHITE);
					} else {
						btnSubmit.setEnabled(false);
						btnSubmit.setClickable(true);
						ivClear.setVisibility(View.GONE);
						btnSubmit.setTextColor(Color.GRAY);
					}
				}
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {

				}
				@Override
				public void afterTextChanged(Editable s) {
				}
			});
			resId = getIdRes(activity, "tv_identify_notify");
			tvIdentifyNotify = (TextView) activity.findViewById(resId);
			resId = getStringRes(activity, "smssdk_send_mobile_detail");
			if (resId > 0) {
				String text = getContext().getString(resId);
				tvIdentifyNotify.setText(Html.fromHtml(text));
			}
			resId = getIdRes(activity, "tv_phone");
			tvPhone = (TextView) activity.findViewById(resId);
			tvPhone.setText(formatedPhone);
			resId = getIdRes(activity, "tv_unreceive_identify");
			tvUnreceiveIdentify = (TextView) activity.findViewById(resId);
			resId = getStringRes(activity, "smssdk_receive_msg");
			if (resId > 0) {
				String unReceive = getContext().getString(resId, time);
				tvUnreceiveIdentify.setText(Html.fromHtml(unReceive));
			}
			tvUnreceiveIdentify.setOnClickListener(this);
			tvUnreceiveIdentify.setEnabled(false);
			resId = getIdRes(activity, "iv_clear");
			ivClear = (ImageView) activity.findViewById(resId);
			ivClear.setOnClickListener(this);
			
			resId = getIdRes(activity, "iv_clear1");
			ivClear1 = (ImageView) activity.findViewById(resId);
			ivClear1.setOnClickListener(this);
			
			resId = getIdRes(activity, "iv_clear2");
			ivClear2 = (ImageView) activity.findViewById(resId);
			ivClear2.setOnClickListener(this);
			
			resId = getIdRes(activity, "edtPwd");
			edtPwd = (EditText) activity.findViewById(resId);
			edtPwd.addTextChangedListener(new TextWatcher() {
				
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					// 如果输入框木有，就隐藏delbtn
					if (s.length() > 0) {
						btnSubmit.setEnabled(true);
						btnSubmit.setClickable(true);
						ivClear1.setVisibility(View.VISIBLE);
						btnSubmit.setTextColor(Color.WHITE);
					} else {
						btnSubmit.setEnabled(false);
						btnSubmit.setClickable(true);
						ivClear1.setVisibility(View.GONE);
					}
				}
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {

				}
				@Override
				public void afterTextChanged(Editable s) {
					// btnSounds.setVisibility(View.GONE);
				}
			});
			resId = getIdRes(activity, "edtPwd2");
			edtPwd2 = (EditText) activity.findViewById(resId);
			edtPwd2.addTextChangedListener(new TextWatcher() {
				
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					// 如果输入框木有，就隐藏delbtn
					if (s.length() > 0) {
						btnSubmit.setEnabled(true);
						btnSubmit.setClickable(true);
						ivClear2.setVisibility(View.VISIBLE);
						btnSubmit.setTextColor(Color.WHITE);
					} else {
						btnSubmit.setEnabled(false);
						btnSubmit.setClickable(true);
						ivClear2.setVisibility(View.GONE);
					}
				}
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {

				}
				@Override
				public void afterTextChanged(Editable s) {
				}
			});
			
			countDown();
		}
	}

	/** 倒数计时 */
	private void countDown() {
		runOnUIThread(new Runnable() {
			public void run() {
				time--;
				if (time == 0) {
					int resId = getStringRes(activity, "smssdk_unreceive_identify_code");
					if (resId > 0) {
						String unReceive = getContext().getString(resId, time);
						tvUnreceiveIdentify.setText(Html.fromHtml(unReceive));
					}
					tvUnreceiveIdentify.setEnabled(true);
					time = RETRY_INTERVAL;
				} else {
					int resId = getStringRes(activity, "smssdk_receive_msg");
					if (resId > 0) {
						String unReceive = getContext().getString(resId, time);
						tvUnreceiveIdentify.setText(Html.fromHtml(unReceive));
					}
					tvUnreceiveIdentify.setEnabled(false);
					runOnUIThread(this, 1000);
				}
			}
		}, 1000);
	}

	public void onClick(View v) {
		int id = v.getId();
		int id_ll_back = getIdRes(activity, "ll_back");
		int id_btn_submit = getIdRes(activity, "btn_submit");
		int id_tv_unreceive_identify = getIdRes(activity, "tv_unreceive_identify");
		int id_iv_clear = getIdRes(activity, "iv_clear");
		int id_iv_clear1 = getIdRes(activity, "iv_clear1");
		int id_iv_clear2 = getIdRes(activity, "iv_clear2");

		if (id == id_ll_back) {
			runOnUIThread(new Runnable() {
				public void run() {
					showNotifyDialog();
				}
			});
		} else if (id == id_btn_submit) {// 点击下一步
			String verificationCode = etIdentifyNum.getText().toString().trim();
			final String password = edtPwd.getText().toString();
			final String password2 = edtPwd2.getText().toString();
			// 1.判断手机号是否为空
			if (TextUtils.isEmpty(phone)) {
				ShowToast("手机号为空");
				return;
			}
			if (TextUtils.isEmpty(verificationCode)) {
				ShowToast("验证码为空");
				return;
			}
			// 1.判断密码是否为空
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
			pd = new ProgressDialog(activity);
			pd.setCanceledOnTouchOutside(false);
			pd.show();
			// 足球app的註冊
			NetWorkManager.getInstance(activity).setNewPassword(phone, verificationCode, password, new Listener<Void>() {
				@Override
				public void onResponse(Void response) {
					DialogUtil.dialogDismiss(pd);
					ShowToast("修改密码成功!");
					finish();
				}
			}, new ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {
					DialogUtil.dialogDismiss(pd);
					if (error.networkResponse == null) {
						ShowToast("修改密码失败，请稍后重试");
					} else if (error.networkResponse.statusCode == 400) {// 400
																			// 参数无效
						ShowToast("无效参数，请稍后重试");
					} else if (error.networkResponse.statusCode == 404) {
						if (error.networkResponse.data != null) {
							Gson gson = new Gson();
							ErrorDescription errDescription = gson.fromJson(new String(error.networkResponse.data),
									new TypeToken<ErrorDescription>() {
							}.getType());
							if (errDescription == null) {
								ShowToast("修改密码失败，请稍后重试");
								return;
							}
							if ("account".equals(errDescription.getError())) {
								ShowToast("手机号码对应的用户找不到");
								return;
							}
							if ("verifycode".equals(errDescription.getError())) {
								ShowToast("没有找到用户对应的验证码");
								return;
							}
							ShowToast("修改密码失败，请稍后重试");
						} else {
							ShowToast("修改密码失败，请稍后重试");
						}
						return;
					} else if (error.networkResponse.statusCode == 403) {
						if (error.networkResponse.data != null) {
							Gson gson = new Gson();
							ErrorDescription errDescription = gson.fromJson(new String(error.networkResponse.data),
									new TypeToken<ErrorDescription>() {
							}.getType());
							if (errDescription == null) {
								ShowToast("修改密码失败，请稍后重试");
								return;
							}
							if ("verifycode is expired".equals(errDescription.getDescription())) {
								ShowToast("验证码过期");
								return;
							}
							if ("verifycode not match".equals(errDescription.getDescription())) {
								ShowToast("验证码不正确");
								return;
							}
							ShowToast("修改密码失败，请稍后重试");
						} else {
							ShowToast("修改密码失败，请稍后重试");
						}
						return;
					} else {
						ShowToast("修改密码失败，请稍后重试");
					}
				}
			});

		} else if (id == id_tv_unreceive_identify) {
			SHOWDIALOGTYPE = 1;
			// 没有接收到短信
			showDialog(SHOWDIALOGTYPE);
		} else if (id == id_iv_clear) {
			etIdentifyNum.getText().clear();
		} else if (id == id_iv_clear1){
			edtPwd.getText().clear();
		} else if (id == id_iv_clear2){
			edtPwd2.getText().clear();
		}
	}

	/** 弹出重新发送短信对话框,或发送语音窗口 */
	private void showDialog(int type) {
		if (type == 1) {
			int resId = getStyleRes(activity, "CommonDialog");
			if (resId > 0) {
				final Dialog dialog = new Dialog(getContext(), resId);
				TextView tv = new TextView(getContext());
				if (type == 1) {
					resId = getStringRes(activity, "smssdk_resend_identify_code");
				} else {
					resId = getStringRes(activity, "smssdk_send_sounds_identify_code");
				}
				if (resId > 0) {
					tv.setText(resId);
				}
				tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
				resId = getColorRes(activity, "smssdk_white");
				if (resId > 0) {
					tv.setTextColor(getContext().getResources().getColor(resId));
				}
				int dp_10 = dipToPx(getContext(), 10);
				tv.setPadding(dp_10, dp_10, dp_10, dp_10);

				dialog.setContentView(tv);
				tv.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						dialog.dismiss();
						tvUnreceiveIdentify.setEnabled(false);

						if (pd != null && pd.isShowing()) {
							pd.dismiss();
						}
						pd = CommonDialog.ProgressDialog(activity);
						if (pd != null) {
							pd.show();
						}
						// 重新获取验证码短信
						NetWorkManager.getInstance(activity).getVerifycode2ResetPassword(phone.trim(),
								new Listener<Void>() {

							@Override
							public void onResponse(Void response) {
								DialogUtil.dialogDismiss(pd);
								time = RETRY_INTERVAL;
								countDown();
							}
						}, new ErrorListener() {

							@Override
							public void onErrorResponse(VolleyError error) {
								DialogUtil.dialogDismiss(pd);
								if (error.networkResponse == null) {
									Toast.makeText(activity, "获取验证码失败，请稍后重试", Toast.LENGTH_LONG).show();
								} else if (error.networkResponse.statusCode == 404) {// 404
																						// 手机号码对应的用户找不到
									Toast.makeText(activity, "该号码暂无注册信息", Toast.LENGTH_LONG).show();
								} else if (error.networkResponse.statusCode == 417) {
									Toast.makeText(activity, "获取验证码失败，请稍后重试", Toast.LENGTH_LONG).show();
								} else {
									Toast.makeText(activity, "获取验证码失败，请稍后重试", Toast.LENGTH_LONG).show();
								}
							}
						});
					}
				});

				dialog.setCanceledOnTouchOutside(true);
				dialog.setOnCancelListener(new OnCancelListener() {

					@Override
					public void onCancel(DialogInterface dialog) {
						tvUnreceiveIdentify.setEnabled(true);
					}
				});
				dialog.show();
			}
		}
	}

	/** 按返回键时，弹出的提示对话框 */
	private void showNotifyDialog() {
		int resId = getStyleRes(activity, "CommonDialog");
		if (resId > 0) {
			final Dialog dialog = new Dialog(getContext(), resId);
			resId = getLayoutRes(activity, "smssdk_back_verify_dialog");
			if (resId > 0) {
				dialog.setContentView(resId);
				resId = getIdRes(activity, "tv_dialog_hint");
				TextView tv = (TextView) dialog.findViewById(resId);
				resId = getStringRes(activity, "smssdk_close_identify_page_dialog");
				if (resId > 0) {
					tv.setText(resId);
				}
				resId = getIdRes(activity, "btn_dialog_ok");
				Button waitBtn = (Button) dialog.findViewById(resId);
				resId = getStringRes(activity, "smssdk_wait");
				if (resId > 0) {
					waitBtn.setText(resId);
				}
				waitBtn.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
				resId = getIdRes(activity, "btn_dialog_cancel");
				Button backBtn = (Button) dialog.findViewById(resId);
				resId = getStringRes(activity, "smssdk_back");
				if (resId > 0) {
					backBtn.setText(resId);
				}
				backBtn.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						dialog.dismiss();
						finish();
					}
				});
				dialog.setCanceledOnTouchOutside(true);
				dialog.show();
			}
		}
	}

	@Override
	public boolean onKeyEvent(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
			runOnUIThread(new Runnable() {
				public void run() {
					showNotifyDialog();
				}
			});
			return true;
		} else {
			return false;
		}
	}

	public void ShowToast(final String text) {
		if (!TextUtils.isEmpty(text)) {
			Toast.makeText(activity, text, Toast.LENGTH_LONG).show();
		}
	}
}
