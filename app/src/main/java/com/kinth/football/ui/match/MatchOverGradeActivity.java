package com.kinth.football.ui.match;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kinth.football.R;
import com.kinth.football.adapter.MatchOverGradeAdapter;
import com.kinth.football.bean.GradeNumber;
import com.kinth.football.bean.GradeResult;
import com.kinth.football.bean.match.MatchInfo;
import com.kinth.football.bean.match.PlayerInTeam;
import com.kinth.football.manager.UserManager;
import com.kinth.football.network.NetWorkManager;
import com.kinth.football.singleton.SingletonBitmapClass;
import com.kinth.football.singleton.ViewCompat;
import com.kinth.football.ui.BaseActivity;
import com.kinth.football.util.DialogUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

/**
 * 比赛评分
 * @author Sola
 * 
 */
@ContentView(R.layout.activity_matchover_grade)
public class MatchOverGradeActivity extends BaseActivity {
	public static final String INTENT_MATCH_INFO = "INTENT_MATCH_INFO";
	
	@ViewInject(R.id.entire_layout)
	private View entireLayout;
	
	@ViewInject(R.id.nav_title)
	private TextView title;
	
	@ViewInject(R.id.nav_left)
	private ImageButton back;
	
	@ViewInject(R.id.nav_right_btn)
	private Button right;//提交评价
	
	@OnClick(R.id.nav_left)
	public void fun_1(View v){
		finish();
	}
	
	@ViewInject(R.id.lv_matchover_grade)
	private ListView lv_matchover_grade;
	
	@OnClick(R.id.nav_right_btn)
	public void fun_2(View v){//提交评价
		final Map<String, GradeNumber> gradeValueMap = adapter.getGradeValue();
		if(gradeValueMap.size() == 0){
			ShowToast("没有新的评价");
			return;
		}
		AlertDialog.Builder builder2 = new Builder(mContext);
		builder2.setMessage("一旦提交将不能修改，确认提交球员评价吗？");
		builder2.setPositiveButton("确认", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog2, int which) {
				dialog2.dismiss();
				dialog = ProgressDialog.show(mContext, "提示", "请稍后...",false, false);
				JSONArray jsonArray =  new JSONArray();
				for (Map.Entry<String, GradeNumber> entry : gradeValueMap.entrySet()) {
					JSONObject obj = new JSONObject();
					try {
						obj.put("playerUuid", entry.getValue().getPlayerUuid());
						obj.put("skill", entry.getValue().getSkill());
						obj.put("morality", entry.getValue().getMorality());
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					jsonArray.put(obj);
				}
				NetWorkManager.getInstance(mContext).batchSubmitComment(matchInfo.getUuid(), is_inhometeam ? matchInfo.getHomeTeam().getUuid() : matchInfo.getAwayTeam().getUuid(),
						jsonArray, UserManager.getInstance(mContext).getCurrentUser().getToken(), 
						new Listener<Void>() {

							@Override
							public void onResponse(Void response) {
								DialogUtil.dialogDismiss(dialog);
								ShowToast("评价成功");
								finish();
							}
						}, new ErrorListener() {

							@Override
							public void onErrorResponse(VolleyError error) {
								DialogUtil.dialogDismiss(dialog);
								ShowToast("评价失败");//TODO
							}
				});
			}
		});
		builder2.setNegativeButton("取消", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder2.show();
	}
	
	private LayoutInflater inflater;
	private MatchInfo matchInfo;
	private String currentUserID;
	private List<HasGrade> initialGradeData = new ArrayList<HasGrade>();//初始的评分数据，不包含自己
	private MatchOverGradeAdapter adapter;//适配器
	
	private boolean is_inhometeam = false;// 判断本人是否在主队
	private Dialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		// 設置背景
		Bitmap background = SingletonBitmapClass.getInstance()
				.getBackgroundBitmap();
		ViewCompat.setBackground(entireLayout, new BitmapDrawable(getResources(),
				background));
		
		currentUserID = UserManager.getInstance(mContext).getCurrentUser().getPlayer().getUuid();
		title.setText("队员评价");
		right.setText("完成");
		
