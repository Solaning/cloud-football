package com.kinth.football.util;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;

public class QuitWay {
	
	public static List<Activity> activityList = new ArrayList<Activity>();

	public static void finishAll(){
		for(Activity activity : QuitWay.activityList){
			if(activity != null){
				activity.finish();
			}
		}
	}
}
