package com.kinth.football.friend;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kinth.football.R;
import com.kinth.football.bean.moments.Comment;
import com.kinth.football.bean.moments.CommentRequest;
import com.kinth.football.bean.moments.SharingWithComments;
import com.kinth.football.eventbus.bean.CommentSharingEvent;
import com.kinth.football.manager.UserManager;
import com.kinth.football.network.NetWorkManager;
import com.kinth.football.util.DialogUtil;
import com.kinth.football.util.ErrorCodeUtil;
import com.kinth.football.util.PhotoUtil;
import com.kinth.football.util.PictureUtil;
import com.kinth.football.util.TimeUtil;
import com.kinth.football.util.UtilFunc;
import com.kinth.football.util.ViewHolder;
import com.kinth.football.view.MyGridView;
import com.kinth.football.view.SmiliesEditText;
import com.kinth.football.view.dialog.DeleteMomentsSharingDialog;
import com.kinth.football.view.dialog.DeleteMomentCommentDialog;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import de.greenrobot.event.EventBus;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.FragmentActivity;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 朋友圈的适配器
 * 
 * @author Sola
 */
public class MomentsAdapter extends BaseAdapter {
	private Context mContext;
	private List<SharingWithComments> sharingList;
	private LayoutInflater inflater;
	private String currentUserId = UserManager.getInstance(mContext).getCurrentUser().getPlayer().getUuid();
	private static Object asyncLock = new Object();//从数据库读数据防止冲突
	
	public MomentsAdapter(Context mContext) {
		super();
		this.mContext = mContext;
		inflater = LayoutInflater.from(mContext);
		sharingList = new ArrayList<SharingWithComments>();
	}

	public void setSharingWithCommentsList(
			List<SharingWithComments> sharingList1) {
		if(sharingList1 == null){
			return;
		}
		this.sharingList = sharingList1;
		notifyDataSetChanged();
	}

	/**
	 * 插入到置顶动态的后面
	 */
	public void addFirstSharingWithCommentsList(SharingWithComments sharing){
		if(this.sharingList == null){
			this.sharingList = new ArrayList<SharingWithComments>();
		}
		
		int start = 0;
		for (SharingWithComments sharingWithComments : sharingList) {
			if(sharingWithComments.getSharing().isOnTop()){
				start ++;
				continue;
			}
			break;
		}
		this.sharingList.add(start, sharing);
		notifyDataSetChanged();
	}
	
	/**
	 * 包含置顶的动态
	 * @return
	 */
	public List<SharingWithComments> getSharingWithCommentsWithTop() {
		return sharingList;
	}
	
	/**
	 * 把置顶的动态过滤掉
	 * @return
	 */
	public List<SharingWithComments> getSharingWithCommentsWithOutTop() {
		if(sharingList == null || sharingList.isEmpty()){
			return null;
		}else{
			int start = 0;
			for (SharingWithComments sharingWithComments : sharingList) {
				if(sharingWithComments.getSharing().isOnTop()){
					start ++;
					continue;
				}
				break;
			}
			return sharingList.subList(start, sharingList.size());
		}
	}

	@Override
	public int getCount() {
		return sharingList == null ? 0 : sharingList.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(
					R.layout.item_moments, parent, false);
		}
		// 用户头像 
		final ImageView userIcon = ViewHolder.get(convertView, R.id.iv_user_icon);
		// 发布者
		TextView publisher = ViewHolder.get(convertView, R.id.tv_publisher);
		// 动态内容
		TextView content = ViewHolder.get(convertView, R.id.tv_content);
		// 显示更多--->隐藏
		TextView more = ViewHolder.get(convertView, R.id.tv_more);
		// 显示图片--->隐藏
		MyGridView gridView = ViewHolder.get(convertView, R.id.gridView_pic);
		
		// 发布时间
		TextView publishTime = ViewHolder
				.get(convertView, R.id.tv_publish_time);
		
		TextView deleteSharing = ViewHolder.get(convertView, R.id.tv_delete_sharing);//删除按钮
		
		// 交互，点赞和评论按钮
		ImageView interaction = ViewHolder.get(convertView,
				R.id.iv_interaction);
		
