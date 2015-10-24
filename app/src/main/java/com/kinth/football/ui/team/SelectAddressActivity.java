/*
 Copyright 2013 Tonic Artos

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package com.kinth.football.ui.team;

import java.util.ArrayList;
import java.util.List;

import kankan.wheel.widget.OnWheelScrollListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.AbstractWheelTextAdapter;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kinth.football.R;
import com.kinth.football.bean.AddressBean;
import com.kinth.football.dao.CityDao;
import com.kinth.football.dao.ProvinceDao;
import com.kinth.football.singleton.SingletonBitmapClass;
import com.kinth.football.singleton.ViewCompat;

/**
 * An activity representing a list of Items. This activity has different
 * presentations for handset and tablet-size devices. On handsets, the activity
 * presents a list of items, which when touched, lead to a
 * {@link ItemDetailActivity} representing item details. On tablets, the
 * activity presents the list of items and item details side-by-side using two
 * vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link ItemListFragment} and the item details (if present) is a
 * {@link ItemDetailFragment}.
 * <p>
 * This activity also implements the required {@link ItemListFragment.Callbacks}
 * interface to listen for item selections.
 * 
 * @author Tonic Artos
 */
/**
 * 选择城市与地区
 *
 */
public class SelectAddressActivity extends ActionBarActivity implements
		ItemListFragment.Callbacks {
	
	public static final String NO_CITY_IN_PROVINCE = "noCity";  //该省份下面没有城市
	public static final String NO_REGION_IN_CITY = "noRegion";  //该城市下面没有地区
	public static final int NO_CITY_IN_PROVINCE_CODE = 900001;  //该省份下面没有城市,此时系统给定ID
	public static final int NO_REGION_IN_CITY_CODE = 900002;  //该城市下面没有地区，此时系统给定ID
	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean mTwoPane;
	private Dialog dlgCityNArea;
	private CityAdapter cityAdapter;
//	private AreaAdapter areaAdapter;
//	private WheelView areas;
	
	private String provinceName;
	/**
	 * Callback method from {@link ItemListFragment.Callbacks} indicating that
	 * the item with the given ID was selected.
	 */
	@Override
	public void onItemSelected(int id) {
		if (mTwoPane) {
			Bundle arguments = new Bundle();
			arguments.putInt(ItemDetailFragment.ARG_ITEM_ID, id);
			ItemDetailFragment fragment = new ItemDetailFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.item_detail_container, fragment).commit();
		} else {
			String itemName = getResources().getStringArray(R.array.countries)[id];
			provinceName = itemName.substring(itemName.lastIndexOf(".") + 1);
			int provinceId = new ProvinceDao(SelectAddressActivity.this)
					.getProvinceByName(provinceName);
			if(provinceId > -1){
				showCityNAreaDialog(String.valueOf(provinceId));
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(android.R.style.Theme_Black_NoTitleBar);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_item_list);
		
		LinearLayout sele_addres_lin = (LinearLayout)this.findViewById(R.id.sele_addres_lin);
		// 設置背景
		Bitmap background = SingletonBitmapClass.getInstance()
				.getBackgroundBitmap();
		ViewCompat.setBackground(sele_addres_lin, new BitmapDrawable(getResources(),
				background));
		
		TextView tvTitle = (TextView) findViewById(R.id.nav_title);
		tvTitle.setText("选择地区");
		
		ImageButton ibLeft = (ImageButton) findViewById(R.id.nav_left);
		ibLeft.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO 自动生成的方法存根
				finish();
			}
			
		});

		if (findViewById(R.id.item_detail_container) != null) {
			mTwoPane = true;

			((ItemListFragment) getSupportFragmentManager().findFragmentById(
					R.id.item_list)).setActivateOnItemClick(true);
		}
	}

	public void showCityNAreaDialog(String provinceId) {
		
		final FrameLayout ll = (FrameLayout) LayoutInflater.from(this).inflate(
				R.layout.dialog_city_n_area, null);
		
		final WheelView cities = (WheelView) ll.findViewById(R.id.city);   //显示“城市”的滚动栏
		//areas = (WheelView) ll.findViewById(R.id.area);                     //显示“地区”的滚动栏
		
		cityAdapter = new CityAdapter(this, provinceId);
		cities.setVisibleItems(5); // Number of items
		cities.setViewAdapter(cityAdapter);
		cities.setWheelForeground(R.drawable.wheel_val_holo);
		cities.setShadowColor(0xffffffff, 0xffffffff, 0x00FFFFFF);
		cities.setCurrentItem(0);   
		cities.addScrollingListener(new OnWheelScrollListener() {
			@Override
			public void onScrollingStarted(WheelView wheel) {
				// TODO 自动生成的方法存根
			}
			
			@Override
			public void onScrollingFinished(WheelView wheel) {
				// TODO 自动生成的方法存根
				String cityId = String.valueOf(cityAdapter.getCityId(cities.getCurrentItem()));
				
				//areaAdapter = new AreaAdapter(SelectAddressActivity.this, cityId);
				//areas.setViewAdapter(areaAdapter);
			}
		});
		
		//当开始显示dlgCityNArea时，将默认第一个城市下面的地区列举出来
//		List<AddressBean> cityList = getCityListByProvinceID(provinceId);
//		AddressBean address = cityList.get(0);  //默认获取得到第一个城市,因为在城市WheelView中默认显示第一个城市
//		areaAdapter = new AreaAdapter(SelectAddressActivity.this, String.valueOf(address.getCityId()));
//		areas.setWheelForeground(R.drawable.wheel_val_holo);
//		areas.setShadowColor(0xffffffff, 0xffffffff, 0x00FFFFFF);
//		areas.setCurrentItem(1);
//		areas.setViewAdapter(areaAdapter);

		Button btnOk = (Button)ll.findViewById(R.id.btn_dialog_ok);
		btnOk.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dlgCityNArea.dismiss();
				
				String cityName = (String) cityAdapter.getItemText(cities
						.getCurrentItem());
				int cityId = cityAdapter.getCityId(cities.getCurrentItem());
				
//				String areaName = (String)areaAdapter.getItemText(areas.getCurrentItem());
//				int areaId = areaAdapter.getAreaId(areas.getCurrentItem());
				
				AddressBean city = new AddressBean();
				city.setProvinceName(provinceName);
				if(cityId == NO_CITY_IN_PROVINCE_CODE){
					city.setCityId(NO_CITY_IN_PROVINCE_CODE);
				}else{
					city.setCityId(cityId);
				}
				if(cityName.equals(NO_CITY_IN_PROVINCE)){
					city.setCityName(NO_CITY_IN_PROVINCE);
				}else{
					city.setCityName(cityName);
				}
//				if(areaId == NO_REGION_IN_CITY_CODE){
//					city.setRegionId(NO_REGION_IN_CITY_CODE);
//				}else{
//					city.setRegionId(areaId);
//				}
//				if(areaName.equals(NO_REGION_IN_CITY)){
//					city.setRegionName(NO_REGION_IN_CITY);
//				}else{
//					city.setRegionName(areaName);
//				}
//				System.out.println("城市ID======" + cityId);
//				System.out.println("城市名======" + cityName);
//				System.out.println("当前选中的地区ID======" + areaId);
//				System.out.println("当前选中的地名======" + areaName);
				
				Intent intent = new Intent();
				intent.putExtra(CreateTeamActivity.RESULT_SELECT_CITY, city);
				SelectAddressActivity.this.setResult(RESULT_OK, intent);
				SelectAddressActivity.this.finish();
			}
		});

		Button btnCancel = (Button)ll.findViewById(R.id.btn_dialog_cancel);
		btnCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dlgCityNArea.dismiss();
			}
		});

		Builder builder = new Builder(this);
		builder.setView(ll);
		builder.create();
		dlgCityNArea = builder.show();
	}

	/**
	 * Adapter for City
	 */
	private class CityAdapter extends AbstractWheelTextAdapter {
		// City names
		List<AddressBean> cities = new ArrayList<AddressBean>();

		protected CityAdapter(Context context, String provinceId) {
			super(context, R.layout.city_holo_layout, NO_RESOURCE);
			setItemTextResource(R.id.city_name);
			
			cities = new CityDao(context).getCityListByProvinceID(provinceId);
		}

		@Override
		public View getItem(int index, View cachedView, ViewGroup parent) {
			View view = super.getItem(index, cachedView, parent);
			return view;
		}

		@Override
		public int getItemsCount() {
			return cities.size();
		}

		@Override
		protected CharSequence getItemText(int index) {
			if(cities.size() == 0) {
				return NO_CITY_IN_PROVINCE;
			} else {
				return cities.get(index).getCityName();
			}
		}

		public int getCityId(int index) {
			if(cities.size() == 0){
				return NO_CITY_IN_PROVINCE_CODE;  
			}else{
				return cities.get(index).getCityId();
			}
			
		}
	}

