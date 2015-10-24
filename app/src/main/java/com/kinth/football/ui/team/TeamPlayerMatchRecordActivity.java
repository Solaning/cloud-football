package com.kinth.football.ui.team;

import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kinth.football.R;
import com.kinth.football.bean.PlayerAllMatchsResultResponse;
import com.kinth.football.bean.Record;
import com.kinth.football.config.MatchResultEnum;
import com.kinth.football.manager.UserManager;
import com.kinth.football.network.NetWorkManager;
import com.kinth.football.singleton.SingletonBitmapClass;
import com.kinth.football.singleton.ViewCompat;
import com.kinth.football.ui.BaseActivity;
import com.kinth.football.ui.fragment.MatchFragment;
import com.kinth.football.ui.match.FriendlyListActivity;
import com.kinth.football.ui.match.LeagueListActivity;
import com.kinth.football.util.ErrorCodeUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
/**
 * 个人比赛战绩--友谊赛 and 锦标赛
 * @author zyq
 *
 */
@ContentView(R.layout.activity_team_player_match_record)
public class TeamPlayerMatchRecordActivity  extends BaseActivity{

	@ViewInject(R.id.nav_left)
	private ImageButton back;

	@ViewInject(R.id.nav_title)
	private TextView title;
	
	@ViewInject(R.id.friendly_result_more)  //友谊赛战绩=======更多
	private LinearLayout friendly_result_more;
	
	@ViewInject(R.id.friend_present_count)
	private TextView friend_present_count;  //友谊赛战绩出场次数
	
	@ViewInject(R.id.friendly_win_count)
	private TextView friendly_win_count;  //友谊赛战绩赢次数

	@ViewInject(R.id.friendly_equal_count)
	private TextView friendly_equal_count;  //友谊赛战绩平次数
	
	@ViewInject(R.id.friendly_failed_count)
	private TextView friendly_failed_count;  //友谊赛战绩负次数
	
	@ViewInject(R.id.friend_one)
	private ImageView friend_one;  
	
	@ViewInject(R.id.friend_two)
	private ImageView friend_two;  
	
	@ViewInject(R.id.friend_third)
	private ImageView friend_third;  
	
	@ViewInject(R.id.friend_four)
	private ImageView friend_four;  
	
	@ViewInject(R.id.friend_five)
	private ImageView friend_five; 
	
	private ImageView[] friendResult_Imgs = new ImageView[5];
	
	@ViewInject(R.id.friend_none)
	private TextView friend_none;
	
	@ViewInject(R.id.tournament_result_more)  //锦标赛战绩=======更多
	private LinearLayout tournament_result_more;
	
	@ViewInject(R.id.tournament_present_count)
	private TextView tournament_present_count;  //锦标赛战绩出场次数
	
	@ViewInject(R.id.tournament_win_count)
	private TextView tournament_win_count;  //锦标赛战绩赢次数

	@ViewInject(R.id.tournament_equal_count)
	private TextView tournament_equal_count;  //锦标赛战绩平次数
	
	@ViewInject(R.id.tournament_failed_count)
	private TextView tournament_failed_count;  //锦标赛战绩负次数
	
	@ViewInject(R.id.tournament_one)
	private ImageView tournament_one;  
	
	@ViewInject(R.id.tournament_two)
	private ImageView tournament_two;  
	
	@ViewInject(R.id.tournament_third)
	private ImageView tournament_third;  
	
	@ViewInject(R.id.tournament_four)
	private ImageView tournament_four;  
	
	@ViewInject(R.id.tournament_five)
	private ImageView tournament_five; 
	
	private ImageView[] tournamentResult_Imgs = new ImageView[5];
	
	@ViewInject(R.id.tournament_none)
	private TextView tournament_none;
	
	
	@OnClick(R.id.nav_left)
	public void fun_1(View v) {
		finish();
	}
	
	@OnClick(R.id.friendly_result_more)
	public void fun_9(View v) {// 查看更多友谊赛
		Intent intent = new Intent(mContext, FriendlyListActivity.class);
		intent.putExtra(MatchFragment.INTENT_FRIEND_MATCH_DATA, intentPlayerId);
		startActivity(intent);
	 }
	@OnClick(R.id.tournament_result_more)
	public void fun_10(View v) {// 查看更多锦标赛
		Intent intent = new Intent(mContext, LeagueListActivity.class);
		intent.putExtra(MatchFragment.INTENT_LEAGUE_MATCH_DATA, intentPlayerId);
		startActivity(intent);
	 }
	@ViewInject(R.id.viewGroup)
	private LinearLayout viewGroup;
	
    private	String intentPlayerId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	// TODO Auto-generated method stub
    	super.onCreate(savedInstanceState);
    	ViewUtils.inject(this);
    	
    	Bitmap background = SingletonBitmapClass.getInstance()
				.getBackgroundBitmap();	
		ViewCompat.setBackground(viewGroup, new BitmapDrawable(getResources(),
				background));
		
