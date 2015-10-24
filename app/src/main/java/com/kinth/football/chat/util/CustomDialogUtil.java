package com.kinth.football.chat.util;

import com.kinth.football.R;
import com.kinth.football.chat.listener.OnDialogItemOnClickListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CustomDialogUtil implements OnClickListener{

	private LinearLayout linLayout = null;
	private TextView txtTitle = null;
	private View vUnderLine = null;
	private TextView txtContent = null;
	private LinearLayout llItems = null;
	private Button btnComfirm = null;
	private Button btnCancel = null;
	
	public AlertDialog dlgComfirm = null;
	
	private static volatile CustomDialogUtil INSTANCE;
	private static Object INSTANCE_LOCK = new Object();
 
	/**
	 * 单例模式<br>
	 * 所谓单例模式就是一个类有且只有一个实例，不像object ob=new object();的这种方式去实例化后去使用。<br>
	 * 如果创造出多个实例，就会导致许多问题产生。例如：程序的行为异常、资源使用过量，or不一致的结果。
	 * @param context
	 * @return
	 */
	public static CustomDialogUtil getInstance(Context context) {
		if (INSTANCE == null)
			synchronized (INSTANCE_LOCK) {
				if (INSTANCE == null)
					INSTANCE = new CustomDialogUtil();
				INSTANCE.initialiseUI(context);
			}
		return INSTANCE;
	}
	
	private void initialiseUI(Context context) {
		// TODO 自动生成的方法存根
		linLayout = (LinearLayout) ((Activity)context).getLayoutInflater().inflate(R.layout.dialog_comfirm, null);
		txtTitle = (TextView) linLayout.findViewById(R.id.dialog_title);
		vUnderLine = linLayout.findViewById(R.id.title_underline);
		txtContent = (TextView) linLayout.findViewById(R.id.dialog_content);
		llItems = (LinearLayout) linLayout.findViewById(R.id.ll_items);
		btnComfirm = (Button) linLayout.findViewById(R.id.btn_comfirm);
		btnCancel = (Button) linLayout.findViewById(R.id.btn_cancel);
		
		txtTitle.setText("更新5.1.2");		//做测试用,可注释掉
		txtContent.setText("1.列表布局再优");	//做测试用,可注释掉
		txtContent.setMovementMethod(ScrollingMovementMethod.getInstance());  //设置TextView可以滚动
		btnComfirm.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
	}

	public void showCustomDailog(Context mContext) {
		// TODO 自动生成的方法存根
		dlgComfirm = new AlertDialog.Builder(mContext).create();
		if(linLayout.getParent()!=null)
			((ViewGroup) linLayout.getParent()).removeView(linLayout);
		dlgComfirm.setView(linLayout,0,0,0,0);	//去除黑边
		dlgComfirm.show();
	}

	@Override
	public void onClick(View v) {
		// TODO 自动生成的方法存根
		switch(v.getId()){
			case R.id.btn_comfirm:
				dlgComfirm.dismiss();
				break;
			case R.id.btn_cancel:
				dlgComfirm.dismiss();
				break;
		}
	}
	
	public void setTitleColor(int color) {
		txtTitle.setTextColor(color);
		vUnderLine.setBackgroundColor(color);
	}
	
	public void setTitleText(String text) {
		txtTitle.setText(text);
	}
	
	public void setContentText(String text) {
		txtContent.setText(text);
	}
	
	public void setItems(Context mcontext, final String[] items, final OnDialogItemOnClickListener listener){
		txtContent.setVisibility(View.GONE);
		llItems.removeAllViews();
		for(int i=0; i<items.length; i++){
			Button btnItem = (Button) ((Activity)mcontext).getLayoutInflater().inflate(R.layout.btn_item, null); 
			btnItem.setText(items[i]);
			final int key = i;
			btnItem.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO 自动生成的方法存根
					dlgComfirm.dismiss();
					listener.onDialogItemOnClick(key,items[key]);
				}
			});
			llItems.addView(btnItem);
		}
		btnComfirm.setVisibility(View.GONE);
		btnCancel.setVisibility(View.GONE);
	}
	
	public void setComfirmText(String text) {
		btnComfirm.setText(text);
	}
	
	public void setCancelText(String text) {
		btnCancel.setText(text);
	}
	
	public void removeComfirmBtn() {
		btnComfirm.setVisibility(View.GONE);
	}
	
	public void removeCancelBtn() {
		btnCancel.setVisibility(View.GONE);
	}

	public void setConfirmOnClickListener(OnClickListener listener) {
		// TODO 自动生成的方法存根
		btnComfirm.setOnClickListener(listener);
	}
	
}