//	/**
//	 * Adapter for Area 
//	 * 
//	 */
//	private class AreaAdapter extends AbstractWheelTextAdapter {
//		List<RegionBean> areas = new ArrayList<RegionBean>();
//
//		protected AreaAdapter(Context context, String cityId) {
//			super(context, R.layout.city_holo_layout, NO_RESOURCE);
//			setItemTextResource(R.id.city_name);
//			
//			Cursor cursorRegion = new RegionDao(SelectAddressActivity.this).getData(cityId);
//			startManagingCursor(cursorRegion);
//			
//			while(cursorRegion.moveToNext()){
//				RegionBean regionBean = new RegionBean();
//				regionBean.setId(cursorRegion.getInt(0));
//				regionBean.setName(cursorRegion.getString(1));
//				areas.add(regionBean);
//				//System.out.println("根据城市ID获取得到的地区=" + cursorRegion.getString(1));
//			}
//		}
//
//		@Override
//		public View getItem(int index, View cachedView, ViewGroup parent) {
//			View view = super.getItem(index, cachedView, parent);
//			return view;
//		}
//
//		@Override
//		public int getItemsCount() {
//			return areas.size();
//		}
//
//		@Override
//		protected CharSequence getItemText(int index) {
//			if(areas.size() == 0){     //说明该城市下面没有地区
//				return NO_REGION_IN_CITY;
//			}else{
//				return areas.get(index).getName();
//			}
//		}
//		
//		public int getAreaId(int index) {
//			if(areas.size() == 0){
//				return NO_REGION_IN_CITY_CODE;
//			}else{
//				return areas.get(index).getId();
//			}
//		}
//	}
	
}
