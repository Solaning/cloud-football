package com.kinth.football.friend;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kinth.football.R;
import com.kinth.football.bean.UploadImage;
import com.kinth.football.bean.moments.Sharing;
import com.kinth.football.bean.moments.SharingRequest;
import com.kinth.football.chat.ChatConstants;
import com.kinth.football.config.MomentsTypeEnum;
import com.kinth.football.eventbus.bean.GenMomentsSharingEvent;
import com.kinth.football.manager.UserManager;
import com.kinth.football.network.AsyncFileUpload;
import com.kinth.football.network.NetWorkManager;
import com.kinth.football.network.AsyncFileUpload.FileCallback;
import com.kinth.football.picture.PickPicture;
import com.kinth.football.ui.BaseActivity;
import com.kinth.football.util.DialogUtil;
import com.kinth.football.util.ErrorCodeUtil;
import com.kinth.football.util.PictureUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import de.greenrobot.event.EventBus;
import eu.inmite.android.lib.dialogs.IListDialogListener;
import eu.inmite.android.lib.dialogs.ListDialogFragment;

/**
 * 发布动态--图片与文字
 * 
 * @author Sola
 */
@ContentView(R.layout.activity_moments_publish)
public class MomentsPublishActivity extends BaseActivity implements IListDialogListener{
	private static final int REQUEST_CODE_PICK_PHOTO = 9979;//选图片的请求码
	private static final int REQUEST_CODE_PREVIEW = 9980;//预览图片请求码
	public static final String INTENT_PUBLISH_ACTIVE_FOR_RESULT = "INTENT_PUBLISH_ACTIVE_FOR_RESULT";//发动态后的结果返回
	public static final String INTENT_PUBLISH_ACTIVE_FOR_RESULT_ACTIVE = "INTENT_PUBLISH_ACTIVE_FOR_RESULT_ACTIVE";//发动态后的该动态
	private static final int UPLOAD_RETRY_LIMIT = 3;//失败重试次数
	private MomentsPublishAdapter adapter;
	private boolean uploading =false;//是否正在上传
	private ProgressDialog proDialog;

	private ArrayList<PickedImage> pickedImages = null;

	@ViewInject(R.id.nav_left)
	private View back;// 返回

	@ViewInject(R.id.nav_title)
	private TextView title;// 标题

	@ViewInject(R.id.nav_right_btn)
	private Button right;// 右侧完成按钮
	
	@ViewInject(R.id.et_thought_of_this_time)
	private EditText thought;

	@ViewInject(R.id.gridGallery)
	private GridView gridGallery;

	@OnClick(R.id.nav_left)
	public void fun_1(View v) {// 退出
		dialog();
	}
	
	@SuppressWarnings("unchecked")
	@OnClick(R.id.nav_right_btn)
	public void fun_2(View v){//发布动态
		//条件检查，排除只有一个“+”的情况
		if(pickedImages == null || pickedImages.size() == 0 || (pickedImages.size() == 1 && pickedImages.get(0).getPath().equals(MomentsPublishAdapter.THIS_IS_PLUS))){
			//木有图片
			String content = thought.getText().toString();
			if(TextUtils.isEmpty(content)){//看看是否有内容
				return;
			}else{
				proDialog = ProgressDialog.show(mContext, "提示", "正在发送,请稍候...", false, false);
	    		SharingRequest request = new SharingRequest();
	    		request.setComment(content);
				request.setType(MomentsTypeEnum.TEXT.getValue());
				/**
    			 * 发动态
    			 */
    			sendSharingRequest(request);
			}
		}else{
			uploading = true;//正在上传
			proDialog = ProgressDialog.show(mContext, "提示", "正在发送,请稍候...", false, false);
			//压缩图片生成要上传的图片数据
			new CompressBitmapTask().execute(pickedImages);
		}
		
	}
	
	/**
	 * 异步压缩图片
	 * @author Sola
	 *
	 */
	class CompressBitmapTask extends AsyncTask<ArrayList<PickedImage>,Void,Boolean>{