		//点赞的布局
		final LinearLayout clickPraise = ViewHolder.get(convertView, R.id.llt_like_nickname);
		// likeIcon
		final ImageView likeIcon = ViewHolder.get(convertView, R.id.iv_likeicon);
		// 集赞的人昵称
		final TextView nameOfLike = ViewHolder.get(convertView,
				R.id.tv_nick_name_of_like);
		
		// 评论的布局，包含点赞
		final LinearLayout commentLayout = ViewHolder.get(convertView,
						R.id.llt_comment_layout);
		// 评论
		final TextView comments = ViewHolder.get(convertView, R.id.tv_comments);

		final SharingWithComments activeWithComments = sharingList.get(position);

		// -------------跟动态有关的-----------------
		// 加载头像
		String playerIconUrl = activeWithComments.getSharing().getPlayer() == null ? "" : activeWithComments.getSharing().getPlayer().getPicture();
		if(TextUtils.isEmpty(playerIconUrl)){//自身没有设置图片
			ImageLoader.getInstance().displayImage(null, userIcon,new DisplayImageOptions.Builder()
					.showImageOnLoading(
							R.drawable.icon_head)
					.showImageForEmptyUri(R.drawable.icon_head)
					.showImageOnFail(R.drawable.icon_head) // 默认头像
					.cacheInMemory(true).cacheOnDisk(true).build());
		}else {
			PictureUtil.UILShowImage(mContext, userIcon, PhotoUtil.getAllPhotoPath(playerIconUrl));
		}
		userIcon.setOnClickListener(new OnClickListener() {//头像点击后预览头像
			
			@Override
			public void onClick(View arg0) {
				//跳到该用户发表内容列表
				Intent intent = new Intent(mContext, MomentPersonalZoneActivity.class);
				intent.putExtra(MomentPersonalZoneActivity.INTENT_PLAYER_UUID, activeWithComments.getSharing().getPlayer().getUuid());
				mContext.startActivity(intent);
			}
		});

