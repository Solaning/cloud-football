package com.kinth.football.ui.team.formation;

import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Dialog;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.cj.ScreenShotUtil.ScreentShotUtil;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.kinth.football.R;
import com.kinth.football.bean.PickedImage;
import com.kinth.football.bean.UploadImage;
import com.kinth.football.bean.User;
import com.kinth.football.bean.match.PlayerInTeam;
import com.kinth.football.chat.util.CompressListener;
import com.kinth.football.chat.util.FileUtil;
import com.kinth.football.chat.util.ImageUtil;
import com.kinth.football.chat.util.MD5Util;
import com.kinth.football.config.Config;
import com.kinth.football.manager.UserManager;
import com.kinth.football.manager.UserSharedPreferences;
import com.kinth.football.network.AsyncUploadImage;
import com.kinth.football.network.NetWorkManager;
import com.kinth.football.singleton.ViewCompat;
import com.kinth.football.ui.ActivityBase;
import com.kinth.football.ui.team.TeamInfoActivity;
import com.kinth.football.ui.team.formation.showtipsview.ShowTipsBuilder;
import com.kinth.football.ui.team.formation.showtipsview.ShowTipsView;
import com.kinth.football.ui.team.formation.showtipsview.ShowTipsViewInterface;
import com.kinth.football.util.PhotoUtil;
import com.kinth.football.util.QuitWay;
import com.kinth.football.view.RoundImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.scvngr.levelup.views.gallery.AdapterView;
import com.scvngr.levelup.views.gallery.AdapterView.OnItemClickListener;
import com.scvngr.levelup.views.gallery.Gallery;

