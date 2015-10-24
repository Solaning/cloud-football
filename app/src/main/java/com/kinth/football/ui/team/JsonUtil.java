package com.kinth.football.ui.team;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.kinth.football.bean.AddressBean;

import android.content.Context;
import android.content.res.AssetManager;
  
public class JsonUtil {
	
	private Context context;
	private String strJson;
	
	public JsonUtil(Context paramContext, String fileName){
		this.context = paramContext;
		strJson = getStrJson(fileName);
	}
	
    public String getStrJson(String fileName) {
  
        StringBuilder sb = new StringBuilder();  
        try {
            AssetManager am = context.getAssets();  
            BufferedReader bf = new BufferedReader(new InputStreamReader(  
            		am.open(fileName)));  
            String line;  
            while ((line = bf.readLine()) != null) {  
            	sb.append(line);  
            }  
        } catch (IOException e) {
            e.printStackTrace();  
        }  
        return sb.toString();
    }  
    
    public String getProvinceId(String provinceName) throws JSONException{
    	JSONArray jsonArr = new JSONArray(strJson);
    	for (int i = 0; i < jsonArr.length(); i++) {
    		if(jsonArr.getJSONObject(i).getString("name").equals(provinceName))
    			return jsonArr.getJSONObject(i).getString("id");
    	}
    	return null;
    }
    
    public ArrayList<AddressBean> getCities(String provinceId) throws JSONException{
    	JSONObject jsonObj = new JSONObject(strJson);
		String provinceJsonStr = jsonObj.get(provinceId).toString();
    	
    	JSONArray jsonArr = new JSONArray(provinceJsonStr);
    	ArrayList<AddressBean> cities = new ArrayList<AddressBean>();
    	for (int i = 0; i < jsonArr.length(); i++) {
    		AddressBean cityBean = new AddressBean();
    		cityBean.setCityId(jsonArr.getJSONObject(i).getInt("id"));
    		cityBean.setCityName(jsonArr.getJSONObject(i).getString("name"));
    		cities.add(cityBean);
    	}
    	
    	return cities;
    }

}  
