package com.kinth.football.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kinth.football.R;
import com.kinth.football.dao.Player;
import com.kinth.football.dao.Team;
import com.kinth.football.util.PhotoUtil;
import com.kinth.football.util.PictureUtil;
import com.kinth.football.util.ViewHolder;
import com.kinth.football.view.RoundImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
/**
 * 搜索球员适配器
 * @author zyq
 *
 */
public class SearchPlayerInHomeAdapter extends BaseAdapter {
	private Context mContext;
	private LayoutInflater mInflater;
	private List<Player> searchMemberList;

	public SearchPlayerInHomeAdapter(Context mContext,
			List<Player> searchMemberList) {
		super();
		this.mContext = mContext;
		this.searchMemberList = searchMemberList;

		this.mInflater = LayoutInflater.from(mContext);
	}
	
	public List<Player> getSearchMemberList() {
		return searchMemberList;
	}

	public void setSearchMemberList(List<Player> searchMemberList) {
		this.searchMemberList = searchMemberList;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return searchMemberList == null ? 0 : searchMemberList.size();
	}

	@Override
	public Player getItem(int position) {
		// TODO Auto-generated method stub
		return searchMemberList == null ? null : searchMemberList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(
					R.layout.item_search_player_in_home, parent, false);
		}
		RoundImageView picture = ViewHolder.get(convertView,R.id.iv_player_picture);
		TextView name = ViewHolder.get(convertView,R.id.tv_player_name);
		TextView other = ViewHolder.get(convertView,R.id.tv_player_description);
	
		
		PictureUtil.getMd5PathByUrl(PhotoUtil.getThumbnailPath(searchMemberList.get(position).getPicture()), 
				picture,
				new DisplayImageOptions.Builder()
					.showImageOnLoading(
							R.drawable.icon_default_head)
					.showImageForEmptyUri(R.drawable.icon_default_head)
					.showImageOnFail(R.drawable.icon_default_head) // 默认球队队徽
					.cacheInMemory(true).cacheOnDisk(true).build());
		name.setText(searchMemberList.get(position).getName());
		
		//获取省份名
//		if(searchMemberList.get(position).getCityId() != null){
//			int cityProId = new CityDao(mContext).getCityByCityId(searchMemberList.get(position).getCityId()).getProvince_id();
//			String proName = new ProvinceDao(mContext).getProvinceByCityProId(cityProId).getName();
//			other.setText(proName + "   " + searchMemberList.get(position).getCity());
//		}
		return convertView;
	}
}
