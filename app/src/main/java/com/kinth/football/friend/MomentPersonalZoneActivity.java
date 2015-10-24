package com.kinth.football.friend;

import java.io.InputStream;
import java.util.List;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import de.greenrobot.event.EventBus;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kinth.football.R;
import com.kinth.football.bean.Pageable;
import com.kinth.football.bean.moments.MomentsPersonalZoneResponse;
import com.kinth.football.bean.moments.Sharing;
import com.kinth.football.config.Config;
import com.kinth.football.dao.Player;
import com.kinth.football.eventbus.bean.DeleteSharingEvent;
import com.kinth.football.manager.UserManager;
import com.kinth.football.network.NetWorkManager;
import com.kinth.football.singleton.ViewCompat;
import com.kinth.football.ui.BaseActivity;
import com.kinth.football.ui.team.TeamPlayerInfo;
import com.kinth.football.util.DialogUtil;
import com.kinth.football.util.ErrorCodeUtil;
import com.kinth.football.util.PhotoUtil;
import com.kinth.football.util.PictureUtil;
import com.kinth.football.view.RoundImageView;
import com.kinth.football.view.xlist.XListView;
import com.kinth.football.view.xlist.XListView.IXListViewListener;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

/**
 * 朋友圈个人主页
 * @author Sola
 */
@ContentView(R.layout.activity_moment_personal_zone)
public class MomentPersonalZoneActivity extends BaseActivity{
	public static final String INTENT_PLAYER_UUID = "INTENT_PLAYER_UUID";//要查看主页的球员id  onSaveInstanceState
	
	private MomentPersonalZoneAdapter adapter;
	private String playerUuid;
	private Pageable pageable;
	private int page;
	private int firstVisible;// 第一项的位置
	private ProgressDialog proDialog;
	
	@ViewInject(R.id.nav_left)
	private View back;
	
	@ViewInject(R.id.nav_title)
	private TextView title;
	
	@ViewInject(R.id.nav_right_image)
	private ImageView right;// 右侧按钮
	
	@ViewInject(R.id.listview_moments)
	private XListView listview;
	
	private ImageView momentBg;//背景图
	private RoundImageView playerIcon;//用户icon
	private TextView playerName;//用户名称

	@OnClick(R.id.nav_left)
	public void fun_1(View v){
		finish();
	}
	@ViewInject(R.id.viewGroup)
	private LinearLayout viewGroup;
	private Bitmap bitmap;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EventBus.getDefault().register(this);
		ViewUtils.inject(this);
		
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.ARGB_8888;
		opt.inInputShareable = true;
		opt.inPurgeable = true;
		// 获取资源图片
		InputStream is = mContext.getResources().openRawResource(
				R.drawable.bg_family_dafult);
		bitmap = BitmapFactory.decodeStream(is, null, opt);
		
		ViewCompat.setBackground(viewGroup, new BitmapDrawable(getResources(),
				bitmap));
		
		playerUuid = getIntent().getStringExtra(INTENT_PLAYER_UUID);
		title.setText("个人空间");
		
		if(TextUtils.isEmpty(playerUuid)){
			finish();
			return;
		}
		