		@Override
		protected Boolean doInBackground(ArrayList<PickedImage>... arg0) {
			for(PickedImage item : pickedImages){//更新上传图片路径为压缩后的图片路径
				if(!item.getPath().equals(MomentsPublishAdapter.THIS_IS_PLUS)){//舍弃加号
					try {
						item.setPath(PictureUtil.getThumbUploadPath(item.getPath(),getResources().getDimensionPixelSize(R.dimen.BitmapMaxWidth)));
					} catch (Exception e) {
						e.printStackTrace();
						return false;
					}
				}
			}
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if(!result){
				DialogUtil.dialogDismiss(proDialog);
				Toast.makeText(mContext, "发送失败：压缩图片失败", Toast.LENGTH_LONG).show();
				return;
			}
			Map<String, UploadImage> uploadMap = new LinkedHashMap<String,UploadImage>();//服务器上的url
			int i = 0;
			for(PickedImage item : pickedImages){
				if(!item.getPath().equals(MomentsPublishAdapter.THIS_IS_PLUS)){//舍弃加号
					uploadMap.put(item.getPath(), new UploadImage(item.getPath(),"TODO"));//TODO
					upload2(uploadMap, item);
				}
			}
		}
	}
	
	private void upload2(final Map<String, UploadImage> uploadMap,final PickedImage item){
		AsyncFileUpload.asynFileUploadToServer(item.getPath(), new FileCallback() {
			@Override
			public void fileUploadCallback(String fileUrl) {
				if (TextUtils.isEmpty(fileUrl)) {   //上传失败
		        	if(!uploading){
		        		return;
		        	}
		        	uploadMap.get(item.getPath()).setSucceed(false);
		        	uploadMap.get(item.getPath()).autoAddFailTime();//失败次数自动加1
		        	if(uploadMap.get(item.getPath()).getFailTime() < UPLOAD_RETRY_LIMIT){//重试
		        		upload2(uploadMap,item);
		        	}else{
		        		uploading = false;//失败了
		        		onUploadFailed();
		        	}
				}else {
		        	if(!uploading){
		        		return;
		        	}
		        	uploadMap.get(item.getPath()).setUrl(fileUrl);
		        	uploadMap.get(item.getPath()).setSucceed(true);
		        	boolean hasDone = true;//判断图片是否都上传完成
		        	for (Map.Entry<String, UploadImage> entry : uploadMap.entrySet()) {
		        		if(entry.getValue().isSucceed()){
		        			continue;
		        		}else{//还有一个没有传完
		        			hasDone = false;
		        			break;
		        		}
		        	}
		        	if(hasDone){//上传完成，发动态
		        		SharingRequest request = new SharingRequest();
		        		request.setComment(thought.getText().toString());
		        		List<String> imageUrls = new ArrayList<String>();
	        			for(Map.Entry<String, UploadImage> entry : uploadMap.entrySet()){
	        				imageUrls.add(entry.getValue().getUrl());
	        			}
	        			request.setImageUrls(imageUrls);
	        			request.setType(MomentsTypeEnum.IMAGE.getValue());
	        			/**
	        			 * 发动态
	        			 */
	        			sendSharingRequest(request);
		        			
//		        			new AsyncNetworkManager().genActiveTimeLine(mContext, keyValue, new genActiveTimeLineCallBack() {
//								
//								@Override
//								public void onGenActiveTimeLineCallBack(int rtn, int activeId) {
//									if (CustomProgressDialogUtil
//											.stopProgressDialog()) {
//										if(rtn == 1){//把图片重命名拷贝
//											Active thisActive = new Active();
//											thisActive.setActiveId(activeId);
//											thisActive.setMobile(mobile);
//											thisActive.setContent(thought.getText().toString());
//											thisActive.setActiveType(ActiveType.PersonalActive.getValue());
//											thisActive.setCreateTime(DateUtil.getStringDateAfterSecond(15));//时间延后15s
//											thisActive.setIconUrl(MainAccountUtil.getCurrentAccount(mContext).getIconURL());
//											thisActive.setNickName(MainAccountUtil.getCurrentAccount(mContext).getNickName());
//											thisActive.setPicUrl(sb.toString());
//											for(Map.Entry<String, UploadImage> entry : uploadMap.entrySet()){
//						        				File source = new File(entry.getValue().getRawPath());
//						        				File target = new File(APPConstant.IMAGE_PERSISTENT_CACHE + File.separator + Md5Util.md5s(entry.getValue().getUrl()));
//						        				if(source.exists()){
//						        					FileUtil.nioTransferCopy(source, target);
//						        				}
//						        			}
//											
//											Intent data = new Intent();
//							        		data.putExtra(INTENT_PUBLISH_ACTIVE_FOR_RESULT, true);//发送成功
//							        		data.putExtra(INTENT_PUBLISH_ACTIVE_FOR_RESULT_ACTIVE, thisActive);//成功后的动态id，可以直接插入数据库
//							        		setResult(RESULT_OK, data);
//							        		finish();
//							        		rightOutFinishAnimation();
//										}else{//发送失败
//											onUploadFailed();
//										}
//									}
//								}
//							});
//		        			onUploadFailed();
		        	}
		        
				}
			}
		});
	}
	