public class CreateFormationActivity extends ActivityBase implements
		OnClickListener {

	// 控件
	private ImageButton ibNavLeft;
	private TextView tvNavRight;
	private FrameLayout flFootballCourt; // 球场布局
	private ImageButton ibFootball; // 球场中央的足球按钮
	private Gallery gallery; // 选择头像的滑动条
	private EditText etName; // 阵容名
	private EditText etDesc; // 阵容描述
	private Dialog dlgBroad;// 包含阵容名和阵容描述的对话框
	private Dialog dlgLoadingImg;

	private GalleryAdapter gAdapter;// 选择球员滑动条的适配器

	// 全局变量
	private View tipView;//教程提示所聚焦的控件
	private View lastTeammate; // 最后一个被选中的队员

	// 控件列表
	private List<ImageButton> btnDeleteList = new ArrayList<ImageButton>(); // 所有球场上的球员右上角的删除按钮

	private static final String TAG_FORMATION = "TAG_FORMATION";// 阵容标签
	private String teamUUID;	//阵容归属的球队的uuid
	private boolean screenShotFail = false;	//用第一种截屏方法截屏是否成功

	private User targetUser; // 聊天对象
	private UserSharedPreferences userSharedPreferences;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_formation);
		initData();
		initView();
	}

	private void initData() {
		// TODO 自动生成的方法存根
		targetUser = (User) getIntent().getSerializableExtra("user");
		teamUUID = getIntent().getStringExtra(FormationConstants.INTENT_TEAM_UUID);
		gAdapter = new GalleryAdapter(this);
		userSharedPreferences = new UserSharedPreferences(this,
				this.getSharedPreferences("formation", MODE_PRIVATE));
	}
	
	private void initView() {
		// TODO 自动生成的方法存根
		initHeader();
		initBroadDialog();

		flFootballCourt = (FrameLayout) findViewById(R.id.fl_football_court);
		ibFootball = (ImageButton) findViewById(R.id.ib_football);
		gallery = (Gallery) findViewById(R.id.tip_selector_gallery);
		gallery.setAdapter(gAdapter);

		flFootballCourt.setOnClickListener(this);
		ibFootball.setOnClickListener(this);
		ibFootball.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				// TODO 自动生成的方法存根
				setDelable();
				return true;
			}

		});
		gallery.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO 自动生成的方法存根
				RoundImageView civHead = (RoundImageView) lastTeammate
						.findViewById(R.id.teammate_cloth);
				ImageLoader.getInstance().displayImage(
						PhotoUtil.getAllPhotoPath(gAdapter.mTeammates
								.get(position).getPlayer().getPicture()),
						civHead);

				TextView tvName = (TextView) lastTeammate
						.findViewById(R.id.teammate_name);
				tvName.setText(gAdapter.mTeammates.get(position).getPlayer()
						.getName());
				tvName.setTag(gAdapter.mTeammates.get(position).getPlayer()
						.getName());
				
				TextView tvNumberTip = (TextView) lastTeammate.findViewById(R.id.tv_number_tips);
				Integer JerseyNo = gAdapter.mTeammates.get(position).getJerseyNo();
				if(JerseyNo!=null){
					tvNumberTip.setVisibility(View.VISIBLE);
					tvNumberTip.setText(String.valueOf(JerseyNo));
				}else{
					tvNumberTip.setVisibility(View.GONE);
				}

				/**
				 * 选中某球员后，将其从球员列表滑动条中移除，在此之前，以名字为key将对应的
				 * TeamMemberResponse对象放入一个HashMap对象中，方便删除球场球员后
				 * 的回收
				 */
				gAdapter.recycleItem(gAdapter.mTeammates.get(position)
						.getPlayer().getName(),
						gAdapter.mTeammates.get(position));	
				gAdapter.removeItem(position);

				gallery.setEnabled(false); // 出现一个Bug，选中一项之后刻意再去点击的时候出现IndexOutOfBoundsException
				hideGallery();
			}
		});
		executeGetTeamMemberList();
		
		setBackgroundWithSaveMemory();
		showTip();
	}

	/**
	 * 设置删除按钮是否可见
	 */
	public void setDelable() {
		// TODO 自动生成的方法存根
		Vibrator vivrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
		vivrator.vibrate(200);//蜂鸣

		if (btnDeleteList.size() != 0) {
			ImageButton btnDelete = btnDeleteList.get(0);
			if (btnDelete != null) {
				if (btnDelete.getVisibility() == View.GONE) {
					for (int i = 0; i < btnDeleteList.size(); i++) {
						btnDeleteList.get(i).setVisibility(View.VISIBLE);
					}
				} else {
					for (int i = 0; i < btnDeleteList.size(); i++) {
						btnDeleteList.get(i).setVisibility(View.GONE);
					}
				}
			}
		}
	}

	private void initHeader() {
		// TODO 自动生成的方法存根
		ibNavLeft = (ImageButton) findViewById(R.id.nav_left);
		tvNavRight = (TextView) findViewById(R.id.nav_right_btn);

		ibNavLeft.setOnClickListener(this);
		tvNavRight.setOnClickListener(this);
	}

	/**
	 * 节约图片内存的一种设置背景图的方法
	 */
	private void setBackgroundWithSaveMemory() {
		// TODO 自动生成的方法存根
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.ARGB_8888;
		opt.inInputShareable = true;
		opt.inPurgeable = true;

		//用此方法无法实现.9图效果
		/*InputStream isFootballCount = getResources().openRawResource(
				R.drawable.football_court);
		Bitmap bmpFootBallCount = BitmapFactory.decodeStream(isFootballCount,
				null, opt);
		ViewCompat.setBackground(ivFootballCourtBg, new BitmapDrawable(
				getResources(), bmpFootBallCount));*/

		InputStream isFootball = getResources().openRawResource(
				R.drawable.football);
		Bitmap bmpFootball = BitmapFactory.decodeStream(isFootball, null, opt);
		ViewCompat.setBackground(ibFootball, new BitmapDrawable(getResources(),
				bmpFootball));
	}

	@Override
	public void onClick(View v) {
		// TODO 自动生成的方法存根
		switch (v.getId()) {
		case R.id.fl_football_court:
			hideGallery();
			break;
		case R.id.ib_football:
			addTeammate();
			break;
		case R.id.nav_left:
			finish();
			break;
		case R.id.nav_right_btn:
			ShowToast("请为此阵容添加描述");
			dlgBroad.show();
			break;
		}
	}

	/**
	 * 隐藏头像滑动条
	 */
	private void hideGallery() {
		// TODO 自动生成的方法存根
		if (gallery.getVisibility() == View.VISIBLE) {
			Animation anim = AnimationUtils.loadAnimation(this,
					R.anim.slide_out);
			gallery.startAnimation(anim);
			gallery.setVisibility(View.GONE);
		}
	}

	/**
	 * 添加球员到球场上
	 */
	private void addTeammate() {
		// TODO 自动生成的方法存根
		Animation anim = AnimationUtils.loadAnimation(
				CreateFormationActivity.this, R.anim.bigger);
		ibFootball.startAnimation(anim);//变大，作为单击的响应动画

		final LinearLayout llTeammate = (LinearLayout) getLayoutInflater()
				.inflate(R.layout.item_teammate, null);
		tipView = llTeammate;//将球员设置为教程提示聚焦的控件，用于提示队员可用的操作
		LayoutParams params = new LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER;
		params.bottomMargin = 100;
		llTeammate.setLayoutParams(params);
		ImageButton btnDelete = (ImageButton) llTeammate
				.findViewById(R.id.teammate_delete);
		btnDeleteList.add(btnDelete);
		btnDelete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO 自动生成的方法存根
				flFootballCourt.removeView(llTeammate);//移除球员
				
				/**
				 * 获取被删除球员的名字，并通过名字将球员重新添加回球员列表滑动条
				 * 这里因为是llTeammate无法通过getText()取得TextView里的名字
				 * 所以用Tag方法保存名字并在这里取出来
				 */
				String name = (String) (llTeammate
						.findViewById(R.id.teammate_name).getTag());
				if (name != null && !name.equals(""))
					gAdapter.addItemBack(name);
			}
		});
		flFootballCourt.addView(llTeammate);//作为子控件添加到球场上
		setDragable(llTeammate);//设置为可拖动的控件
	}

	/**
	 * 保存阵容
	 */
	private void saveFormation() {
		// TODO 自动生成的方法存根
		showLoadingDialog(); //截屏、压缩图像、上传图片过程会有短时间的卡顿，显示环形进度条进行过度
		new ScreenshotUtil(this).saveScreenshot(Config.FORMATION_DIR,
				String.valueOf(System.currentTimeMillis()) + ".jpg",
				new ScreenshotUtil.ScreenshotStateListener() {
					@Override
					public void screenshotSuccess(String filepath) {
						// TODO 自动生成的方法存根
						ImageUtil.getCompressedImage(filepath,
								new CompressListener() {
									@Override
									public void CompressSuccess(String imagePath) {
										// TODO 自动生成的方法存根
										uploadFile(imagePath);
									}

									@Override
									public void CompressFail(String failReason) {
										// TODO 自动生成的方法存根
										Toast.makeText(mContext, failReason,
												Toast.LENGTH_LONG).show();
									}
								}, 500);
					}

					@Override
					public void screenshotFail() {
						// TODO 自动生成的方法存根
						ShowToast("操作失败，请重新再试");
						dlgLoadingImg.dismiss();
						System.gc(); // 告诉垃圾收集器打算进行垃圾收集，但垃圾收集器进不进行收集是不确定的
						screenShotFail = true;
					}

				}, -1);
	}

	private void saveFormationAgain() {
		// TODO 自动生成的方法存根
		// 1秒延迟，保证描述对话框消失
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO 自动生成的方法存根
				uploadFile(screenShot());
			}

		}, 1000);
		screenShotFail = false;
	}

	/**
	 * 上传屏幕截图文件
	 * 
	 * @param filepath
	 */
	private void uploadFile(final String filepath) {
		// TODO 自动生成的方法存根
		AsyncUploadImage asyncUploadImage = new AsyncUploadImage() {

			@Override
			public void onUploadSuccess(Map<String, UploadImage> uploadMap) {
				// ibFootball.clearAnimation();
				dlgLoadingImg.dismiss();
				Formation formation = buildFormationExpectUrl();
				String uploadUrl = null;
				for (Map.Entry<String, UploadImage> entry : uploadMap
						.entrySet()) {
					if (TAG_FORMATION.equals(entry.getValue().getTag())) {
						uploadUrl = entry.getValue().getUrl();
						formation.setImage(uploadUrl);
						continue;
					}
				}
				executeCreateFormation(formation);
				try {
					String localPath = Config.FORMATION_DIR
							+ MD5Util.getMD5(uploadUrl.trim()) + ".jpg";
					FileUtil.renameFile(filepath, localPath);
				} catch (NoSuchAlgorithmException e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				}
			}

			@Override
			public void onUploadFailed() {
				ShowToast("上传失败");
			}
		};
		Map<String, UploadImage> uploadMap = new LinkedHashMap<String, UploadImage>();// 服务器上的url
		if (!TextUtils.isEmpty(filepath)) {
			uploadMap.put(filepath, new UploadImage(filepath, TAG_FORMATION));
			asyncUploadImage.upload2(uploadMap, new PickedImage(filepath));
		}
	}

	/**
	 * 构建除了网络路径之外的Formation对象
	 * @return
	 */
	private Formation buildFormationExpectUrl() {
		// TODO 自动生成的方法存根
		Formation formation = new Formation();
		formation.setName(etName.getText().toString());
		formation.setDescription(etDesc.getText().toString());
		return formation;
	}

	/**
	 * 将阵容对象上传到服务器
	 * @param formation 阵容对象
	 */
	private void executeCreateFormation(Formation formation) {
		// TODO 自动生成的方法存根
		NetWorkManager.getInstance(getApplicationContext()).createFormation(
				teamUUID, footBallUserManager.getCurrentUser().getToken(),
				formation, new Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						// {"firstCaptainUuid":"22bee0e9-15c9-4ddf-87b6-79bd4d21698b","slogan":null,"secondCaptainUuid":null,"cityId":289,"description":null,"name":"HK","familyPhoto":null,"thirdCaptainUuid":null,"creatorUuid":"22bee0e9-15c9-4ddf-87b6-79bd4d21698b","badge":null,"uuid":"2dqYfDRmij-05yW8yAQsr4","date":1425372009889,"regionId":1}
						Gson gson = new Gson();
						Formation formation = null;
						try {
							formation = gson.fromJson(response.toString(),
									new TypeToken<Formation>() {
									}.getType());
						} catch (JsonSyntaxException e) {
							formation = null;
							e.printStackTrace();
						}
						if (formation != null) { // TODO
							Toast.makeText(CreateFormationActivity.this,
									"创建阵容成功", Toast.LENGTH_SHORT).show();
							if (targetUser != null)
								toFormationDetail(formation);
							else if(getIntent().getAction()!=null)
								toTeamInfo();
							else
								toFormationList();
						}
					}

				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						VolleyLog.e("Error: ", error.getMessage());
						Log.e("response error", "" + error.getMessage());
						ShowToast("创建阵容失败");
					}
				});
	}

	/**
	 * 跳转到阵容详情
	 * @param formation
	 */
	private void toFormationDetail(Formation formation) {
		// TODO 自动生成的方法存根
		Intent intent = new Intent(CreateFormationActivity.this,
				FormationDetailActivity.class);
		intent.putExtra("INTENT_FORMATION_INFO_BEAN", formation);
		intent.putExtra("user", targetUser);
		startActivity(intent);
		QuitWay.activityList.add(this);
	}
	
	private void toTeamInfo() {
		// TODO 自动生成的方法存根
		if(getIntent().getAction().equals("ACTION_CREATE_DEFAULT_FORMATION")){
			Intent intent = new Intent(CreateFormationActivity.this,
					TeamInfoActivity.class);
			CreateFormationActivity.this.setResult(-1, intent);
			finish();
		}
	}

	/**
	 * 跳转回阵容列表
	 */
	private void toFormationList() {
		// TODO 自动生成的方法存根
		Intent intent = new Intent(CreateFormationActivity.this,
				FormationListActivity.class);
		CreateFormationActivity.this.setResult(0, intent);
		finish();
	}

	/**
	 * 将控件设置为可拖动状态
	 * @param v
	 */
	private void setDragable(View v) {
		DisplayMetrics dm = getResources().getDisplayMetrics();
		final int screenWidth = dm.widthPixels;
		final int screenHeight = dm.heightPixels + 50;

		v.setOnTouchListener(new OnTouchListener() {

			int firstX, firstY;
			int lastX, lastY;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				int ea = event.getAction();
				switch (ea) {
				case MotionEvent.ACTION_DOWN:
					firstX = lastX = (int) event.getRawX();
					firstY = lastY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_MOVE:
					int dx = (int) event.getRawX() - lastX;
					int dy = (int) event.getRawY() - lastY;

					int left = v.getLeft() + dx;
					int top = v.getTop() + dy;
					int right = v.getRight() + dx;
					int bottom = v.getBottom() + dy;

					if (left < 0) {
						left = 0;
						right = left + v.getWidth();
					}

					if (right > screenWidth) {
						right = screenWidth;
						left = right - v.getWidth();
					}

					if (top < 0) {
						top = 0;
						bottom = top + v.getHeight();
					}

					if (bottom > screenHeight) {
						bottom = screenHeight;
						top = bottom - v.getHeight();
					}

					v.layout(left, top, right, bottom);

					lastX = (int) event.getRawX();
					lastY = (int) event.getRawY();

					// 克隆view的width、height、margin的值生成margin对象
					MarginLayoutParams margin = new MarginLayoutParams(v
							.getLayoutParams());
					// 设置新的边距
					/**
					 * 注意这个地方，之前是设置margin.setMargins(left, top, right, bottom)的
					 * ，然后自从修改前面设置的参数flFootballCourt.addView(llTeammate)而不是
					 * flFootballCourt.addView(llTeammate,30,30)后就会出现一个错误
					 * 那就是只有左上角四分之一的空间可以移动，移到其他空间会被遮挡住
					 * 想来也的确只需要设置left和top，可能是因为之前addView(llTeammate,30,30)设置
					 * 了外部大小所以是覆盖了被遮挡的部分所以看得见，现在改为addView(llTeammate)了，不再有
					 * 外部大小可以覆盖，所以会小时
					 */
					margin.setMargins(left, top, 0, 0);
					// 把新的边距生成layoutParams对象
					FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
							margin);
					// 设制view的新的位置
					v.setLayoutParams(layoutParams);

					break;
				case MotionEvent.ACTION_UP:
					if (firstX == (int) event.getRawX() // 判断按下松开的位置有没有变化，如果没有，则视为单击事件
							|| firstY == (int) event.getRawY()) {
						lastTeammate = v;

						if (((TextView) v.findViewById(R.id.teammate_name))
								.getText().toString().equals("球员")) {

							Animation scaleAnim = AnimationUtils
									.loadAnimation(
											CreateFormationActivity.this,
											R.anim.bigger); 
							lastTeammate.startAnimation(scaleAnim);

							Animation anim = AnimationUtils.loadAnimation(
									CreateFormationActivity.this,
									R.anim.slide_in);
							gallery.startAnimation(anim);
							gallery.setVisibility(View.VISIBLE);
							gallery.setEnabled(true); // 重新设置为可选
						}
					}
				}
				return true;
			}
		});
	}

	private void showTip() {
		// TODO 自动生成的方法存根
		String isNeedTip = userSharedPreferences.getValue(
				"create_formation_tip", "0");
		if (isNeedTip.equals("0")) {
			addTeammate();
			ShowTipsView showtips = new ShowTipsBuilder(this)
					.setTarget(ibFootball).setTitle("足球(单按)")
					.setTitleColor(Color.WHITE).setDescription("生成可拖曳的球员")
					.setDescriptionColor(Color.LTGRAY).setDelay(500)
					.setCircleColor(Color.WHITE)
					.setCallback(new ShowTipsViewInterface() {
						@Override
						public void gotItClicked() {
							// TODO 自动生成的方法存根
							setDelable();
							ShowTipsView showtips = new ShowTipsBuilder(
									CreateFormationActivity.this)
									.setTarget(ibFootball).setTitle("足球(长按)")
									.setTitleColor(Color.WHITE)
									.setDescription("可执行删除球员操作")
									.setDescriptionColor(Color.LTGRAY)
									.setDelay(500).setCircleColor(Color.WHITE)
									.setCallback(new ShowTipsViewInterface() {
										@Override
										public void gotItClicked() {
											// TODO 自动生成的方法存根
											ShowTipsView showtips = new ShowTipsBuilder(
													CreateFormationActivity.this)
													.setTarget(tipView)
													.setTitle("球员")
													.setTitleColor(Color.WHITE)
													.setDescription("单按更换头像及姓名")
													.setDescriptionColor(
															Color.LTGRAY)
													.setDelay(500)
													.setCircleColor(Color.WHITE)
													.build();
											showtips.show(CreateFormationActivity.this);
											setDelable();
										}
									}).build();
							showtips.show(CreateFormationActivity.this);
						}
					}).build();
			showtips.show(this);
			userSharedPreferences.saveString("create_formation_tip", "1");
		}
	}

	/**
	 * 点击菜单按钮也是直接显示描述对话框
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		dlgBroad.show();
		return false;
	}

	/**
	 * 显示描述对话框
	 */
	@SuppressWarnings("deprecation")
	private void initBroadDialog() {
		// TODO 自动生成的方法存根
		View root = this.getLayoutInflater().inflate(R.layout.popup, null);
		etName = (EditText) root.findViewById(R.id.et_name);
		etDesc = (EditText) root.findViewById(R.id.et_desctiption);
		dlgBroad = new Dialog(this, R.style.customDialog);
		dlgBroad.setContentView(root);
		Window window = dlgBroad.getWindow();
		Display display = getWindowManager().getDefaultDisplay(); // 获取屏幕宽、高用
		WindowManager.LayoutParams lp = window.getAttributes(); // 获取对话框当前的参数值
		lp.height = LayoutParams.WRAP_CONTENT;
		lp.width = (int) (display.getWidth() * 0.8); // 宽度设置为屏幕的0.65
		window.setAttributes(lp);

		window.setGravity(Gravity.CENTER); // 此处可以设置dialog显示的地位
		window.setWindowAnimations(R.style.mystyle); // 添加动画
		root.findViewById(R.id.close).setOnClickListener(
				new View.OnClickListener() {
					public void onClick(View v) {
						etName.setText("");
						etDesc.setText("");
						dlgBroad.dismiss();
					}
				});
		root.findViewById(R.id.confirm).setOnClickListener(
				new View.OnClickListener() {
					public void onClick(View v) {
						if (TextUtils.isEmpty(etName.getText().toString())
								|| TextUtils.isEmpty(etDesc.getText()
										.toString())) {
							ShowToast("请将标题与说明内容补充完整");
						} else {
							dlgBroad.dismiss();
							if (screenShotFail)
								saveFormationAgain();
							else
								saveFormation();
						}
					}

				});
	}

	/**
	 * 获取球队成员列表 by sola
	 */
	private void executeGetTeamMemberList() {
		NetWorkManager.getInstance(mContext).getTeamMember(teamUUID,
				UserManager.getInstance(mContext).getCurrentUser().getToken(),
				new Listener<JSONArray>() {

					@Override
					public void onResponse(JSONArray response) {
						Gson gson = new Gson();
						List<PlayerInTeam> myTeamList = null;
						try {
							myTeamList = gson.fromJson(
									response.toString(),
									new TypeToken<ArrayList<PlayerInTeam>>() {
									}.getType());
						} catch (JsonSyntaxException e) {
							myTeamList = null;
							e.printStackTrace();
						}
						gAdapter.updateListView(myTeamList);
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						// ShowToast("获取球队成员失败：" + error.getMessage());
					}
				});
	}

	private void showLoadingDialog() {
		View lLayout = getLayoutInflater().inflate(R.layout.loading_img, null);
		TextView tvTip = (TextView) lLayout.findViewById(R.id.tv_loading_tip);
		tvTip.setText("保存中...");

		dlgLoadingImg = new Dialog(mContext, R.style.dialog);
		dlgLoadingImg.setContentView(lLayout);
		dlgLoadingImg.setCanceledOnTouchOutside(false);
		dlgLoadingImg.show();
		dlgLoadingImg.getWindow().setGravity(Gravity.CENTER);
	}

	/**
	 * 另一种用第三方库截屏的方法，此方法缺点是不能进行图片裁剪
	 * @return
	 */
	public String screenShot() {
		String formationDetailPath = Config.FORMATION_DIR
				+ String.valueOf(System.currentTimeMillis()) + ".jpg";
		ScreentShotUtil.getInstance().takeScreenshot(
				CreateFormationActivity.this, formationDetailPath);
		return formationDetailPath;
	}
}