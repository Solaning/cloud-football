package com.kinth.football.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import com.kinth.football.bean.AddressBean;
import com.kinth.football.bean.City;
import com.kinth.football.database.EventsData;

public class CityDao {

	private EventsData events = null;
	private SQLiteDatabase db =null;
	
	public CityDao(Context context) {
		super();
		events = new EventsData(context);
	}
	
	/**
	 * 通过省份id获取城市列表
	 * @param provinceId
	 * @return
	 */
	public List<String> getCityListByProvinceId(String provinceId){
		List<String> result = new ArrayList<String>();
		open();
		Cursor cursor = db.rawQuery("SELECT * FROM city WHERE province_id = "+ provinceId , null);
		while (cursor.moveToNext()) {
			String cityName = cursor.getString(1);
			result.add(cityName);
		}
		cursor.close();
		close();
		return result;
	} 
	
	public List<AddressBean> getCityListByProvinceID(String ProvinceID){
		List<AddressBean> cities = new ArrayList<AddressBean>();
		open();
		Cursor cursor = db.rawQuery("SELECT * FROM city WHERE province_id = "+ ProvinceID , null);
		
		while(cursor.moveToNext()){
			AddressBean cityBean = new AddressBean();
			cityBean.setCityId(cursor.getInt(0));
			cityBean.setCityName(cursor.getString(1));
			cities.add(cityBean);
		}
		cursor.close();
		close();
		return cities;
	}
	
	public int getCityByName(String cityName){
		int cityId = -1;
		open();
		Cursor cursor = db.rawQuery("SELECT * FROM city WHERE name LIKE '"+ cityName +"'", null);
		if (cursor.moveToNext()) {
			cityId = Integer.valueOf(cursor.getString(0));
		}
		cursor.close();
		close();
		return cityId;
	} 
	
	public City getCityByCityId(int cityId) {
		City city = null;
		if(cityId < 0){
			return city;
		}
		open();
		Cursor cursor = db.rawQuery("SELECT * FROM city WHERE id = " + cityId, null);
		
		if(cursor != null && cursor.moveToFirst()){
			city = new City();
			city.setId(cursor.getInt(cursor.getColumnIndex("id")));
			city.setName(cursor.getString(cursor.getColumnIndex("name")));
			city.setProvince_id(cursor.getInt(cursor.getColumnIndex("province_id")));
		}
		cursor.close();
		close();
		return city;
	}
	
	public void closeSQLiteDatabase(){
		if (db != null)
			db.close();
	}
	
	public void open() throws SQLException{
		db = events.getWritableDatabase();
	}
	
	public void close() throws SQLException{
		events.close();
	}
}
