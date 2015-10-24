package com.kinth.football.ui.team.formation;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.kinth.football.R;
import com.kinth.football.bean.User;
import com.kinth.football.chat.ChatConstants;
import com.kinth.football.manager.UserManager;
import com.kinth.football.manager.UserSharedPreferences;
import com.kinth.football.network.NetWorkManager;
import com.kinth.football.singleton.SingletonBitmapClass;
import com.kinth.football.singleton.ViewCompat;
import com.kinth.football.ui.ActivityBase;
import com.kinth.football.ui.match.MatchDetailOnPendingStateActivity;
import com.kinth.football.ui.team.formation.showtipsview.ShowTipsBuilder;
import com.kinth.football.ui.team.formation.showtipsview.ShowTipsView;
import com.kinth.football.ui.team.formation.showtipsview.ShowTipsViewInterface;
import com.kinth.football.util.ErrorCodeUtil;
import com.kinth.football.util.QuitWay;
import com.kinth.football.view.HeaderLayout.onRightImageButtonClickListener;
import com.kinth.football.view.xlist.XListView;
import com.kinth.football.view.xlist.XListView.IXListViewListener;

import eu.inmite.android.lib.dialogs.ISimpleDialogListener;
import eu.inmite.android.lib.dialogs.SimpleDialogFragment;

/**
 * 阵容列表
 * 
 * @author Alfred Chan
 */
