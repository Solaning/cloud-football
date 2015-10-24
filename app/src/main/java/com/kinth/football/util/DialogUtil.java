package com.kinth.football.util;

import android.app.Dialog;

/**
 * 对话框工具类
 * @author Sola
 *
 */
public class DialogUtil {
	
	public static void dialogDismiss(Dialog dialog){
		if(dialog != null && dialog.isShowing()){
			dialog.dismiss();
		}
	}
}
