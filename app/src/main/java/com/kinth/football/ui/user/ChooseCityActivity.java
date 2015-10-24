package com.kinth.football.ui.user;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.kinth.football.R;
import com.kinth.football.adapter.EreaListAdapter;
import com.kinth.football.bean.AreaInfo;
import com.kinth.football.bean.User;
import com.kinth.football.network.UserNetworkManager;
import com.kinth.football.ui.BaseActivity;
import com.kinth.football.util.UtilFunc;

public class ChooseCityActivity extends BaseActivity {
	private ListView lv;

	private List<AreaInfo> citys = new ArrayList<AreaInfo>();
	EreaListAdapter ereaListAdapter;
	String province = "";
	String cityString = "";
	ProgressDialog progress ;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_choose_province);
		String cityString = getIntent().getStringExtra("city");
		province = getIntent().getStringExtra("province");
		
		citys = UtilFunc.getClassObjListFromJsonStr(cityString, AreaInfo.class);
		if (citys.equals("")) {
			ShowToast("解析城市出错");
			ChooseCityActivity.this.finish();
		}
		lv = (ListView) this.findViewById(R.id.choose_province_lv);
		initTopBarForLeft("选择地区");
		initList();
	}

	private void initList() {
		ereaListAdapter = new EreaListAdapter(ChooseCityActivity.this, citys);
		lv.setAdapter(ereaListAdapter);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				final int mCityID = citys.get(arg2).getId();
				final String mCityName = citys.get(arg2).getName();
				progress = new ProgressDialog(
						ChooseCityActivity.this);
				progress.setMessage("正在提交...");
				progress.setCanceledOnTouchOutside(false);
				progress.show();
				String token = footBallUserManager.getCurrentUser().getToken();
				
				UserNetworkManager.getInstance(getApplicationContext()).modifyUserErea(mCityID, token, new Listener<Void>() {
					@Override
					public void onResponse(Void response) {
						User user = footBallUserManager.getCurrentUser();
						user.getPlayer().setCity(mCityName);
						user.getPlayer().setProvince(province);
						user.getPlayer().setCityId(mCityID);
						
						footBallUserManager.saveCurrentUser(user);
						
						ShowToast("修改成功");
						ChooseCityActivity.this.finish();
						dismiss(progress);
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						ShowToast("修改失败");
						dismiss(progress);
					}
				});
			}
		});
	}
	
	private void dismiss(ProgressDialog progressDialog) {
		if (progressDialog!=null&&progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
	}
}
