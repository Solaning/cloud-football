package com.kinth.football.ui.match;

import java.util.List;

import org.json.JSONArray;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.kinth.football.R;
import com.kinth.football.adapter.TournamentListAdapter;
import com.kinth.football.manager.UserManager;
import com.kinth.football.network.NetWorkManager;
import com.kinth.football.singleton.SingletonBitmapClass;
import com.kinth.football.singleton.ViewCompat;
import com.kinth.football.ui.BaseActivity;
import com.kinth.football.util.ErrorCodeUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

/**
 * 锦标赛(联赛)列表
 * @author zyq
 * 传link_in_where(在哪里点进来)
 */
public class TournamentActivity  extends BaseActivity{
	
	public static final String LINK_IN_WHERE = "LINK_IN_WHERE";
	public static final String LINK_IN_HOMEPAGE = "LINK_IN_HOMEPAGE";
	public static final String LINK_IN_PERSONINFO = "LINK_IN_PERSONINFO";
	public static final String LINK_IN_MATCH = "LINK_IN_MATCH";
	public static final String INTENT_PLAYER_UUID = "INTENT_PLAYER_UUID";
	
	private String playerId;
	
	@ViewInject(R.id.entire_layout)
	private View entireLayout;
	
	@ViewInject(R.id.nav_left)
	private ImageButton back;

	@ViewInject(R.id.nav_title)
	private TextView title;
	
	@ViewInject(R.id.loaddata)
	private ProgressBar loaddataBar;
	
	@ViewInject(R.id.list_tournament)
	private ListView list_tournament;
	
	@ViewInject(R.id.txt_nocount)  //没有数据时显示的text
	private TextView txt_nocount;
	
	@OnClick(R.id.nav_left)
	public void fun_1(View v) {
		finish();
	}
	private TournamentListAdapter adapter;
	
  @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tournament);
		ViewUtils.inject(this);
		Bitmap background = SingletonBitmapClass.getInstance()
				.getBackgroundBitmap();
		ViewCompat.setBackground(entireLayout, new BitmapDrawable(
				getResources(), background));
		title.setText("锦标赛");
		String link_in_where = getIntent().getStringExtra(LINK_IN_WHERE); // 得到从哪里点击进来的
																			// 传进Adapter
		playerId = getIntent().getStringExtra(INTENT_PLAYER_UUID);
		adapter = new TournamentListAdapter(mContext, null, link_in_where, playerId);
		list_tournament.setAdapter(adapter);
		if(LINK_IN_PERSONINFO.equals(link_in_where)){
			getPersonalTournamentList(playerId);// mod @2015-07-28
		}else{
			getTournamentList(); // 获得联赛列表，传当前user的id
		}
	}

  	/**
  	 * 获取锦标赛个人比赛数据
  	 */
	private void getPersonalTournamentList(String playerUuid) {
		NetWorkManager.getInstance(mContext).getPersonalTournamentList(playerUuid, UserManager.getInstance(mContext).getCurrentUser().getToken(), 
				new Listener<JSONArray>() {

					@Override
					public void onResponse(JSONArray response) {
						List<com.kinth.football.bean.match.Tournament> tournamentList=null;
						Gson gson = new Gson();
						try{
							tournamentList = gson.fromJson(response.toString(),
								new TypeToken<List<com.kinth.football.bean.match.Tournament>>() {
								}.getType());
						}catch (JsonSyntaxException e) {
							tournamentList = null;
							e.printStackTrace(); 
						}	
		                if (tournamentList ==null || tournamentList.size()==0) {
		                	txt_nocount.setVisibility(View.VISIBLE); 
							return;
						}        
						adapter.setTournamentList(tournamentList);
					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						txt_nocount.setVisibility(View.VISIBLE);
						txt_nocount.setText("加载失败");
						if (!NetWorkManager.getInstance(mContext)
								.isNetConnected()) {
							ShowToast("当前网络不可用");
						} else if (error.networkResponse == null) {
//							ShowToast("TeamInfoActivity-getFriendlyMatchResultOfTeam-服务器连接错误");
						} else if (error.networkResponse.statusCode == 401) {
							ErrorCodeUtil.ErrorCode401(mContext);
						} else if (error.networkResponse.statusCode == 404) {
							ShowToast("错误:球员找不到");
						}
					}
				});
		loaddataBar.setVisibility(View.GONE);
	}

	// 获得联赛列表
  private void getTournamentList(){
	  NetWorkManager.getInstance(mContext).getTournamentList(UserManager.getInstance(mContext).getCurrentUser().getPlayer().getUuid(), UserManager.getInstance(mContext).getCurrentUser().getToken(),
			  new Listener<JSONArray>() {

			@Override
			public void onResponse(JSONArray response) {
				List<com.kinth.football.bean.match.Tournament> tournamentList=null;
				Gson gson = new Gson();
				try{
					tournamentList = gson.fromJson(response.toString(),
						new TypeToken<List<com.kinth.football.bean.match.Tournament>>() {
						}.getType());
				}catch (JsonSyntaxException e) {
					tournamentList = null;
					e.printStackTrace(); 
						
				}	
                if (tournamentList ==null || tournamentList.size()==0) {
                	txt_nocount.setVisibility(View.VISIBLE); 
					return;
				}        
				adapter.setTournamentList(tournamentList);
			}
			
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				txt_nocount.setVisibility(View.VISIBLE);
				txt_nocount.setText("加载失败");
				if (!NetWorkManager.getInstance(mContext)
						.isNetConnected()) {
					ShowToast("当前网络不可用");
				} else if (error.networkResponse == null) {
//					ShowToast("TeamInfoActivity-getFriendlyMatchResultOfTeam-服务器连接错误");
				} else if (error.networkResponse.statusCode == 401) {
					ErrorCodeUtil.ErrorCode401(mContext);
				} else if (error.networkResponse.statusCode == 404) {
					ShowToast("错误:球员找不到");
				}
			}
		});
	    loaddataBar.setVisibility(View.GONE);
  }
}
