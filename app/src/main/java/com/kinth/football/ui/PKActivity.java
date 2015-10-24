package com.kinth.football.ui;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.kinth.football.R;
import com.kinth.football.dao.Player;
import com.kinth.football.manager.UserManager;
import com.kinth.football.network.NetWorkManager;
import com.kinth.football.singleton.SingletonBitmapClass;
import com.kinth.football.singleton.ViewCompat;
import com.kinth.football.util.AndroidChartUtil;
import com.kinth.football.util.ErrorCodeUtil;
import com.kinth.football.util.PhotoUtil;
import com.kinth.football.util.PictureUtil;
import com.kinth.football.view.RoundImageView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
/**
 * 云五PK
 * @author zyq
 * 
 */
public class PKActivity extends BaseActivity {
	private static int FIND_PLAYER_ONE_REQUESTCODE = 5001;  //找球员1的请求码
	private static int FIND_PLAYER_TWO_REQUESTCODE = 5002;  //找球员2的请求码

	public static String SELECT_PLAYER = "select_player";  //选择的球员key值
    public static String FROM_PK = "fromPK";     //跳到找球员activity时，标志来自云五PK--
    
    @ViewInject(R.id.viewGroup)
    private View viewGroup;
    
	@ViewInject(R.id.iv_player_one)
	private RoundImageView iv_player_one;

	@ViewInject(R.id.iv_player_two)
	private RoundImageView iv_player_two;

	@ViewInject(R.id.tx_name_one)	// 名字1
	private TextView tx_name_one;

	@ViewInject(R.id.tx_name_two)  	// 名字2
	private TextView tx_name_two;

	@OnClick(R.id.iv_player_one)
	public void fun_1(View v) {
		Intent intent = new Intent(mContext, HomeSearchPlayerActivity.class);
		intent.putExtra(FROM_PK, "fromPK");
		startActivityForResult(intent, FIND_PLAYER_ONE_REQUESTCODE);
	}

	@OnClick(R.id.iv_player_two)
	public void fun_2(View v) {
		Intent intent = new Intent(mContext, HomeSearchPlayerActivity.class);
		intent.putExtra(FROM_PK, "fromPK");
		startActivityForResult(intent, FIND_PLAYER_TWO_REQUESTCODE);
	}

	@ViewInject(R.id.tx_synthesize_one)// 综合1
	private TextView tx_synthesize_one;

	@ViewInject(R.id.pro_synthesize_one)// 综合长条1
	private ProgressBar pro_synthesize_one;

	@ViewInject(R.id.tx_synthesize_two)// 综合2
	private TextView tx_synthesize_two;

	@ViewInject(R.id.pro_synthesize_two)// 综合长条2
	private ProgressBar pro_synthesize_two;

	@ViewInject(R.id.tx_stren_one)// 体能1
	private TextView tx_stren_one;

	@ViewInject(R.id.pro_stren_one)// 体能长条1
	private ProgressBar pro_stren_one;

	@ViewInject(R.id.tx_stren_two)// 体能2
	private TextView tx_stren_two;

	@ViewInject(R.id.pro_stren_two)// 体能长条2
	private ProgressBar pro_stren_two;

	@ViewInject(R.id.tx_attack_one)// 进攻1
	private TextView tx_attack_one;

	@ViewInject(R.id.pro_attack_one)// 进攻长条1
	private ProgressBar pro_attack_one;

	@ViewInject(R.id.tx_attack_two)// 进攻2
	private TextView tx_attack_two;

	@ViewInject(R.id.pro_attack_two)// 进攻长条2
	private ProgressBar pro_attack_two;

	@ViewInject(R.id.tx_defence_one)// 防守1
	private TextView tx_defence_one;

	@ViewInject(R.id.pro_defence_one)// 防守长条1
	private ProgressBar pro_defence_one;

	@ViewInject(R.id.tx_defence_two)// 防守2
	private TextView tx_defence_two;

	@ViewInject(R.id.pro_defence_two)// 防守长条2
	private ProgressBar pro_defence_two;

	@ViewInject(R.id.tx_skill_one)// 技术1
	private TextView tx_skill_one;

	@ViewInject(R.id.pro_skill_one)// 技术长条1
	private ProgressBar pro_skill_one;

	@ViewInject(R.id.tx_skill_two)// 技术2
	private TextView tx_skill_two;

	@ViewInject(R.id.pro_skill_two)// 技术长条2
	private ProgressBar pro_skill_two;

	@ViewInject(R.id.tx_awareness_one)// 侵略性1
	private TextView tx_awareness_one;

	@ViewInject(R.id.pro_awareness_one)// 侵略性长条1
	private ProgressBar pro_awareness_one;

	@ViewInject(R.id.tx_awareness_two)// 侵略性2
	private TextView tx_awareness_two;

	@ViewInject(R.id.pro_awareness_two)// 侵略性长条2
	private ProgressBar pro_awareness_two;

	private Player player_one;
	private Player player_two;

