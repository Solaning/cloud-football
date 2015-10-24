package com.kinth.football.ui.cloud5ranking;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kinth.football.R;
import com.kinth.football.util.ImageLoadOptions;
import com.kinth.football.util.ViewHolder;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class Clound5RankingAdapter extends BaseAdapter {

	private Context mContext;
	private List<CloudFive> cloudFiveList;
	private String type;
	private LayoutInflater mInflater;
	private Typeface tf;

	public Clound5RankingAdapter(Context mContext) {
		super();
		this.mContext = mContext;
		cloudFiveList = new ArrayList<CloudFive>();
		mInflater = LayoutInflater.from(mContext);
		tf = Typeface.createFromAsset(mContext.getAssets(),
				"fonts/AGENCYB_0.TTF");
	}

	public void updateListView(List<CloudFive> cloudFiveList, String type) {
		this.cloudFiveList = cloudFiveList;
		this.type = type;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO 自动生成的方法存根
		return cloudFiveList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO 自动生成的方法存根
		return cloudFiveList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO 自动生成的方法存根
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO 自动生成的方法存根
		if (convertView == null) {
			convertView = mInflater
					.inflate(R.layout.item_clound5_ranking, null);
		}
		TextView tvRanking = ViewHolder.get(convertView, R.id.tv_ranking);
		ImageView ivMemberPic = ViewHolder.get(convertView,
				R.id.iv_member_picture);
		TextView tvMemberName = ViewHolder
				.get(convertView, R.id.tv_member_name);
		TextView tvScore = ViewHolder.get(convertView, R.id.tv_score);

		tvRanking.setText(cloudFiveList.get(position).getRank()+"");
		tvRanking.setTypeface(tf);
		ImageLoader.getInstance().displayImage(
				cloudFiveList.get(position).getPicture() + "Q03.jpg",
				ivMemberPic,
				new DisplayImageOptions.Builder()
						.showImageOnLoading(R.drawable.icon_default_head)
						.showImageForEmptyUri(R.drawable.icon_default_head)
						.showImageOnFail(R.drawable.icon_default_head)
						.cacheInMemory(true).cacheOnDisk(true).build());
		tvMemberName.setText(cloudFiveList.get(position).getName());
		
		String strScore = "";
		if(type.equals(Cloud5RankingTypeEnum.COMPOSITE.getValue()))
			strScore = Math.floor((cloudFiveList.get(position).getComposite() == null ? 0 : cloudFiveList.get(position).getComposite())*10d)/10 + "";
		else if(type.equals(Cloud5RankingTypeEnum.ATTACK.getValue()))
			strScore = Math.floor((cloudFiveList.get(position).getAttack() == null ? 0 : cloudFiveList.get(position).getAttack())*10d)/10 + "";
		else if(type.equals(Cloud5RankingTypeEnum.DEFENCE.getValue()))
			strScore = Math.floor((cloudFiveList.get(position).getDefence() == null ? 0 : cloudFiveList.get(position).getDefence())*10d)/10 + "";
		else if(type.equals(Cloud5RankingTypeEnum.AWARENESS.getValue()))
			strScore = Math.floor((cloudFiveList.get(position).getAwareness() == null ? 0 : cloudFiveList.get(position).getAwareness())*10d)/10 + "";
		else if(type.equals(Cloud5RankingTypeEnum.SKILL.getValue()))
			strScore = Math.floor((cloudFiveList.get(position).getSkill() == null ? 0 : cloudFiveList.get(position).getSkill())*10d)/10 + "";
		else if(type.equals(Cloud5RankingTypeEnum.STRENGTH.getValue()))
			strScore = Math.floor((cloudFiveList.get(position).getStrength() == null ? 0:cloudFiveList.get(position).getStrength())*10d)/10 + "";
		
		tvScore.setText(strScore);
		tvScore.setTypeface(tf);

		return convertView;
	}

}
