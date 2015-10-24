package com.kinth.football.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kinth.football.R;
import com.kinth.football.dao.Player;
import com.kinth.football.dao.Team;
import com.kinth.football.util.PhotoUtil;
import com.kinth.football.util.PictureUtil;
import com.kinth.football.util.ViewHolder;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 搜索裁判
 * @author Sola
 *
 */
public class SearchReffreeAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mInflater;
	private List<Player> playerList;
	private OnClickRivingListener listener;

	
	public SearchReffreeAdapter(Context mContext,List<Player> playerList, 
			OnClickRivingListener listener) {
		this.mContext = mContext;
		this.playerList = playerList;
		this.listener = listener;
		this.mInflater = LayoutInflater.from(mContext);
	}
	
	/**
	 * 点击邀请对手比赛
	 * @author Sola
	 *
	 */
	public interface OnClickRivingListener {
		public void onClickRiving(Player player);
	}
	
	public List<Player> getplayerList() {
		return playerList;
	}
	
	public void setplayerList(List<Player> playerList) {
		this.playerList = playerList;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return playerList == null ? 0 : playerList.size();
	}

	@Override
	public Player getItem(int position) {
		return playerList == null ? null : playerList.get(position);
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
		challenge.setText("邀请 ");
		
		name.setText(playerList.get(position).getName());
		other.setText("");
		
		PictureUtil.getMd5PathByUrl(PhotoUtil.getThumbnailPath(playerList.get(position).getPicture()), 
				picture,
				new DisplayImageOptions.Builder()
					.showImageOnLoading(R.drawable.icon_default_head)
					.showImageForEmptyUri(R.drawable.icon_default_head)
					.showImageOnFail(R.drawable.icon_default_head).build());
		
		challenge.setOnClickListener(new OnClickListener() {//点击邀请
			@Override
			public void onClick(View v) {
				if(listener != null){
					listener.onClickRiving(playerList.get(position));
				}
			}
		});
		
		return convertView;
	}
}
