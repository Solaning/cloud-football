//package com.kinth.football.ui.user;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.kinth.football.R;
//import com.kinth.football.bean.User;
//import com.kinth.football.chat.ChatConfig;
//import com.kinth.football.chat.bean.ChatRecent;
//import com.kinth.football.chat.db.ChatDBManager;
//import com.kinth.football.chat.ui.ChatActivity;
//import com.kinth.football.dao.Player;
//import com.kinth.football.manager.UserManager;
//import com.kinth.football.ui.ActivityBase;
//import com.kinth.football.util.ImageLoadOptions;
//import com.kinth.football.util.PhotoUtil;
//import com.nostra13.universalimageloader.core.ImageLoader;
//
///**
// * 通用显示“用户，球员”资料Activity
// * @author Botision.Huang
// *
// */
//
//public class AllUserInfoActivity extends ActivityBase{
//	
//	private Player player;
//	
//	private TextView user_name = null;
//	private TextView user_gender = null;
//	private TextView user_accountname = null;
//	private TextView tv_email = null;
//	private TextView tv_phone = null;
//	private TextView tv_area = null;
//	private ImageView user_headphoto = null;
//	private Button startchat_btn = null;
//	
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		// TODO Auto-generated method stub
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_alluser_info);
//		
//		initView();
//		
//		//show data
//		ShowData();
//	}
//	
//	private void initView(){
//		initTopBarForLeft("详细资料");
//		
//		user_name = (TextView)this.findViewById(R.id.user_name);
//		user_gender = (TextView)this.findViewById(R.id.user_gender);
//		user_accountname = (TextView)this.findViewById(R.id.user_accountname);
//		tv_email = (TextView)this.findViewById(R.id.tv_email);
//		tv_phone = (TextView)this.findViewById(R.id.tv_phone);
//		tv_area = (TextView)this.findViewById(R.id.tv_area);
//		user_headphoto = (ImageView)this.findViewById(R.id.user_headphoto);
//		startchat_btn = (Button)this.findViewById(R.id.startchat_btn);
//		
//		//发起会话
//		startchat_btn.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				String playerUuid = UserManager.getInstance(mContext).getCurrentUser().getPlayer().getUuid();
//				//首先，将其添加到“聊天”Fragment中
//				ChatRecent chatRecent = new ChatRecent(player.getAccountName(), player.getUuid(), player.getName(), 
//						PhotoUtil.getAllPhotoPath(player.getPicture()), 
//						"你可以和他进行聊天", 
//						System.currentTimeMillis(), 
//						ChatConfig.MSG_TYPE_TEXT);
//				ChatDBManager.create(mContext, playerUuid).saveRecent(chatRecent);
//				
//				//其次，跳转到聊天Activity
//				//重置未读消息(目前是靠phone)
//				ChatDBManager.create(mContext).resetUnread(chatRecent.getTargetid());
//				//组装聊天对象
//				User user = new User();
//				Player player = new Player();
//				player.setName(chatRecent.getNick());
//				player.setPicture(chatRecent.getAvatar());
//				player.setUuid(chatRecent.getAvatar());
//				player.setAccountName(chatRecent.getTargetid());
//				user.setPlayer(player);
//				
//				Intent intent = new Intent(mContext, ChatActivity.class);
//				intent.putExtra("user", user);
//				startAnimActivity(intent);
//				
//			}
//		});
//	}
//	
//	private void ShowData(){
//		Intent intent = this.getIntent();
//		player = intent.getExtras().getParcelable("searchPerson");
//		
//		user_name.setText(player.getName());
//		user_gender.setText(player.getGender());
//		user_accountname.setText(player.getAccountName());
//		if(player.getEmail() == null){
//			tv_email.setText("未知");
//		}else{
//			tv_email.setText(player.getEmail());
//		}
//		
//		tv_phone.setText(player.getPhone());
//		if(player.getProvince() == null && player.getCity() == null){
//			tv_area.setText("未知");
//		}else if(player.getProvince() != null && player.getCity() == null){
//			tv_area.setText(player.getProvince());
//		}else if(player.getProvince() == null && player.getCity() != null){
//			tv_area.setText(player.getCity());
//		}else{
//			tv_area.setText(player.getProvince() + "　" + player.getCity());
//		}
//		showHeadPhoto(player.getPicture());
//	}	
//	
//	/**
//	 * 显示头像showHeadPhoto
//	 * 
//	 * @return void
//	 * @throws
//	 */
//	private void showHeadPhoto(String picture) {
//		if (picture != null && !picture.equals("")) {
//			ImageLoader.getInstance().displayImage(PhotoUtil.getAllPhotoPath(picture), user_headphoto,
//					ImageLoadOptions.getOptions());
//		} else {
//			user_headphoto.setImageResource(R.drawable.default_head);
//		}
//	}
//}
