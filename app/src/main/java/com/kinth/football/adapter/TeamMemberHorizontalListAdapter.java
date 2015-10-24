package com.kinth.football.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.kinth.football.R;
import com.kinth.football.bean.match.PlayerInTeam;
import com.kinth.football.config.PlayerTypeEnum;
import com.kinth.football.util.PhotoUtil;
import com.kinth.football.util.PictureUtil;
import com.kinth.football.util.ViewHolder;
import com.kinth.football.view.RoundImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 横向的球队成员适配器
 * @author Sola
 * 加入了排序 --zyq 5.13
 */
public class TeamMemberHorizontalListAdapter extends BaseAdapter{

	private Context mContext;
	private List<PlayerInTeam> playerList;
	private LayoutInflater mInflater;
	
	private List<PlayerInTeam> playerList_order; //排序后的list
	private HashMap<String, Integer> hashMap = new HashMap<String, Integer>();
	
	public TeamMemberHorizontalListAdapter(Context mContext,
			List<PlayerInTeam> playerList) {
		super();
		this.mContext = mContext;
		this.mInflater = LayoutInflater.from(mContext);
		
		hashMap.put("FIRST_CAPTAIN",1);
		hashMap.put("SECOND_CAPTAIN",2);
		hashMap.put("THIRD_CAPTAIN",3);
		hashMap.put("GENERAL",4);
		if (playerList!=null) { //进行排序
			playerList = new ArrayList<PlayerInTeam>(playerList);
			Comparator<PlayerInTeam> comparator = new Comparator<PlayerInTeam>() {		
				@Override
				public int compare(PlayerInTeam arg0, PlayerInTeam arg1) {
					// TODO Auto-generated method stub
					//顺序比较的逻辑处理
					int i =hashMap.get(arg0.getType())
							.compareTo(hashMap.get(arg1.getType()));
					return i;
				}
			};
			Collections.sort(playerList, comparator);//对集合排序
		}
		this.playerList = playerList;
	}

	public List<PlayerInTeam> getPlayerList() {
		return playerList;
	}

	public void setPlayerList(List<PlayerInTeam> playerList) {
		if (playerList!=null) { //进行排序
			playerList = new ArrayList<PlayerInTeam>(playerList);
			Comparator<PlayerInTeam> comparator = new Comparator<PlayerInTeam>() {		
				@Override
				public int compare(PlayerInTeam arg0, PlayerInTeam arg1) {
					//顺序比较的逻辑处理
					int i = hashMap.get(arg0.getType())
							.compareTo(hashMap.get(arg1.getType()));
					return i;
				}
			};
			Collections.sort(playerList, comparator);//对集合排序

		}
		this.playerList = playerList;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return playerList == null ? 0 : playerList.size();
	}

	@Override
	public PlayerInTeam getItem(int position) {
		// TODO Auto-generated method stub
		return playerList == null ? null : playerList.get(position);
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
					R.layout.activity_match_detail_scrollview, parent, false);
		}
		RoundImageView img = ViewHolder.get(convertView, R.id.matchdetail_img);
		TextView txt =  ViewHolder.get(convertView, R.id.matchdetail_txt);
		ImageView captainTips = ViewHolder.get(convertView, R.id.tv_captain_tips);
		
		TextView numberTips = ViewHolder.get(convertView, R.id.tv_number_tips);
		if (playerList.get(position).getJerseyNo()!=null && playerList.get(position).getJerseyNo()!=-1) {
			numberTips.setVisibility(View.VISIBLE);
			numberTips.setText(playerList.get(position).getJerseyNo()+"");
		}else{
			numberTips.setVisibility(View.GONE);
		}
		PictureUtil.getMd5PathByUrl(PhotoUtil.getThumbnailPath(playerList.get(position).getPlayer().getPicture()), img,new DisplayImageOptions.Builder()
				.showImageOnLoading(
						R.drawable.icon_head)
				.showImageForEmptyUri(R.drawable.icon_head)
				.showImageOnFail(R.drawable.icon_head) // 默认头像
				.cacheInMemory(true).cacheOnDisk(true).build());
		txt.setText(StringUtils.defaultIfEmpty(playerList.get(position).getPlayer().getName(),""));
		
		switch(PlayerTypeEnum.getEnumFromString(playerList.get(position).getType())){
		case FIRST_CAPTAIN:
			captainTips.setVisibility(View.VISIBLE);
			captainTips.setImageResource(R.drawable.icon_duizhang);
			break;
		case SECOND_CAPTAIN:
			captainTips.setVisibility(View.VISIBLE);
			captainTips.setImageResource(R.drawable.icon_duifu);
			break;
		case THIRD_CAPTAIN:
			captainTips.setVisibility(View.VISIBLE);
			captainTips.setImageResource(R.drawable.icon_jiaolian);
			break;
		case GENERAL:
		case NULL:
			captainTips.setVisibility(View.GONE);
			captainTips.setImageResource(0);
			break;
		}
		
		return convertView;
	}

}
