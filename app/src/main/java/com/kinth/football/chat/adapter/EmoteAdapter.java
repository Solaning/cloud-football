package com.kinth.football.chat.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.kinth.football.R;
import com.kinth.football.chat.bean.FaceText;

public class EmoteAdapter extends BaseAdapter {
	List<FaceText> datas;
	Context mContext;
	private LayoutInflater mInflater;

	public EmoteAdapter(Context context,List<FaceText> datas) {
		super();
		this.datas = datas;
		this.mContext = context;
		mInflater = LayoutInflater.from(mContext);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return datas.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return datas.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolder holder;

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_face_text, null);
		
			holder = new ViewHolder();
			holder.mIvImage = (ImageView) convertView.findViewById(R.id.v_face_text);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		FaceText faceText = (FaceText) getItem(position);
		String key = faceText.text.substring(1);
		key = key.substring(key.indexOf("[")+1, key.indexOf("]"));
		Drawable drawable =mContext.getResources().getDrawable(mContext.getResources().getIdentifier(key, "drawable", mContext.getPackageName()));
		holder.mIvImage.setImageDrawable(drawable);
		
		return convertView;
	}
	class ViewHolder { 
		ImageView mIvImage;
	}
}