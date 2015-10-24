package com.kinth.football.ui.match;

import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.kinth.football.CustomApplcation;
import com.kinth.football.R;
import com.kinth.football.adapter.MessageCentreAdapter;
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
import com.kinth.football.dao.PushMessageDao;
import com.kinth.football.eventbus.bean.NotifyReCountTeamPushMessageEvent;
import com.kinth.football.singleton.SingletonBitmapClass;
import com.kinth.football.singleton.ViewCompat;
import com.kinth.football.ui.BaseActivity;
import com.kinth.football.ui.team.HandlePushMessageActivity;
import com.kinth.football.util.JSONUtils;
import com.kinth.football.view.xlist.XListView;
import com.kinth.football.view.xlist.XListView.IXListViewListener;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.event.EventBus;

/**
 * 球队消息中心
 * @author Sola
 *
 */
@ContentView(R.layout.activity_message_centre)
public class MessageCenterActivity extends BaseActivity{
	
	public static final int HAD_CLICK = 1;
	public static final int HAD_CLICK_NOT = 0;
	
	public static final int INTENT_HANDLE_MESSAGE = 4003;   //处理消息请求码
	
	private MessageCentreAdapter adapter;
	
	private boolean isClick = false;  //用于标记用户是否点击了ListView某一子项
	
	@ViewInject(R.id.entire_layout)
	private View entireLayout;
	
	@ViewInject(R.id.nav_left)
	private ImageButton back;
	
	@ViewInject(R.id.nav_title)
	private TextView title;
	
	@ViewInject(R.id.list_push_message)
	private XListView listview;
	
	@OnClick(R.id.nav_left)
	public void fun_1(View v){
		back();
	}

