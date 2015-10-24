package com.kinth.football.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kinth.football.R;
import com.kinth.football.dao.Team;
import com.kinth.football.util.PhotoUtil;
import com.kinth.football.util.ViewHolder;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 搜索"球队"适配器
 * @author Botision.Huang
 *
 */
public class SearchTeamAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mInflater;
	private List<Team> searchTeamList;
	private OnSearchTeamListener listener;
	
	public SearchTeamAdapter(Context mContext, List<Team> searchTeamList, 
			OnSearchTeamListener listener) {
		this.mContext = mContext;
		this.searchTeamList = searchTeamList;
		this.listener = listener;
		this.mInflater = LayoutInflater.from(mContext);
	}
	
	public interface OnSearchTeamListener {
		public void onSearchTeam(Team bean);
	}
	
	public List<Team> getSearchTeamList() {
		return searchTeamList;
	}
	
	public void setSearchTeamList(List<Team> searchTeamList) {
		this.searchTeamList = searchTeamList;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return searchTeamList == null ? 0 : searchTeamList.size();
	}

	@Override
	public Team getItem(int position) {
		// TODO Auto-generated method stub
		return searchTeamList == null ? null : searchTeamList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if(convertView == null){
			convertView = mInflater.inflate(R.layout.item_search_team, parent, false);
		}
		RelativeLayout item = ViewHolder.get(convertView, R.id.rtl_invite_member_item);
		ImageView picture = ViewHolder.get(convertView,R.id.iv_invite_member_picture);
		TextView name = ViewHolder.get(convertView,R.id.tv_invite_member_name);
		TextView other = ViewHolder.get(convertView,R.id.tv_invite_member_description);
		name.setText(searchTeamList.get(position).getName());
		other.setText(searchTeamList.get(position).getDescription());
		
		ImageLoader.getInstance().displayImage(PhotoUtil.getAllPhotoPath(searchTeamList.get(position).getBadge()), picture);
		
		picture.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub 点击查看大图
				
			}
		});
		item.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(listener != null){
					listener.onSearchTeam(searchTeamList.get(position));
				}
			}
		});
		
		return convertView;
	}
}