		title.setText("比赛战绩");
		intentPlayerId = this.getIntent().getStringExtra(TeamPlayerInfo.PLAYER_UUID);
		if(intentPlayerId == null){
			return;
		}
		initView();
		executeFriendMatchsResult();
		executeTournamentMatchsResult();
    }
    private void initView(){
    	friendResult_Imgs[0] = friend_one;
		friendResult_Imgs[1] = friend_two;
		friendResult_Imgs[2] = friend_third;
		friendResult_Imgs[3] = friend_four;
		friendResult_Imgs[4] = friend_five;
		
		tournamentResult_Imgs[0] = tournament_one;
		tournamentResult_Imgs[1] = tournament_two;
		tournamentResult_Imgs[2] = tournament_third;
		tournamentResult_Imgs[3] = tournament_four;
		tournamentResult_Imgs[4] = tournament_five;
    	
		
    }
    private void executeFriendMatchsResult(){
		NetWorkManager.getInstance(mContext).playerFriendMatchData(intentPlayerId, 
				UserManager.getInstance(mContext).getCurrentUser().getToken(), 
				new Listener<JSONObject>(){
					@Override
					public void onResponse(JSONObject response) {
						// TODO Auto-generated method stub
//						System.out.println("球员友谊赛战绩（出场次数，胜平负等）" + response.toString());
						Gson gson = new Gson();
						PlayerAllMatchsResultResponse allResult = null;
						allResult = gson.fromJson(response.toString(), new TypeToken<PlayerAllMatchsResultResponse>(){}.getType());
						if(allResult != null){
							Record record = allResult.getRecord(); 
							
							friend_present_count.setText("" + record.getPlayed());
							friendly_win_count.setText("" + record.getWon());
							friendly_equal_count.setText("" + record.getDrawn());
							friendly_failed_count.setText("" + record.getLost());
							
							//判断显示“胜”，“平”，“负”img
							showKindOfFriendImg(allResult.getResults());
						}
						
					}
				}, new ErrorListener(){
					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						if(!NetWorkManager.getInstance(mContext).isNetConnected()){
//							ShowToast("当前网络不可用");
							return;
						}
						if(error.networkResponse == null){
//							ShowToast("服务器连接错误");
						}else if(error.networkResponse.statusCode == 401){
							ErrorCodeUtil.ErrorCode401(mContext);
						}else if(error.networkResponse.statusCode == 404){
//							ShowToast("找不到该球员,友谊赛战绩数据加载失败");
						}
					}
				});
	}
    private void showKindOfFriendImg(String[] results){
		if(results != null && results.length != 0 ){
			friend_none.setVisibility(View.GONE);
			for (int i = 0; i < results.length; i++) {
				switch(MatchResultEnum.getEnumFromString(results[i])){
				case WIN:
					friendResult_Imgs[i]
							.setImageResource(R.drawable.win);
					break;
				case DRAW:
					friendResult_Imgs[i]
							.setImageResource(R.drawable.equal);
					break;
				case LOSS:
					friendResult_Imgs[i]
							.setImageResource(R.drawable.falie);
					break;
				case NULL:
					friendResult_Imgs[i].setImageResource(0); // 显示空
					break;
				}
			}
		}else {
			friend_none.setVisibility(View.VISIBLE);
		}
	}
    private void executeTournamentMatchsResult(){
		NetWorkManager.getInstance(mContext).playerTournamentMatchData(intentPlayerId, 
				UserManager.getInstance(mContext).getCurrentUser().getToken(), 
				new Listener<JSONObject>(){
					@Override
					public void onResponse(JSONObject response) {
						// TODO Auto-generated method stub
//						System.out.println("球员友谊赛战绩（出场次数，胜平负等）" + response.toString());
						Gson gson = new Gson();
						PlayerAllMatchsResultResponse allResult = null;
						allResult = gson.fromJson(response.toString(), new TypeToken<PlayerAllMatchsResultResponse>(){}.getType());
						if(allResult != null){
							Record record = allResult.getRecord(); 
							
							tournament_present_count.setText("" + record.getPlayed());
							tournament_win_count.setText("" + record.getWon());
							tournament_equal_count.setText("" + record.getDrawn());
							tournament_failed_count.setText("" + record.getLost());
							
							//判断显示“胜”，“平”，“负”img
							showKindOfTournamentImg(allResult.getResults());
						}
						
					}
				}, new ErrorListener(){
					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						if(!NetWorkManager.getInstance(mContext).isNetConnected()){
//							ShowToast("当前网络不可用");
							return;
						}
						if(error.networkResponse == null){
//							ShowToast("服务器连接错误");
						}else if(error.networkResponse.statusCode == 401){
							ErrorCodeUtil.ErrorCode401(mContext);
						}else if(error.networkResponse.statusCode == 404){
//							ShowToast("找不到该球员,友谊赛战绩数据加载失败");
						}
					}
				});
	}
    private void showKindOfTournamentImg(String[] results){
		if(results != null && results.length != 0 ){
			tournament_none.setVisibility(View.GONE);
			for (int i = 0; i < results.length; i++) {
				switch(MatchResultEnum.getEnumFromString(results[i])){
				case WIN:
					tournamentResult_Imgs[i]
							.setImageResource(R.drawable.win);
					break;
				case DRAW:
					tournamentResult_Imgs[i]
							.setImageResource(R.drawable.equal);
					break;
				case LOSS:
					tournamentResult_Imgs[i]
							.setImageResource(R.drawable.falie);
					break;
				case NULL:
					tournamentResult_Imgs[i].setImageResource(0); // 显示空
					break;
				}
			}
		}else {
			tournament_none.setVisibility(View.VISIBLE);
		}
	}
}
