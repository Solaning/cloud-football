package com.kinth.football.ui.team;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.time.FastDateFormat;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.kinth.football.R;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.kinth.football.bean.ErrorDescription;
import com.kinth.football.bean.PickedImage;
import com.kinth.football.bean.Province;
import com.kinth.football.bean.AddressBean;
import com.kinth.football.bean.RegionBean;
import com.kinth.football.bean.TeamRequest;
import com.kinth.football.bean.UploadImage;
import com.kinth.football.chat.ChatConstants;
import com.kinth.football.config.JConstants;
import com.kinth.football.dao.CityDao;
import com.kinth.football.dao.ProvinceDao;
import com.kinth.football.dao.Team;
import com.kinth.football.gallery.CropImageActivity;
import com.kinth.football.gallery.CustomGalleryBean;
import com.kinth.football.network.AsyncUploadImage;
import com.kinth.football.network.NetWorkManager;
import com.kinth.football.singleton.SingletonBitmapClass;
import com.kinth.football.singleton.ViewCompat;
import com.kinth.football.ui.BaseActivity;
import com.kinth.football.ui.fragment.TeamFragment;
import com.kinth.football.util.DialogUtil;
import com.kinth.football.util.ErrorCodeUtil;
import com.kinth.football.util.FileUtil;
import com.kinth.football.util.PhotoUtil;
import com.kinth.football.util.PictureUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import eu.inmite.android.lib.dialogs.IListDialogListener;
import eu.inmite.android.lib.dialogs.ListDialogFragment;

/**
 * 创建球队
 * @author Sola
 */