		View headerView = LayoutInflater.from(this).inflate(
				R.layout.header_moment_personal_zone, null);
		momentBg = (ImageView) headerView.findViewById(R.id.iv_mtopimg);
		playerIcon = (RoundImageView) headerView.findViewById(R.id.siv_img);
		playerName = (TextView) headerView.findViewById(R.id.tv_player_name);
		playerIcon.setOnClickListener(new OnClickListener() {//跳转球员个人信息
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(mContext, TeamPlayerInfo.class);
				intent.putExtra(TeamPlayerInfo.PLAYER_UUID,
						playerUuid);
				startActivity(intent);
			}
		});
		listview.addHeaderView(headerView);
		adapter = new MomentPersonalZoneAdapter(mContext, null);
		listview.setAdapter(adapter);
		listview.setPullRefreshEnable(false);
		listview.setPullLoadEnable(false);     
		
		listview.setXListViewListener(new IXListViewListener() {
			
			@Override
			public void onRefresh() {//刷新，加载更晚发的动态
				page = 0;  //重置page
				getPersonalSharing(page);
			}
			
			@Override
			public void onLoadMore() {//加载更多，加载更早发的动态
				if(pageable == null){
					getPersonalSharing(page);
					return;
				}
				if(pageable.getTotalPages() > page){
					getPersonalSharing(page);
				}else{//没用更多
					listview.stopRefresh();
					listview.stopLoadMore();
					listview.setPullLoadEnable(false);  
					ShowToast("没用更多数据");
				}
			}
		});

		listview.setOnScrollListener(new AbsListView.OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// 当列表滚动停止，并且当前可见条目不是第一条的话，运行动画。
				if (SCROLL_STATE_IDLE == scrollState && firstVisible > 0) {
					
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				firstVisible = firstVisibleItem;
			}
		});
		
		listview.setOnItemClickListener(new OnItemClickListener() {//查看某一条动态

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(mContext,
						MomentsPersonalZoneItemActivity.class);
				intent.putExtra(MomentsPersonalZoneItemActivity.INTENT_SHARING, 
						(Parcelable) parent.getAdapter().getItem(position));
				startActivity(intent);
			}
		});
		
		proDialog = ProgressDialog.show(mContext, "提示", "请稍候...", false, false);
		listview.pullRefreshing();
		getPersonalSharing(page);
	}
	
	//取该用户所有动态
	private void getPersonalSharing(int requestPage){
		NetWorkManager.getInstance(mContext).getMomentsPersonalSharing(playerUuid, requestPage, Config.DEFAULT_NUM_OF_ACTIVE,
				UserManager.getInstance(mContext).getCurrentUser().getToken(), new Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						listview.stopRefresh();
						listview.stopLoadMore();
						DialogUtil.dialogDismiss(proDialog);
						Gson gson = new Gson();
						MomentsPersonalZoneResponse momentsPersonalZoneResponse = gson.fromJson(response.toString(), 
								new TypeToken<MomentsPersonalZoneResponse>(){}.getType());
						if(momentsPersonalZoneResponse == null){
							return;
						}
						
						pageable = momentsPersonalZoneResponse.getPageable();
						refreshPlayerInfo(momentsPersonalZoneResponse.getPlayer());
						adapter.addSharingList(momentsPersonalZoneResponse.getSharings());
						
						if(momentsPersonalZoneResponse.getSharings() != null && 
								momentsPersonalZoneResponse.getSharings().size() != 0){
							if(momentsPersonalZoneResponse.getPageable().getTotalPages() > 1){
								listview.setPullLoadEnable(true);  
							}else{
								listview.setPullLoadEnable(false);
							}
						}else{
							ShowToast("暂无数据");
							listview.setPullLoadEnable(false);  
						}
						
						page ++;
					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						listview.stopRefresh();
						listview.stopLoadMore();
						DialogUtil.dialogDismiss(proDialog);
						if (!NetWorkManager.getInstance(mContext)
								.isNetConnected()) {
							ShowToast("当前网络不可用，请稍后重试");
						} else if (error.networkResponse == null) {
							ShowToast("获取最新数据失败，请稍后重试");
						} else if (error.networkResponse.statusCode == 401) {
							ErrorCodeUtil.ErrorCode401(mContext);
						} else if (error.networkResponse.statusCode == 404){
							ShowToast("找不到该球员");
						} else {
							ShowToast("获取最新数据失败，请稍后重试");
						}
					}
				});
	}

	private void refreshPlayerInfo(Player player){
		if(player == null){
			return;
		}
		playerName.setText(player.getName());
//		ImageLoader.getInstance().displayImage(PhotoUtil.getAllPhotoPath(player.getPicture()), playerIcon);
		PictureUtil.getMd5PathByUrl(PhotoUtil.getThumbnailPath(player.getPicture()), playerIcon,new DisplayImageOptions.Builder()
		.showImageOnLoading(
				R.drawable.icon_head)
		.showImageForEmptyUri(R.drawable.icon_head)
		.showImageOnFail(R.drawable.icon_head) // 默认头像
		.cacheInMemory(true).cacheOnDisk(true).build());
	}
	
	@Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(INTENT_PLAYER_UUID)) {
        	playerUuid = savedInstanceState.getString(INTENT_PLAYER_UUID);
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	if(!TextUtils.isEmpty(playerUuid)){
    		outState.putString(INTENT_PLAYER_UUID, playerUuid);
        }
        super.onSaveInstanceState(outState);
    }
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	EventBus.getDefault().unregister(this);
    	if (bitmap != null) {
			bitmap.recycle();
			bitmap = null;
		}
    }
    
	/**
	 * 删除朋友圈动态的事件
	 */
	public void onEventMainThread(DeleteSharingEvent deleteSharingEvent){
		String sharingUuid = deleteSharingEvent.getSharingUuid();
		if(TextUtils.isEmpty(sharingUuid)){
			return;
		}
		List<Sharing> sharingLists = adapter.getSharingList();
		int size = sharingLists.size();
		for(int i = 0; i < size; i++){
			if(sharingUuid.equals(sharingLists.get(i).getUuid())){
				sharingLists.remove(i);
				break;
			}
		}
		adapter.notifyDataSetChanged();
	}
}
