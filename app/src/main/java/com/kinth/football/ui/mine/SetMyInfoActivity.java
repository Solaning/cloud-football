package com.kinth.football.ui.mine;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.fourmob.datetimepicker.date.DatePickerDialog.OnDateSetListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kinth.football.CustomApplcation;
import com.kinth.football.R;
import com.kinth.football.bean.AddressBean;
import com.kinth.football.bean.User;
import com.kinth.football.chat.ChatConstants;
import com.kinth.football.config.PlayerPositionEnum;
import com.kinth.football.dao.CityDao;
import com.kinth.football.dao.Player;
import com.kinth.football.eventbus.bean.ModifyPlayerBirthdayEvent;
import com.kinth.football.eventbus.bean.ModifyPlayerHeightEvent;
import com.kinth.football.eventbus.bean.ModifyPlayerPhotoEvent;
import com.kinth.football.eventbus.bean.ModifyPlayerRegionEvent;
import com.kinth.football.eventbus.bean.ModifyPlayerWeightEvent;
import com.kinth.football.friend.PickedImage;
import com.kinth.football.gallery.CropImageActivity;
import com.kinth.football.gallery.CustomGalleryBean;
import com.kinth.football.manager.UserManager;
import com.kinth.football.network.AsyncFileUpload;
import com.kinth.football.network.AsyncFileUpload.FileCallback;
import com.kinth.football.network.NetWorkManager;
import com.kinth.football.network.UserNetworkManager;
import com.kinth.football.picture.PickPicture;
import com.kinth.football.singleton.SingletonBitmapClass;
import com.kinth.football.singleton.ViewCompat;
import com.kinth.football.ui.ActivityBase;
import com.kinth.football.ui.team.CreateTeamActivity;
import com.kinth.football.ui.team.SelectAddressActivity;
import com.kinth.football.util.DateUtil;
import com.kinth.football.util.DialogUtil;
import com.kinth.football.util.ErrorCodeUtil;
import com.kinth.football.util.LogUtil;
import com.kinth.football.util.PhotoUtil;
import com.kinth.football.util.PictureUtil;
import com.kinth.football.util.QuitWay;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import de.greenrobot.event.EventBus;
import eu.inmite.android.lib.dialogs.IListDialogListener;
import eu.inmite.android.lib.dialogs.ListDialogFragment;

/**
 * 个人资料页面
 * 
 * @ClassName: SetMyInfoActivity
 */
@ContentView(R.layout.activity_set_info)
@SuppressLint("SimpleDateFormat")
public class SetMyInfoActivity extends ActivityBase implements IListDialogListener {

	public static final String INTENT_EDIT_PLAYER_INFO = "INTENT_EDIT_PLAYER_INFO";
	public static final String DATEPICKER_TAG = "DATE_PICKER";// 选择日期
	
	private ProgressDialog proDialog;
	private User user;

	@ViewInject(R.id.tv_set_gender)
	TextView tv_set_gender;

	@ViewInject(R.id.tv_account)
	private TextView tv_account;

	@ViewInject(R.id.tv_set_positon)
	private TextView tv_set_positon;

	@ViewInject(R.id.tv_set_age)
	private TextView tv_set_age;

	@ViewInject(R.id.tv_set_weight)
	private TextView tvWeight;

	@ViewInject(R.id.tv_set_height)
	private TextView tvHeight;

	@ViewInject(R.id.tv_set_email)
	private TextView tvEmail;

	@ViewInject(R.id.tv_set_nick)
	private TextView tvNick;

	@ViewInject(R.id.tv_set_erea)
	private TextView tvCity;

	@ViewInject(R.id.iv_set_avator)
	private ImageView iv_set_avator;

	@OnClick(R.id.iv_set_avator)
	public void fun_2(View v) {// 头像预览大图
		if (!TextUtils.isEmpty(user.getPlayer().getPicture())) {
			ArrayList<String> photos = new ArrayList<String>();
			photos.add(PhotoUtil.getAllPhotoPath(user.getPlayer().getPicture()));

			PictureUtil.viewLargerImage(mContext, photos);
		}
	}

