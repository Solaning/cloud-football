package com.kinth.football.ui.team.formation;

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import uk.co.senab.photoview.PhotoViewAttacher;
import uk.co.senab.photoview.PhotoViewAttacher.OnPhotoTapListener;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.kinth.football.CustomApplcation;
import com.kinth.football.R;
import com.kinth.football.bean.User;
import com.kinth.football.chat.ChatConstants;
import com.kinth.football.chat.DownloadManager;
import com.kinth.football.chat.listener.DownloadListener;
import com.kinth.football.chat.ui.ChatActivity;
import com.kinth.football.chat.util.CompressListener;
import com.kinth.football.chat.util.ImageUtil;
import com.kinth.football.chat.util.MD5Util;
import com.kinth.football.config.Config;
import com.kinth.football.dao.Team;
import com.kinth.football.dao.TeamDao;
import com.kinth.football.dao.TeamPlayer;
import com.kinth.football.manager.UserManager;
import com.kinth.football.ui.ActivityBase;
import com.kinth.football.util.DBUtil;
import com.kinth.football.util.QuitWay;
import com.kinth.football.view.HeaderLayout;
import com.kinth.football.view.HeaderLayout.onRightImageButtonClickListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXImageObject;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import eu.inmite.android.lib.dialogs.ISimpleDialogListener;
import eu.inmite.android.lib.dialogs.SimpleDialogFragment;

