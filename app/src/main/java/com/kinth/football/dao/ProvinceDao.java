package com.kinth.football.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import com.kinth.football.bean.Province;
import com.kinth.football.database.EventsData;

//省份
public class ProvinceDao {

	private EventsData events = null;
	private SQLiteDatabase db = null;

	public ProvinceDao(Context context) {
		super();
		events = new EventsData(context);
	}

	//获取所有省份的名称
	public List<String> getProvinceList(){
		List<String> result = new ArrayList<String>();
		open();
		Cursor cursor = db.rawQuery("SELECT * FROM province " , null);
		while (cursor.moveToNext()) {
			String provinceName = cursor.getString(1);
			result.add(provinceName);
		}
		cursor.close();
		close();
		return result;
	} 

	//通过名称获取省份id
	public int getProvinceByName(String provinceName) {
		int provinceId = -1;
		open();
		Cursor cursor = db.rawQuery("SELECT * FROM province WHERE name LIKE '"
				+ provinceName + "'", null);
		if (cursor.moveToNext()) {
			provinceId = Integer.valueOf(cursor.getString(0));
		}
		cursor.close();
		close();
		return provinceId;
	}

	//通过cityProId获取Province实体
	public Province getProvinceByCityProId(int cityProId) {
		Province pro = null;
		if(cityProId < 0){
			return pro;
		}
		open();
		Cursor cursor = db.rawQuery("SELECT * FROM province WHERE id = "
				+ cityProId, null);
		if (cursor != null && cursor.moveToFirst()) {
			pro = new Province();
			pro.setId(cursor.getInt(cursor.getColumnIndex("id")));
			pro.setName(cursor.getString(cursor.getColumnIndex("name")));
		}
		cursor.close();
		close();
		return pro;
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