		publisher.setText(StringUtils.defaultIfBlank(activeWithComments.getSharing().getPlayer().getName(),""));
		publishTime.setText(TimeUtil.TransTime(activeWithComments.getSharing().getDate(), true));
		if(currentUserId.equals(activeWithComments.getSharing().getPlayer().getUuid())){//删除
			deleteSharing.setVisibility(View.VISIBLE);
			deleteSharing.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					DeleteMomentsSharingDialog deleteMomentsSharingDialog = DeleteMomentsSharingDialog.newInstance("提示", "确定删除吗？");
					deleteMomentsSharingDialog.setListener(new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							final ProgressDialog proDialog = ProgressDialog.show(mContext, "提示", "请稍候...", false, false);
							NetWorkManager.getInstance(mContext).deleteMomentsSharing(activeWithComments.getSharing().getUuid(), UserManager.getInstance(mContext).getCurrentUser().getToken(),
									new Listener<Void>() {

										@Override
										public void onResponse(Void response) {
											synchronized (asyncLock) {
												sharingList.remove(position);
											}
											notifyDataSetChanged();
											DialogUtil.dialogDismiss(proDialog);
										}
									}, new ErrorListener() {

										@Override
										public void onErrorResponse(VolleyError error) {
											DialogUtil.dialogDismiss(proDialog);
											if (!NetWorkManager.getInstance(mContext)
													.isNetConnected()) {
												Toast.makeText(mContext, "当前网络不可用，请稍后重试", Toast.LENGTH_LONG).show();
											} else if (error.networkResponse == null) {
												Toast.makeText(mContext, "删除失败，请稍后重试", Toast.LENGTH_LONG).show();
											} else if (error.networkResponse.statusCode == 401) {
												ErrorCodeUtil.ErrorCode401(mContext);
											} else if (error.networkResponse.statusCode == 403){
												Toast.makeText(mContext, "删除失败：没有权限", Toast.LENGTH_LONG).show();
											} else if (error.networkResponse.statusCode == 404) {
												Toast.makeText(mContext, "删除失败：动态找不到", Toast.LENGTH_LONG).show();
											} else {
												Toast.makeText(mContext, "删除失败，请稍后重试", Toast.LENGTH_LONG).show();
											}
										}
									});
						}

					});
					deleteMomentsSharingDialog.show(((FragmentActivity) mContext).getSupportFragmentManager(), "DeleteMomentsSharingDialog");
				}
			});
		}else{
			deleteSharing.setVisibility(View.GONE);
		}
		content.setText(activeWithComments.getSharing().getComment());
		
		//朋友圈显示图片
		if(activeWithComments.getSharing().getImageUrls() == null || activeWithComments.getSharing().getImageUrls().size() == 0){//空
			gridView.setVisibility(View.GONE);
		}else {
			gridView.setVisibility(View.VISIBLE);
			final ActiveGalleryAdapter adapter = new ActiveGalleryAdapter(mContext, activeWithComments.getSharing().getImageUrls());
			if(activeWithComments.getSharing().getImageUrls().size() == 1){//只有一项时，去掉gridView限制
				gridView.setGravity(Gravity.LEFT);
				gridView.setNumColumns(1);
			}else{
				gridView.setGravity(Gravity.CENTER_HORIZONTAL);
				gridView.setNumColumns(GridView.AUTO_FIT);
			}
			gridView.setAdapter(adapter);
			gridView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					List<String> url = adapter.getItem(arg2);
					Intent intent = new Intent(mContext, MomentsGridPreviewActivity.class);
					intent.putStringArrayListExtra(MomentsGridPreviewActivity.INTENT_IMAGES_TO_PREVIEW, (ArrayList<String>) url);//图片url
					intent.putExtra(MomentsGridPreviewActivity.INTENT_IMAGE_POSITION, arg2);//当前加载位置
					mContext.startActivity(intent);
				}
			});
		}
		//-------------------end--------------------

		// ----------跟交互有关的--------------------
		//评论和点赞被混合在一起，需要逐条解开
		// 设置评论
		StringBuffer likeNameSB = new StringBuffer();
		final SpannableStringBuilder ssb = new SpannableStringBuilder();  //评论的人名和内容  用文字特效处理  人名蓝色 内容白色
		SpannableStringBuilder ssTemp = new SpannableStringBuilder();
		boolean isLikeByMe = false;//是否已like
		for(final Comment item : activeWithComments.getComments()){
			if(item.getPlayer() == null){
				continue;
			}
			if("COMMENT".equals(item.getType())){//该条是评论
				ssTemp.clear();
				final String nameWithColon = StringUtils.defaultIfEmpty(item.getPlayer().getName(), "") + ": ";
				final String comment = item.getComment();
				int start = nameWithColon.length();
				
				ssTemp.append(nameWithColon).append(comment)
					.append("\n");
				
				ssTemp.setSpan(new ClickableSpan(){

					@Override
					public void onClick(View widget) {//点击某一条评论
						//当前用户的评论  AlertDialog
						if(currentUserId.equals(item.getPlayer().getUuid())){
							final DeleteMomentCommentDialog deleteMommentCommentDialog = new DeleteMomentCommentDialog();  
							deleteMommentCommentDialog.setListener(new OnClickListener() {
								
								@Override
								public void onClick(View v) {
									deleteMommentCommentDialog.dismiss();
									//删除评论
									NetWorkManager.getInstance(mContext).deleteMomentsComment(item.getUuid(), UserManager.getInstance(mContext).getCurrentUser().getToken(), 
											new Listener<Void>() {

										@Override
										public void onResponse(Void response) {
											synchronized (activeWithComments.getComments()) {
												activeWithComments.getComments().remove(item);
											}
											notifyDataSetChanged();
										}
									}, new ErrorListener() {

										@Override
										public void onErrorResponse(VolleyError error) {
											if (!NetWorkManager.getInstance(mContext)
													.isNetConnected()) {
												Toast.makeText(mContext, "当前网络不可用，请稍后重试", Toast.LENGTH_LONG).show();
											} else if (error.networkResponse == null) {
												Toast.makeText(mContext, "删除失败，请稍后重试", Toast.LENGTH_LONG).show();
											} else if (error.networkResponse.statusCode == 401) {
												ErrorCodeUtil.ErrorCode401(mContext);
											} else if (error.networkResponse.statusCode == 403){
												Toast.makeText(mContext, "删除失败：没有权限", Toast.LENGTH_LONG).show();
											} else if (error.networkResponse.statusCode == 404) {
												Toast.makeText(mContext, "删除失败：评论找不到", Toast.LENGTH_LONG).show();
											} else {
												Toast.makeText(mContext, "删除失败，请稍后重试", Toast.LENGTH_LONG).show();
											}
										}
									});
									
								}
							});
							deleteMommentCommentDialog.show(((FragmentActivity) mContext).getSupportFragmentManager(), "DeleteMomentCommentDialog"); 
						}
					}
					
					@Override
					public void updateDrawState(TextPaint ds) {
						super.updateDrawState(ds);
						// 去掉下划线
						ds.setUnderlineText(false);
						ds.setColor(Color.WHITE); 
					}
					
					}, 0, ssTemp.length(), 0);
				ssTemp.setSpan(new ForegroundColorSpan(0xFF638793), 0, // 将评论者名变成其他颜色
						start, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				ssb.append(ssTemp);
				continue;
			}
			if("LIKE".equals(item.getType())){//该条是like
				likeNameSB.append(StringUtils.defaultIfEmpty(item.getPlayer().getName(), "")).append(", ");
				if(UserManager.getInstance(mContext).getCurrentUser().getPlayer().getUuid().equals(item.getPlayer().getUuid())){
					isLikeByMe = true;
				}
				continue;
			}
		}
		if(likeNameSB.length() > 0){
			String likeNameSetStr = likeNameSB.substring(0, likeNameSB.length() - 2);// 去掉最后一个（,）逗号  +空格
			clickPraise.setVisibility(View.VISIBLE);
			likeIcon.setVisibility(View.VISIBLE);
			nameOfLike.setText(likeNameSetStr);
			if(isLikeByMe){
				likeIcon.setImageResource(R.drawable.iconlike_xin_byme);
			}else{
				likeIcon.setImageResource(R.drawable.iconlike_xin);
			}
		}else {
			clickPraise.setVisibility(View.GONE);
			likeIcon.setVisibility(View.GONE);
			nameOfLike.setText("");
		}
		comments.setMovementMethod(LinkMovementMethod.getInstance()); 
		if(ssb.length() > 0){
			SpannableStringBuilder ssb_ = (SpannableStringBuilder) ssb.subSequence(0, ssb.length() - 1);//去掉最后一个换行号
			comments.setText(ssb_);
			comments.setHighlightColor(0xaa1b252b);//评论按下去的颜色
			comments.setVisibility(View.VISIBLE);
		}else{
			comments.setText(ssb);
			comments.setVisibility(View.GONE);
		}
		if(ssb.length() > 0 || likeNameSB.length() > 0){
			commentLayout.setVisibility(View.VISIBLE);
		} else {
			commentLayout.setVisibility(View.GONE);
		}

		// ----------跟操作有关的----------------------
		interaction.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				View view = LayoutInflater.from(mContext).inflate(
						R.layout.popupwindow_friend_comment_layout, null);
				LinearLayout commentLike = (LinearLayout) view
						.findViewById(R.id.llt_comment_like);
				LinearLayout commentWrite = (LinearLayout) view
						.findViewById(R.id.llt_comment_write);
				// 创建PopupWindow对象
				final PopupWindow popupWindow = new PopupWindow(view, UtilFunc.dip2px(mContext, 150),
						UtilFunc.dip2px(mContext, 35), false);
				popupWindow.setBackgroundDrawable(new BitmapDrawable());
				// 设置点击窗口外边窗口消失
				popupWindow.setOutsideTouchable(true);
				// 设置此参数获得焦点，否则无法点击
				popupWindow.setFocusable(true);
				// 点击事件
				commentLike.setOnClickListener(new OnClickListener() {// 点赞

							@Override
							public void onClick(View v) {
								if (popupWindow != null
										&& popupWindow.isShowing()) {
									// 隐藏窗口，如果设置了点击窗口外消失即不需要此方式隐藏
									popupWindow.dismiss();
								}
								
								StringBuffer likeNameSB = new StringBuffer();
								boolean isLikeByMe = false;//是否已like
								for(Comment item : activeWithComments.getComments()){
									if(item.getPlayer() == null){
										continue;
									}
									if("LIKE".equals(item.getType())){//该条是like
										likeNameSB.append(StringUtils.defaultIfEmpty(item.getPlayer().getName(), "")).append(", ");
										if(UserManager.getInstance(mContext).getCurrentUser().getPlayer().getUuid().equals(item.getPlayer().getUuid())){
											isLikeByMe = true;
										}
										continue;
									}
								}
								if (isLikeByMe) {
									Toast.makeText(mContext, "已经赞过", Toast.LENGTH_SHORT).show();
									likeIcon.setImageResource(R.drawable.iconlike_xin_byme);
									return;
								}
								//调接口点赞
								CommentRequest request = new CommentRequest();
								request.setSharingUuid(activeWithComments.getSharing().getUuid());
								request.setType("LIKE");
								NetWorkManager.getInstance(mContext).commentSharing(request, UserManager.getInstance(mContext).getCurrentUser().getToken(), 
										new Listener<JSONObject>() {

											@Override
											public void onResponse(
													JSONObject response) {
												Gson gson = new Gson();
												Comment comment = gson.fromJson(response.toString(), new TypeToken<Comment>(){}.getType());
												if(comment == null){
													return;
												}
												// 点赞成功，更新数据库和页面
//												if (commentLayout
//														.getVisibility() == View.GONE) {
//													commentLayout
//															.setVisibility(View.VISIBLE);
//												}
//												clickPraise.setVisibility(View.VISIBLE);
//												likeIcon.setVisibility(View.VISIBLE);
//												likeIcon.setImageResource(R.drawable.iconlike_xin_byme);
												
												sharingList.get(position).getComments().add(comment);
												notifyDataSetChanged();
//												nameOfLike.setText((TextUtils.isEmpty(nameOfLike.getText()) ? "" : nameOfLike.getText()+", ")
//														+ UserManager.getInstance(mContext).getCurrentUser().getPlayer().getName());
											}
										}, new ErrorListener() {

											@Override
											public void onErrorResponse(
													VolleyError error) {
												if (!NetWorkManager.getInstance(mContext)
														.isNetConnected()) {
													Toast.makeText(mContext, "当前网络不可用，请稍后重试", Toast.LENGTH_SHORT).show();
												} else if (error.networkResponse == null) {
													Toast.makeText(mContext, "点赞失败，请稍后重试", Toast.LENGTH_SHORT).show();
												} else if(error.networkResponse.statusCode == 400){//commentType is null
													Toast.makeText(mContext, "点赞失败，请稍后重试", Toast.LENGTH_SHORT).show();
												} else if(error.networkResponse.statusCode == 401) {
													ErrorCodeUtil.ErrorCode401(mContext);
												} else if(error.networkResponse.statusCode == 404){
													Toast.makeText(mContext, "服务器未找到该动态", Toast.LENGTH_SHORT).show();
												} else if(error.networkResponse.statusCode == 409){
													Toast.makeText(mContext, "已经赞过", Toast.LENGTH_SHORT).show();
												} else {
													Toast.makeText(mContext, "错误: 请稍后重试", Toast.LENGTH_SHORT).show();
												}
											}
										});
							}
						});
				commentWrite.setOnClickListener(new OnClickListener() {// 评论

							@Override
							public void onClick(View v) {
								if (popupWindow != null
										&& popupWindow.isShowing()) {
									// 隐藏窗口，如果设置了点击窗口外消失即不需要此方式隐藏
									popupWindow.dismiss();
								}
								comment(v, comments, commentLayout, activeWithComments, "");
							}
						});
				// 显示窗口
				popupWindow.showAsDropDown(v, -popupWindow.getWidth(),
						-(v.getHeight() + popupWindow.getHeight()) / 2);
			}
		});
		return convertView;
	}
	
	/**
	 * 评论
	 * @param v
	 * @param comments
	 * @param commentLayout
	 * @param sharingWithComments
	 * @param defaultComment 默认评论
	 * 
	 */
	private void comment(View v, final TextView comments,final LinearLayout commentLayout,
			final SharingWithComments sharingWithComments,String defaultComment){
		// 弹出输入的框
		View view = LayoutInflater
				.from(mContext)
				.inflate(R.layout.popupwindow_friend_comment_input, null);
		final SmiliesEditText inputComment = (SmiliesEditText) view
				.findViewById(R.id.et_input_comment);
		Button commentWrite = (Button) view
				.findViewById(R.id.bt_send_comment);
		inputComment.setText(defaultComment);
		inputComment
				.setOnTouchListener(new OnTouchListener() {

					@Override
					public boolean onTouch(View v,
							MotionEvent event) {
						switch (event.getAction()) {
						case MotionEvent.ACTION_DOWN:// 变成激活的状态
//							v.setBackgroundResource(R.drawable.input_bar_bg_active);
							break;
						}
						return false;
					}
				});
		// 创建PopupWindow对象
		final PopupWindow popInputComment = new PopupWindow(
				view, LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT, false);
		popInputComment
				.setBackgroundDrawable(new BitmapDrawable());
		// 设置点击窗口外边窗口消失
		popInputComment.setOutsideTouchable(true);
		// 设置此参数获得焦点，否则无法点击
		popInputComment.setFocusable(true);
		popInputComment
				.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		popInputComment
				.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
		popInputComment.showAtLocation(v,
				Gravity.BOTTOM, 0, 0);
		
		inputComment.postDelayed(new Runnable() {//弹出输入法
			
			@Override
			public void run() {
                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
			}
		}, 200);
		
		// 发送的事件
		commentWrite
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (popInputComment != null
								&& popInputComment
										.isShowing()) {
							// 隐藏窗口，如果设置了点击窗口外消失即不需要此方式隐藏
							popInputComment
									.dismiss();
						}
						final String commentStr = inputComment
								.getText()
								.toString();
						if (TextUtils
								.isEmpty(commentStr)) {
							return;
						}
						// 上传评论
						CommentRequest request = new CommentRequest();
						request.setSharingUuid(sharingWithComments.getSharing().getUuid());
						request.setType("COMMENT");
						request.setComment(commentStr);
						NetWorkManager.getInstance(mContext).commentSharing(request, UserManager.getInstance(mContext).getCurrentUser().getToken(), 
								new Listener<JSONObject>() {

									@Override
									public void onResponse(JSONObject response) {
										Gson gson = new Gson();
										Comment comment = gson.fromJson(response.toString(), new TypeToken<Comment>(){}.getType());
										if(comment == null){
											return;
										}
										// 评论成功，更新界面
										if (commentLayout
												.getVisibility() == View.GONE) {
											commentLayout
													.setVisibility(View.VISIBLE);
										}
										sharingWithComments.getComments().add(
												comment);// 更新内存数据		
										notifyDataSetChanged();
										}
								}, new ErrorListener() {

									@Override
									public void onErrorResponse(
											VolleyError error) {
										if (!NetWorkManager.getInstance(mContext)
												.isNetConnected()) {
											Toast.makeText(mContext, "当前网络不可用，请稍后重试", Toast.LENGTH_SHORT).show();
										} else if (error.networkResponse == null) {
											Toast.makeText(mContext, "评论失败，请稍后重试", Toast.LENGTH_SHORT).show();
										} else if(error.networkResponse.statusCode == 400){//commentType is null
											Toast.makeText(mContext, "评论失败，请稍后重试", Toast.LENGTH_SHORT).show();
										} else if(error.networkResponse.statusCode == 401) {
											ErrorCodeUtil.ErrorCode401(mContext);
										} else if(error.networkResponse.statusCode == 404){
											Toast.makeText(mContext, "服务器未找到该动态", Toast.LENGTH_SHORT).show();
										} else {
											Toast.makeText(mContext, "错误: 请稍后重试", Toast.LENGTH_SHORT).show();
										}
									}
								});
					}
				});
	}
	
}
