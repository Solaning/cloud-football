package com.kinth.football.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kinth.football.R;
import com.kinth.football.dao.Team;
import com.kinth.football.util.PhotoUtil;
import com.kinth.football.util.PictureUtil;
import com.kinth.football.util.ViewHolder;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 搜索比赛对手adapter
 * @author Sola
 *
 */
public class SearchRivingAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mInflater;
	private List<Team> searchTeamList;
	private OnClickRivingListener listener;
	
	public SearchRivingAdapter(Context mContext, List<Team> searchTeamList, 
			OnClickRivingListener listener) {
		this.mContext = mContext;
		this.searchTeamList = searchTeamList;
		this.listener = listener;
		this.mInflater = LayoutInflater.from(mContext);
	}
	
	/**
	 * 点击邀请对手比赛
	 * @author Sola
	 *
	 */
	public interface OnClickRivingListener {
		public void onClickRiving(Team teamResponse);
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
		return searchTeamList == null ? null : searchTeamList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if(convertView == null){
			convertView = mInflater.inflate(R.layout.item_select_riving_layout, parent, false);
		}
		RelativeLayout item = ViewHolder.get(convertView, R.id.rtl_select_riving_item);
		ImageView picture = ViewHolder.get(convertView,R.id.iv_team_picture);
		TextView name = ViewHolder.get(convertView,R.id.tv_team_name);
		TextView other = ViewHolder.get(convertView,R.id.tv_team_description);
		Button challenge = ViewHolder.get(convertView, R.id.btn_challenge);
		
		name.setText(searchTeamList.get(position).getName());
		other.setText(searchTeamList.get(position).getDescription());
		
		PictureUtil.getMd5PathByUrl(PhotoUtil.getThumbnailPath(searchTeamList.get(position).getBadge()), 
				picture,
				new DisplayImageOptions.Builder()
					.showImageOnLoading(R.drawable.team_bage_default)
					.showImageForEmptyUri(R.drawable.team_bage_default)
					.showImageOnFail(R.drawable.team_bage_default).build());
		
		//点击“查看”按钮事件
		challenge.setOnClickListener(new OnClickListener() {//点击邀请
			@Override
			public void onClick(View v) {
				if(listener != null){
					listener.onClickRiving(searchTeamList.get(position));
				}
			}
		});
		
		return convertView;
	}
}