		matchInfo = getIntent().getParcelableExtra(INTENT_MATCH_INFO);
		inflater = LayoutInflater.from(this);
		if(matchInfo == null){
			return;
		}
		// 判断自己在主队or客队，标记自己的位置
		for (int i = 0; i < matchInfo.getHomeTeamPlayers().size(); i++) {
			if (currentUserID.equals(matchInfo.getHomeTeamPlayers().get(i).getPlayer().getUuid())) {
				is_inhometeam = true;
				break;
			}
		}
		if(is_inhometeam){//用户在主队，找出主队的其他球员
			for (PlayerInTeam playerInTeam : matchInfo.getHomeTeamPlayers()) {
				if (!currentUserID.equals(playerInTeam.getPlayer().getUuid())) {
					HasGrade gradeResult = new HasGrade(new GradeResult(), false);
					gradeResult.getGrade().setUuid(playerInTeam.getPlayer().getUuid());
					gradeResult.getGrade().setName(playerInTeam.getPlayer().getName());
					gradeResult.getGrade().setPosition(playerInTeam.getPlayer().getPosition());
					gradeResult.getGrade().setPicture(playerInTeam.getPlayer().getPicture());
					gradeResult.getGrade().setSkill(3f);
					gradeResult.getGrade().setMorality(3f);
					initialGradeData.add(gradeResult);
				}
			}
		}else{//用户在客队，找出客队的其他球员
			for (PlayerInTeam playerInTeam : matchInfo.getAwayTeamPlayers()) {
				if (!currentUserID.equals(playerInTeam.getPlayer().getUuid())) {
					HasGrade gradeResult = new HasGrade(new GradeResult(), false);
					gradeResult.getGrade().setUuid(playerInTeam.getPlayer().getUuid());
					gradeResult.getGrade().setName(playerInTeam.getPlayer().getName());
					gradeResult.getGrade().setPosition(playerInTeam.getPlayer().getPosition());
					gradeResult.getGrade().setPicture(playerInTeam.getPlayer().getPicture());
					gradeResult.getGrade().setSkill(3f);
					gradeResult.getGrade().setMorality(3f);
					initialGradeData.add(gradeResult);
				}
			}
		}
		dialog = ProgressDialog.show(mContext, "提示", "请稍后...",false, false);
		//获取评价结果
		NetWorkManager.getInstance(mContext).getGradeResultForMatch(matchInfo.getUuid(), is_inhometeam ? matchInfo.getHomeTeam().getUuid() : matchInfo.getAwayTeam().getUuid(),
				UserManager.getInstance(mContext).getCurrentUser().getToken(), new Listener<JSONArray>() {

					@Override
					public void onResponse(JSONArray response) {
						DialogUtil.dialogDismiss(dialog);
						Gson gson = new Gson();
						List<GradeResult> gradeResultList = gson.fromJson(response.toString(), new TypeToken<List<GradeResult>>(){}.getType());//评分数据
						if(gradeResultList == null){
							return;
						}
						//合并数据，刷新页面
						for(int i = 0; i < initialGradeData.size(); i++){//用户需要评价的
							for(GradeResult item : gradeResultList){//用户已评价
								if(initialGradeData.get(i).getGrade().getUuid().equals(item.getUuid())){//用户对该球员已经评分
									String position = initialGradeData.get(i).getGrade().getPosition();
									initialGradeData.get(i).setHasGrade(true);
									initialGradeData.get(i).setGrade(item);
									initialGradeData.get(i).getGrade().setPosition(position);//找回丢失的位置
								}
							}
						}
						if(gradeResultList.size() > 0){
							right.setVisibility(View.GONE);
						}else{
							right.setVisibility(View.VISIBLE);
						}
						adapter.setDataList(initialGradeData);
					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						DialogUtil.dialogDismiss(dialog);
						ShowToast("获取评分数据失败");
					}
				});
		inflater = LayoutInflater.from(this);
		lv_matchover_grade.setDivider(null);
		lv_matchover_grade.setDividerHeight(0);
		adapter = new MatchOverGradeAdapter(inflater, initialGradeData);
		lv_matchover_grade.setAdapter(adapter);
	}

	public class HasGrade{
		private GradeResult grade;//默认结果
		private boolean hasGrade;//是否有评价
		
		public HasGrade(GradeResult grade, boolean hasGrade) {
			super();
			this.grade = grade;
			this.hasGrade = hasGrade;
		}
		public GradeResult getGrade() {
			return grade;
		}
		public void setGrade(GradeResult grade) {
			this.grade = grade;
		}
		public boolean isHasGrade() {
			return hasGrade;
		}
		public void setHasGrade(boolean hasGrade) {
			this.hasGrade = hasGrade;
		}
	}
}