	/**
	 * 网络请求发动态
	 * @param request
	 */
	private void sendSharingRequest(SharingRequest request){
		NetWorkManager.getInstance(mContext).genMomentsSharing(request, UserManager.getInstance(mContext).getCurrentUser().getToken(), 
				new Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						DialogUtil.dialogDismiss(proDialog);
						Gson gson = new Gson();
						Sharing sharing = gson.fromJson(
								response.toString(),
								new TypeToken<Sharing>() {
								}.getType());
						if (sharing == null){
							ShowToast("发送失败");
							return;
						}
						ShowToast("发送成功");
						EventBus.getDefault().post(new GenMomentsSharingEvent(sharing));
						finish();
					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(
							VolleyError error) {
						DialogUtil.dialogDismiss(proDialog);
						if (!NetWorkManager.getInstance(mContext)
								.isNetConnected()) {
							ShowToast("当前网络不可用，请稍后重试");
						} else if (error.networkResponse == null) {
							onUploadFailed();
						} else if(error.networkResponse.statusCode == 400){
							onUploadFailed();
						}else if (error.networkResponse.statusCode == 401) {
							ErrorCodeUtil.ErrorCode401(mContext);
						}
					}
				});
	}
	
	private void onUploadFailed(){
		DialogUtil.dialogDismiss(proDialog);
		Toast.makeText(mContext, "发送失败", Toast.LENGTH_LONG).show();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		pickedImages = getIntent().getParcelableArrayListExtra(
				CustomGalleryActivity2.INTENT_IMG_PATH_ARRAY);
		if (pickedImages == null || pickedImages.size() == 0) {// 没有数据
//			return;
			//只是发布 文字
			right.setEnabled(true);
			right.setText("发送");
			thought.setFocusable(true);
			thought.requestFocus(); //主动获得焦点
		}else {
			right.setEnabled(false);
			right.setText("发送");
			gridGallery.setFastScrollEnabled(false);

		adapter = new MomentsPublishAdapter(mContext, pickedImages);
		gridGallery.setAdapter(adapter);
		right.setEnabled(true);//有选择图片
		thought.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String content = s.toString();
				if (TextUtils.isEmpty(content)) {// 没有其他输入，替换成原来的列表
					if(pickedImages == null || pickedImages.size() == 0 || (pickedImages.size() == 1 && pickedImages.get(0).getPath().equals(MomentsPublishAdapter.THIS_IS_PLUS))){
						right.setEnabled(false);
					}
				} else {// 有输入内容
					right.setEnabled(true);
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		});
		gridGallery.setOnItemClickListener(new OnItemClickListener() {//点击图片或加号

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				String path = adapter.getItem(position);
				if(MomentsPublishAdapter.THIS_IS_PLUS.equals(path)){//加号
//					Intent intent = new Intent(mContext,CustomGalleryActivity2.class);
//					intent.putExtra(CustomGalleryActivity2.INTENT_PICKED_IMG_CAPACITY, adapter.getCount() - 1);//已选图片张数
//					startActivityForResult(intent, REQUEST_CODE_PICK_PHOTO);
					ListDialogFragment
							.createBuilder(mContext,
									getSupportFragmentManager()).setTitle("发布动态")
							.setItems(new String[] { "拍照", "选择相册" })
							.hideDefaultButton(true).show();
				}else{//发动态点图片预览
					//预览前把加号“+”去掉
					Intent intent = new Intent(mContext,MomentsPreviewActivity.class);
					int size = pickedImages.size();
					if(size <= 9 && pickedImages.get(size - 1).getPath().equals(MomentsPublishAdapter.THIS_IS_PLUS)){//小于或者刚好为9个，并且最后一个是“+”
						ArrayList<PickedImage> subList = new ArrayList<PickedImage>();
						for (int i = 0; i < size - 1; i++) {// 数量不多，无关性能
							PickedImage item = new PickedImage(pickedImages.get(i).getPosition(), pickedImages.get(i).getPath());
							subList.add(item);
						}
						intent.putParcelableArrayListExtra(MomentsPreviewActivity.INTENT_IMAGES_TO_PREVIEW, subList);//传递需要预览的图片列表
					}else{
						intent.putParcelableArrayListExtra(MomentsPreviewActivity.INTENT_IMAGES_TO_PREVIEW, pickedImages);//传递需要预览的图片列表
					}
					intent.putExtra(MomentsPreviewActivity.INTENT_IMAGE_POSITION, position);
					startActivityForResult(intent, REQUEST_CODE_PREVIEW);
				}
			}
		});
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if(resultCode != RESULT_OK){
			return;
		}
		if (requestCode == REQUEST_CODE_PICK_PHOTO) {//加号“+”，新加入图片
			ArrayList<PickedImage> addImages = intent.getParcelableArrayListExtra(CustomGalleryActivity2.INTENT_IMG_PATH_ARRAY);
			if(addImages == null || addImages.size() == 0){
				return;
			}
			right.setEnabled(true);
			adapter.addPickedImage(addImages);
			adapter.notifyDataSetChanged();
			return;
		}
		if(requestCode == REQUEST_CODE_PREVIEW){//预览图片后返回
			ArrayList<PickedImage> imageResult = intent.getParcelableArrayListExtra(MomentsPreviewActivity.INTENT_PREVIEW_IMAGES_FOR_RESULT);
			if(imageResult == null){
				return;
			}
			if(imageResult.size() == 0 ){//选择的图片都删完
				if(TextUtils.isEmpty(thought.getText().toString())){//如果还有文字就可以发送
					right.setEnabled(false);
				}else{
					right.setEnabled(true);
				}
				pickedImages.clear();
				pickedImages.add(new PickedImage(Integer.MAX_VALUE, MomentsPublishAdapter.THIS_IS_PLUS));
				adapter.notifyDataSetChanged();
				return;
			}else {
				pickedImages.clear();
				pickedImages = imageResult;
				if (pickedImages.size() < 9 && !pickedImages.get(pickedImages.size() - 1).getPath().equals(MomentsPublishAdapter.THIS_IS_PLUS)) {// 小于9张图片，添加“+”
					pickedImages.add(new PickedImage(Integer.MAX_VALUE, MomentsPublishAdapter.THIS_IS_PLUS));
				}
				adapter.setPickedImage(pickedImages);
				adapter.notifyDataSetChanged();
				return;
			}
		}
		if(requestCode == ChatConstants.REQUESTCODE_UPLOADAVATAR_CAMERA){//拍照后返回
			ArrayList<PickedImage> imageResult = new ArrayList<PickedImage>();
			imageResult.add(new PickedImage(0, mPhotoPath));
			right.setEnabled(true);
			adapter.addPickedImage(imageResult);
			adapter.notifyDataSetChanged();
			return;
		}
		if(requestCode == ChatConstants.REQUESTCODE_UPLOADAVATAR_LOCATION){//本地图库选择图片返回
			Uri uri = intent.getData();
			String imagePath = PickPicture.getPath(mContext, uri);
			if(PictureUtil.isImage(imagePath)){
				ArrayList<PickedImage> imageResult = new ArrayList<PickedImage>();
				imageResult.add(new PickedImage(0, imagePath));
				right.setEnabled(true);
				adapter.addPickedImage(imageResult);
				adapter.notifyDataSetChanged();
			}else{
				ShowToast("所选文件非图片");
				Log.e("选的非图片","imagePath = "+imagePath);
			}
			return;
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			dialog();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void dialog() {
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle("提示");
		builder.setMessage("退出此次编辑？");
		builder.setNegativeButton("取消",
				new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		builder.setPositiveButton("退出",
				new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						finish();
					}
				});
		builder.create().show();
	}
	
	private String mPhotoPath;//TODO

	@Override
	public void onListItemSelected(String value, int number) {
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
}