public class FormationListActivity extends ActivityBase implements
		OnItemClickListener, IXListViewListener, FormationConstants,
		ISimpleDialogListener {

	private XListView mListView; // 下拉刷新列表视图，该XListView类继承了ListView并实现了滚动监听接口
	private ImageView ivTips;// 教程提示聚焦的视图
	private LinearLayout llFormationList;// 背景
	private Dialog dlgLoading;// 显示加载中的对话框
	private Dialog dlgDeleting;// 显示删除中的对话框

	private FormationAdapter fAdapter;// 阵容适配器
	private User targetUser; // 聊天对象
	private UserSharedPreferences userSharedPreferences;

	private String teamUUID;// 阵容归属的球队的uuid
	private boolean isCaptain = false;// 是否具有队长的权限
	private boolean isForSelect = false;// 是否用来选择阵容，还是仅仅浏览
	private String delFormationUuid;// 要删除的阵容的Uuid

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_formation_list);
		initData();
		initView();
	}

	private void initData() {
		teamUUID = getIntent().getStringExtra(INTENT_TEAM_UUID);
		targetUser = (User) getIntent().getParcelableExtra(
				ChatConstants.INTENT_USER_BEAN);

		userSharedPreferences = new UserSharedPreferences(this,
				this.getSharedPreferences("formation", MODE_PRIVATE));

		isCaptain = getIntent().getBooleanExtra(INTENT_IS_CAPTAIN, false);
		isForSelect = getIntent().getBooleanExtra(INTENT_IS_FOR_SELECT, false);
	}

	private void initView() {
		ivTips = (ImageView) findViewById(R.id.iv_tips);
		initHeader();
		initXListView();
		setBackgroundWithSaveMemory();
		showTip();
	}

	private void initHeader() {
		if (isCaptain) {// 是队长的话则能够创建阵容
			initTopBarForBoth("阵容列表", R.drawable.add,
					new onRightImageButtonClickListener() {

						@Override
						public void onClick() {
							// TODO 自动生成的方法存根
							Intent intent = new Intent(
									FormationListActivity.this,
									CreateFormationActivity.class);
							intent.putExtra(INTENT_TEAM_UUID, teamUUID);
							startActivityForResult(intent, 1);
							System.gc();
						}
					});
		} else {// 非队长只能浏览及选择阵容
			initTopBarForLeft("阵容列表");
		}
	}

	private void initXListView() {
		// TODO 自动生成的方法存根
		mListView = (XListView) findViewById(R.id.list_formation);
		mListView.setOnItemClickListener(this);
		mListView.setPullLoadEnable(false); // 首先不允许加载更多
		mListView.setPullRefreshEnable(true); // 允许下拉
		mListView.setXListViewListener(this); // 设置监听器
		mListView.setDividerHeight(5);// 列表项间隔高度
		mListView.pullRefreshing(); // 下拉刷新
		if (isCaptain) {
			mListView.setOnTouchListener(new OnTouchListener() {

				private View selectedItem;
				private float moveX, moveY;

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					// TODO 自动生成的方法存根
					if (fAdapter.getSeletedItem() == null)
						return false;

					switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						// 按下事件，获取用户要操作的项，和事件坐标
						selectedItem = fAdapter.getSeletedItem();
						moveX = event.getRawX();
						moveY = event.getRawY();
						break;
					case MotionEvent.ACTION_MOVE:
						// 移动事件，若y方向移动距离大于x方向移动距离， 则认为用户是想上下滚动列表。
						// 否则，用户是想滑动列表的某一项。
						float distanceX = event.getRawX() - moveX;
						float distanceY = event.getRawY() - moveY;
						if (Math.abs(distanceX) > Math.abs(distanceY)) {
							selectedItem.setX(selectedItem.getX() + distanceX);
							selectedItem.setAlpha(Math.max(0.1f,
									1 - Math.abs(selectedItem.getX() / 200)));
						}
						moveX = event.getRawX();
						moveY = event.getRawY();
						break;
					case MotionEvent.ACTION_UP:
						// 弹起事件，根据x方向移动距离的大小， 判断要进行什么操作
						if (Math.abs(selectedItem.getX()) > 200) {
							// 移动距离大于200px，则删除该项
							delFormationUuid = ((Formation) fAdapter
									.getItem(fAdapter.getSeletedItemPosition()))
									.getUuid();
							fAdapter.deleteSelectedItem();
							showConfirmDialog();
						} else {
							// 否则，将其还原
							fAdapter.cancelDeleting();
						}
						break;
					}
					return false;
				}

			});
		}
		fAdapter = new FormationAdapter(this, null);
		mListView.setAdapter(fAdapter);
		mListView.setOnItemClickListener(this);
		showLoadingDialog();
		executeGetFormationList();
	}

	/**
	 * 删除阵容时要用户再次确认
	 */
	private void showConfirmDialog() {
		// TODO 自动生成的方法存根
		String isNeedConfirm = userSharedPreferences.getValue(
				"formation_delete_confirm", "0");
		if (isNeedConfirm.equals("0")) {// 判断用户是否关闭了删除提示
			setTheme(R.style.DefaultLightTheme);
			SimpleDialogFragment
					.createBuilder(FormationListActivity.this,
							getSupportFragmentManager()).setTitle("确认框")
					.setMessage("是否确定删除该阵容")
					.setPositiveButtonText(R.string.logout_positive_button)
					.setNegativeButtonText(R.string.logout_negative_button)
					.setNeutralButtonText("不再提示").setRequestCode(42)
					.setTag("custom-tag").show();
		} else {// 不提示直接删除
			deleteFormation();
		}
	}

	/**
	 * 查询所有阵容信息
	 */
	private void executeGetFormationList() {
		mListView.pullRefreshing();
		NetWorkManager.getInstance(mContext).getAllFormation(teamUUID,
				UserManager.getInstance(mContext).getCurrentUser().getToken(),
				new Listener<JSONArray>() {

					@Override
					public void onResponse(JSONArray response) {
						Log.e("TAG", response.toString());
						Gson gson = new Gson();
						List<Formation> formationList = null;
						try {
							formationList = gson.fromJson(response.toString(),
									new TypeToken<ArrayList<Formation>>() {
									}.getType());
						} catch (JsonSyntaxException e) {
							formationList = null;
							e.printStackTrace();
						}
						fAdapter.updateListView(formationList);
						mListView.stopRefresh();
						dlgLoading.dismiss();
					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						ShowToast("获取阵容列表失败");
						mListView.stopRefresh();
					}
				});
	}

	/**
	 * 涉及网络访问，耗时操作因此要显示加载中的对话框
	 */
	private void showLoadingDialog() {
		View lLayout = getLayoutInflater().inflate(R.layout.loading_img, null);
		TextView tvTip = (TextView) lLayout.findViewById(R.id.tv_loading_tip);
		tvTip.setText("加载中...");

		dlgLoading = new Dialog(mContext, R.style.dialog);
		dlgLoading.setContentView(lLayout);
		dlgLoading.setCanceledOnTouchOutside(false);
		dlgLoading.show();
		dlgLoading.getWindow().setGravity(Gravity.CENTER);
	}

	/**
	 * 一种节约内存的设置背景的方法
	 */
	private void setBackgroundWithSaveMemory() {
		// TODO 自动生成的方法存根
		llFormationList = (LinearLayout) this
				.findViewById(R.id.ll_formation_list);
		// 設置背景
		Bitmap background = SingletonBitmapClass.getInstance()
				.getBackgroundBitmap();
		ViewCompat.setBackground(llFormationList, new BitmapDrawable(
				getResources(), background));
	}

	@Override
	public void onRefresh() {
		// TODO 自动生成的方法存根
		executeGetFormationList();
	}

	@Override
	public void onLoadMore() {
		// TODO 自动生成的方法存根

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Formation formation = (Formation) fAdapter.getItem(position - 1);
		if (formation == null)
			return;
		if (isForSelect) {// 比赛选择阵容
			Intent intent = new Intent();
			intent.putExtra(RESULT_MATCH_FORMATION, formation);
			setResult(MatchDetailOnPendingStateActivity.RESULT_OK, intent);
			FormationListActivity.this.finish();
		} else {// 浏览阵容详情
			Intent intent = new Intent(FormationListActivity.this,
					FormationDetailActivity.class);
			intent.putExtra(INTENT_FORMATION_BEAN, formation);
			if (targetUser != null) {// 聊天发送阵容
				intent.putExtra(ChatConstants.INTENT_USER_BEAN, targetUser);
			}
			if(teamUUID!=null){
				intent.putExtra(INTENT_TEAM_UUID, teamUUID);
			}
			startActivity(intent);
			QuitWay.activityList.add(this);
		}
	}

	@Override
	protected void onResume() {
		// TODO 自动生成的方法存根
		super.onResume();
	}

	/**
	 * 创建完阵容后，刷新阵容列表
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		// TODO 自动生成的方法存根
		executeGetFormationList();
	}

	/**
	 * 未添加删除阵容功能，所以先不做教程提示
	 */
	private void showTip() {
		// TODO 自动生成的方法存根
		String isNeedTip = userSharedPreferences.getValue("formation_list_tip",
				"0");
		if (isNeedTip.equals("0")) {
			ivTips.setVisibility(View.VISIBLE);
			ShowTipsView showtips = new ShowTipsBuilder(this).setTarget(ivTips)
					.setTitle("触控").setTitleColor(Color.WHITE)
					.setDescription("向左或向右滑动一段距离即可删除阵容")
					.setDescriptionColor(Color.LTGRAY).setDelay(500)
					.setCircleColor(Color.WHITE)
					.setCallback(new ShowTipsViewInterface() {

						@Override
						public void gotItClicked() {
							// TODO 自动生成的方法存根
							ivTips.setVisibility(View.GONE);
						}
					}).build();

			showtips.show(this);
			userSharedPreferences.saveString("formation_list_tip", "1");
		}
	}

	@Override
	public void onPositiveButtonClicked(int requestCode) {
		// TODO 自动生成的方法存根
		deleteFormation();
	}

	private void deleteFormation() {
		// TODO 自动生成的方法存根
		showDeletingDialog();
		NetWorkManager.getInstance(this).deleteFormation(
				UserManager.getInstance(this).getCurrentUser().getToken(),
				teamUUID, delFormationUuid, new Listener<String>() {

					@Override
					public void onResponse(String response) {
						dlgDeleting.dismiss();
						ShowToast("删除成功");
						executeGetFormationList();// 刷新列表
					}

				}, new ErrorListener() {

					@Override
					public void onErrorResponse(final VolleyError error) {

						runOnUiThread(new Runnable() {
							public void run() {
								if (!NetWorkManager.getInstance(mContext)
										.isNetConnected()) {
									ShowToast("没有可用的网络");
								} else if (error.networkResponse == null) {
									ShowToast("服务器连接错误");
								} else if (error.networkResponse.statusCode == 401) { // access
									ErrorCodeUtil.ErrorCode401(mContext);
								} else if (error.networkResponse.statusCode == 404) { // 旧密码错误
									ShowToast("阵容找不到");
								}
							}
						});

					}

				});
	}

	/**
	 * 涉及网络访问，耗时操作因此要显示删除中的对话框
	 */
	private void showDeletingDialog() {
		View lLayout = getLayoutInflater().inflate(R.layout.loading_img, null);
		TextView tvTip = (TextView) lLayout.findViewById(R.id.tv_loading_tip);
		tvTip.setText("删除中...");

		dlgDeleting = new Dialog(mContext, R.style.dialog);
		dlgDeleting.setContentView(lLayout);
		dlgDeleting.setCanceledOnTouchOutside(false);
		dlgDeleting.show();
		dlgDeleting.getWindow().setGravity(Gravity.CENTER);
	}

	@Override
	public void onNegativeButtonClicked(int requestCode) {
		// TODO 自动生成的方法存根
		executeGetFormationList();// 刷新列表
	}

	/**
	 * 不再提醒按钮
	 */
	@Override
	public void onNeutralButtonClicked(int requestCode) {
		// TODO 自动生成的方法存根
		userSharedPreferences.saveString("formation_delete_confirm", "1");
		deleteFormation();
	}

}
