package com.kinth.football.adapter;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.kinth.football.R;
import com.kinth.football.bean.match.MatchInfo;
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

/**
 * 待开赛的adapter
 * @author Sola
 *
 */
public class MatchPendingAdapter extends BaseAdapter{
	private Context mContext;
	private LayoutInflater mInflater;
	private List<MatchInfo> matchList;//日程列表
	
	public MatchPendingAdapter(Context mContext, List<MatchInfo> matchList) {
		super();
		this.mContext = mContext;
		this.matchList = matchList;
		this.mInflater = LayoutInflater.from(mContext);
	}

	public List<MatchInfo> getMatchList() {
		return matchList;
	}

	public void setMatchList(List<MatchInfo> matchList) {
		this.matchList = matchList;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return matchList == null ? 0 : matchList.size();
	}

	@Override
	public MatchInfo getItem(int position) {
		// TODO Auto-generated method stub
		return matchList == null ? null : matchList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_match_list_no_score, parent , false);
		} 
		ImageView homeTeamIcon = ViewHolder.get(convertView, R.id.iv_home_team_icon);
		TextView homeTeamName = ViewHolder.get(convertView, R.id.tv_home_team_name);
		TextView matchdate = ViewHolder.get(convertView, R.id.tv_match_date);
		ImageView awayTeamIcon = ViewHolder.get(convertView, R.id.iv_away_team_icon);
		TextView awayTeamName = ViewHolder.get(convertView, R.id.tv_away_team_name);
		TextView matchField = ViewHolder.get(convertView, R.id.tv_match_field);
//		TextView matchDescription = ViewHolder.get(convertView, R.id.tv_match_description);
		
		awayTeamName.setText(matchList.get(position).getAwayTeam().getName());
		homeTeamName.setText(matchList.get(position).getHomeTeam().getName());
		matchdate.setText(StringUtils.defaultIfEmpty(DateUtil.parseTimeInMillis_no_ss(matchList.get(position).getKickOff()),""));
		matchField.setText(StringUtils.defaultIfEmpty(matchList.get(position).getField(),""));
//		matchDescription.setText(StringUtils.defaultIfEmpty(matchInfoList.get(position).getName(),""));
		
		PictureUtil.getMd5PathByUrl(PhotoUtil.getThumbnailPath(matchList.get(position).getHomeTeam().getBadge()), 
				homeTeamIcon,
				new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.team_bage_default)
		.showImageForEmptyUri(R.drawable.team_bage_default)
		.showImageOnFail(R.drawable.team_bage_default).build());
		PictureUtil.getMd5PathByUrl(PhotoUtil.getThumbnailPath(matchList.get(position).getAwayTeam().getBadge()), 
				awayTeamIcon,
				new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.team_bage_default)
		.showImageForEmptyUri(R.drawable.team_bage_default)
		.showImageOnFail(R.drawable.team_bage_default).build());
		
		return convertView;
	}

}