	private Typeface tf1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pk);
		ViewUtils.inject(this);
		
		Bitmap background = SingletonBitmapClass.getInstance()
				.getBackgroundBitmap();	
		ViewCompat.setBackground(viewGroup, new BitmapDrawable(getResources(),
				background));

		initTopBarForLeft("云五PK");

		init_tv_font();//设置字体
		
		init_playerOneInfo(UserManager.getInstance(mContext).getCurrentUser()
				.getPlayer().getUuid());//默认球员1是自己
		
	}
    //球员1的数据显示
	private void init_playerone() {
		if (player_one!=null) {
		PictureUtil.getMd5PathByUrl(
				PhotoUtil.getThumbnailPath(player_one.getPicture()),
				iv_player_one, new DisplayImageOptions.Builder()
						.showImageOnLoading(R.drawable.icon_default_head)
						.showImageForEmptyUri(R.drawable.icon_default_head)
						.showImageOnFail(R.drawable.icon_default_head) // 默认球队队徽
						.cacheInMemory(true).cacheOnDisk(true).build());
		tx_name_one.setText(StringUtils.defaultIfEmpty(player_one.getName(),
				"未知"));
		if (player_one.getComposite() != null) {
			tx_synthesize_one.setText(AndroidChartUtil.roundNumber(player_one
					.getComposite()) + "");
			//这里暂时设置2个相同的第一第二进度  --不然左边有点点红色的东西
			pro_synthesize_one.setProgress(100-AndroidChartUtil.roundNumber(player_one
					.getComposite()));   
			pro_synthesize_one.setSecondaryProgress(100-AndroidChartUtil.roundNumber(player_one
					.getComposite()));
		}
		if (player_one.getStrength() != null) {
			tx_stren_one.setText(AndroidChartUtil.roundNumber(player_one
					.getStrength()) + "");
			//这里暂时设置2个相同的第一第二进度  --不然左边有点点红色的东西
			pro_stren_one.setProgress(100-AndroidChartUtil.roundNumber(player_one
					.getStrength()));
			pro_stren_one.setSecondaryProgress(100-AndroidChartUtil.roundNumber(player_one
					.getStrength()));
		}
		if (player_one.getAttack() != null) {
			tx_attack_one.setText(AndroidChartUtil.roundNumber(player_one
					.getAttack()) + "");
			//这里暂时设置2个相同的第一第二进度  --不然左边有点点红色的东西
			pro_attack_one.setProgress(100-AndroidChartUtil.roundNumber(player_one
					.getAttack()));
			pro_attack_one.setSecondaryProgress(100-AndroidChartUtil.roundNumber(player_one
					.getAttack()));
		}
		if (player_one.getDefence() != null) {
			tx_defence_one.setText(AndroidChartUtil.roundNumber(player_one
					.getDefence()) + "");
			//这里暂时设置2个相同的第一第二进度  --不然左边有点点红色的东西
			pro_defence_one.setProgress(100-AndroidChartUtil.roundNumber(player_one
					.getDefence()));
			pro_defence_one.setSecondaryProgress(100-AndroidChartUtil.roundNumber(player_one
					.getDefence()));
		}
		if (player_one.getSkill() != null) {
			tx_skill_one.setText(AndroidChartUtil.roundNumber(player_one
					.getSkill()) + "");
			//这里暂时设置2个相同的第一第二进度  --不然左边有点点红色的东西
			pro_skill_one.setProgress(100-AndroidChartUtil.roundNumber(player_one
					.getSkill()));
			pro_skill_one.setSecondaryProgress(100-AndroidChartUtil.roundNumber(player_one
					.getSkill()));
		}
		if (player_one.getAwareness() != null) {
			tx_awareness_one.setText(AndroidChartUtil.roundNumber(player_one
					.getAwareness()) + "");
			//这里暂时设置2个相同的第一第二进度  --不然左边有点点红色的东西
			pro_awareness_one.setProgress(100-AndroidChartUtil.roundNumber(player_one
					.getAwareness()));
			pro_awareness_one.setSecondaryProgress(100-AndroidChartUtil.roundNumber(player_one
					.getAwareness()));
		}
		
	}
	}
	//球员2的数据显示
	private void init_playertwo() {
		PictureUtil.getMd5PathByUrl(
				PhotoUtil.getThumbnailPath(player_two.getPicture()),
				iv_player_two, new DisplayImageOptions.Builder()
						.showImageOnLoading(R.drawable.icon_default_head)
						.showImageForEmptyUri(R.drawable.icon_default_head)
						.showImageOnFail(R.drawable.icon_default_head) // 默认球队队徽
						.cacheInMemory(true).cacheOnDisk(true).build());
		tx_name_two.setText(StringUtils.defaultIfEmpty(player_two.getName(),
				"未知"));
		if (player_two.getComposite() != null) {
			tx_synthesize_two.setText(AndroidChartUtil.roundNumber(player_two
					.getComposite()) + "");
			pro_synthesize_two.setSecondaryProgress(AndroidChartUtil.roundNumber(player_two
					.getComposite()));
		}
		if (player_two.getStrength() != null) {
			tx_stren_two.setText(AndroidChartUtil.roundNumber(player_two
					.getStrength()) + "");
			pro_stren_two.setSecondaryProgress(AndroidChartUtil.roundNumber(player_two
					.getStrength()));
		}
		if (player_two.getAttack() != null) {
			tx_attack_two.setText(AndroidChartUtil.roundNumber(player_two
					.getAttack()) + "");
			pro_attack_two.setSecondaryProgress(AndroidChartUtil.roundNumber(player_two
					.getAttack()));
		}
		if (player_two.getDefence() != null) {
			tx_defence_two.setText(AndroidChartUtil.roundNumber(player_two
					.getDefence()) + "");
			pro_defence_two.setSecondaryProgress(AndroidChartUtil.roundNumber(player_two
					.getDefence()));
		}
		if (player_two.getSkill() != null) {
			tx_skill_two.setText(AndroidChartUtil.roundNumber(player_two
					.getSkill()) + "");
			pro_skill_two.setSecondaryProgress(AndroidChartUtil.roundNumber(player_two
					.getSkill()));
		}
		if (player_two.getAwareness() != null) {
			tx_awareness_two.setText(AndroidChartUtil.roundNumber(player_two
					.getAwareness()) + "");
			pro_awareness_two.setSecondaryProgress(AndroidChartUtil.roundNumber(player_two
					.getAwareness()));
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, intent);
		if (resultCode != RESULT_OK) {
			return;
		}
		if (requestCode == FIND_PLAYER_ONE_REQUESTCODE) {
			player_one = intent.getParcelableExtra(SELECT_PLAYER);
			init_playerone();
			if (player_two!=null) {
				create_compare();
			}
			return;
		}
		if (requestCode == FIND_PLAYER_TWO_REQUESTCODE) {
			player_two = intent.getParcelableExtra(SELECT_PLAYER);
			init_playertwo();
			if (player_one!=null) {
				create_compare();
			}
			return;
		}
	}
	//同一数据的比较，小的字体较小
	private void compare(Float f1,Float f2,TextView tx1,TextView tx2){
		if (f1>f2) {
			tx1.setTextSize(40);
			tx2.setTextSize(30);
		}else if (f1<f2){
			tx1.setTextSize(30);
			tx2.setTextSize(40);
		}else {
			tx1.setTextSize(40);
			tx2.setTextSize(40);
		}
	}
	//数据比较后的显示
	private void create_compare(){
		    compare(player_one
				.getComposite(), player_two
				.getComposite(), tx_synthesize_one, tx_synthesize_two);
			compare(player_one
					.getStrength(), player_two
					.getStrength(), tx_stren_one, tx_stren_two);
			compare(player_one
					.getAttack(), player_two
					.getAttack(), tx_attack_one, tx_attack_two);
			compare(player_one
					.getDefence(), player_two
					.getDefence(), tx_defence_one, tx_defence_two);
			compare(player_one
					.getSkill(), player_two
					.getSkill(), tx_skill_one, tx_skill_two);
			compare(player_one
					.getAwareness(), player_two
					.getAwareness(), tx_awareness_one, tx_awareness_two);
	}
	//获得默认是自己的数据
	private void init_playerOneInfo(String playerID ){
			NetWorkManager.getInstance(mContext).getPlayerInfoDetailByUuid(playerID, 
					UserManager.getInstance(mContext).getCurrentUser().getToken(), 
					new Listener<JSONObject>(){
						@Override
						public void onResponse(JSONObject response) {
							Gson gson = new Gson();
							try {
								player_one = gson.fromJson(response.toString(), 
										new TypeToken<Player>(){}.getType());
							} catch (JsonSyntaxException e) {
								player_one = null;
								e.printStackTrace();
							}
							init_playerone();
						
					}}, new ErrorListener(){
						@Override
						public void onErrorResponse(VolleyError error) {
							// TODO Auto-generated method stub
							if(!NetWorkManager.getInstance(mContext).isNetConnected()){
								//ShowToast("当前网络不可用");
								return;
							}
							if(error.networkResponse == null){
								//ShowToast("服务器连接错误");
							}else if(error.networkResponse.statusCode == 401){
								ErrorCodeUtil.ErrorCode401(mContext);
							}else if(error.networkResponse.statusCode == 404){
//								ShowToast("球员找不到");
							}
						}
				});	
					}
	//字体样式的初始化
	private void init_tv_font(){
		tf1 = Typeface.createFromAsset(this.getAssets(),
				"fonts/AGENCYB_0.TTF");
		tx_synthesize_one.setTypeface(tf1);
		tx_synthesize_two.setTypeface(tf1);
		tx_stren_one.setTypeface(tf1);
		tx_stren_two.setTypeface(tf1);
		tx_attack_one.setTypeface(tf1);
		tx_attack_two.setTypeface(tf1);
		tx_defence_one.setTypeface(tf1);
		tx_defence_two.setTypeface(tf1);
		tx_skill_one.setTypeface(tf1);
		tx_skill_two.setTypeface(tf1);
		tx_awareness_one.setTypeface(tf1);
		tx_awareness_two.setTypeface(tf1);
	}
}
