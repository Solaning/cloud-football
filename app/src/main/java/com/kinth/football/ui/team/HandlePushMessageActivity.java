package com.kinth.football.ui.team;

import org.apache.commons.lang3.StringUtils;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.kinth.football.R;
import com.kinth.football.bean.message.InviteMemberMC;
import com.kinth.football.bean.message.MessageContent;
import com.kinth.football.bean.message.PushMessageAbstract;
import com.kinth.football.bean.message.RequestJoinTeamConfirmMC;
import com.kinth.football.bean.message.RequestJoinTeamMC;
import com.kinth.football.config.PushMessageEnum;
import com.kinth.football.eventbus.bean.MemberJoinTeamEvent;
import com.kinth.football.manager.UserManager;
import com.kinth.football.network.NetWorkManager;
import com.kinth.football.singleton.SingletonBitmapClass;
import com.kinth.football.singleton.ViewCompat;
import com.kinth.football.ui.BaseActivity;
import com.kinth.football.ui.match.MessageCenterActivity;
import com.kinth.football.util.DateUtil;
import com.kinth.football.util.DialogUtil;
import com.kinth.football.util.ErrorCodeUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import de.greenrobot.event.EventBus;

/**
 * 处理后台的消息推送
 * @author Sola
 * 
 */
@ContentView(R.layout.activity_handle_push_message)
public class HandlePushMessageActivity extends BaseActivity{
	public static final String INTENT_PUSH_MESSAGE_CONTENT = "INTENT_PUSH_MESSAGE_CONTENT";
	public static final String Current_MESSAGE_HAD_CLICK = "Current_MESSAGE_HAD_REDA";   //当前消息是否已读
	
	public static final String INTENT_FOR_RESULT = "INTENT_FOR_RESULT";  //intent中传递的内容
	
	private PushMessageAbstract<MessageContent> pushMessage;
	private ProgressDialog progressDia;
	
	private int currentMsgHadClick;
	
	@ViewInject(R.id.nav_left)
	private ImageButton back;
	
	@ViewInject(R.id.nav_title)
	private TextView title;
	
	@ViewInject(R.id.tv_push_message_time)
	private TextView pushMessageTime;
	
	@ViewInject(R.id.tv_push_message_description)
	private TextView pushMessageDescription;
	
	@ViewInject(R.id.btn_confirm)
	private Button confirm;//确认
	
	@ViewInject(R.id.btn_cancle)
	private Button cancle;//取消
	
	@ViewInject(R.id.handle_lin)
	private LinearLayout handle_lin;
	
	@OnClick(R.id.nav_left)
	public void fun_1(View v){
		finish();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		
		// 設置背景
		Bitmap background = SingletonBitmapClass.getInstance()
				.getBackgroundBitmap();
		ViewCompat.setBackground(handle_lin, new BitmapDrawable(getResources(),
				background));
		
		pushMessage = getIntent().getParcelableExtra(INTENT_PUSH_MESSAGE_CONTENT);
		currentMsgHadClick = getIntent().getIntExtra(Current_MESSAGE_HAD_CLICK, 0);
		if(pushMessage == null){
			return;
		}
		
		//确认取消按钮的显示
		showSureCancelBtnOrNot();
		
		pushMessageTime.setText(StringUtils.defaultIfEmpty(DateUtil.parseTimeInMillis(pushMessage.getDate()),""));
		pushMessageDescription.setText(pushMessage.getContent().getDescription());
		
		switch(PushMessageEnum.getEnumFromString(pushMessage.getType())){
		case TEAM_PLAYER_INVITATION://球队邀请
			fun_TeamPlayerInvitation();
			break;
		case TEAM_PLAYER_INVITATION_CONFIRM://球队邀请确认
			fun_TeamPlayerInvitationConfirm();
			break;
		case TEAM_PLAYER_APPLICATION://申请加入球队
			fun_TeamPlayerApplication();
			break;
		case TEAM_PLAYER_APPLICATION_CONFIRM://申请加入球队确认
			fun_TeamPlayerApplicationConfirm();
			break;
		case TEAM_PLAYER_QUIT://退出球队
			fun_TeamPlayerQuit();
			break;
		default:
			break;
		}
	}

