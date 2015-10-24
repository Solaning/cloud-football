package com.kinth.football.ui.mine;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.kinth.football.R;
import com.kinth.football.adapter.FeedbackGridViewAdapter;
import com.kinth.football.bean.FeedbackBean;
import com.kinth.football.bean.UploadImage;
import com.kinth.football.chat.ChatConstants;
import com.kinth.football.friend.CustomGalleryActivity2;
import com.kinth.football.friend.MomentsPreviewActivity;
import com.kinth.football.friend.PickedImage;
import com.kinth.football.manager.UserManager;
import com.kinth.football.network.AsyncFileUpload;
import com.kinth.football.network.NetWorkManager;
import com.kinth.football.network.AsyncFileUpload.FileCallback;
import com.kinth.football.picture.PickPicture;
import com.kinth.football.ui.ActivityBase;
import com.kinth.football.util.DialogUtil;
import com.kinth.football.util.PictureUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import eu.inmite.android.lib.dialogs.IListDialogListener;
import eu.inmite.android.lib.dialogs.ListDialogFragment;
/**
 * 反馈
 * @author zyq
 *
 */
public class FeedbackActivity extends ActivityBase implements IListDialogListener {
	private static final int REQUEST_CODE_PICK_PHOTO = 9979;//选图片的请求码
	private static final int REQUEST_CODE_PREVIEW = 9980;//预览图片请求码
	
	@ViewInject(R.id.edt_feedback_content)
	private EditText edt_feedback_content;
	
	
	@ViewInject(R.id.gridGallery)
	private GridView gridGallery;
	
	@ViewInject(R.id.btn_commit)
	private Button btn_commit;
	
	@OnClick(R.id.btn_commit)
	public void fun_1(View v){
		if (TextUtils.isEmpty(edt_feedback_content.getText().toString())) {
			ShowToast("请输入问题和意见！");
		}else {
		uploading = true;//正在上传
		proDialog = ProgressDialog.show(mContext, "提示", "正在发送,请稍候...", false, false);
		if (pickedImages!=null && pickedImages.size()>1) {//有图片的时候
//			压缩图片生成要上传的图片数据
			new CompressBitmapTask().execute(pickedImages);
		}
		else {//没有图片的时候
    		FeedbackBean feedbackBean = new FeedbackBean();
    		feedbackBean.setTitle("暂无");
    		feedbackBean.setDescription(edt_feedback_content.getText().toString());
			CreateFeedback(feedbackBean);
		}
		}
	}
	
	private ArrayList<PickedImage> pickedImages = new ArrayList<PickedImage>();
	private FeedbackGridViewAdapter adapter;
	private static final int UPLOAD_RETRY_LIMIT = 3;//失败重试次数
	
	private String mPhotoPath;
	private ProgressDialog proDialog;
	
	private boolean uploading =false;//是否正在上传
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feedback);
		ViewUtils.inject(this);
		
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

		initTopBarForLeft("反馈");
		setTheme(R.style.DefaultLightTheme);
		
		pickedImages.add(new PickedImage(Integer.MAX_VALUE, FeedbackGridViewAdapter.THIS_IS_PLUS));
		adapter = new FeedbackGridViewAdapter(mContext, pickedImages);
		gridGallery.setAdapter(adapter);
		gridGallery.setOnItemClickListener(new OnItemClickListener() {//点击图片或加号

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				String path = adapter.getItem(position);
				if(FeedbackGridViewAdapter.THIS_IS_PLUS.equals(path)){//加号
					ListDialogFragment
							.createBuilder(mContext,
									getSupportFragmentManager()).setTitle("发布动态")
							.setItems(new String[] { "拍照", "选择相册" })
							.hideDefaultButton(true).show();
				}else{//发动态点图片预览
					//预览前把加号“+”去掉
					Intent intent = new Intent(mContext,MomentsPreviewActivity.class);
					int size = pickedImages.size();
					if(size <= 9 && pickedImages.get(size - 1).getPath().equals(FeedbackGridViewAdapter.THIS_IS_PLUS)){//小于或者刚好为9个，并且最后一个是“+”
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
	/**
	 * 异步压缩图片
	 * @author Sola
	 *
	 */
	class CompressBitmapTask extends AsyncTask<ArrayList<PickedImage>,Void,Boolean>{

		@Override
		protected Boolean doInBackground(ArrayList<PickedImage>... arg0) {
			for(PickedImage item : pickedImages){//更新上传图片路径为压缩后的图片路径
				if(!item.getPath().equals(FeedbackGridViewAdapter.THIS_IS_PLUS)){//舍弃加号
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
			for(final PickedImage item : pickedImages){
				if(!item.getPath().equals(FeedbackGridViewAdapter.THIS_IS_PLUS)){//舍弃加号
					uploadMap.put(item.getPath(), new UploadImage(item.getPath(),"TODO"));//TODO
					upload2(uploadMap,item);
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
		        		DialogUtil.dialogDismiss(proDialog);
		        		Toast.makeText(mContext, "反馈失败", Toast.LENGTH_LONG).show();
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
		        	if(hasDone){//上传完成
		        		FeedbackBean feedbackBean = new FeedbackBean();
		        		feedbackBean.setTitle("暂无");
		        		feedbackBean.setDescription(edt_feedback_content.getText().toString());
		        		List<String> urlList = new ArrayList<String>();
	        			for(Map.Entry<String, UploadImage> entry : uploadMap.entrySet()){
	        				urlList.add(entry.getValue().getUrl());
	        			}
	        			feedbackBean.setUrlList(urlList);
	        			/**
	        			 * 反馈
	        			 */
	        			CreateFeedback(feedbackBean);
		        	}
		        
				}
			}
		});
	}
	private void CreateFeedback(FeedbackBean feedbackBean){

		NetWorkManager.getInstance(mContext).createFeedback(UserManager.getInstance(mContext).getCurrentUser()
							.getToken(), feedbackBean, 	new Listener<Void>() {

			@Override
			public void onResponse(Void response) {
				DialogUtil.dialogDismiss(proDialog);
                ShowToast("反馈成功");
                new Handler().postDelayed(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
				          finish();
					}
				}, 1500);
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				DialogUtil.dialogDismiss(proDialog);
				ShowToast("反馈失败");
			}
		});

	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if(resultCode != RESULT_OK){
			return;
		}
		if (requestCode == REQUEST_CODE_PICK_PHOTO) {//加号“+”，新加入图片
			ArrayList<PickedImage> addImages = intent.getParcelableArrayListExtra(CustomGalleryActivity2.INTENT_IMG_PATH_ARRAY);
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
				pickedImages.clear();
				pickedImages.add(new PickedImage(Integer.MAX_VALUE, FeedbackGridViewAdapter.THIS_IS_PLUS));
				adapter.notifyDataSetChanged();
				return;
			}else {
				pickedImages.clear();
				pickedImages = imageResult;
				if (pickedImages.size() < 9 && !pickedImages.get(pickedImages.size() - 1).getPath().equals(FeedbackGridViewAdapter.THIS_IS_PLUS)) {// 小于9张图片，添加“+”
					pickedImages.add(new PickedImage(Integer.MAX_VALUE, FeedbackGridViewAdapter.THIS_IS_PLUS));
				}
				adapter.setPickedImage(pickedImages);
				adapter.notifyDataSetChanged();
				return;
			}
		}
		if(requestCode == ChatConstants.REQUESTCODE_UPLOADAVATAR_CAMERA){//拍照后返回
			ArrayList<PickedImage> imageResult = new ArrayList<PickedImage>();
			imageResult.add(new PickedImage(0, mPhotoPath));
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
