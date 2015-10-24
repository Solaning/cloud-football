package com.kinth.football.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.kinth.football.dao.Player;
import com.kinth.football.manager.UserManager;
import com.kinth.football.ui.mine.SetMyInfoActivity;
import com.kinth.football.ui.team.TeamPlayerInfo;
import com.kinth.football.util.PhotoUtil;
import com.kinth.football.util.PictureUtil;
import com.kinth.football.util.ViewHolder;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class InviteMemberAdapter extends BaseAdapter{
	private Context mContext;
	private LayoutInflater mInflater;
	private List<Player> searchMemberList;
	private OnInviteMemberListener listener;

	public InviteMemberAdapter(Context mContext,
			List<Player> searchMemberList, OnInviteMemberListener listener) {
		super();
		this.mContext = mContext;
		this.searchMemberList = searchMemberList;
		this.listener = listener;
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

	public interface OnInviteMemberListener{
		public void onInviteMember(Player bean);
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(
					R.layout.item_invite_member, parent, false);
		}
		RelativeLayout item = ViewHolder.get(convertView, R.id.rtl_invite_member_item);
		ImageView picture = ViewHolder.get(convertView,R.id.iv_invite_member_picture);
		TextView name = ViewHolder.get(convertView,R.id.tv_invite_member_name);
		Button invite = ViewHolder.get(convertView, R.id.but_invite);
		invite.setVisibility(View.VISIBLE);
		PictureUtil.getMd5PathByUrl(
				PhotoUtil.getThumbnailPath(searchMemberList.get(position).getPicture()),
				picture, new DisplayImageOptions.Builder()
						.showImageOnLoading(R.drawable.icon_head)
						.showImageForEmptyUri(R.drawable.icon_head)
						.showImageOnFail(R.drawable.icon_head) // 默认头像
						.cacheInMemory(true).cacheOnDisk(true).build());
//		ImageLoader.getInstance().displayImage(PhotoUtil.getAllPhotoPath(searchMemberList.get(position).getPicture()), picture);
		name.setText(searchMemberList.get(position).getName());
		
		//点击“邀请”按钮事件
		invite.setOnClickListener(new OnClickListener() {//点击邀请
			
			@Override
			public void onClick(View v) {
				if(listener != null){
					listener.onInviteMember(searchMemberList.get(position));
				}
			}
		});
		
		//点击一整项查看“个人（球员）详细信息”
		item.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String uid = searchMemberList.get(position).getUuid();  //当前用户ID
				String ownerID = UserManager.getInstance(mContext).getCurrentUser().getPlayer().getUuid();
				if(ownerID.equals(uid)){
					Intent intent =new Intent(mContext,SetMyInfoActivity.class);
					mContext.startActivity(intent);
				}else{
//					Intent intent = new Intent(mContext, AllUserInfoActivity.class);
//					Bundle bundle = new Bundle();	
//					bundle.putParcelable("searchPerson", searchMemberList.get(position));
//					intent.putExtras(bundle);
//					mContext.startActivity(intent);
					Intent intent = new Intent(mContext, TeamPlayerInfo.class);
					Bundle bundle = new Bundle();	
					bundle.putString(TeamPlayerInfo.PLAYER_UUID, searchMemberList.get(position).getUuid());
					intent.putExtras(bundle);
					mContext.startActivity(intent);
				}
			}
		});
		
		return convertView;
	}

}
