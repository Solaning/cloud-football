package com.kinth.football.ui.team;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.kinth.football.CustomApplcation;
import com.kinth.football.R;
import com.kinth.football.bean.TeamRequest;
import com.kinth.football.dao.Team;
import com.kinth.football.dao.TeamDao;
import com.kinth.football.eventbus.bean.ModifyTeamSloganEvent;
import com.kinth.football.manager.UserManager;
import com.kinth.football.network.NetWorkManager;
import com.kinth.football.singleton.SingletonBitmapClass;
import com.kinth.football.singleton.ViewCompat;
import com.kinth.football.util.DialogUtil;
import com.kinth.football.view.ClearEditText;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import de.greenrobot.event.EventBus;

/**
 * 修改球队口号
 * 
 * @author Sola
 *
 */
@ContentView(R.layout.activity_modify_team_simple_input_info)
public class ModifyTeamSloganActivity extends ModifyTeamInfoBaseActivity {
	private Team team;// 球队实体
	private ProgressDialog proDialog;

	@ViewInject(R.id.nav_left)
	private ImageButton back;

	@ViewInject(R.id.nav_right_btn)
	private Button save;// 保存

	@ViewInject(R.id.nav_title)
	private TextView title;

	@ViewInject(R.id.et_modify_team_attribute)
	private ClearEditText input;
	
	@ViewInject(R.id.linear)
	private LinearLayout linear;
	
	@OnClick(R.id.nav_left)
	public void fun_1(View v) {
		finish();
	}

	@OnClick(R.id.nav_right_btn)
	public void fun_2(View v) {// 保存介绍修改
		final String slogan = input.getText().toString();
		if (TextUtils.isEmpty(slogan)) {
			return;
		}
		TeamRequest teamRequest = new TeamRequest();
		teamRequest.setSlogan(slogan);
		proDialog = ProgressDialog.show(mContext, "提示", "请稍后", false, false);
		NetWorkManager.getInstance(mContext).updateTeamInfo(
				team.getUuid(),
				UserManager.getInstance(mContext).getCurrentUser().getToken(),
				teamRequest, new Listener<Void>() {

					@Override
					public void onResponse(Void response) {
						DialogUtil.dialogDismiss(proDialog);
						Toast.makeText(mContext, "编辑成功", Toast.LENGTH_LONG).show();
						
						updateTeamInfo(slogan);            //顺带修改本地数据库中信息
						EventBus.getDefault().post(new ModifyTeamSloganEvent(team.getUuid(), slogan));
						
						finish();
					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						DialogUtil.dialogDismiss(proDialog);
//						VolleyLog.e("Error: ", error.getMessage());
						Toast.makeText(mContext, "编辑失败", Toast.LENGTH_LONG).show();
					}
				});
	}

	@Override
	public void saveTeamInfo(TeamRequest teamRequest) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);

		// 設置背景
		Bitmap background = SingletonBitmapClass.getInstance()
				.getBackgroundBitmap();
		ViewCompat.setBackground(linear, new BitmapDrawable(getResources(),
				background));
		
		title.setText(getResources().getString(R.string.slogan));
		save.setText(getResources().getString(R.string.save));
		save.setEnabled(false);

		team = getIntent().getParcelableExtra(
				TeamInfoActivity.INTENT_TEAM_INFO_BEAN);
		if (team == null) {
			return;
		}
		input.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				String content = s.toString();
				if (TextUtils.isEmpty(content)) {// 没有其他输入，替换成原来的列表
					save.setEnabled(false);
				} else {// 有输入内容
					save.setEnabled(true);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});
		input.setText(team.getSlogan());
		if (!TextUtils.isEmpty(team.getSlogan())) {
			input.setSelection(team.getSlogan().trim().length());
		}
		
	}

	private void updateTeamInfo(String slogan){
		TeamDao teamDao = CustomApplcation.getDaoSession(mContext).getTeamDao();
		team.setSlogan(slogan);
		teamDao.insertOrReplace(team);
	}
}
