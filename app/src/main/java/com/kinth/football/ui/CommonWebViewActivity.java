package com.kinth.football.ui;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
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
import com.kinth.football.R;

/**
 * 通用的WebView組件
 * @author Sola
 */
@SuppressLint("SetJavaScriptEnabled")
@ContentView(R.layout.activity_common_webview)
public class CommonWebViewActivity extends BaseActivity{
	public static final String INTENT_URL = "INTENT_URL";//要去往的url地址
	public static final String INTENT_TITLE = "INTENT_TITLE";//要显示的title内容
	private Context mContext;
	private String url;
	private String title;
	
	private boolean found = false;
	
	private Dialog dlgShare;
	
	@ViewInject(R.id.back)
	private ImageView back;
	
	@ViewInject(R.id.tv_title)
	private TextView titleView;
	
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
	
	@OnClick(R.id.back)
	public void fun_4(View v){
		finish();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		ViewUtils.inject(this);
		mProgressBar.setMax(100);
		if(getIntent() !=null){
			url = getIntent().getStringExtra(INTENT_URL);
			title = getIntent().getStringExtra(INTENT_TITLE);
			/*if(title.equals("关于"))
				findViewById(R.id.share).setVisibility(View.VISIBLE);*/
			if(!TextUtils.isEmpty(title)){
				titleView.setText(title);
			}
//			Log.e("url",""+url);
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

			});
			webView.setDownloadListener(new MyWebViewDownLoadListener());
			webView.loadUrl(url);
		}else{
			titleView.setText("空白页面");
			webView.loadDataWithBaseURL(null, "无效的链接地址","text/html", "utf-8", null);
		}
		
		initShareDialog();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if(webView.canGoBack()){
				webView.goBack();
			}else{
				CommonWebViewActivity.this.finish();
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
	
	public void shareBtnOnclick(View v){
		dlgShare.show();
	}
	
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
						initShareIntent("com.tencent.mm");
					}
				});
		root.findViewById(R.id.tv_share_qq).setOnClickListener(
				new View.OnClickListener() {
					public void onClick(View v) {
						dlgShare.dismiss();
						initShareIntent("tencent.mobileqq");
					}
				});
		root.findViewById(R.id.tv_share_weibo).setOnClickListener(
				new View.OnClickListener() {
					public void onClick(View v) {
						dlgShare.dismiss();
						initShareIntent("com.sina.weibo");
					}
				});
	}
	
	private void initShareIntent(String type) {
		final Intent share = new Intent(android.content.Intent.ACTION_SEND);
		share.setType("text/plain");  
		// gets the list of intents that can be loaded.
		List<ResolveInfo> resInfo = getPackageManager().queryIntentActivities(
				share, 0);
		if (!resInfo.isEmpty()) {
			for (final ResolveInfo info : resInfo) {
				if (info.activityInfo.packageName.toLowerCase().contains(type)
						|| info.activityInfo.name.toLowerCase().contains(type)) {
					share.putExtra(Intent.EXTRA_SUBJECT, "分享给你一款有趣的应用");
	                share.putExtra(Intent.EXTRA_TEXT, url);
	                share.setPackage(info.activityInfo.packageName);
	                found = true;
				}
			}
			if (!found) {
				ShowToast("应用没找到，请确认您手机上是否已安装此运用");
				return;
			}
			startActivity(Intent.createChooser(share, "球队阵容分享"));
			found = false;
		}
	}
	
}