	@ViewInject(R.id.viewGroup)
	private LinearLayout viewGroup;

	@ViewInject(R.id.layout_head)
	private LinearLayout layout_head;

	@OnClick(R.id.layout_head)
	public void fun_1(View v) {
		ListDialogFragment
				.createBuilder(SetMyInfoActivity.this,
						getSupportFragmentManager()).setTitle("更换头像")
				.setItems(new String[] { "拍照", "选择相册" })
				.hideDefaultButton(true).show();
	}

	@ViewInject(R.id.layout_height)
	private LinearLayout layout_height;

	@ViewInject(R.id.layout_weight)
	private LinearLayout layout_weight;

	@ViewInject(R.id.layout_position)
	private LinearLayout layout_positon;

	@ViewInject(R.id.layout_age)
	private LinearLayout layout_age;

	@OnClick(R.id.layout_nick)
	public void fun_4(View v) {
		Intent intent = new Intent(this, ModifyInfoActivity.class);
		intent.putExtra("from", "nick");
		startAnimActivity(intent);
	}

	@OnClick(R.id.layout_height)
	public void fun_5(View v) {
		Intent intent = new Intent(this, ModifyInfoActivity.class);
		intent.putExtra("from", "height");
		startAnimActivity(intent);
	}

	@OnClick(R.id.layout_weight)
	public void fun_6(View v) {
		Intent intent = new Intent(this, ModifyInfoActivity.class);
		intent.putExtra("from", "weight");
		startAnimActivity(intent);
	}

	@OnClick(R.id.layout_position)
	public void fun_7(View v) {
		Intent intent = new Intent(this, ModifyPositionActivity.class);
		startAnimActivity(intent);
	}

	@OnClick(R.id.layout_gender)
	public void fun_8(View v) {
		ListDialogFragment
				.createBuilder(SetMyInfoActivity.this,
						getSupportFragmentManager()).setTitle("请选择你的性别")
				.setItems(new String[] { "男", "女" }).hideDefaultButton(true)
				.show();
	}

	@OnClick(R.id.layout_email)
	public void fun_9(View v) {
		Intent intent = new Intent(this, ModifyInfoActivity.class);
		intent.putExtra("from", "email");
		startAnimActivity(intent);
	}

	@OnClick(R.id.layout_erea)
	public void fun_10(View v) {
		Intent intent = new Intent(this, SelectAddressActivity.class);
		startActivityForResult(intent, CreateTeamActivity.REQUEST_CODE_CITY);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 因为魅族手机下面有三个虚拟的导航按钮，需要将其隐藏掉，不然会遮掉拍照和相册两个按钮，且在setContentView之前调用才能生效
		// int currentapiVersion=android.os.Build.VERSION.SDK_INT;
		// if(currentapiVersion>=14){
		// getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
		// }
		EventBus.getDefault().register(this);
		QuitWay.activityList.add(this);
		ViewUtils.inject(this);

		initView();
		layout_age.setOnTouchListener(touchListener);
		// 根据用户Uid获取得到该用户的详细资料
		executeGetPersonInfo();
	}
	
	@Override
	protected void onDestroy() {
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}

