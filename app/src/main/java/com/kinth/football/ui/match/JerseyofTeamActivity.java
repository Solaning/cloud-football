package com.kinth.football.ui.match;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.kinth.football.R;
import com.kinth.football.dao.Team;
import com.kinth.football.singleton.SingletonBitmapClass;
import com.kinth.football.singleton.ViewCompat;
import com.kinth.football.ui.BaseActivity;
import com.kinth.football.util.PhotoUtil;
import com.kinth.football.util.PictureUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

/**
 * 设置队服
 * @author Sola
 * 
 */
@ContentView(R.layout.activity_select_teamjersey)
public class JerseyofTeamActivity extends BaseActivity {

	//比赛队服
	public static final String MATCH_JERSEY = "MATCH_JERSEY";
	
	public static final String INTENT_MATCH_TEAM = "INTENT_MATCH_TEAM";
	
	private Team team;
	
	@ViewInject(R.id.entire_layout)
	private View entireLayout;
	
	@ViewInject(R.id.nav_left)
	private ImageButton nav_left;
	
	@ViewInject(R.id.nav_title)
	private TextView nav_title;
	
	@ViewInject(R.id.homejersey)
	private ImageView homejersey;  //主队队服
	
	@ViewInject(R.id.homejersey_text)
	private TextView homejersey_text;
	
	@ViewInject(R.id.roadjersey)
	private ImageView roadjersey;  //客队队服
	
	@ViewInject(R.id.roadjersey_text)
	private TextView roadjersey_text;
	
	@ViewInject(R.id.alternatejersey)
	private ImageView alternatejersey;  //第三队服
	
	@ViewInject(R.id.alternatejersey_text)
	private TextView alternatejersey_text;
	
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
		ViewCompat.setBackground(entireLayout, new BitmapDrawable(getResources(),
				background));
		
		nav_title.setText("选择队服");
		
		team = getIntent().getParcelableExtra(INTENT_MATCH_TEAM);
		if(team == null){
			return;
		}
		
		initData();
	}

	private void initData(){
		if(team.getHomeJersey() != null){
			PictureUtil.getMd5PathByUrl(PhotoUtil.getThumbnailPath(team.getHomeJersey()), 
					homejersey,
					new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.jersey_default)
			.showImageForEmptyUri(R.drawable.jersey_default)
			.showImageOnFail(R.drawable.jersey_default).build());
			homejersey.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
//					Intent intent = new Intent();
					Intent intent = getIntent();
					intent.putExtra(MATCH_JERSEY, team.getHomeJersey());
					JerseyofTeamActivity.this.setResult(RESULT_OK, intent);
					JerseyofTeamActivity.this.finish();
				}
			});
		}else{
			homejersey.setVisibility(View.GONE);
			homejersey_text.setText("该队队长未设置主队队服");
		}
		
		if(team.getRoadJersey() != null){
			PictureUtil.getMd5PathByUrl(PhotoUtil.getThumbnailPath(team.getRoadJersey()), 
					roadjersey,
					new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.jersey_default)
			.showImageForEmptyUri(R.drawable.jersey_default)
			.showImageOnFail(R.drawable.jersey_default).build());
			roadjersey.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					intent.putExtra(MATCH_JERSEY, team.getRoadJersey());
					JerseyofTeamActivity.this.setResult(RESULT_OK, intent);
					JerseyofTeamActivity.this.finish();
				}
			});
		}else{
			roadjersey.setVisibility(View.GONE);
			roadjersey_text.setText("该队队长未设置客队队服");
		}

		if(team.getAlternateJersey() != null){
			PictureUtil.getMd5PathByUrl(PhotoUtil.getThumbnailPath(team.getAlternateJersey()), 
					alternatejersey,
					new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.jersey_default)
			.showImageForEmptyUri(R.drawable.jersey_default)
			.showImageOnFail(R.drawable.jersey_default).build());
			alternatejersey.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					intent.putExtra(MATCH_JERSEY, team.getAlternateJersey());
					JerseyofTeamActivity.this.setResult(RESULT_OK, intent);
					JerseyofTeamActivity.this.finish();
				}
			});
		}else{
			alternatejersey.setVisibility(View.GONE);
			alternatejersey_text.setText("该队队长未设置第三队服");
		}
	}
}
