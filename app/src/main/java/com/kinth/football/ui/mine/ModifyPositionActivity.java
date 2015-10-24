package com.kinth.football.ui.mine;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.kinth.football.R;
import com.kinth.football.bean.User;
import com.kinth.football.config.PlayerPositionEnum;
import com.kinth.football.eventbus.bean.ModifyPlayerPositionEvent;
import com.kinth.football.network.NetWorkManager;
import com.kinth.football.singleton.SingletonBitmapClass;
import com.kinth.football.singleton.ViewCompat;
import com.kinth.football.ui.BaseActivity;
import com.kinth.football.util.ErrorCodeUtil;

import de.greenrobot.event.EventBus;

public class ModifyPositionActivity extends BaseActivity {

	String[] name = { PlayerPositionEnum.GK.getName(),
			PlayerPositionEnum.CB.getName(), PlayerPositionEnum.LCB.getName(),
			PlayerPositionEnum.RCB.getName(), PlayerPositionEnum.LWB.getName(),
			PlayerPositionEnum.RWB.getName(), PlayerPositionEnum.CDM.getName(),
			PlayerPositionEnum.LCM.getName(), PlayerPositionEnum.RCM.getName(),
			PlayerPositionEnum.CM.getName(), PlayerPositionEnum.LWM.getName(),
			PlayerPositionEnum.RWM.getName(), PlayerPositionEnum.CAM.getName(),
			PlayerPositionEnum.LF.getName(), PlayerPositionEnum.RF.getName(),
			PlayerPositionEnum.CF.getName(), PlayerPositionEnum.ST.getName(),
			PlayerPositionEnum.SS.getName(), PlayerPositionEnum.LW.getName(),
			PlayerPositionEnum.RW.getName() };
	String[] value = { PlayerPositionEnum.GK.getValue(),
			PlayerPositionEnum.CB.getValue(),
			PlayerPositionEnum.LCB.getValue(),
			PlayerPositionEnum.RCB.getValue(),
			PlayerPositionEnum.LWB.getValue(),
			PlayerPositionEnum.RWB.getValue(),
			PlayerPositionEnum.CDM.getValue(),
			PlayerPositionEnum.LCM.getValue(),
			PlayerPositionEnum.RCM.getValue(),
			PlayerPositionEnum.CM.getValue(),
			PlayerPositionEnum.LWM.getValue(),
			PlayerPositionEnum.RWM.getValue(),
			PlayerPositionEnum.CAM.getValue(),
			PlayerPositionEnum.LF.getValue(),
			 PlayerPositionEnum.RF.getValue(),
			PlayerPositionEnum.CF.getValue(), PlayerPositionEnum.ST.getValue(),
			PlayerPositionEnum.SS.getValue(), PlayerPositionEnum.LW.getValue(),
			PlayerPositionEnum.RW.getValue() };
	private int isFromRegist;

	private LinearLayout set_userpositon_lin;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_position);
		
		set_userpositon_lin = (LinearLayout)this.findViewById(R.id.set_userpositon_lin);
		// 設置背景
		Bitmap background = SingletonBitmapClass.getInstance()
				.getBackgroundBitmap();
		ViewCompat.setBackground(set_userpositon_lin, new BitmapDrawable(getResources(),
				background));
		
		isFromRegist = getIntent().getIntExtra("isFromRegist", 0);
		initTopBarForLeft("修改位置");
		ListView listView = (ListView) this
				.findViewById(R.id.listView_position);
		listView.setAdapter(new MyAdater());
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
	
				if (isFromRegist == 1) {
					Intent intent = getIntent();
					getIntent().putExtra("position", value[arg2]);
					setResult(555, intent);
					finish();
				} else {
					executeUpdateUserPosition(value[arg2]);
				}
			}
		});
	}

	class MyAdater extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return name.length;
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			arg1 = LayoutInflater.from(mContext).inflate(
					R.layout.activity_set_position_listview, null);
			// 门将:GK, 中后卫:CB, 左后卫:LCB, 右后卫:RCB, 左边后卫:LWB, 右边后卫:RWB, 后腰:CDM,
			// 左前卫:LCM, 右前卫:RCM, 前卫:CM, 左边前卫:LWM, 右边前卫:RWM, 前腰:CAM, 左前锋:LF,
			// 右前锋:RF, 前锋:CF, 中锋:ST, 影子前锋:SS, 左边锋:LW, 右边锋:RW

			TextView txt_position = (TextView) arg1
					.findViewById(R.id.txt_position);
			for (int i = 0; i < name.length; i++) {
				txt_position.setText(name[arg0] + "  " + value[arg0]);
			}
			return arg1;
		}

	}

	private ProgressDialog progress = null; // 进度

	private void executeUpdateUserPosition(final String position) {
		progress = new ProgressDialog(this);
		progress.setMessage("正在修改...");
		progress.setCanceledOnTouchOutside(false);
		progress.setCancelable(false);
		progress.show();
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("position", position);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		NetWorkManager.getInstance(getApplicationContext()).monifyUserInfo(footBallUserManager.getCurrentUser().getToken(), jsonObject, new Listener<Void>() {
			@Override
			public void onResponse(Void response) {
				ShowToast("修改成功 !");
				dismissProgress();
				User user = footBallUserManager.getCurrentUser();
				user.getPlayer().setPosition(position);
				
				EventBus.getDefault().post(new ModifyPlayerPositionEvent(position));
				footBallUserManager.saveCurrentUser(user);
				finish();
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				//ShowToast("修改失败，请稍后再试 !");
				if(!NetWorkManager.getInstance(mContext).isNetConnected()){
					ShowToast("没有可用的网络");
				}else if(error.networkResponse == null){
//					ShowToast("ModifyPositionActivity-executeUpdateUserPosition-服务器连接错误");
				}else if(error.networkResponse.statusCode == 401){  //access token无效
						ErrorCodeUtil.ErrorCode401(mContext);
				}
				dismissProgress();
			}
		});
	}

	private void dismissProgress() {
		if (progress != null && progress.isShowing()) {
			progress.dismiss();
		}
	}
}