public class FormationDetailActivity extends ActivityBase implements
		OnClickListener, ISimpleDialogListener, FormationConstants {

	public HeaderLayout header;//头部
	public static ProgressBar pbLoad;//加载阵容图片时的环形进度条
	private ImageView ivFormation;//阵容图片
	private TextView tvName;//阵容标题
	private TextView tvDesc;//阵容说明内容
	private PhotoViewAttacher mAttacher;//使图片可缩放
	private Dialog dlgShare;//选择分享到哪款应用的对话框

	private String teamUUID;
	private Formation formation;//阵容实体类
	private User targetUser;//聊天对象
	
	private String picUrl = "";//阵容图片的网络路径
	private boolean found = false;	//分享阵容时是否在手机里找到相应的应用

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_formation_detail);
		initData();
		initHeader();
		initView();
		if (targetUser != null)
			isSendToFriend();
	}

	private void initData() {
		// TODO 自动生成的方法存根
		formation = getIntent().getParcelableExtra(INTENT_FORMATION_BEAN);
		teamUUID = getIntent().getStringExtra(INTENT_TEAM_UUID);
		if (formation == null) {
			formation = new Formation();
		}
		targetUser = (User) getIntent().getParcelableExtra(ChatConstants.INTENT_USER_BEAN);
	}

	private void initHeader() {
		// TODO 自动生成的方法存根
		boolean isGuest = true;//是否来宾权限
		List<TeamPlayer> teamPlayerList = CustomApplcation.getDaoSession(mContext).getTeamPlayerDao()._queryPlayer_TeamPlayerList(UserManager.getInstance(mContext).getCurrentUser().getPlayer().getUuid());
		for(TeamPlayer teamPlayer : teamPlayerList){
			Team team = CustomApplcation.getDaoSession(mContext).getTeamDao().queryBuilder().where(TeamDao.Properties.Uuid.eq(teamPlayer.getTeam_id())).build().unique();
			if(team.getUuid().equals(teamUUID)){
				isGuest = false;
				break;
			}
		}
		if(isGuest){
			initTopBarForLeft("阵容详情");
		}else{
			initTopBarForBoth("阵容详情", R.drawable.share,
					new onRightImageButtonClickListener() {

						@Override
						public void onClick() {
							// TODO 自动生成的方法存根
							dlgShare.show();
						}

					});
		}
	}

	private void initView() {
		header = (HeaderLayout) findViewById(R.id.common_actionbar);
		pbLoad = (ProgressBar) findViewById(R.id.pb_load);
		ivFormation = (ImageView) findViewById(R.id.iv_photo);
		tvName = (TextView) findViewById(R.id.tv_name);
		tvDesc = (TextView) findViewById(R.id.tv_description);

		if (formation.getImage() != null) {
			picUrl = formation.getImage().trim();
		}
		String localPath = null;
		try {
			localPath = Config.FORMATION_DIR + MD5Util.getMD5(picUrl) + ".jpg";
		} catch (NoSuchAlgorithmException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		if (new File(localPath).exists()) {
			ivFormation.setImageBitmap(BitmapFactory.decodeFile(localPath));
			pbLoad.setVisibility(View.GONE);
		} else {
			avoidNetworkOnMainThreadException();
			ivFormation.setImageBitmap(ImageLoader.getInstance().loadImageSync(
					picUrl));
			DownloadManager downloadTask = new DownloadManager(
					new DownloadListener() {
						@Override
						public void onStart() {
							// TODO Auto-generated method stub
						}

						@Override
						public void onSuccess() {
							// TODO Auto-generated method stub
							pbLoad.setVisibility(View.GONE);
						}

						@Override
						public void onError(String error) {
							// TODO Auto-generated method stub
						}
					});
			downloadTask.execute(picUrl, localPath);
		}

		tvName.setText(formation.getName());
		tvDesc.setText(formation.getDescription());

		mAttacher = new PhotoViewAttacher(ivFormation);
		mAttacher.setOnPhotoTapListener(new OnPhotoTapListener() {

			@Override
			public void onPhotoTap(View arg0, float arg1, float arg2) {	//单击
				// TODO 自动生成的方法存根
				if (header.getVisibility() == View.GONE) {
					header.setVisibility(View.VISIBLE);
					getWindow().clearFlags(
							WindowManager.LayoutParams.FLAG_FULLSCREEN);
				} else {
					header.setVisibility(View.GONE);//隐藏头部
					getWindow().setFlags(
							WindowManager.LayoutParams.FLAG_FULLSCREEN,
							WindowManager.LayoutParams.FLAG_FULLSCREEN);// 隐藏状态栏
				}
			}

		});

		initShareDialog();
	}

	/**
	 * 初始化分享对话框
	 */
	private void initShareDialog() {
		// TODO 自动生成的方法存根
		View root = this.getLayoutInflater().inflate(R.layout.dlg_share_way,
				null);
		dlgShare = new Dialog(this, R.style.customDialog);
		dlgShare.setContentView(root);
		Window window = dlgShare.getWindow();
		WindowManager.LayoutParams lp = window.getAttributes(); // 获取对话框当前的参数值
		lp.height = LayoutParams.WRAP_CONTENT;
		lp.width = LayoutParams.MATCH_PARENT;
		window.setAttributes(lp);
		window.setGravity(Gravity.BOTTOM); // 此处可以设置dialog显示的地位
		window.setWindowAnimations(R.style.share_dialog_anim); // 添加动画
		
		root.findViewById(R.id.tv_quit_share).setOnClickListener(this);
		root.findViewById(R.id.tv_share_mm).setOnClickListener(this);
		root.findViewById(R.id.tv_share_friend).setOnClickListener(this);
//		root.findViewById(R.id.tv_share_qq).setOnClickListener(this);
//		root.findViewById(R.id.tv_share_weibo).setOnClickListener(this);
	}

	/**
	 * 聊天点发送阵容，在阵容列表选择阵容之后跳转到阵容详情时会弹出此对话框
	 */
	public void isSendToFriend() {
		setTheme(R.style.DefaultLightTheme);
		SimpleDialogFragment
				.createBuilder(FormationDetailActivity.this,
						getSupportFragmentManager())
				.setTitle("确认框")
				.setMessage("是否确定将此阵容发送给好友 " + targetUser.getPlayer().getName())
				.setPositiveButtonText(R.string.logout_positive_button)
				.setNegativeButtonText(R.string.logout_negative_button)
				.setRequestCode(42).setTag("custom-tag").show();
	}

	/**
	 * 调用系统分享，不同于之前的是有指定分享到应用的包名
	 * @param type
	 */
	private void initShareIntent(final String type, final int sence) {
		final Intent share = new Intent(android.content.Intent.ACTION_SEND);
		share.setType("image/jpeg");//分享类型:jpg格式图片
		List<ResolveInfo> resInfo = getPackageManager().queryIntentActivities(
				share, 0);//获得能够实现图片分享的所有应用
		if (!resInfo.isEmpty()) {
			for (final ResolveInfo info : resInfo) {
				if (info.activityInfo.packageName.toLowerCase().contains(type)
						|| info.activityInfo.name.toLowerCase().contains(type)) {
					header.setVisibility(View.GONE);//隐藏头部
					getWindow().setFlags(
							WindowManager.LayoutParams.FLAG_FULLSCREEN,
							WindowManager.LayoutParams.FLAG_FULLSCREEN);// 隐藏状态栏
					DisplayMetrics dm = new DisplayMetrics();
				    getWindowManager().getDefaultDisplay().getMetrics(dm);
				    int height = dm.heightPixels;  //得到屏幕高度
				    int cutHeight = 40;
				    if(height>960){//如果屏幕高度大于960，则截取高度为75
				    	cutHeight = 75;
				    }
					new ScreenshotUtil(FormationDetailActivity.this)
							.saveScreenshot(
									Config.FORMATION_DIR,
									String.valueOf(System.currentTimeMillis())
											+ ".jpg",
									new ScreenshotUtil.ScreenshotStateListener() {

										@Override
										public void screenshotSuccess(
												String filepath) {
											// TODO 自动生成的方法存根
											ImageUtil.getCompressedImage(
													filepath,
													new CompressListener() {

														@Override
														public void CompressSuccess(
																String imagePath) {
															// TODO 自动生成的方法存根
															initHeader();
															if(sence==WX_SCENE_SESSION)
																shareToWX(imagePath, 0);
															else if(sence==WX_SCENE_TIMELINE)
																shareToWX(imagePath, 1);
															found = true;
														}

														@Override
														public void CompressFail(
																String failReason) {
															// TODO 自动生成的方法存根
															Toast.makeText(
																	mContext,
																	failReason,
																	Toast.LENGTH_LONG)
																	.show();
														}
													}, 500);
										}

										@Override
										public void screenshotFail() {
											// TODO 自动生成的方法存根

										}

									}, cutHeight);
					break;
				}
			}
			if (!found) {
				ShowToast("应用没找到，请确认您手机上是否已安装此运用");
				initHeader();
				return;
			}
			found = false;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// Need to call clean-up
		mAttacher.cleanup();
	}

	@Override
	public void onPositiveButtonClicked(int requestCode) {
		// TODO 自动生成的方法存根
		header.setVisibility(View.GONE);
		initTopBarForLeft("正在截图,请勿退出...");
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				DisplayMetrics dm = new DisplayMetrics();
			    getWindowManager().getDefaultDisplay().getMetrics(dm);
			    int height = dm.heightPixels; 
			    int cutHeight = 40;
			    if(height>960){
			    	cutHeight = 75;
			    }
				new ScreenshotUtil(FormationDetailActivity.this)
						.saveScreenshot(Config.FORMATION_DIR,
								String.valueOf(System.currentTimeMillis())
										+ ".jpg",
								new ScreenshotUtil.ScreenshotStateListener() {

									@Override
									public void screenshotSuccess(
											String filepath) {
										// TODO 自动生成的方法存根
										ImageUtil.getCompressedImage(filepath,
												new CompressListener() {

													@Override
													public void CompressSuccess(
															String imagePath) {
														// TODO 自动生成的方法存根
														initHeader();
														toChat(imagePath);
													}

													@Override
													public void CompressFail(
															String failReason) {
														// TODO 自动生成的方法存根
														Toast.makeText(
																mContext,
																failReason,
																Toast.LENGTH_LONG)
																.show();
													}
												}, 500);
									}

									@Override
									public void screenshotFail() {
										// TODO 自动生成的方法存根

									}

								}, cutHeight);
			}
		}, 1500);
	}

	/**
	 * 跳转到聊天界面
	 * @param formationDetailPath
	 */
	private void toChat(String formationDetailPath) {
		// TODO 自动生成的方法存根
		Intent intent = new Intent(FormationDetailActivity.this,
				ChatActivity.class);
		intent.putExtra(INTENT_FORMATION_DETAIL_PATH, formationDetailPath);
		intent.putExtra(ChatConstants.INTENT_USER_BEAN, targetUser);
		boolean isTeamExist = DBUtil.isTeamExitByUuid(targetUser.getPlayer().getAccountName(), UserManager.getInstance(mContext).getCurrentUser().getPlayer().getUuid());
		if(isTeamExist){//球队群聊
			intent.putExtra(ChatConstants.TAG, ChatConstants.TAG_GROUP);
		}else{
			intent.putExtra(ChatConstants.TAG, ChatConstants.TAG_PRIVATE);
		}
		startActivity(intent);
		QuitWay.activityList.add(this);
		QuitWay.finishAll();//finish掉自点选发送阵容之后打开的Activity，避免出错
	}
	
	@Override
	public void onNegativeButtonClicked(int requestCode) {
		// TODO 自动生成的方法存根

	}

	@Override
	public void onNeutralButtonClicked(int requestCode) {
		// TODO 自动生成的方法存根

	}

	/**
	 * 更改StrictMode属性，避免跑出NetworkOnMainThreadException错误
	 */
	private static void avoidNetworkOnMainThreadException() {
		// TODO 自动生成的方法存根
		// 详见StrictMode文档
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectDiskReads().detectDiskWrites().detectNetwork()
				.penaltyLog().build());
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
				.detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
				.penaltyLog().penaltyDeath().build());
	}

	@Override
	public void onClick(View v) {
		// TODO 自动生成的方法存根
		switch(v.getId()){
		case R.id.tv_quit_share:
			dlgShare.dismiss();
			break;
		case R.id.tv_share_mm:
			preShare("com.tencent.mm", WX_SCENE_SESSION);
			break;
		case R.id.tv_share_friend:
			preShare("com.tencent.mm", WX_SCENE_TIMELINE);
			break;
		/*case R.id.tv_share_qq:
			preShare("tencent.mobileqq");
			break;
		case R.id.tv_share_weibo:
			preShare("com.sina.weibo");
			break;*/
		}
	}

	/**
	 * 分享到应用之前的操作:关闭分享对话框，加截图提示，延迟0.5秒分享
	 * @param packageName
	 */
	private void preShare(final String packageName, final int sence) {
		// TODO 自动生成的方法存根
		dlgShare.dismiss();
		initTopBarForLeft("正在截图,请勿退出...");
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				initShareIntent(packageName, sence);
			}

		}, 500);
	}
	
	public static final String APP_ID = "wx422abbf509a5487b";
	public static final int WX_SCENE_SESSION = 0;
	public static final int WX_SCENE_TIMELINE = 1;
	
	public void shareToWX(String url, int scene) {
		
		IWXAPI api =  WXAPIFactory.createWXAPI(this, APP_ID, false); 
		api.registerApp(APP_ID); 
		
		// 网页
		/*WXWebpageObject webPageObj = new WXWebpageObject();
		webPageObj.webpageUrl = url;*/
		WXImageObject imgObj = new WXImageObject();
		imgObj.imagePath = url;
 
		// 用WXTextObject对象初始化一个WXMediaMessage对象
		WXMediaMessage msg = new WXMediaMessage();
//		msg.mediaObject = textObj;
//		msg.mediaObject = webPageObj;
		msg.mediaObject = imgObj;
		// 发送文本类型的消息时，title字段不起作用
		msg.title = "云球-专业足球App";
		msg.description = "专业足球的数据统计，深入草根的社交平台，数据、足球连接你我";

		// 构造一个Req
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = String.valueOf(System.currentTimeMillis()); // transaction字段用于唯一标识一个请求
		req.message = msg;
		if(scene==0)
			req.scene = SendMessageToWX.Req.WXSceneSession;	//微信好友
		else if(scene==1)
			req.scene = SendMessageToWX.Req.WXSceneTimeline;	//朋友圈
		
		// 调用api接口发送数据到微信
		api.sendReq(req); 
	}
	
	
}
