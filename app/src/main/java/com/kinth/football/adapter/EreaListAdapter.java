package com.kinth.football.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kinth.football.R;
import com.kinth.football.bean.AreaInfo;
import com.kinth.football.util.ImageLoadOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class EreaListAdapter extends BaseAdapter {
	private List<AreaInfo>mYoudians;
	private Context mContext;
	
	public EreaListAdapter(Context context, List<AreaInfo> list) {
		this.mYoudians = new ArrayList<AreaInfo>();
		this.mYoudians.addAll(list);
		mContext = context;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mYoudians.size();
	}
	
	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return mYoudians.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			LayoutInflater mInflater = LayoutInflater.from(mContext);
			convertView = mInflater.inflate(R.layout.item_list_erea,
					parent, false);
			holder = new ViewHolder();
			holder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		AreaInfo areaInfo = (AreaInfo) getItem(position);
		
		if (null != areaInfo)
        {
        	holder.tvName.setText(areaInfo.getName());
        }

		return convertView;
	}

	/**
	 * 		TextView tv_name = ViewHolder.get(convertView, R.id.tv_name);
		TextView tvLocationName = ViewHolder.get(convertView, R.id.tv_location_name);
		TextView tvNum = ViewHolder.get(convertView, R.id.tv_num);
		TextView tvDisatance =  ViewHolder.get(convertView, R.id.tv_distance);
		ImageView ivIcon =  ViewHolder.get(convertView, R.id.iv_icon);
		TextView tvLeft =  ViewHolder.get(convertView, R.id.tv_left);
	 * @author admin
	 *
	 */
	static class ViewHolder {
		TextView tvName;
	}

}
