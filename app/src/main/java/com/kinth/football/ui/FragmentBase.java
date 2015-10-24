package com.kinth.football.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.kinth.football.CustomApplcation;
import com.kinth.football.R;
import com.kinth.football.util.LogUtil;
import com.kinth.football.view.HeaderLayout;
import com.kinth.football.view.HeaderLayout.HeaderStyle;
import com.kinth.football.view.HeaderLayout.onLeftImageButtonClickListener;
import com.kinth.football.view.HeaderLayout.onRightImageButtonClickListener;

/** Fragmenet 基类
  * @ClassName: FragmentBase
  * @Description: TODO
  * @author smile
  * @date 2014-5-22 下午2:43:50
  */
public abstract class FragmentBase extends Fragment {
	
	/**
	 * 公用的Header布局
	 */
	public HeaderLayout mHeaderLayout;

	protected View contentView;
	
	public LayoutInflater mInflater;
	protected Context mContext;
	
	@Override
	public void onAttach(Activity activity) {
		mContext = activity;
		super.onAttach(activity);
	}

	public void runOnWorkThread(Runnable action) {
		new Thread(action).start();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		mInflater = LayoutInflater.from(getActivity());
	}
	
	public FragmentBase() {
		
	}

	Toast mToast;

	public void ShowToast(String text) {
		if (mToast == null) {
			mToast = Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT);
		} else {
			mToast.setText(text);
		}
		mToast.show();
	}

	public void ShowToast(int text) {
		if (mToast == null) {
			mToast = Toast.makeText(getActivity(), text, Toast.LENGTH_LONG);
		} else {
			mToast.setText(text);
		}
		mToast.show();
	}

	
	/** 打Log
	  * ShowLog
	  * @return void
	  * @throws
	  */
	public void ShowLog(String msg){
		LogUtil.i(msg);
	}
	
	public View findViewById(int paramInt) {
		return getView().findViewById(paramInt);
	}

	/**
	 * 只有title initTopBarLayoutByTitle
	 * @Title: initTopBarLayoutByTitle
	 * @throws
	 */
	public void initTopBarForOnlyTitle(String titleName) {
		mHeaderLayout = (HeaderLayout)findViewById(R.id.common_actionbar);
		mHeaderLayout.init(HeaderStyle.DEFAULT_TITLE);
		mHeaderLayout.setDefaultTitle(titleName);
	}

	/**
	 * 初始化标题栏-带左右按钮
	 * 
	 * @return void
	 * @throws
	 */
	public void initTopBarForBoth(String titleName, int rightDrawableId,
			onRightImageButtonClickListener listener) {
		mHeaderLayout = (HeaderLayout)findViewById(R.id.common_actionbar);
		mHeaderLayout.init(HeaderStyle.TITLE_DOUBLE_IMAGEBUTTON);
		mHeaderLayout.setTitleAndLeftImageButton(titleName,
				R.drawable.base_action_bar_back_bg_selector,
				new OnLeftButtonClickListener());
		mHeaderLayout.setTitleAndRightImageButton(titleName, rightDrawableId,
				listener);
	}

	/**
	 * 只有左边按钮和Title initTopBarLayout
	 * 
	 * @throws
	 */
	public void initTopBarForLeft(String titleName) {
		mHeaderLayout = (HeaderLayout)findViewById(R.id.common_actionbar);
		mHeaderLayout.init(HeaderStyle.TITLE_LIFT_IMAGEBUTTON);
		mHeaderLayout.setTitleAndLeftImageButton(titleName,
				R.drawable.base_action_bar_back_bg_selector,
				new OnLeftButtonClickListener());
	}
	
	/** 右边+title
	  * initTopBarForRight
	  * @return void
	  * @throws
	  */
	public void initTopBarForRight(String titleName,int rightDrawableId,
			onRightImageButtonClickListener listener) {
		mHeaderLayout = (HeaderLayout)findViewById(R.id.common_actionbar);
		mHeaderLayout.init(HeaderStyle.TITLE_RIGHT_IMAGEBUTTON);
		mHeaderLayout.setTitleAndRightImageButton(titleName, rightDrawableId,
				listener);
	}
	
	// 左边按钮的点击事件
	public class OnLeftButtonClickListener implements
			onLeftImageButtonClickListener {

		@Override
		public void onClick() {
			getActivity().finish();
		}
	}
	
	/**
	 * 动画启动页面 startAnimActivity
	 * @throws
	 */
	public void startAnimActivity(Intent intent) {
		this.startActivity(intent);
	}
	
	public void startAnimActivity(Class<?> cla) {
		getActivity().startActivity(new Intent(getActivity(), cla));
	}
}
