package com.kinth.football.ui.match;

import org.apache.commons.lang3.StringUtils;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.kinth.football.R;
import com.kinth.football.bean.match.MatchInfo;
import com.kinth.football.dao.Team;
import com.kinth.football.ui.BaseActivity;
import com.kinth.football.ui.team.TeamInfoActivity;
import com.kinth.football.ui.team.TeamInfoForGuestActivity;
import com.kinth.football.ui.team.TeamPlayerInfo;
import com.kinth.football.util.DBUtil;
import com.kinth.football.util.DateUtil;
import com.kinth.football.util.PhotoUtil;
import com.kinth.football.util.PictureUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

@ContentView(R.layout.activity_match_detail_on_cancle)
public class MatchDetailOnCancleStateActivity extends BaseActivity{
	public static final String INTENT_MATCH_DETAIL_BEAN = "INTENT_MATCH_DETAIL_BEAN";// intent中传递的比赛数据信息

	private static final int REQUEST_CODE_RECEIPT_HOMETEAM_INFO = 9001;   //查看主队球队信息请求码
	private static final int REQUEST_CODE_RECEIPT_AWAYTEAM_INFO = 9002;   //查看客队球队信息请求码
	
	private MatchInfo matchInfo;
	
	@ViewInject(R.id.nav_left)
	private ImageButton back;

	@ViewInject(R.id.nav_title)
	private TextView title;

	@ViewInject(R.id.tv_home_team_name)
	private TextView homeTeamName;// 主队名称

	@ViewInject(R.id.iv_home_team_badge)
	private ImageView homeTeamBadge;// 主队队徽

	@ViewInject(R.id.tv_home_team_score)
	private TextView homeTeamScore;// 主队比分

	@ViewInject(R.id.tv_away_team_name)
	private TextView awayTeamName;// 客队名称

	@ViewInject(R.id.iv_away_team_badge)
	private ImageView awayTeamBadge;// 客队队徽

	@ViewInject(R.id.tv_away_team_score)
	private TextView awayTeamScore;// 客队比分

	@ViewInject(R.id.tv_match_name)
	private TextView matchName;// 比赛名称

	@ViewInject(R.id.tv_match_field)
	private TextView matchField;// 比赛场地

	@ViewInject(R.id.tv_match_date)
	private TextView matchDate;// 比赛时间

	@ViewInject(R.id.tv_match_player_count)
	private TextView matchPlayerCount;

	@OnClick(R.id.nav_left)
	public void fun_1(View v) {
		finish();
	}
	
	@OnClick(R.id.iv_home_team_badge)  //点击主队队徽
	public void fun_6(View v){  
		Intent intent = null;
		if(DBUtil.isGuest(mContext, matchInfo.getHomeTeam())){  //来宾
			intent = new Intent(mContext, TeamInfoForGuestActivity.class);
			intent.putExtra(TeamInfoForGuestActivity.INTENT_TEAM_INFO_BEAN, matchInfo.getHomeTeam());
			intent.putExtra(TeamInfoForGuestActivity.INTENT_NEET_TO_RETURN_BEAN, true);
			startActivityForResult(intent,REQUEST_CODE_RECEIPT_HOMETEAM_INFO);
		}else{//成员
			intent = new Intent(mContext, TeamInfoActivity.class);
			intent.putExtra(TeamInfoActivity.INTENT_TEAM_INFO_BEAN, matchInfo.getHomeTeam());
			intent.putExtra(TeamInfoActivity.INTENT_NEET_TO_RETURN_BEAN, true);
			startActivityForResult(intent,REQUEST_CODE_RECEIPT_HOMETEAM_INFO);
		}
	}
	
	@OnClick(R.id.iv_away_team_badge)  //点击客队队徽
	public void fun_7(View v){
		Intent intent = null;
		if(DBUtil.isGuest(mContext, matchInfo.getAwayTeam())){  //来宾
			intent = new Intent(mContext, TeamInfoForGuestActivity.class);
			intent.putExtra(TeamInfoForGuestActivity.INTENT_TEAM_INFO_BEAN, matchInfo.getAwayTeam());
			intent.putExtra(TeamInfoForGuestActivity.INTENT_NEET_TO_RETURN_BEAN, true);
			startActivityForResult(intent,REQUEST_CODE_RECEIPT_AWAYTEAM_INFO);
		}else{//成员
			intent = new Intent(mContext, TeamInfoActivity.class);
			intent.putExtra(TeamInfoActivity.INTENT_TEAM_INFO_BEAN, matchInfo.getAwayTeam());
			intent.putExtra(TeamInfoActivity.INTENT_NEET_TO_RETURN_BEAN, true);
			startActivityForResult(intent,REQUEST_CODE_RECEIPT_AWAYTEAM_INFO);
		}
	}
	
   @Override
   protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	ViewUtils.inject(this);
	
	matchInfo = getIntent().getParcelableExtra(INTENT_MATCH_DETAIL_BEAN);
	if (matchInfo == null) {
		return;
	}
	title.setText("比赛取消");
	initDate();
	
}
   private void initDate() {
		homeTeamName.setText(matchInfo.getHomeTeam().getName());
		awayTeamName.setText(matchInfo.getAwayTeam().getName());

		PictureUtil.getMd5PathByUrl(PhotoUtil.getThumbnailPath(
				matchInfo.getHomeTeam().getBadge()), homeTeamBadge,
				new DisplayImageOptions.Builder()
					.showImageOnLoading(R.drawable.team_bage_default)
					.showImageForEmptyUri(R.drawable.team_bage_default)
					.showImageOnFail(R.drawable.team_bage_default).build());
		PictureUtil.getMd5PathByUrl(PhotoUtil.getThumbnailPath(
				matchInfo.getAwayTeam().getBadge()), awayTeamBadge,
				new DisplayImageOptions.Builder()
					.showImageOnLoading(R.drawable.team_bage_default)
					.showImageForEmptyUri(R.drawable.team_bage_default)
					.showImageOnFail(R.drawable.team_bage_default).build());

		matchName.setText(matchInfo.getName());
		matchField.setText(matchInfo.getField());
		matchDate.setText(StringUtils.defaultIfEmpty(DateUtil.parseTimeInMillis_hadweek(matchInfo.getKickOff()),""));//比赛时间
		matchPlayerCount.setText(matchInfo.getPlayerCount() + "人");
	}
   
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, intent);
		if (resultCode != RESULT_OK) {
			return;
		}
		if(requestCode == REQUEST_CODE_RECEIPT_HOMETEAM_INFO){
			Team receiptTeam = intent.getParcelableExtra(TeamInfoForGuestActivity.INTENT_TEAM_INFO_BEAN);
			if(receiptTeam == null){
				return;
			}
			matchInfo.setHomeTeam(receiptTeam);  //替换之前的HomeTeam
			return;
		}
		if(requestCode == REQUEST_CODE_RECEIPT_AWAYTEAM_INFO){
			Team receiptTeam = intent.getParcelableExtra(TeamInfoForGuestActivity.INTENT_TEAM_INFO_BEAN);
			if(receiptTeam == null){
				return;
			}
			matchInfo.setAwayTeam(receiptTeam);  //替换之前的AwayTeam
			return;    
		}
	}
}