	PushMessage pushMessage;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);	
		// 設置背景
		Bitmap background = SingletonBitmapClass.getInstance()
				.getBackgroundBitmap();
		ViewCompat.setBackground(entireLayout, new BitmapDrawable(getResources(),
				background));
		
		title.setText("球队新消息");
		adapter = new MessageCentreAdapter(mContext, null);
		
		listview.setDivider(null);
		listview.setDividerHeight(10);
		
		listview.setAdapter(adapter);
		
		listview.setOnItemClickListener(new OnItemClickListener() {//单击某一项
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				isClick = true;
				
				//将PushMessage转化为PushMessageAbstract<? extends MessageContent>
				pushMessage = adapter.getItem(position - 1);
				
				//当用户点击该项时，设置该消息已读，即将PushMessage中hasRead字段设为true    TODO
				updateHasRead(pushMessage);
				
				PushMessageAbstract<? extends MessageContent> messageAbs = null;
				switch(PushMessageEnum.getEnumFromString(pushMessage.getType())){
				case TEAM_PLAYER_INVITATION:
					messageAbs = new InviteMemberPM();
					messageAbs.setType(pushMessage.getType());
					messageAbs.setDate(pushMessage.getDate());
		    		((InviteMemberPM )messageAbs).setContent(JSONUtils.fromJson(pushMessage.getContent(), InviteMemberMC.class));//转json为对象
		    		break;
				case TEAM_PLAYER_INVITATION_CONFIRM:
					messageAbs = new InviteMemberConfirmPM();
					messageAbs.setType(pushMessage.getType());
					messageAbs.setDate(pushMessage.getDate());
		    		((InviteMemberConfirmPM )messageAbs).setContent(JSONUtils.fromJson(pushMessage.getContent(), InviteMemberConfirmMC.class));//转json为对象
					break;
				case TEAM_PLAYER_APPLICATION://申请加入球队
					messageAbs = new RequestJoinTeamPM();
					messageAbs.setType(pushMessage.getType());
					messageAbs.setDate(pushMessage.getDate());
					((RequestJoinTeamPM)messageAbs).setContent(JSONUtils.fromJson(pushMessage.getContent(), RequestJoinTeamMC.class));//转json为对象
					break;
				case TEAM_PLAYER_APPLICATION_CONFIRM://申请加入球队
					messageAbs = new RequestJoinTeamConfirmPM();
					messageAbs.setType(pushMessage.getType());
					messageAbs.setDate(pushMessage.getDate());
					((RequestJoinTeamConfirmPM)messageAbs).setContent(JSONUtils.fromJson(pushMessage.getContent(), RequestJoinTeamConfirmMC.class));
					break;
				case TEAM_PLAYER_QUIT://退出球队
					messageAbs = new ExitTeamPM();
					messageAbs.setType(pushMessage.getType());
					messageAbs.setDate(pushMessage.getDate());
					((ExitTeamPM)messageAbs).setContent(JSONUtils.fromJson(pushMessage.getContent(), ExitTeamMC.class));
					break;
				default:
					break;
				}
				
				Intent intent = new Intent(mContext, HandlePushMessageActivity.class);
				intent.putExtra(HandlePushMessageActivity.INTENT_PUSH_MESSAGE_CONTENT, messageAbs);
				intent.putExtra(HandlePushMessageActivity.Current_MESSAGE_HAD_CLICK, pushMessage.getIsClick());
				startActivityForResult(intent, INTENT_HANDLE_MESSAGE);
			}
		});
		
		listview.setXListViewListener(new IXListViewListener() {
			@Override
			public void onRefresh() {//刷新
				new LoadCacheDataTask().execute();
			}
			
			@Override
			public void onLoadMore() {//加载更多
				
			}
		});
		
		new LoadCacheDataTask().execute();
	}

	/**
	 * 异步加载数据库缓存数据
	 * （从数据库中读出与“球队邀请--邀请确认”相关的系统消息）
	 */
	class LoadCacheDataTask extends AsyncTask<Void, Void, List<PushMessage>> {
		
		@Override
		protected List<PushMessage> doInBackground(Void... arg0) {
			return getTeamAboutMsg();
		}

		@Override
		protected void onPostExecute(List<PushMessage> result) {
			super.onPostExecute(result);
			listview.stopRefresh();
			if(result != null){
				adapter.setMessageList(result);
			}
		}
	}
	
	private List<PushMessage> getTeamAboutMsg(){
		QueryBuilder<PushMessage> qb = CustomApplcation.getDaoSession(mContext).getPushMessageDao().queryBuilder();
		qb.whereOr(PushMessageDao.Properties.Type.eq(PushMessageEnum.TEAM_PLAYER_INVITATION.getValue()), 
				PushMessageDao.Properties.Type.eq(PushMessageEnum.TEAM_PLAYER_INVITATION_CONFIRM.getValue()),
				PushMessageDao.Properties.Type.eq(PushMessageEnum.TEAM_PLAYER_APPLICATION.getValue()),
				PushMessageDao.Properties.Type.eq(PushMessageEnum.TEAM_PLAYER_APPLICATION_CONFIRM.getValue()),
				PushMessageDao.Properties.Type.eq(PushMessageEnum.TEAM_PLAYER_QUIT.getValue()));//TODO
		
		qb.orderDesc(PushMessageDao.Properties.Date);
	    List<PushMessage> pushMessageList = qb.list();
	    
	    return pushMessageList;
	}

	private void updateHasRead(PushMessage pushMessage){
		pushMessage.setHasRead(true);
		PushMessageDao dao = CustomApplcation.getDaoSession(mContext).getPushMessageDao();
		dao.insertOrReplace(pushMessage);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(isClick == true){
			isClick = false;
			new LoadCacheDataTask().execute();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (resultCode != RESULT_OK) {
			return;
		}
		if(requestCode == INTENT_HANDLE_MESSAGE){
			int about = intent.getIntExtra(HandlePushMessageActivity.INTENT_FOR_RESULT, 0);
			
			PushMessageDao dao = CustomApplcation.getDaoSession(mContext).getPushMessageDao();
			PushMessage pushMsg = new PushMessage();
			pushMsg.setId(pushMessage.getId());
			pushMsg.setType(pushMessage.getType());
			pushMsg.setContent(pushMessage.getContent());
			pushMsg.setDate(pushMessage.getDate());
			pushMsg.setHasRead(pushMessage.getHasRead());
			pushMsg.setIsClick(about);
			
			dao.insertOrReplace(pushMsg);
			return;
		}
	}
	
	@Override
	public void onBackPressed() {
		back();
	}

	private void back() {
		EventBus.getDefault().post(new NotifyReCountTeamPushMessageEvent());
		finish();
	}
}