	private OnTouchListener touchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				switch (v.getId()) {
				case R.id.layout_age:
					final DatePickerDialog datePickerDialog = DatePickerDialog
							.newInstance(new OnDateSetListener() {

								@Override
								public void onDateSet(
										DatePickerDialog datePickerDialog,
										int year, int month, int day) {
									// Calendar月份是从0开始，所以month要加1
									dateStart = year + "-" + (month + 1) + "-"
											+ day;
									int i = 0;
									try {
										i = DateUtil
												.compareDateWithToday(dateStart);
									} catch (ParseException e1) {
										e1.printStackTrace();
//										Toast.makeText(mContext, "日期解析出错",
//												Toast.LENGTH_LONG).show();
										return;
									}
									if (i > 0) {// 所选日期在今天之后
										Toast.makeText(mContext, "生日不能比当前时间晚！",
												Toast.LENGTH_LONG).show();
										return;
									} else {// 所选日期在今天或之前
										monifyUserInfo_age(DateUtil
												.parseTimeToMillis2(dateStart));

									}
								}
							},
									1980, 0, 1, true);
					datePickerDialog.setYearRange(1903, 2037);
					datePickerDialog.setCloseOnSingleTapDay(true);
					datePickerDialog.show(getSupportFragmentManager(),
							DATEPICKER_TAG);
					break;
				}
			}
			return true;
		}
	};

	private void initView() {
		// 設置背景
		Bitmap background = SingletonBitmapClass.getInstance()
				.getBackgroundBitmap();
		ViewCompat.setBackground(viewGroup, new BitmapDrawable(getResources(),
				background));

		initTopBarForLeft("个人资料");
	}

	@Override
	public void onResume() {
		super.onResume();
		user = footBallUserManager.getCurrentUser();
		if (user == null) {
			return;
		}
		tv_account.setText(StringUtils.defaultIfEmpty(user.getPlayer().getAccountName(), ""));
		PictureUtil.getMd5PathByUrl(PhotoUtil.getThumbnailPath(user.getPlayer().getPicture()), iv_set_avator,
				new DisplayImageOptions.Builder()
						.showImageOnLoading(
								R.drawable.icon_default_head)
						.showImageForEmptyUri(
								R.drawable.icon_default_head)
						.showImageOnFail(
								R.drawable.icon_default_head)
						.cacheInMemory(true).cacheOnDisk(true)
						.build());
		// 设置邮箱
		tvEmail.setText(StringUtils.defaultIfEmpty(user.getPlayer().getEmail(),""));

		// 设置昵称
		tvNick.setText(StringUtils.defaultIfEmpty(user.getPlayer().getName(),""));
		// 设置生日
		if (user.getPlayer().getBirthday() != null) {
			// int age =
			// DateUtil.calcAgeByBirthday(user.getPlayer().getBirthday());
			String dataStr = DateUtil.parseTimeInMillis_only_nyr(user
					.getPlayer().getBirthday());
			tv_set_age.setText(dataStr);
		}
		// 设置身高
		if (user.getPlayer().getHeight() != null) {
			tvHeight.setText(user.getPlayer().getHeight() + "cm");
		}
		// 设置体重
		if (user.getPlayer().getWeight() != null) {
			tvWeight.setText(user.getPlayer().getWeight() + "kg");
		}
		// 设置位置
		if (!TextUtils.isEmpty(user.getPlayer().getPosition())) {
			String name_position = PlayerPositionEnum.getEnumFromString(
					user.getPlayer().getPosition()).getName();
			tv_set_positon.setText(name_position);
		}

		String gender = user.getPlayer().getGender();
		if (!TextUtils.isEmpty(user.getPlayer().getGender())) {
			if (gender.equals("FEMALE")) {
				tv_set_gender.setText("女");
			} else {
				tv_set_gender.setText("男");
			}
		}
	}

	RelativeLayout layout_choose;
	RelativeLayout layout_photo;
	PopupWindow avatorPop;

	public String filePath = "";

	/**
	 * 更换头像
	 */
	private void changeIcon(String value, int number) {
		if (number == 0) { // 拍照
			mPhotoPath = PickPicture.genCachePhotoFileName();// 文件路径
			startActivityForResult(PickPicture.genCameraShootIntent(mPhotoPath),
					ChatConstants.REQUESTCODE_UPLOADAVATAR_CAMERA);
		} else { // 选择照片
			Intent intent = PickPicture.genPickPictureIntent(mContext);
			startActivityForResult(intent,
					ChatConstants.REQUESTCODE_UPLOADAVATAR_LOCATION);
		}
	}

	Bitmap newBitmap;
	boolean isFromCamera = false;// 区分拍照旋转
	int degree = 0;
	String mPhotoPath;

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case ChatConstants.REQUESTCODE_UPLOADAVATAR_CAMERA:// 拍照修改头像
			if (resultCode == RESULT_OK) {
				CustomGalleryBean galleryBean = new CustomGalleryBean(0, 0,
						false, mPhotoPath, mPhotoPath);

				Intent intent2 = new Intent(mContext, CropImageActivity.class);
				intent2.putExtra(
						CropImageActivity.INTENT_GALLERY_IMAGE_TO_CROP,
						galleryBean);
				startActivityForResult(intent2,
						ChatConstants.REQUESTCODE_UPLOADAVATAR_CROP);
			}
			break;
		case ChatConstants.REQUESTCODE_UPLOADAVATAR_LOCATION:// 本地修改头像
			if (avatorPop != null) {
				avatorPop.dismiss();
			}
			if (resultCode == RESULT_OK) {
				if (!Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					ShowToast("SD不可用");
					return;
				}
				isFromCamera = false;
				
				Uri uri = data.getData();
				String imagePath = PickPicture.getPath(mContext, uri);
				if(PictureUtil.isImage(imagePath)){
					ArrayList<PickedImage> imageResult = new ArrayList<PickedImage>();
					imageResult.add(new PickedImage(0, imagePath));
					
					if (imagePath != null && imagePath.length() > 0) {
						mPhotoPath = imagePath;
					}
					CustomGalleryBean galleryBean = new CustomGalleryBean(0, 0,
							false, mPhotoPath, mPhotoPath);

					Intent intent2 = new Intent(mContext, CropImageActivity.class);
					intent2.putExtra(
							CropImageActivity.INTENT_GALLERY_IMAGE_TO_CROP,
							galleryBean);
					startActivityForResult(intent2,
							ChatConstants.REQUESTCODE_UPLOADAVATAR_CROP);
					
				}else{
					ShowToast("所选文件非图片");
					Log.e("选的非图片","imagePath = "+imagePath);
				}
			} else {
				ShowToast("照片获取失败");
			}
			break;
		case ChatConstants.REQUESTCODE_UPLOADAVATAR_CROP:// 裁剪头像返回
			// TODO sent to crop
			// if (requestCode == RESULT_OK) {

			if (avatorPop != null) {
				avatorPop.dismiss();
			}
			if (data == null) {
				// Toast.makeText(this, "取消选择", Toast.LENGTH_SHORT).show();
				return;
			} else {
				saveCropAvator(data);
			}
			// 初始化文件路径
			filePath = "";
			// 上传头像
			path = data
					.getStringExtra(CropImageActivity.INTENT_NEW_IMAGE_PATH_FOR_RESULT);
			uploadAvatar();
			// }
			break;
		case CreateTeamActivity.REQUEST_CODE_CITY: // 选择地区返回
			if (resultCode == RESULT_OK) {
				AddressBean addressBean = data
						.getParcelableExtra(CreateTeamActivity.RESULT_SELECT_CITY);
				if (addressBean == null) {
					return;
				}
				tvCity.setText(addressBean.getProvinceName() + "　"
						+ addressBean.getCityName());
				executeUpdateUserArea(addressBean.getCityId(),
						addressBean.getCityName(),
						addressBean.getProvinceName());
			}
			break;
		default:
			break;

		}
	}

	/**
	 * 上传头像
	 */
	@SuppressWarnings("static-access")
	private void uploadAvatar() {

		LogUtil.i("头像地址：" + path);
		ShowToast("正在上传图片...");

		AsyncFileUpload asyncFileUpload = new AsyncFileUpload();
		asyncFileUpload.asynFileUploadToServer(path, new FileCallback() {
			@Override
			public void fileUploadCallback(final String fileUrl) {
				// TODO Auto-generated method stub
				LogUtil.i("头像地址：" + path);
				if (TextUtils.isEmpty(fileUrl)) {
					ShowToast("上传失败");
					return;
				}
				runOnUiThread(new Runnable() {
					public void run() {
						ShowToast("上传成功");
						updateUserAvatar(fileUrl);
					}
				});
			}
		});
	}

	/**
	 * 更新用户头像信息
	 * 
	 * @param url
	 */
	private void updateUserAvatar(final String url) {
		UserNetworkManager.getInstance(getApplicationContext()).modifyUserIcon(
				url, new Listener<Void>() {
					@Override
					public void onResponse(Void response) {
						ShowToast("修改头像成功");
						User user = footBallUserManager.getCurrentUser();
						user.getPlayer().setPicture(url);
						footBallUserManager.saveCurrentUser(user);
						ImageLoader.getInstance().displayImage(
								PhotoUtil.getAllPhotoPath(user.getPlayer()
										.getPicture()), iv_set_avator);

						EventBus.getDefault().post(new ModifyPlayerPhotoEvent(url));
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						ShowToast("修改头像失败");
					}
				});
	}

	String path;

	/**
	 * 保存裁剪的头像
	 * 
	 * @param data
	 */
	private void saveCropAvator(Intent data) {
		Bundle extras = data.getExtras();
		if (extras != null) {
			Bitmap bitmap = extras.getParcelable("data");
			Log.i("life", "avatar - bitmap = " + bitmap);
			if (bitmap != null) {
				bitmap = PhotoUtil.toRoundCorner(bitmap, 10);
				if (isFromCamera && degree != 0) {
					bitmap = PhotoUtil.rotaingImageView(degree, bitmap);
				}
				iv_set_avator.setImageBitmap(bitmap);
				// 保存图片
				String filename = new SimpleDateFormat("yyMMddHHmmss")
						.format(new Date());
				path = ChatConstants.MyAvatarDir + filename;
				PhotoUtil.saveBitmap(ChatConstants.MyAvatarDir, filename, bitmap,
						true);
				// 上传头像
				if (bitmap != null && bitmap.isRecycled()) {
					bitmap.recycle();
				}
			}
		}
	}

	@Override
	public void onListItemSelected(String value, int number) {
		if (value.equals("男") || value.equals("女")) {
			tv_set_gender.setText(value + "");
			String gender = "";
			if (value.equals("男")) {
				gender = "MALE";
			} else {
				gender = "FEMALE";
			}
			updataSex(gender);
		} else {
			changeIcon(value, number);
		}
	}

	/**
	 * 更新性别
	 * @param gender
	 */
	public void updataSex(final String gender) {
		UserNetworkManager.getInstance(getApplicationContext()).modifyUserSex(
				gender, new Listener<Void>() {
					@Override
					public void onResponse(Void response) {
						ShowToast("修改成功");
						User user = footBallUserManager.getCurrentUser();
						user.getPlayer().setGender(gender);
						footBallUserManager.saveCurrentUser(user);
					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						ShowToast("修改失败");
					}
				});
	}

	/**
	 * 获取个人信息
	 */
	private void executeGetPersonInfo() {
		NetWorkManager.getInstance(mContext).getPlayerInfo(
				UserManager.getInstance(mContext).getCurrentUser().getToken(),
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Gson gson = new Gson();
						Player player = gson.fromJson(response.toString(),
								new TypeToken<Player>() {
								}.getType());
						if (player == null) {
							return;
						}
						// 将其保存到本地数据库中
						CustomApplcation.getDaoSession(mContext)
								.getPlayerDao().insertOrReplace(player);
						if(user.getPlayer().getUuid().equals(player.getUuid())){
							user.setPlayer(player);
						}
						PictureUtil.getMd5PathByUrl(PhotoUtil.getThumbnailPath(player.getPicture()), iv_set_avator,
								new DisplayImageOptions.Builder()
										.showImageOnLoading(
												R.drawable.icon_default_head)
										.showImageForEmptyUri(
												R.drawable.icon_default_head)
										.showImageOnFail(
												R.drawable.icon_default_head)
										.cacheInMemory(true).cacheOnDisk(true)
										.build());
						tvEmail.setText(StringUtils.defaultIfEmpty(player.getEmail(), ""));
						if (player.getCityId() != null
								&& player.getProvince() != null) { 
							//因为后台返回的地区字数可能大于4，而手机前台全部修改为小于等于4，故要根据cityid去本地获取数据，统一
							CityDao dao = new CityDao(mContext);
							String cityName = dao.getCityByCityId(player.getCityId()).getName();
							tvCity.setText(player.getProvince() + "　"
									+ cityName);
							dao.closeSQLiteDatabase();
						} else {
							tvCity.setText("");
						}
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						if (!NetWorkManager.getInstance(mContext)
								.isNetConnected()) {
							ShowToast("当前网络不可用");
						} else if (error.networkResponse == null) {
//							ShowToast("SetMyInfoActivity-executeGetPersonInfo-服务器连接错误");
						} else if (error.networkResponse.statusCode == 401) {
							ErrorCodeUtil.ErrorCode401(mContext);
						}
					}
				});
	}

	/**
	 * 修改地区
	 * @param mCityId
	 * @param cityName
	 * @param provinceName
	 */
	private void executeUpdateUserArea(final int mCityId,
			final String cityName, final String provinceName) {
		proDialog = ProgressDialog.show(mContext, "提示", "请稍候...", false, false);
		UserNetworkManager.getInstance(mContext).modifyUserErea(mCityId,
				UserManager.getInstance(mContext).getCurrentUser().getToken(),
				new Listener<Void>() {
					@Override
					public void onResponse(Void response) {
						User user = footBallUserManager.getCurrentUser();
						user.getPlayer().setCity(cityName);
						user.getPlayer().setProvince(provinceName);
						user.getPlayer().setCityId(mCityId);

						footBallUserManager.saveCurrentUser(user);

						ShowToast("修改成功");
						DialogUtil.dialogDismiss(proDialog);

						EventBus.getDefault().post(new ModifyPlayerRegionEvent(provinceName, cityName));
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						ShowToast("修改失败");
						DialogUtil.dialogDismiss(proDialog);
					}
				});
	}

	private ProgressDialog progress = null;
	private String dateStart;

	/**
	 * 修改年龄
	 * @param birthday
	 */
	private void monifyUserInfo_age(final long birthday) {
		progress = ProgressDialog.show(mContext, "提示", "请稍候...", false, false);

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("birthday", birthday);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		NetWorkManager.getInstance(getApplicationContext()).monifyUserInfo(
				footBallUserManager.getCurrentUser().getToken(), jsonObject,
				new Listener<Void>() {
					@Override
					public void onResponse(Void response) {
						ShowToast("修改成功 !");
						DialogUtil.dialogDismiss(progress);
						User user = footBallUserManager.getCurrentUser();
						user.getPlayer().setBirthday(birthday);
						
						footBallUserManager.saveCurrentUser(user);
						EventBus.getDefault().post(new ModifyPlayerBirthdayEvent(birthday));
						tv_set_age.setText(dateStart);
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						DialogUtil.dialogDismiss(progress);
						ShowToast("修改失败!");

						if (!NetWorkManager.getInstance(mContext)
								.isNetConnected()) {
							ShowToast("没有可用的网络");
						} else if (error.networkResponse == null) {
//							ShowToast("服务器连接错误");
						} else if (error.networkResponse.statusCode == 401) { // access
							ErrorCodeUtil.ErrorCode401(mContext);
						} 
					}
				});
	}
	
	//修改了身高的事件
	public void onEventMainThread(ModifyPlayerHeightEvent modifyPlayerHeightEvent){
		tvHeight.setText(modifyPlayerHeightEvent.getNewHeight() + "cm");
	}
	
	//修改了体重的事件
	public void onEventMainThread(ModifyPlayerWeightEvent modifyPlayerWeightEvent){
		tvWeight.setText(modifyPlayerWeightEvent.getNewWeight() + "kg");
	}
}
