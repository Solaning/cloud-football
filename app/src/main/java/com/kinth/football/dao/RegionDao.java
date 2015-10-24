package com.kinth.football.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import com.kinth.football.bean.RegionBean;
import com.kinth.football.database.EventsData;

//区县
public class RegionDao {

	private EventsData events = null;
	public SQLiteDatabase db =null;
	
	public RegionDao(Context context) {
		super();
		events = new EventsData(context);
	}
	
	/**
	 * 通过cityId获取第一个RegionId
	 * @param cityId
	 * @return
	 */
	public int getRegionIdByCityId(int cityId){
		int regionId = -1;
		open();
		Cursor cursor = db.rawQuery("SELECT * FROM region WHERE city_id = "+ cityId , null);
		if (cursor.moveToFirst()) {
			regionId = cursor.getInt(0);
		}
		cursor.close();
		close();
		return regionId;
	}
	/**
	 * 通过区县id获取区县列表
	 * @param provinceId
	 * @return
	 */
	public List<String> getRegionNameListByCityId(int cityId){
		List<String> result = new ArrayList<String>();
		open();
		Cursor cursor = db.rawQuery("SELECT * FROM region WHERE city_id = "+ cityId , null);
		while (cursor.moveToNext()) {
			String regionName = cursor.getString(1);
			result.add(regionName);
		}
		cursor.close();
		close();
		return result;
	} 
	
	/**
	 * 通过城市id获取区县的Region实体
	 * @param ProvinceID
	 * @return
	 */
	public List<RegionBean> getRegionListByProvinceID(int cityId){
		List<RegionBean> regions = new ArrayList<RegionBean>();
		open();
		Cursor cursor = db.rawQuery("SELECT * FROM region WHERE city_id = "+ cityId , null);
		
		while(cursor.moveToNext()){
			RegionBean regionBean = new RegionBean();
			regionBean.setId(cursor.getInt(0));
			regionBean.setName(cursor.getString(1));
			regions.add(regionBean);
		}
		
		cursor.close();
		close();
		return regions;
	}
	
	//通过名称查询区县id
	public int getRegionIdByName(String cityName){
		int reginId = -1;
		open();
		Cursor cursor = db.rawQuery("SELECT * FROM region WHERE name LIKE '"+ cityName +"'", null);
		if (cursor.moveToNext())
			reginId = Integer.valueOf(cursor.getString(0));
		cursor.close();
		close();
		return reginId;
	}
	
	//通过id获取Region的名称
	public RegionBean getRegionById(int regionId) {
		RegionBean region = null;
		if(regionId < 0){
			return region;
		}
		open();
		Cursor cursor = db.rawQuery("SELECT * FROM region WHERE id = " + regionId, null);
		
		if(cursor != null && cursor.moveToFirst()){
			region = new RegionBean();
			region.setId(cursor.getInt(cursor.getColumnIndex("id")));
			region.setName(cursor.getString(cursor.getColumnIndex("name")));
			region.setCityId(cursor.getInt(cursor.getColumnIndex("city_id")));
		}
		cursor.close();
		close();
		return region;
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
