package com.kinth.football.ui;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.FrameLayout.LayoutParams;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.apache.commons.lang3.StringUtils;

import com.kinth.football.R;
import com.kinth.football.ui.fragment.MineFragment;

/**
 * 通用的WebView組件
 * @author Sola
 */
@SuppressLint("SetJavaScriptEnabled")
@ContentView(R.layout.activity_league_webview)
public class LeagueWebViewActivity extends BaseActivity{
	public static final String INTENT_URL = "INTENT_URL";//要去往的url地址
	public static final String INTENT_TITLE = "INTENT_TITLE";//要显示的title内容
	public static final String INTENT_SHORT_NAME = "INTENT_SHORT_NAME";//要显示的title内容
	private Context mContext;
	private String url;
	private String shortName;
	private String currentPageTitle;//当前页面的title
	
	@ViewInject(R.id.nav_left)
	private TextView tvBack;
	
	@ViewInject(R.id.WebViewProgress)
	private ProgressBar mProgressBar;
	
	@ViewInject(R.id.webview)
	private WebView webView;
	
	@ViewInject(R.id.PreviousBtn)
	private ImageView previous;
	
	@ViewInject(R.id.NextBtn)
	private ImageView next;
	
	@ViewInject(R.id.RefreshBtn)
	private ImageView refresh;
	
	@ViewInject(R.id.nav_right)
	private ImageView right;//右侧按钮
	
	@OnClick(R.id.PreviousBtn)
	public void fun_1(View v){//上一个
		if(webView.canGoBack()){
			webView.goBack();
		}else{
			finish();
		}
	}
	
	@OnClick(R.id.NextBtn)
	public void fun_2(View v){//下一个
		if(webView.canGoForward()){
			webView.goForward();
		}
	}
	
	@OnClick(R.id.RefreshBtn)
	public void fun_3(View v){//刷新
		webView.reload();
	}
	
	@OnClick(R.id.nav_left)
	public void fun_4(View v){
		finish();
	}
	
	@OnClick(R.id.nav_right)
	public void fun_5(View v){//分享
		View root = getLayoutInflater().inflate(R.layout.dlg_share_way,
				null);
		final Dialog dlgShare = new Dialog(mContext, R.style.customDialog);
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
						shareToWX(url, 0);
					}
				});
		root.findViewById(R.id.tv_share_friend).setVisibility(View.VISIBLE);
		root.findViewById(R.id.tv_share_friend).setOnClickListener(
				new View.OnClickListener() {
					public void onClick(View v) {
						dlgShare.dismiss();
						shareToWX(url, 1);
					}
				});
		root.findViewById(R.id.tv_share_qq).setVisibility(View.GONE);
		root.findViewById(R.id.tv_share_weibo).setVisibility(View.GONE);
		dlgShare.show();
	}
	
	//分享到微信和朋友圈
	public void shareToWX(String url, int scene) {
		IWXAPI api =  WXAPIFactory.createWXAPI(mContext, MineFragment.APP_ID, false); 
		api.registerApp(MineFragment.APP_ID); 
		
		// 网页
		WXWebpageObject webPageObj = new WXWebpageObject();
		webPageObj.webpageUrl = webView.getUrl();
 
		// 用WXTextObject对象初始化一个WXMediaMessage对象
		WXMediaMessage msg = new WXMediaMessage();
//		msg.mediaObject = textObj;
		msg.mediaObject = webPageObj;
		// 发送文本类型的消息时，title字段不起作用
		msg.title = StringUtils.defaultIfBlank(currentPageTitle, "云球-专业足球App");
//		msg.description = "云球-专业足球App";
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		ViewUtils.inject(this);
		mProgressBar.setMax(100);
		if(getIntent() !=null){
			url = getIntent().getStringExtra(INTENT_URL);
			shortName = getIntent().getStringExtra(INTENT_SHORT_NAME);
			tvBack.setText(shortName);
			if(TextUtils.isEmpty(url)){
				webView.loadDataWithBaseURL(null, "无效的链接地址","text/html", "utf-8", null);
				return;
			}
		
			webView.getSettings().setJavaScriptEnabled(true);
//			webView.getSettings().setBuiltInZoomControls(true);
			webView.getSettings().setSupportZoom(true);
//			webView.getSettings().setUseWideViewPort(true);
//			webView.setInitialScale(39);
//			webView.setInitialScale(100);
			webView.setWebViewClient(new WebViewClient() {

				@Override
				public boolean shouldOverrideUrlLoading(WebView view, String url) { // 重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
					view.loadUrl(url);
					return true;
				}

				@Override
				public void onPageStarted(WebView view, String url,
						Bitmap favicon) {
					mProgressBar.setVisibility(View.VISIBLE);
					super.onPageStarted(view, url, favicon);
				}

				@Override
				public void onPageFinished(WebView view, String url) {
					mProgressBar.setVisibility(View.GONE);
					super.onPageFinished(view, url);
				}

			});
			webView.setWebChromeClient(new WebChromeClient() {

				@Override
				public void onProgressChanged(WebView view, int newProgress) {
					mProgressBar.setProgress(newProgress);
				}

				@Override
				public void onReceivedTitle(WebView view, String title) {
					super.onReceivedTitle(view, title);
					currentPageTitle = title;
				}
				

			});
			webView.setDownloadListener(new MyWebViewDownLoadListener());
			webView.loadUrl(url);
		}else{
			webView.loadDataWithBaseURL(null, "无效的链接地址","text/html", "utf-8", null);
		}
		
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if(webView.canGoBack()){
				webView.goBack();
			}else{
				LeagueWebViewActivity.this.finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private class MyWebViewDownLoadListener implements DownloadListener {

		@Override
		public void onDownloadStart(String url, String userAgent,
				String contentDisposition, String mimetype, long contentLength) {
			Uri uri = Uri.parse(url);
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);// 使用浏览器下载
			startActivity(intent);
		}
	}
	
}
