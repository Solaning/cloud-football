package com.kinth.football.chat.adapter;

import java.util.List;

import android.content.Context;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kinth.football.R;
import com.kinth.football.chat.ChatConstants;
import com.kinth.football.chat.bean.ChatRecent;
import com.kinth.football.chat.db.ChatDBManager;
import com.kinth.football.chat.util.FaceTextUtils;
import com.kinth.football.util.ImageLoadOptions;
import com.kinth.football.util.TimeUtil;
import com.kinth.football.view.RoundImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ChatRecentAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private List<ChatRecent> mData;
	private Context mContext;

	public ChatRecentAdapter(Context context, int textViewResourceId,
			List<ChatRecent> objects) {
		inflater = LayoutInflater.from(context);
		this.mContext = context;
		mData = objects;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mData == null ? 0 : mData.size();
	}

	@Override
	public Object getItem(int position) {
		return mData == null ? null : mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = new ViewHolder();
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_conversation, parent,
					false);

			holder.iVavatar = (RoundImageView) convertView
					.findViewById(R.id.iv_recent_avatar);
			holder.tvName = (TextView) convertView
					.findViewById(R.id.tv_recent_name);
			holder.tvMsg = (TextView) convertView
					.findViewById(R.id.tv_recent_msg);
			holder.tvTime = (TextView) convertView
					.findViewById(R.id.tv_recent_time);
			holder.tvUnread = (TextView) convertView
					.findViewById(R.id.tv_recent_unread);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final ChatRecent item = mData.get(position); 
		String avatar = item.getAvatar();
		if (avatar != null && !avatar.equals("")) {
			if(item.getNick().equals("云球团队")){
				ImageLoader.getInstance().displayImage(avatar.trim(), holder.iVavatar,
						new DisplayImageOptions.Builder()
				.showImageOnLoading(
						R.drawable.ic_launcher)
				.showImageForEmptyUri(
						R.drawable.ic_launcher)
				.showImageOnFail(
						R.drawable.ic_launcher)
				.cacheInMemory(true)
				.cacheOnDisk(true).build());
			}else if(item.getTag()==0){
				ImageLoader.getInstance().displayImage(avatar.trim(), holder.iVavatar,
						new DisplayImageOptions.Builder()
				.showImageOnLoading(
						R.drawable.icon_default_head)
				.showImageForEmptyUri(
						R.drawable.icon_default_head)
				.showImageOnFail(
						R.drawable.icon_default_head)
				.cacheInMemory(true)
				.cacheOnDisk(true).build());
			}else {
				ImageLoader.getInstance().displayImage(avatar.trim(), holder.iVavatar,
						new DisplayImageOptions.Builder()
				.showImageOnLoading(
						R.drawable.team_bage_default)
				.showImageForEmptyUri(
						R.drawable.team_bage_default)
				.showImageOnFail(
						R.drawable.team_bage_default)
				.cacheInMemory(true)
				.cacheOnDisk(true).build());
			}
			
		} else {
			holder.iVavatar.setImageResource(R.drawable.head);
		} 
		// 设置holder
		holder.tvName.setText(item.getNick());

		holder.tvTime.setText(TimeUtil.getChatTime(item.getTime()));

		int num = ChatDBManager.create(mContext).getUnreadAndUnplayCount(
				item.getTargetid(), item.getTag());

		if (item.getType().equals(ChatConstants.MSG_TYPE_TEXT)) {
			SpannableString spannableString = FaceTextUtils.toSpannableString(
					mContext, item.getMessage());
			holder.tvMsg.setText(spannableString);
		} else if (item.getType().equals(ChatConstants.MSG_TYPE_IMAGE)) {
			holder.tvMsg.setText("[图片]");
		} else if (item.getType().equals(ChatConstants.MSG_TYPE_AUDIO)) {
			holder.tvMsg.setText("[语音]");
		}

		if (num > 0) {
			holder.tvUnread.setVisibility(View.VISIBLE);
			holder.tvUnread.setText(num + "");
		} else {
			holder.tvUnread.setVisibility(View.GONE);
		}

		return convertView;
	}

	static class ViewHolder {
		RoundImageView iVavatar;
		TextView tvName;
		TextView tvMsg;
		TextView tvTime;
		TextView tvUnread;
	}

}