@ContentView(R.layout.activity_create_team)
public class CreateTeamActivity extends BaseActivity implements
		IListDialogListener {
	public static final int REQUEST_CODE_FAMILY_PHOTO = 9001;  // 全家福照片请求码
	public static final int REQUEST_CODE_BADGE = 9002;// 队徽照片请求码
	public static final int REQUEST_CODE_CITY = 9003;// 城市请求码
	public static final int REQUEST_CODE_REGION = 9004;// 区县请求码
	public static final String RESULT_SELECT_CITY = "RESULT_SELECT_CITY";// 选择城市的结果
	private static final String TAG_FAMILY_PHOTO = "TAG_FAMILY_PHOTO";// 全家福标签
	private static final String TAG_BADGE = "TAG_BADGE";// 队徽标签
	private int cityId;// 城市id
	private int regionId;// 地区id
	private String familyPhotoPath;   // 本地路径
	private String familyPhotoUrl;    // 合照的图片Url
	private String badgePath;         // 队徽本地路径
	private String badgeUrl;          // 队徽的图片Url
	private String id;
	private ProgressDialog proDialog;

	public static final String REGION_SELECT = "REGION_SELECT";

	private int flag;

	@ViewInject(R.id.entire_layout)
	private View entireLayout;
	
	@ViewInject(R.id.nav_left)  //返回
	private ImageButton nav_left;
	
	@ViewInject(R.id.nav_right_btn)
	private TextView nav_right_btn;
	
	@ViewInject(R.id.nav_title)
	private TextView nav_title;
	
	@ViewInject(R.id.layout_team_allfamily)
	private RelativeLayout layout_team_allfamily;
	
	@ViewInject(R.id.layout_team_brage)
	private RelativeLayout layout_team_brage;	
	
	@ViewInject(R.id.family_photo)
	private ImageView groupPhoto;// 合照

	@ViewInject(R.id.badge)
	private ImageView badge;// 队徽

	@ViewInject(R.id.et_input_team_name)
	private EditText teamName;// 球队名称

	@ViewInject(R.id.btn_input_city)
	private RelativeLayout city;// 所在城市

	@ViewInject(R.id.city_name)
	private TextView city_name;

	@ViewInject(R.id.select_region)
	private RelativeLayout region; // 区县

	@ViewInject(R.id.region_name)
	private TextView region_name;

	@ViewInject(R.id.et_input_home_ground)
	private EditText homeGround;// 主场

	@ViewInject(R.id.et_input_description)
	private EditText description;// 介绍

	@ViewInject(R.id.et_input_slogan)
	private EditText slogan;// 口号

	@OnClick(R.id.nav_left)
	public void fun_1(View v){
		finish();
	}
	
	@OnClick(R.id.layout_team_allfamily)
	public void fun_2(View v) {
		flag = 1;
		ListDialogFragment.createBuilder(this, getSupportFragmentManager())
				.setTitle("选择全家福").setItems(new String[] { "拍照", "选择相册" })
				.hideDefaultButton(true).show();
	}

	@OnClick(R.id.layout_team_brage)
	public void fun_3(View v) {// 队徽选图片
		flag = 2;
		ListDialogFragment.createBuilder(this, getSupportFragmentManager())
				.setTitle("选择队徽").setItems(new String[] { "拍照", "选择相册" })
				.hideDefaultButton(true).show();
	}

	@OnClick(R.id.btn_input_city)
	public void fun_4(View v) {// 选择城市
		Intent intent = new Intent(CreateTeamActivity.this,
				SelectAddressActivity.class);
		startActivityForResult(intent, REQUEST_CODE_CITY);
	}

	@OnClick(R.id.nav_right_btn)
	public void fun_5(View v) {// 创建球队
		String nameStr = teamName.getText().toString();
		if (TextUtils.isEmpty(nameStr)) {
			ShowToast("请输入球队名称");
			return;
		}
		String cityStr = city_name.getText().toString();
		String regionStr = region_name.getText().toString();
		if (cityId == 0 || TextUtils.isEmpty(cityStr)) {
			ShowToast("请选择城市");
			return;
		}
		if (regionId == 0 || TextUtils.isEmpty(regionStr)) {
			ShowToast("请选择区县");
			return;
		}
		if (!TextUtils.isEmpty(familyPhotoPath)
				&& !TextUtils.isEmpty(badgePath)) {
			// 需要先上传图片
			proDialog = ProgressDialog.show(mContext, "提示", "请稍候...", false,
					false);

			uploadFile();
		} else { // 直接执行创建操作
			proDialog = ProgressDialog.show(mContext, "提示", "请稍候...", false,
								false);
			TeamRequest teamRequest = buildTeamRequestExceptUrl();
			executeCreateTeam(teamRequest);
		}
	}

	@OnClick(R.id.select_region)
	public void fun_6(View v) {
		if (!TextUtils.isEmpty(city_name.getText().toString())) { // 说明已经选择了城市
			Intent intent = new Intent(mContext, RegionListActivity.class);
			intent.putExtra(REGION_SELECT, cityId);
			startActivityForResult(intent, REQUEST_CODE_REGION);
		} else {
			ShowToast("请先选择城市");
			return;
		}
	}

	/**
	 * 生成TeamRequest，除了图片的url
	 * 
	 * @return
	 */
	private TeamRequest buildTeamRequestExceptUrl() {
		TeamRequest teamRequest = new TeamRequest();
		String nameStr = teamName.getText().toString();
		teamRequest.setName(nameStr);
		teamRequest.setCityId(cityId);
		teamRequest.setRegionId(regionId);
		String sloganStr = slogan.getText().toString();
		if (!TextUtils.isEmpty(sloganStr)) {// 口号
			teamRequest.setSlogan(sloganStr);
		}
		String descriptionStr = description.getText().toString();
		if (!TextUtils.isEmpty(descriptionStr)) {// 介绍
			teamRequest.setDescription(descriptionStr);
		}
		return teamRequest;
	}

	// 执行创建球队的操作
	private void executeCreateTeam(TeamRequest teamRequest) {
		// 创建球队
		NetWorkManager.getInstance(getApplicationContext()).createTeam(
				footBallUserManager.getCurrentUser().getToken(), teamRequest,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						DialogUtil.dialogDismiss(proDialog);
						Gson gson = new Gson();
						Team team = null;
						try {
							team = gson.fromJson(response.toString(),
									new TypeToken<Team>() {
									}.getType());
						} catch (JsonSyntaxException e) {
							team = null;
							e.printStackTrace();
						}
						if (team != null) { // TODO
							ShowToast("创建球队成功");
							Intent intent = new Intent();
							intent.putExtra(
									TeamFragment.RESULT_AFTER_TEAM_FOUNDED,
									team);
							setResult(RESULT_OK, intent);
							finish();
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						DialogUtil.dialogDismiss(proDialog);
						if (!NetWorkManager.getInstance(mContext)
								.isNetConnected()) {
							ShowToast("没有可用的网络");
							return;
						} 
						if (error.networkResponse == null) {
							ShowToast("创建球队失败");
							return;
						}
						if (error.networkResponse.statusCode == 401) {
							ErrorCodeUtil.ErrorCode401(mContext);
							return;
						} 
						if (error.networkResponse.statusCode == 404) {
							if(error.networkResponse.data != null){
								Gson gson = new Gson();
								ErrorDescription errDescription = gson.fromJson(new String(error.networkResponse.data), new TypeToken<ErrorDescription>(){}.getType());
								if(errDescription == null){
									ShowToast("创建球队失败");
									return;
								}
								if("region".equals(errDescription.getError())){
									ShowToast("创建球队失败：地区查找不到");
									return;
								}
								if("city".equals(errDescription.getError())){
									ShowToast("创建球队失败：城市查找不到");
									return;
								}
								if("firstCaptain".equals(errDescription.getError())){
									ShowToast("创建球队失败：第一队长找不到");
									return;
								}
							}else{
								ShowToast("创建球队失败");
							}
							return;
						}
						if(error.networkResponse.statusCode == 409){
							if(error.networkResponse.data != null){
								Gson gson = new Gson();
								ErrorDescription errDescription = gson.fromJson(new String(error.networkResponse.data), new TypeToken<ErrorDescription>(){}.getType());
								if(errDescription == null){
									ShowToast("创建球队失败");
									return;
								}
								if("secondCaptain".equals(errDescription.getError())){
									ShowToast("创建球队失败：队长和队副是同一个人");
									return;
								}
								if("thirdCaptain".equals(errDescription.getError())){
									ShowToast("创建球队失败：教练和队长或队副是同一个人");
									return;
								}
							}else{
								ShowToast("创建球队失败");
							}
							return;
						}
					}
				});
	}
	
	private void uploadFile1() {
		AsyncUploadImage asyncUploadImage = new AsyncUploadImage() {

			@Override
			public void onUploadSuccess(Map<String, UploadImage> uploadMap) {
			
				TeamRequest teamRequest = buildTeamRequestExceptUrl();
				for (Map.Entry<String, UploadImage> entry : uploadMap
						.entrySet()) {
					if (TAG_FAMILY_PHOTO.equals(entry.getValue().getTag())) {// 全家福
						teamRequest.setFamilyPhoto(entry.getValue().getUrl());
						familyPhotoUrl = entry.getValue().getUrl();
						PictureUtil.getMd5PathByUrl(PhotoUtil.getThumbnailPath(entry.getValue().getUrl()),
								groupPhoto,
								new DisplayImageOptions.Builder()
						.showImageOnLoading(R.drawable.team_family_defualt)
						.showImageForEmptyUri(R.drawable.team_family_defualt)
						.showImageOnFail(R.drawable.team_family_defualt)
						.cacheInMemory(true).cacheOnDisk(true).build());
						
						continue;
					}
					if (TAG_BADGE.equals(entry.getValue().getTag())) {// 队徽
						teamRequest.setBadge(entry.getValue().getUrl());
						badgeUrl = entry.getValue().getUrl();
						PictureUtil.getMd5PathByUrl(PhotoUtil.getThumbnailPath(entry.getValue().getUrl()),
								badge,
								new DisplayImageOptions.Builder()
						.showImageOnLoading(R.drawable.team_bage_default)
						.showImageForEmptyUri(R.drawable.team_bage_default)
						.showImageOnFail(R.drawable.team_bage_default)
						.cacheInMemory(true).cacheOnDisk(true).build());
						continue;
					}
				}
		
			}

			@Override
			public void onUploadFailed() {
		
				Toast.makeText(mContext, "发送失败", Toast.LENGTH_LONG).show();
			}
		};
		Map<String, UploadImage> uploadMap = new LinkedHashMap<String, UploadImage>();// 服务器上的url
		if (flag ==1) {
			if (!TextUtils.isEmpty(familyPhotoPath)) {
				uploadMap.put(familyPhotoPath, new UploadImage(familyPhotoPath,
						TAG_FAMILY_PHOTO));
				asyncUploadImage.upload2(uploadMap,
						new PickedImage(familyPhotoPath));
			}
		}
		else{
		if (!TextUtils.isEmpty(badgePath)) {
			uploadMap.put(badgePath, new UploadImage(badgePath, TAG_BADGE));
			asyncUploadImage.upload2(uploadMap, new PickedImage(badgePath));
		}
		}
	}
	private void uploadFile() {
		AsyncUploadImage asyncUploadImage = new AsyncUploadImage() {

			@Override
			public void onUploadSuccess(Map<String, UploadImage> uploadMap) {
				DialogUtil.dialogDismiss(proDialog);
				TeamRequest teamRequest = buildTeamRequestExceptUrl();
				for (Map.Entry<String, UploadImage> entry : uploadMap
						.entrySet()) {
					if (TAG_FAMILY_PHOTO.equals(entry.getValue().getTag())) {// 全家福
						teamRequest.setFamilyPhoto(entry.getValue().getUrl());
						familyPhotoUrl = entry.getValue().getUrl();
					
						continue;
					}
					if (TAG_BADGE.equals(entry.getValue().getTag())) {// 队徽
						teamRequest.setBadge(entry.getValue().getUrl());
						badgeUrl = entry.getValue().getUrl();
						
						continue;
					}
				}
				executeCreateTeam(teamRequest);
			}

			@Override
			public void onUploadFailed() {
				DialogUtil.dialogDismiss(proDialog);
//				Toast.makeText(mContext, "发送失败", Toast.LENGTH_LONG).show();
			}
		};
		Map<String, UploadImage> uploadMap = new LinkedHashMap<String, UploadImage>();// 服务器上的url
		if (!TextUtils.isEmpty(familyPhotoPath)) {
			uploadMap.put(familyPhotoPath, new UploadImage(familyPhotoPath,
					TAG_FAMILY_PHOTO));
			asyncUploadImage.upload2(uploadMap,
					new PickedImage(familyPhotoPath));
		}
		if (!TextUtils.isEmpty(badgePath)) {
			uploadMap.put(badgePath, new UploadImage(badgePath, TAG_BADGE));
			asyncUploadImage.upload2(uploadMap, new PickedImage(badgePath));
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		
		// 設置背景
		Bitmap background = SingletonBitmapClass.getInstance()
				.getBackgroundBitmap();
		ViewCompat.setBackground(entireLayout, new BitmapDrawable(getResources(),
				background));
		
		nav_title.setText("创建球队");
	}

	boolean isFromCamera = false;  // 区分拍照旋转
	int degree = 0;
	RelativeLayout layout_choose;
	RelativeLayout layout_photo;
	PopupWindow avatorPop;

	public String filePath = "";
	String mPhotoPath;
	String path;
	/**
	 * @Title: startImageAction
	 * @return void
	 * @throws
	 */
	private void startImageAction(Uri uri, int outputX, int outputY,
			int requestCode, boolean isCrop) {
		Intent intent = null;
		if (isCrop) {
			intent = new Intent("com.android.camera.action.CROP");
		} else {
			intent = new Intent(Intent.ACTION_GET_CONTENT, null);
		}
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", outputX);
		intent.putExtra("outputY", outputY);
		intent.putExtra("scale", true);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		intent.putExtra("return-data", true);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true); // no face detection
		startActivityForResult(intent, requestCode);
	}

	/**
	 * 保存裁剪的头像
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
				if (flag == 1) {
					groupPhoto.setImageBitmap(bitmap);
				} else {
					badge.setImageBitmap(bitmap);
				}
				// 保存图片
				String filename = "football" +  new SimpleDateFormat("yyMMddHHmmss").format(new Date());
				if (flag == 1) {
					familyPhotoPath = ChatConstants.MyAvatarDir + filename;
				} else {
					badgePath = ChatConstants.MyAvatarDir + filename;
				}
				// filePath = ChatConstants.MyAvatarDir + filename;
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
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (resultCode != RESULT_OK) {
			return;
		}
		if (requestCode == ChatConstants.REQUESTCODE_UPLOADAVATAR_CAMERA) {//照相
		// needToRefreshCount++;
			if (resultCode == RESULT_OK) {
//				if (!Environment.getExternalStorageState().equals(
//						Environment.MEDIA_MOUNTED)) {
//					ShowToast("SD不可用");
//					return;
//				}
//				isFromCamera = true;
//				File file = new File(filePath);
//				degree = PhotoUtil.readPictureDegree(file.getAbsolutePath());
//				Log.i("life", "拍照后的角度：" + degree);
//				startImageAction(Uri.fromFile(file), 200, 200,
//						ChatConstants.REQUESTCODE_UPLOADAVATAR_CROP, true);
				CustomGalleryBean galleryBean = new CustomGalleryBean(0, 0, false, mPhotoPath, mPhotoPath);

				Intent intent2 = new Intent(mContext,
						CropImageActivity.class);
				intent2.putExtra(CropImageActivity.INTENT_GALLERY_IMAGE_TO_CROP, galleryBean);
				startActivityForResult(intent2, ChatConstants.REQUESTCODE_UPLOADAVATAR_CROP);
			}
			return;
		}
		if (requestCode == ChatConstants.REQUESTCODE_UPLOADAVATAR_CROP) {//剪切
			if (avatorPop != null) {
				avatorPop.dismiss();
			}
			if (intent == null) {
				// Toast.makeText(this, "取消选择", Toast.LENGTH_SHORT).show();
				return;
			} else {
				saveCropAvator(intent);
			}
			// 初始化文件路径
			filePath = "";
			// 上传头像
//			path =  intent.getStringExtra(CropImageActivity.INTENT_NEW_IMAGE_PATH_FOR_RESULT);
			if (flag == 1) {
				familyPhotoPath =  intent.getStringExtra(CropImageActivity.INTENT_NEW_IMAGE_PATH_FOR_RESULT);
				uploadFile1();
			} else {
				badgePath =  intent.getStringExtra(CropImageActivity.INTENT_NEW_IMAGE_PATH_FOR_RESULT);
				uploadFile1();
			}
			return;
		}
		if (requestCode == ChatConstants.REQUESTCODE_UPLOADAVATAR_LOCATION) {  //本地照片
			Uri uri = null;
			if (intent == null) {
				return;
			}
			if (resultCode == RESULT_OK) {
				if (!Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					ShowToast("SD不可用");
					return;
				}
				isFromCamera = false;
				uri = intent.getData();
//				startImageAction(uri, 200, 200,
//						ChatConstants.REQUESTCODE_UPLOADAVATAR_CROP, true);
				 ContentResolver resolver = this.getContentResolver();
				    String[] pojo = {MediaStore.Images.Media.DATA};
				  
				    CursorLoader cursorLoader = new CursorLoader(this, uri, pojo, null,null, null);
				    Cursor cursor = cursorLoader.loadInBackground();
				     cursor.moveToFirst();
				     String path = cursor.getString(cursor.getColumnIndex(pojo[0]));
				     if (path != null && path.length() > 0) {
				    	 mPhotoPath = path;
				      }
//				startImageAction(uri, 200, 200,
//						ChatConstants.REQUESTCODE_UPLOADAVATAR_CROP, true);
				CustomGalleryBean galleryBean = new CustomGalleryBean(0, 0, false, mPhotoPath, mPhotoPath);

				Intent intent2 = new Intent(mContext,
						CropImageActivity.class);
				intent2.putExtra(CropImageActivity.INTENT_GALLERY_IMAGE_TO_CROP, galleryBean);
				startActivityForResult(intent2, ChatConstants.REQUESTCODE_UPLOADAVATAR_CROP);
			} else {
				ShowToast("照片获取失败");
			}
			return;
		
		}
//		if (requestCode == ChatConstants.REQUESTCODE_UPLOADAVATAR_CROP) {
//			// TODO sent to crop
//			if (avatorPop != null) {
//				avatorPop.dismiss();
//			}
//			if (intent == null) {
//				// Toast.makeText(this, "取消选择", Toast.LENGTH_SHORT).show();
//				return;
//			} else {
//				saveCropAvator(intent);
//			}
//		}
		if (requestCode == REQUEST_CODE_CITY) { // 城市
			AddressBean addressBean = intent
					.getParcelableExtra(RESULT_SELECT_CITY);
			if (addressBean == null) {
				return;
			}
			// 根据传递过来的城市ID获取得到其省份
			int cityProId = new CityDao(mContext).getCityByCityId(
					addressBean.getCityId()).getProvince_id();
			Province province= new ProvinceDao(mContext).getProvinceByCityProId(
					cityProId);
			String proName = province == null ? "" : province.getName();
			city_name.setText(proName + "　" + addressBean.getCityName());

			cityId = addressBean.getCityId();
			
			region_name.setText("");
			regionId = 0;
			return;
		}
		if (requestCode == REQUEST_CODE_REGION) { // 区县
			RegionBean regionBean = intent
					.getParcelableExtra(RegionListActivity.REGION_BEAN);

			if (regionBean == null) {
				return;
			}
			region_name.setText(regionBean.getName());
			regionId = regionBean.getId();
			return;
		}
	}

	/**
	 * 更换头像
	 */
	@SuppressLint("SimpleDateFormat")
	private void changeIcon(String value, int number) {
		if (number == 0) { // 拍照
//			ShowLog("点击拍照");
//			// TODO Auto-generated method stub
//			File dir = new File(ChatConstants.MyAvatarDir);
//			if (!dir.exists()) {
//				dir.mkdirs();
//			}
//			// 原图
//			File file = new File(dir,
//					new SimpleDateFormat("yyMMddHHmmss").format(new Date()));
//			filePath = file.getAbsolutePath();    // 获取相片的保存路径
//			Uri imageUri = Uri.fromFile(file);
//
//			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//			intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//			startActivityForResult(intent,
//					ChatConstants.REQUESTCODE_UPLOADAVATAR_CAMERA);
			java.util.Date now = new java.util.Date();
			String fileName = FastDateFormat
					.getInstance("'IMG'_yyyyMMdd_HHmmss").format(now)
					+ ".jpg";

			File mPhotoFile;

			// 文件路径
			mPhotoPath = JConstants.IMAGE_CACHE
					+ fileName;
			try {
				mPhotoFile = new File(mPhotoPath);
				if (!mPhotoFile.exists()) {
					FileUtil.createFile(mPhotoFile, true);
				}
	
//				ContentValues values = new ContentValues();
//				values.put(MediaStore.Images.Media.TITLE,
//						fileName);
//				values.put(MediaStore.Images.Media.DESCRIPTION,
//						"Image capture by camera");
//				values.put(MediaStore.Images.Media.MIME_TYPE,
//						"image/jpeg");
				// Uri imageUri =
				// mContext.getContentResolver().insert(
				// MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
				// values);
				Intent intent = new Intent(
						MediaStore.ACTION_IMAGE_CAPTURE);
				intent.putExtra("mPhotoPath", mPhotoPath);
				intent.putExtra(MediaStore.EXTRA_OUTPUT,
						Uri.fromFile(mPhotoFile));
				intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY,
						1);
				startActivityForResult(
								intent,
								ChatConstants.REQUESTCODE_UPLOADAVATAR_CAMERA);
		
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {      // 选择照片
			ShowLog("点击相册");
			List<Intent> targets = new ArrayList<Intent>();
			Intent intent = new Intent();
			intent.setType("image/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);
			intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
			intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
					"image/*");
			List<ResolveInfo> candidates = getPackageManager().queryIntentActivities(intent, 0);

			for (ResolveInfo candidate : candidates) {
			  String packageName = candidate.activityInfo.packageName;
			  if (!packageName.equals("com.google.android.apps.photos") && !packageName.equals("com.google.android.apps.plus") && !packageName.equals("com.android.documentsui")) {
			      Intent iWantThis = new Intent();
			      iWantThis.setType("image/*");
			      iWantThis.setAction(Intent.ACTION_GET_CONTENT);
			      iWantThis.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
			      iWantThis.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
							"image/*");
			      iWantThis.setPackage(packageName);
			      targets.add(iWantThis);
			    }
			}
			Intent chooser = Intent.createChooser(targets.remove(0), "Select Picture");
			chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, targets.toArray(new Parcelable[targets.size()]));
			startActivityForResult(chooser, ChatConstants.REQUESTCODE_UPLOADAVATAR_LOCATION);
		}
	}

	@Override
	public void onListItemSelected(String value, int number) {
		// TODO Auto-generated method stub
		changeIcon(value, number);
	}

}
