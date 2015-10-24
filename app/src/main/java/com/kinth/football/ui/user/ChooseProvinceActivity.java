package com.kinth.football.ui.user;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.kinth.football.R;
import com.kinth.football.adapter.EreaListAdapter;
import com.kinth.football.bean.AreaInfo;
import com.kinth.football.ui.BaseActivity;
import com.kinth.football.util.UtilFunc;

public class ChooseProvinceActivity extends BaseActivity {
	private static final String TAG = "ChooseProvinceActivity1";
	private ListView lv;             //用于显示省份数据的ListView
	private List<AreaInfo> areaDBList = new ArrayList<AreaInfo>();  //地区数据
	private List<String> nameList = new ArrayList<String>();        //地区名字数据
	
	private String cityStr;
	EreaListAdapter ereaListAdapter; 
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_choose_province);
		
		lv = (ListView) this.findViewById(R.id.choose_province_lv);
		
		initTopBarForLeft("选择地区");
		getAreaInfoFromDb();
		initList();
	}

	private void getAreaInfoFromDb() {
		//从raw获取省份信息
		String areaJsonStr = UtilFunc.getRawFileStr(this,
				R.raw.province);
		areaDBList = UtilFunc.getClassObjListFromJsonStr(areaJsonStr,
				AreaInfo.class);
		
		for (AreaInfo i : areaDBList) {
			nameList.add(i.getName());
		}
		
		cityStr = UtilFunc.getRawFileStr(this,
				R.raw.city);
	}

	private void initList() {
		// TODO Auto-generated method stub
		if (nameList == null || nameList.size() == 0) {
			Log.e(TAG, "map.size() == 0,获取数据库失败");
			return;
		}
		ereaListAdapter = new EreaListAdapter(ChooseProvinceActivity.this, areaDBList);
		
		lv.setAdapter(ereaListAdapter);
		
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				try {
					int cityId = areaDBList.get(arg2).getId();
					String province = areaDBList.get(arg2).getName();
					JSONObject jsonObject = new JSONObject(cityStr);
					String jsonObject1 =  jsonObject.getString(cityId+"");  //根据“省份ID”获取其下的城市JsonArray数据字符串
					
					if (jsonObject1.equals("")) {
						ShowToast("获取城市出错");
					}else{
						Intent intent = new Intent(ChooseProvinceActivity.this,ChooseCityActivity.class);
						intent.putExtra("city", jsonObject1);    //城市ID
						intent.putExtra("province", province);   //该城市对应的省份
 						startAnimActivity(intent);
						
						ChooseProvinceActivity.this.finish();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
			}
		});
		
		/*
		lv.setAdapter(new ArrayAdapter<String>(this,
				R.layout.simple_list_item_1, nameList));
		lv.setClickable(true);
		lv.setCacheColorHint(Color.TRANSPARENT);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
				// TODO Auto-generated method stub
//				String province = nameList.get(arg2);
//				String[] citiesArray = null;
//				for (AreaInfo i : areaDBList) {
//					if (i.getState().equals(province)) {
//						citiesArray = i.getCities().split("##");
//						break;
//					}
//				}
//				ArrayList<String> citiesList = new ArrayList<String>();
//				if (citiesArray != null) {
//					int size = citiesArray.length;
//					for (int i = 0; i < size; i++) {
//						citiesList.add(citiesArray[i]);
//					}
//				}
//				Intent intent = new Intent(ChooseProvinceActivity1.this,
//						ChooseCityActivity.class);
//				intent.putExtra(ChooseCityActivity.PROVINCE, province);
//				intent.putStringArrayListExtra(ChooseCityActivity.CITIES_LIST,
//						citiesList);
//				startActivity(intent);
//				ChooseProvinceActivity1.this.finish();
			}
		});
		*/
	}
}
