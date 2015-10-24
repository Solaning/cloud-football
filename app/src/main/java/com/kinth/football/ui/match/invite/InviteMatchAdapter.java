package com.kinth.football.ui.match.invite;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.kinth.football.R;
import com.kinth.football.bean.match.MatchInfo;
import com.kinth.football.ui.team.formation.Formation;
import com.kinth.football.util.DateUtil;
import com.kinth.football.util.PhotoUtil;
import com.kinth.football.util.PictureUtil;
import com.kinth.football.util.ViewHolder;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class InviteMatchAdapter extends BaseAdapter{

	private Context mContext;
	private List<MatchInfo> matchInfoList;
	private LayoutInflater mInflater;
	
	public InviteMatchAdapter(Context mContext) {
		// TODO 自动生成的构造函数存根
		this.mContext = mContext;
		this.matchInfoList = new ArrayList<MatchInfo>();
		this.mInflater = LayoutInflater.from(mContext);
	}

	public void updateListView(List<MatchInfo> matchInfoList) {
		this.matchInfoList = matchInfoList;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		// TODO 自动生成的方法存根
		return matchInfoList == null ? 0:matchInfoList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO 自动生成的方法存根
		if(position==-1)//点击正在加载会报错
			return null;
		return matchInfoList == null ? null : matchInfoList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO 自动生成的方法存根
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO 自动生成的方法存根
		if(convertView==null){
			convertView = mInflater.inflate(R.layout.item_match_list_inviting, null);
		}
		ImageView homeTeamIcon = ViewHolder.get(convertView, R.id.iv_home_team_icon);
		TextView homeTeamName = ViewHolder.get(convertView, R.id.tv_home_team_name);
		TextView matchDate = ViewHolder.get(convertView, R.id.tv_match_date);
		TextView matchField = ViewHolder.get(convertView, R.id.tv_match_field);
		
		homeTeamName.setText(matchInfoList.get(position).getHomeTeam().getName());
		matchDate.setText(StringUtils.defaultIfEmpty(DateUtil.parseTimeInMillis_no_ss(matchInfoList.get(position).getKickOff()),""));
		matchField.setText(StringUtils.defaultIfEmpty(matchInfoList.get(position).getField(),""));
		
		PictureUtil.getMd5PathByUrl(PhotoUtil.getThumbnailPath(matchInfoList.get(position).getHomeTeam().getBadge()), 
				homeTeamIcon,
				new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.team_bage_default)
		.showImageForEmptyUri(R.drawable.team_bage_default)
		.showImageOnFail(R.drawable.team_bage_default).build());
		
		return convertView;
	}

}
