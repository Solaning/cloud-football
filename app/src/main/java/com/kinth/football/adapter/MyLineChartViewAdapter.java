package com.kinth.football.adapter;

import java.util.List;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;

/**
 *  填充ViewPager页面的适配器(球员信息与主界面中的两个LineChart)
 * @author Botision.Huang
 * Date: 2015-4-4
 * Descp:
 */
public class MyLineChartViewAdapter extends PagerAdapter {

	private Context mContext;
	private int[] linearLayoutIDs;
	private List<LinearLayout> linearLayouts;
	
	public MyLineChartViewAdapter(Context mContext, int[] linearLayoutIDs, List<LinearLayout> linearLayouts){
		this.mContext = mContext;
		this.linearLayoutIDs = linearLayoutIDs;
		this.linearLayouts = linearLayouts;
	}
	
	@Override
	public int getCount() {
		return linearLayoutIDs.length;
	}

	@Override
	public Object instantiateItem(View arg0, int arg1) {
		((ViewPager) arg0).addView(linearLayouts.get(arg1));
		return linearLayouts.get(arg1);
	}

	@Override
	public void destroyItem(View arg0, int arg1, Object arg2) {
		((ViewPager) arg0).removeView((View) arg2);
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1) {

	}

	@Override
	public Parcelable saveState() {
		return null;
	}

	@Override
	public void startUpdate(View arg0) {

	}

	@Override
	public void finishUpdate(View arg0) {

	}

}
