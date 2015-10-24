package com.kinth.football.ui.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.FrameLayout.LayoutParams;

import com.kinth.football.R;
import com.kinth.football.config.UrlConstants;
import com.kinth.football.friend.MomentPersonalZoneActivity;
import com.kinth.football.manager.UserManager;
import com.kinth.football.ui.CommonWebViewActivity;
import com.kinth.football.ui.FragmentBase;
import com.kinth.football.ui.mine.FeedbackActivity;
import com.kinth.football.ui.mine.SetMyInfoActivity;
import com.kinth.football.ui.mine.SettingActivity;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * 我
 * @author Sola
 * 
 */
@SuppressLint("SimpleDateFormat")
public class MineFragment extends FragmentBase {
	@ViewInject(R.id.rlt_my_info)
	private LinearLayout rltSetMyInfo;//个人信息
	
	@ViewInject(R.id.llt_moments)
	private LinearLayout lltMoments;//我的空间
	
	@ViewInject(R.id.rlt_setting)
	private LinearLayout rltSetting;//设置
	
	@ViewInject(R.id.rlt_feedback)
	private LinearLayout rltFeedback;//反馈
	
	@ViewInject(R.id.rlt_recommend)
	private LinearLayout rltRecommend;//推荐给好友
	
	@ViewInject(R.id.rlt_about)
	private LinearLayout rltAbout;//关于
	
	private Dialog dlgShare;
	private boolean found = false;
	
	@OnClick(R.id.rlt_my_info)//个人资料页面
	public void fun_1(View v){
		Intent intent =new Intent(getActivity(), SetMyInfoActivity.class);
		startActivity(intent);
		
	}
	
	@OnClick(R.id.llt_moments)
	public void fun_2(View v){//朋友圈个人空间
		Intent intent = new Intent(getActivity(), MomentPersonalZoneActivity.class);
		intent.putExtra(MomentPersonalZoneActivity.INTENT_PLAYER_UUID, UserManager.getInstance(getActivity()).getCurrentUser().getPlayer().getUuid());
		startActivity(intent);
	}
	
	@OnClick(R.id.rlt_setting)
	public void fun_3(View v){//设置
		startActivity(new Intent(getActivity(), SettingActivity.class));
	}
	@OnClick(R.id.rlt_feedback)
	public void fun_4(View v){//反馈
		startActivity(new Intent(getActivity(), FeedbackActivity.class));
	}
	
	@OnClick(R.id.rlt_about)
	public void fun_5(View v){//关于
		Log.e(",,",""+UserManager.getInstance(getActivity()).getCurrentUser().getToken());//输出token
		Intent intent = new Intent(getActivity(), CommonWebViewActivity.class);
		intent.putExtra(CommonWebViewActivity.INTENT_TITLE, "关于");
		intent.putExtra(CommonWebViewActivity.INTENT_URL, UrlConstants.ABOUT_WEB_URL);
		startActivity(intent);
	}
	
	@OnClick(R.id.rlt_recommend)
	public void fun_6(View v){
		dlgShare.show();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_me, container, false);
		ViewUtils.inject(this, view);
		// 設置背景
//		Bitmap background = SingletonBitmapClass.getInstance()
//				.getBackgroundBitmap();
//		ViewCompat.setBackground(view, new BitmapDrawable(getResources(),
//				background));
		initShareDialog();
		return view;
	}

	public static final MineFragment newInstance(String key){
		MineFragment fragment = new MineFragment();
        Bundle bundle = new Bundle();
        bundle.putString("Tag", key);
        fragment.setArguments(bundle);
        return fragment;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView();
		initData();
	}
	
	public MineFragment() {
		super();
		// TODO 自动生成的构造函数存根
	}

	private void initView() {
		initTopBarForOnlyTitle("我");
	}

	private void initData() {
		
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	
	private void initShareDialog() {
		// TODO 自动生成的方法存根
		View root = getActivity().getLayoutInflater().inflate(R.layout.dlg_share_way,
				null);
		dlgShare = new Dialog(getActivity(), R.style.customDialog);
		dlgShare.setContentView(root);
		Window window = dlgShare.getWindow();
		WindowManager.LayoutParams lp = window.getAttributes(); // 获取对话框当前的参数值
		lp.height = LayoutParams.WRAP_CONTENT;
		lp.width = LayoutParams.MATCH_PARENT;
		window.setAttributes(lp);

		window.setGravity(Gravity.BOTTOM); // 此处可以设置dialog显示的地位
		window.setWindowAnimations(R.style.share_dialog_anim); // 添加动画
		root.findViewById(R.id.tv_quit_share).setOnClickListener(
				new View.OnClickListener() {
					public void onClick(View v) {
						dlgShare.dismiss();
					}
				});
		root.findViewById(R.id.tv_share_mm).setOnClickListener(
				new View.OnClickListener() {
					public void onClick(View v) {
						dlgShare.dismiss();
						shareToWX(UrlConstants.ABOUT_WEB_URL+"?&", 0);
					}
				});
		root.findViewById(R.id.tv_share_friend).setVisibility(View.VISIBLE);
		root.findViewById(R.id.tv_share_friend).setOnClickListener(
				new View.OnClickListener() {
					public void onClick(View v) {
						dlgShare.dismiss();
						shareToWX(UrlConstants.ABOUT_WEB_URL+"?&", 1);
					}
				});
		root.findViewById(R.id.tv_share_qq).setVisibility(View.GONE);
		root.findViewById(R.id.tv_share_weibo).setVisibility(View.GONE);
	}
	
	public static final String APP_ID = "wx422abbf509a5487b";
	public static final int WX_SCENE_SESSION = 0;
	public static final int WX_SCENE_TIMELINE = 1;
	
	public void shareToWX(String url, int scene) {
		
		IWXAPI api =  WXAPIFactory.createWXAPI(getActivity(), APP_ID, false); 
		api.registerApp(APP_ID); 
		
		// 网页
		WXWebpageObject webPageObj = new WXWebpageObject();
		webPageObj.webpageUrl = url;
 
		// 用WXTextObject对象初始化一个WXMediaMessage对象
		WXMediaMessage msg = new WXMediaMessage();
//		msg.mediaObject = textObj;
		msg.mediaObject = webPageObj;
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