	private void fun_TeamPlayerApplication() {//有人申请加入球队
		confirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {//同意加入
				progressDia = ProgressDialog.show(mContext, "提示", "请稍后...",false, false);
				NetWorkManager.getInstance(mContext).confirmJoinTeamRequest(((RequestJoinTeamMC)pushMessage.getContent()).getUuid(), "true",
						UserManager.getInstance(mContext).getCurrentUser().getToken(),new Listener<Void>() {
							@Override
							public void onResponse(Void response) {
								DialogUtil.dialogDismiss(progressDia);
								ShowToast("成功加入球队");
								
								EventBus.getDefault().post(new MemberJoinTeamEvent());
								Intent intent = new Intent();
								intent.putExtra(INTENT_FOR_RESULT, 1);
								setResult(RESULT_OK, intent);
								finish();
							}
						}, new ErrorListener() {
							@Override
							public void onErrorResponse(VolleyError error) {
								DialogUtil.dialogDismiss(progressDia);
								if(!NetWorkManager.getInstance(mContext).isNetConnected()){
									ShowToast("当前网络不可用，请稍后重试");
									return;
								}else if(error.networkResponse == null){
									ShowToast("加入球队失败，请稍后重试");
								}else if(error.networkResponse.statusCode == 401){
									ErrorCodeUtil.ErrorCode401(mContext);
								}else if(error.networkResponse.statusCode == 403){
									ShowToast("错误：您没有权限");
								}else if(error.networkResponse.statusCode == 404){
									ShowToast("错误：球队找不到");
								}else if(error.networkResponse.statusCode == 409){
									ShowToast("错误：该申请不在已过期");
								}
							}
						});
			}
		});
		
		cancle.setText("拒绝");
		cancle.setOnClickListener(new OnClickListener() {//拒绝加入
			@Override
			public void onClick(View v) {
				progressDia = ProgressDialog.show(mContext, "提示", "请稍后...",false, false);
				NetWorkManager.getInstance(mContext).confirmJoinTeamRequest(((RequestJoinTeamMC)pushMessage.getContent()).getUuid(), "false",
						UserManager.getInstance(mContext).getCurrentUser().getToken(),new Listener<Void>() {
							@Override
							public void onResponse(Void response) {
								DialogUtil.dialogDismiss(progressDia);
								ShowToast("拒绝加入球队");
								
								Intent intent = new Intent();
								intent.putExtra(INTENT_FOR_RESULT, 1);
								setResult(RESULT_OK, intent);
								
								finish();
							}
						}, new ErrorListener() {

							@Override
							public void onErrorResponse(VolleyError error) {
								DialogUtil.dialogDismiss(progressDia);
								if(!NetWorkManager.getInstance(mContext).isNetConnected()){
									ShowToast("当前网络不可用，请稍后重试");
									return;
								}else if(error.networkResponse == null){
									ShowToast("拒绝加入球队失败，请稍后重试");
								}else if(error.networkResponse.statusCode == 401){
									ErrorCodeUtil.ErrorCode401(mContext);
								}else if(error.networkResponse.statusCode == 403){
									ShowToast("错误：您没有权限");
								}else if(error.networkResponse.statusCode == 404){
									ShowToast("错误：球队找不到");
								}else if(error.networkResponse.statusCode == 409){
									ShowToast("错误：该申请不在已过期");
								}
							}
						});
			}
		});
	}

	private void fun_TeamPlayerApplicationConfirm() {
		readOnlyMessage();
	}
	
	private void fun_TeamPlayerQuit() {
		readOnlyMessage();
	}

	private void showSureCancelBtnOrNot(){
		if(currentMsgHadClick == MessageCenterActivity.HAD_CLICK){  //已读, 不让用户操作，隐藏按钮
			confirm.setVisibility(View.GONE);
			cancle.setVisibility(View.GONE);
		}else if(currentMsgHadClick == MessageCenterActivity.HAD_CLICK_NOT){  //未读
			confirm.setVisibility(View.VISIBLE);
			cancle.setVisibility(View.VISIBLE);
		}
	}
	
	private void fun_TeamPlayerInvitation(){ //邀请成员消息
		confirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {//同意加入
				progressDia = ProgressDialog.show(mContext, "提示", "请稍后...",false, false);
				NetWorkManager.getInstance(mContext).confirmInvitePushMessage(((InviteMemberMC)pushMessage.getContent()).getUuid(), "true",
						UserManager.getInstance(mContext).getCurrentUser().getToken(),new Listener<Void>() {
							@Override
							public void onResponse(Void response) {
								DialogUtil.dialogDismiss(progressDia);
								ShowToast("成功加入球队");
								
								EventBus.getDefault().post(new MemberJoinTeamEvent());
								
								Intent intent = new Intent();
								intent.putExtra(INTENT_FOR_RESULT, 1);
								setResult(RESULT_OK, intent);
								finish();
							}
						}, new ErrorListener() {
							@Override
							public void onErrorResponse(VolleyError error) {
								DialogUtil.dialogDismiss(progressDia);
								if(!NetWorkManager.getInstance(mContext).isNetConnected()){
									ShowToast("当前网络不可用");
									return;
								}
								if(error.networkResponse != null){
									if(error.networkResponse.statusCode == 401){
										ErrorCodeUtil.ErrorCode401(mContext);
									}else if(error.networkResponse.statusCode == 403){
//										ShowToast("此邀请对象不是你");
									}else if(error.networkResponse.statusCode == 404){
//										error.networkResponse.data
//										ShowToast("邀请信息找不到或者用户找不到");
									}
								}
							}
						});
			}
		});
		
		cancle.setText("拒绝");
		cancle.setOnClickListener(new OnClickListener() {//拒绝加入
			@Override
			public void onClick(View v) {
				progressDia = ProgressDialog.show(mContext, "提示", "请稍后...",false, false);
				NetWorkManager.getInstance(mContext).confirmInvitePushMessage(((InviteMemberMC)pushMessage.getContent()).getUuid(), "false",
						UserManager.getInstance(mContext).getCurrentUser().getToken(),new Listener<Void>() {
							@Override
							public void onResponse(Void response) {
								DialogUtil.dialogDismiss(progressDia);
								ShowToast("拒绝加入球队");
								
								Intent intent = new Intent();
								intent.putExtra(INTENT_FOR_RESULT, 1);
								setResult(RESULT_OK, intent);
								
								finish();
							}
						}, new ErrorListener() {

							@Override
							public void onErrorResponse(VolleyError error) {
								DialogUtil.dialogDismiss(progressDia);
								if(!NetWorkManager.getInstance(mContext).isNetConnected()){
									ShowToast("当前网络不可用");
									return;
								}
								if(error.networkResponse != null){
									if(error.networkResponse.statusCode == 401){
										ErrorCodeUtil.ErrorCode401(mContext);
									}else if(error.networkResponse.statusCode == 403){
//										ShowToast("此邀请对象不是你");
									}else if(error.networkResponse.statusCode == 404){
//										ShowToast("邀请信息找不到或者用户找不到");
									}
								}
							}
						});
			}
		});
	}
	
	private void fun_TeamPlayerInvitationConfirm(){//邀请成员确认
		readOnlyMessage();
	}

	
	//不需要交互的消息
	private void readOnlyMessage(){
		confirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {//知道了
				Intent intent = new Intent();
				intent.putExtra(INTENT_FOR_RESULT, 1);
				setResult(RESULT_OK, intent);
				
				finish();
			}
		});
		cancle.setVisibility(View.GONE);
	}
}
