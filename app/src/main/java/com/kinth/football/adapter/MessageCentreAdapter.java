package com.kinth.football.adapter;


import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.kinth.football.R;
import com.kinth.football.bean.message.ExitTeamMC;
import com.kinth.football.bean.message.ExitTeamPM;
import com.kinth.football.bean.message.InviteMemberConfirmMC;
import com.kinth.football.bean.message.InviteMemberConfirmPM;
import com.kinth.football.bean.message.InviteMemberMC;
import com.kinth.football.bean.message.InviteMemberPM;
import com.kinth.football.bean.message.MessageContent;
import com.kinth.football.bean.message.PushMessageAbstract;
import com.kinth.football.bean.message.RequestJoinTeamConfirmMC;
import com.kinth.football.bean.message.RequestJoinTeamConfirmPM;
import com.kinth.football.bean.message.RequestJoinTeamMC;
import com.kinth.football.bean.message.RequestJoinTeamPM;
import com.kinth.football.config.PushMessageEnum;
import com.kinth.football.dao.PushMessage;
import com.kinth.football.util.DateUtil;
import com.kinth.football.util.JSONUtils;
import com.kinth.football.util.ViewHolder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 球队消息中心adapter  TODO getView里需要优化
 * @author Sola
 *
 */
public class MessageCentreAdapter extends BaseAdapter{
	private Context mContext;
	private List<PushMessage>  messageList;
	
	public MessageCentreAdapter(Context mContext,
			List<PushMessage>  messageList) {
		super();
		this.mContext = mContext;
		this.messageList = messageList;
	}
 
	public List<PushMessage> getMessageList() {
		return messageList;
	}

	public void setMessageList(
			List<PushMessage> messageList) {
		this.messageList = messageList;
		notifyDataSetChanged();
	}

	//新加项
	public void addTeamListBean(PushMessage message){
		if(this.messageList == null){
			this.messageList = new ArrayList<PushMessage>();
		}
		this.messageList.add(message);
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return messageList == null ? 0 : messageList.size();
	}

	@Override
	public PushMessage getItem(int position) {
		// TODO Auto-generated method stub
		return messageList == null ? null : messageList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.item_message_centre, parent, false);
		}
		TextView messageType = ViewHolder.get(convertView, R.id.tv_message_type);
		TextView messageDescription = ViewHolder.get(convertView, R.id.tv_message_description);
		TextView messageDate = ViewHolder.get(convertView, R.id.tv_message_date);
		ImageView tips = ViewHolder.get(convertView, R.id.iv_tips_is_message_read);
		
		String type = messageList.get(position).getType();
		//将PushMessage转化为PushMessageAbstract<? extends MessageContent>
		PushMessageAbstract<? extends MessageContent> pushMsgAbs = null;
		String descp = null;
		switch(PushMessageEnum.getEnumFromString(type)){
		case TEAM_PLAYER_INVITATION:
			messageType.setText("球队邀请");
			pushMsgAbs = new InviteMemberPM();
			pushMsgAbs.setType(type);
			pushMsgAbs.setDate(messageList.get(position).getDate());
    		((InviteMemberPM )pushMsgAbs).setContent(JSONUtils.fromJson(messageList.get(position).getContent(), InviteMemberMC.class));//转json为对象
    		descp = ((InviteMemberPM )pushMsgAbs).getContent().getDescription();
    		break;
		case TEAM_PLAYER_INVITATION_CONFIRM:
			messageType.setText("球队邀请确认");
			pushMsgAbs = new InviteMemberConfirmPM();
			pushMsgAbs.setType(type);
			pushMsgAbs.setDate(messageList.get(position).getDate());
    		((InviteMemberConfirmPM )pushMsgAbs).setContent(JSONUtils.fromJson(messageList.get(position).getContent(), InviteMemberConfirmMC.class));//转json为对象
    		descp = ((InviteMemberConfirmPM )pushMsgAbs).getContent().getDescription();
			break;
		case TEAM_PLAYER_APPLICATION:
			messageType.setText("申请加入球队");
			pushMsgAbs = new RequestJoinTeamPM();
			pushMsgAbs.setType(type);
			pushMsgAbs.setDate(messageList.get(position).getDate());
    		((RequestJoinTeamPM )pushMsgAbs).setContent(JSONUtils.fromJson(messageList.get(position).getContent(), RequestJoinTeamMC.class));
			break;
		case TEAM_PLAYER_APPLICATION_CONFIRM:
			messageType.setText("申请加入球队确认");
			pushMsgAbs = new RequestJoinTeamConfirmPM();
			pushMsgAbs.setType(type);
			pushMsgAbs.setDate(messageList.get(position).getDate());
    		((RequestJoinTeamConfirmPM )pushMsgAbs).setContent(JSONUtils.fromJson(messageList.get(position).getContent(), RequestJoinTeamConfirmMC.class));
			break;
		case TEAM_PLAYER_QUIT:
			messageType.setText("退出球队");
			pushMsgAbs = new ExitTeamPM();
			pushMsgAbs.setType(type);
			pushMsgAbs.setDate(messageList.get(position).getDate());
    		((ExitTeamPM )pushMsgAbs).setContent(JSONUtils.fromJson(messageList.get(position).getContent(), ExitTeamMC.class));
			break;
		default:
			break;
		}

		messageDescription.setText(StringUtils.defaultIfEmpty(descp, ""));
		messageDate.setText(StringUtils.defaultIfEmpty(DateUtil.parseTimeInMillis(messageList.get(position).getDate()),""));
		
		if(messageList.get(position).getHasRead()){
			tips.setVisibility(View.GONE);//TODO 默认不可见
		}else{
			tips.setVisibility(View.VISIBLE);
		}
		return convertView;
	}

}
