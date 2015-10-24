package com.kinth.football.adapter;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kinth.football.R;
import com.kinth.football.bean.TeamInfoComplete;
import com.kinth.football.util.PhotoUtil;
import com.kinth.football.util.PictureUtil;
import com.kinth.football.util.ViewHolder;
import com.kinth.football.view.RoundImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

/**
 * 我的球队的Adapter
 * @author Sola
 *
 */
public class MyTeamListAdapter extends BaseAdapter{
	private Context mContext;
	private LayoutInflater mInflater;
	private List<TeamInfoComplete> teamList;

	public MyTeamListAdapter(Context mContext, List<TeamInfoComplete> teamList) {
		super();
		this.mContext = mContext;
		this.teamList = teamList;
		this.mInflater = LayoutInflater.from(mContext);
	}
	
	public List<TeamInfoComplete> getTeamList() {
		return teamList;
	}

	public synchronized void setTeamList(List<TeamInfoComplete> teamList) {
		this.teamList = teamList;
		notifyDataSetChanged();
	}
	
	public void updateTeamInfoComplete(TeamInfoComplete team){//更新新的球队信息
		for(int i = 0; i < teamList.size(); i++){
			if(teamList.get(i).getTeam().getUuid().equals(team.getTeam().getUuid())){
				teamList.set(i, team);
				notifyDataSetChanged();
			}
		}
	}

	public void addTeamListBean(TeamInfoComplete teamWithPlayerNum){
		if(this.teamList == null){
			this.teamList = new ArrayList<TeamInfoComplete>();
		}
		this.teamList.add(teamWithPlayerNum);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return teamList == null ? 0 : teamList.size();
	}

	@Override
	public TeamInfoComplete getItem(int position) {
		// TODO Auto-generated method stub
		return teamList == null ? null : teamList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(
					R.layout.item_my_team, parent, false);
		}
		RoundImageView badge = ViewHolder.get(convertView, R.id.iv_team_badge);//队徽
		TextView name = ViewHolder.get(convertView, R.id.tv_team_name);//球队名称
		TextView descp = ViewHolder.get(convertView, R.id.team_descp);
		TextView teamPlayerNum = ViewHolder.get(convertView, R.id.tv_team_player_num);
		
		name.setText("队名： " + StringUtils.defaultString(teamList.get(position).getTeam().getName(), ""));
		descp.setText("介绍： "
				+ StringUtils.defaultString(teamList.get(position).getTeam()
						.getDescription(), "暂无介绍"));
		PictureUtil.getMd5PathByUrl(
				PhotoUtil.getThumbnailPath(teamList.get(position).getTeam()
						.getBadge()),
				badge,
				new DisplayImageOptions.Builder()
						.showImageOnLoading(
								R.drawable.team_bage_default)
						.showImageForEmptyUri(R.drawable.team_bage_default)
						.showImageOnFail(R.drawable.team_bage_default) // 默认球队队徽
						.cacheInMemory(true).cacheOnDisk(true).build());

		teamPlayerNum.setText((teamList.get(position).getPlayers() == null ? 0 : teamList.get(position).getPlayers().size()) + "人");
		
//		//获取城市名
//		String cityName = new CityDao(mContext).getCityByCityId(teamList.get(position).getTeam().getCityId()).getName();
//		//获取省份名
//		int cityProId = new CityDao(mContext).getCityByCityId(teamList.get(position).getTeam().getCityId()).getProvince_id();
//		String proName = new ProvinceDao(mContext).getProvinceByCityProId(cityProId).getName();
		
		return convertView;
	}

}
