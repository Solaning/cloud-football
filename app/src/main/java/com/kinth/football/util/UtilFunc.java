package com.kinth.football.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import com.kinth.football.bean.Parameters;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources.NotFoundException;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class UtilFunc {
	
	//将raw文件夹下的文件内容转换为字符串String
	public static String getRawFileStr(Context context, int resId) {
		String rslt = "";
		InputStream is = null;
		BufferedReader br = null;
		String temp;
		try {
			is = context.getResources().openRawResource(resId);
			br = new BufferedReader(new InputStreamReader(is, "utf-8"));
			while ((temp = br.readLine()) != null) {
				rslt += temp;
			}
		} catch (NotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return rslt;
	}
	
	public static <T> List<T> getClassObjListFromJsonStr(String jsonStr,
			Class<T> t) {
		ArrayList<T> list = new ArrayList<T>();
		try {
			JSONArray jsonArray = new JSONArray(jsonStr);
			int len = jsonArray.length();
			for (int i = 0; i < len; i++) {
				T temp = getClassObjFromJson(jsonArray.getString(i), t);
				if (temp != null) {
					list.add(temp);
				}
			}
			return list;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
//			FileOperation.writeFileAppend(UtilFunc.getCurrentTime()
//					+ UtilFunc.getExceptionString(e));
			list = null;
		}
		return list;
	}
	
	public static <T> T getClassObjFromJson(String json, Class<T> t) {
		return JSONUtils.fromJson(json, t);
	}
	
	/**
	 * 通过类名称来加载类
	 * @param mContext
	 * @param className
	 * @return
	 */
	public static Class<?> loadClass(Context mContext, String className){
		if(TextUtils.isEmpty(className))
			return null;
		Class<?> clazz = null;
		try {
            // 载入这个类 
            clazz = mContext.getClassLoader().loadClass(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
		return clazz;
	}
	
	/**
	 * 获取一下手机初始化信息
	 * 
	 * @param activity
	 */
	public static Parameters getCommonParameters(Context activity) {
		TelephonyManager tm = (TelephonyManager) activity
				.getSystemService(Context.TELEPHONY_SERVICE);
		Parameters parameter = new Parameters();
		parameter.setCFB_Udid(tm.getDeviceId());//udid
		parameter.setCFB_Device(Build.MODEL);//可见名字
		
		DisplayMetrics dm = new DisplayMetrics();
		WindowManager windowManager = (WindowManager) activity
				.getSystemService(Context.WINDOW_SERVICE);
		windowManager.getDefaultDisplay().getMetrics(dm);
		parameter.setmScreenWidth(dm.widthPixels);
		parameter.setmScreenHeight(dm.heightPixels);
		StringBuffer sb = new StringBuffer();
		sb.append(parameter.getmScreenHeight()).append("_").append(parameter.getmScreenWidth());
		parameter.setCFB_Resolution(sb.toString());//分辨率
		
		parameter.setCFB_Platform_Version(Build.VERSION.SDK_INT + "_" + Build.VERSION.RELEASE);//系统版本
		parameter.setCFB_App_Version(getVersionName(activity));
		parameter.setCFB_App_Source("GUANWANG");//GUANWANG - 官网
		parameter.setCFB_Operator(tm.getNetworkOperatorName());//运营商
		return parameter;
	}
	
	//获取当前应用的版本号：
	public static String getVersionName(Context mContext){
		// 获取packagemanager的实例
		PackageManager packageManager = mContext.getPackageManager();
		// getPackageName()是你当前类的包名，0代表是获取版本信息
		PackageInfo packInfo = null;
		try {
			packInfo = packageManager.getPackageInfo(
					mContext.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return "";
		}
		String version = packInfo.versionName;
		return version;
	}
	
	public static int getCurrentVersion(Context context) {
		PackageManager pm = context.getPackageManager();
		try {
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
			return pi.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return 0;
		}
	}
	
    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
