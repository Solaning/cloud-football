package com.kinth.football.ui.team.formation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.kinth.football.R;
import com.kinth.football.bean.match.PlayerInTeam;
import com.kinth.football.util.PhotoUtil;
import com.kinth.football.view.RoundImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class GalleryAdapter extends BaseAdapter {

	private final Context mContext;

	public List<PlayerInTeam> mTeammates;
	
	public HashMap<String, PlayerInTeam> recycledTeammates;

	public GalleryAdapter(final Context context) {
		mContext = context;
		mTeammates = new ArrayList<PlayerInTeam>();
		recycledTeammates = new HashMap<String, PlayerInTeam>();
	}

	@Override
	public int getCount() {
		return mTeammates.size();
	}
	
	public void updateListView(List<PlayerInTeam> mTeammates) {
		this.mTeammates = mTeammates;
		notifyDataSetChanged();
	}
	
	public void recycleItem(String key, PlayerInTeam value){
		recycledTeammates.put(key, value);
	}
	
	public void addItemBack(String name){
		mTeammates.add(recycledTeammates.get(name));
		notifyDataSetChanged();
	}
	
	public void removeItem(int position){
		mTeammates.remove(position);
		notifyDataSetChanged();
	}
	
	@Override
	public Object getItem(final int position) {
		return mTeammates.get(position);
	}

	@Override
	public long getItemId(final int position) {
		return position;
	}

	@Override
	public View getView(final int position, final View convertView,
			final ViewGroup parent) {
		FrameLayout view;
		RoundImageView ibHead;
		TextView tvName;

		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = (FrameLayout) inflater.inflate(R.layout.gallery_item,
					parent, false);
			ibHead = (RoundImageView) view.findViewById(R.id.teammate_head);
			ImageLoader.getInstance().displayImage(
					PhotoUtil.getAllPhotoPath(mTeammates.get(position).getPlayer().getPicture()), 
					ibHead,
					new DisplayImageOptions.Builder()
					.showImageOnLoading(R.drawable.icon_default_head)
					.showImageForEmptyUri(R.drawable.icon_default_head)
					.showImageOnFail(R.drawable.icon_default_head)
					.cacheInMemory(true).cacheOnDisk(true).build());
			tvName = (TextView) view.findViewById(R.id.teammate_name);
			tvName.setText(mTeammates.get(position).getPlayer().getName());
		} else {
			view = (FrameLayout) convertView;
			ibHead = (RoundImageView) view.findViewById(R.id.teammate_head);
			ImageLoader.getInstance().displayImage(
					PhotoUtil.getAllPhotoPath(mTeammates.get(position).getPlayer().getPicture()), 
					ibHead,
					new DisplayImageOptions.Builder()
					.showImageOnLoading(R.drawable.icon_default_head)
					.showImageForEmptyUri(R.drawable.icon_default_head)
					.showImageOnFail(R.drawable.icon_default_head)
					.cacheInMemory(true).cacheOnDisk(true).build());
		}
		
		return view;
	}
}
