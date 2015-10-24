package com.kinth.football.friend;

import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;

import org.apache.commons.lang3.time.FastDateFormat;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kinth.football.bean.Pageable;
import com.kinth.football.bean.Paging;
import com.kinth.football.bean.message.SharingCommentedMC;
import com.kinth.football.bean.message.SharingCommentedPM;
import com.kinth.football.bean.moments.Comment;
import com.kinth.football.bean.moments.MomentsResponse;
import com.kinth.football.bean.moments.SharingWithComments;
import com.kinth.football.chat.ChatConstants;
import com.kinth.football.config.Config;
import com.kinth.football.config.PushMessageEnum;
import com.kinth.football.dao.PushMessage;
import com.kinth.football.dao.PushMessageDao;
import com.kinth.football.eventbus.bean.CommentSharingEvent;
import com.kinth.football.eventbus.bean.DeleteCommentEvent;
import com.kinth.football.eventbus.bean.DeleteSharingEvent;
import com.kinth.football.eventbus.bean.GenMomentsSharingEvent;
import com.kinth.football.eventbus.bean.MomentsHasReadPushMessageEvent;
import com.kinth.football.eventbus.message.SharingCommentedPushMessageEvent;
import com.kinth.football.manager.UserManager;
import com.kinth.football.network.NetWorkManager;
import com.kinth.football.picture.PickPicture;
import com.kinth.football.singleton.ViewCompat;
import com.kinth.football.ui.BaseActivity;
import com.kinth.football.util.ErrorCodeUtil;
import com.kinth.football.util.JSONUtils;
import com.kinth.football.util.PhotoUtil;
import com.kinth.football.util.PictureUtil;
import com.kinth.football.view.xlist.XListView;
import com.kinth.football.view.xlist.XListView.IXListViewListener;
import com.kinth.football.CustomApplcation;
import com.kinth.football.R;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.event.EventBus;
import eu.inmite.android.lib.dialogs.IListDialogListener;
import eu.inmite.android.lib.dialogs.ListDialogFragment;

/**
 * 朋友圈
 * 
 * @author Sola
 */
@ContentView(R.layout.activity_moments)
public class MomentsActivity extends BaseActivity implements IListDialogListener{
	private static final int REQUEST_CODE_SEND_ACTIVE = 9980;//发动态请求码
	private static Object asyncLock = new Object();

	private MomentsAdapter adapter;
	private int firstVisible;// 第一项的位置
	private	List<PushMessage> pushMessageList = new ArrayList<PushMessage>();
	
	private List<Paging> pagings = new ArrayList<Paging>();
	private View headView;

	@ViewInject(R.id.viewGroup)
	private RelativeLayout viewGroup;
	
	@ViewInject(R.id.nav_left)
	private View back;

	@ViewInject(R.id.nav_right_image)
	private ImageView right;// 右侧按钮
	
	@ViewInject(R.id.nav_title)
	private TextView title;

	@ViewInject(R.id.listview_moments)
	private XListView listview;
	
	@OnClick(R.id.nav_left)
	public void fun_1(View v){
		finish();
	}
	
	@OnClick(R.id.nav_right_image)
	public void fun_2(View v){//发动态 --改为从图库选择
		ListDialogFragment
			.createBuilder(mContext,
					getSupportFragmentManager()).setTitle("发布动态")
			.setItems(new String[] { "拍照", "选择相册" })
			.hideDefaultButton(true).show();
	}
	
	private String mPhotoPath;//拍照图片的本地路径
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
	
	private Bitmap bitmap;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EventBus.getDefault().register(this);
		ViewUtils.inject(this);
		
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.ARGB_8888;
		opt.inInputShareable = true;
		opt.inPurgeable = true;
		// 获取资源图片
		InputStream is = mContext.getResources().openRawResource(
				R.drawable.bg_family_dafult);
		bitmap = BitmapFactory.decodeStream(is, null, opt);
		
		ViewCompat.setBackground(viewGroup, new BitmapDrawable(getResources(),
				bitmap));

		title.setText("云球圈");
		right.setImageResource(R.drawable.comments_camera);
		right.setVisibility(View.VISIBLE);
		//长按事件 -- 只发布 只有文字的动态
		right.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View arg0) {
				Intent intent = new Intent(mContext, MomentsPublishActivity.class);
				startActivity(intent);
				return false;
			}
		});
		
		headView = LayoutInflater.from(mContext).inflate(R.layout.moments_list_headview, null);
		headView.findViewById(R.id.item_container).setVisibility(View.GONE);;
		listview.addHeaderView(headView);
		listview.setHeaderDividersEnabled(false);
		listview.setPullRefreshEnable(true);
		listview.setPullLoadEnable(true);
		
		listview.setXListViewListener(new IXListViewListener() {
			
			@Override
			public void onRefresh() {// 加载更晚发的动态
				//取列表第一个的时间戳作为beginDate
				//endDate不传，则表示取日期大于或等于beginDate的数据 
				long beginDate = 0l;//刷新
				List<SharingWithComments> sharingWithCommentsWithOutTops = adapter.getSharingWithCommentsWithOutTop();
				if (sharingWithCommentsWithOutTops == null
						|| sharingWithCommentsWithOutTops.size() == 0
						|| sharingWithCommentsWithOutTops.get(0).getSharing() == null) {
					
				} else {
					// 取第一个动态的时间戳
					beginDate = sharingWithCommentsWithOutTops.get(0)
							.getSharing().getDate() + 1;
				}
				getMomentsSharingList(0, beginDate, 0l, 2);
			}
			
			@Override
			public void onLoadMore() {//加载更多// 加载更早发的动态
				long beginDate = 0l;
				long endDate = 0l;
				int page = 0;
				if(pagings != null && !pagings.isEmpty()){
					page = pagings.get(0).getTotalGotElements() / Config.DEFAULT_NUM_OF_ACTIVE;//当前加载到的页面
					if(page > pagings.get(0).getPageable().getTotalPages()){//超出分页范围 TODO
						pagings.remove(0);
						int activeWithCommentsListSize;
						if (adapter.getSharingWithCommentsWithOutTop() == null
								|| (activeWithCommentsListSize = adapter
										.getSharingWithCommentsWithOutTop().size()) == 0
								|| adapter.getSharingWithCommentsWithOutTop()
										.get(activeWithCommentsListSize - 1)
										.getSharing() == null) {
							
						} else {// 取最后一个动态的时间戳
							endDate = adapter.getSharingWithCommentsWithOutTop()
									.get(activeWithCommentsListSize - 1).getSharing()
									.getDate() - 1;
						}
					}else {
						beginDate = pagings.get(0).getBeginDate();//搜索条件保持一致
						endDate = pagings.get(0).getEndDate();
					}
				}else{//没有需要分页的情况，直接取当前列表的最后一个动态时间戳
					//beginDate不传，则表示取日期小于或等于endDate的数据
					int activeWithCommentsListSize;
					if (adapter.getSharingWithCommentsWithOutTop() == null
							|| (activeWithCommentsListSize = adapter
									.getSharingWithCommentsWithOutTop().size()) == 0
							|| adapter.getSharingWithCommentsWithOutTop()
									.get(activeWithCommentsListSize - 1)
									.getSharing() == null) {
						
					} else {// 取最后一个动态的时间戳
						endDate = adapter.getSharingWithCommentsWithOutTop()
								.get(activeWithCommentsListSize - 1).getSharing()
								.getDate() - 1;
					}
				}
				getMomentsSharingList(page, beginDate, endDate, 3);
			}
		});

		listview.setOnScrollListener(new AbsListView.OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// 当列表滚动停止，并且当前可见条目不是第一条的话，运行动画。
				if (SCROLL_STATE_IDLE == scrollState && firstVisible > 0) {
					
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				firstVisible = firstVisibleItem;
			}
		});

		adapter = new MomentsAdapter(mContext);
		listview.setAdapter(adapter);
		
		new CountMomentsPushMessageTask().execute();
		listview.pullRefreshing();
		getMomentsSharingList(0, 0l, 0l, 1);
//		new LoadDataFromDBTask().setFirestLoadData(true).execute();// 加载数据库数据
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
		
		if (bitmap != null) {
			bitmap.recycle();
			bitmap = null;
		}
	}

	/**
	 * 获取动态列表
	 * @param beginDate
	 * @param endDate
	 * @param loadState 1表示加载最新  2表示下拉刷新  3表示加载更多
	 */
	private void getMomentsSharingList(int page, final long beginDate, final long endDate, final int loadState){
		NetWorkManager.getInstance(mContext).getMomentsActives(page, Config.DEFAULT_NUM_OF_ACTIVE, beginDate, endDate, 
				UserManager.getInstance(mContext).getCurrentUser().getToken(), new Listener<JSONObject>() {
					
					@Override
					public void onResponse(JSONObject response) {
						Gson gson = new Gson();
						MomentsResponse momentsResponse = gson.fromJson(
								response.toString(),
								new TypeToken<MomentsResponse>() {
								}.getType());
						if (momentsResponse == null)
							return;
						//本地记录分页情况
						switch(loadState){
						case 1://第一次打开获取
							//该次所获取到的动态条数
							int totalGotElement = momentsResponse.getSharings() == null ? 0 : momentsResponse.getSharings().size();
							Pageable pageable = momentsResponse.getPageable();
							if(pageable.getTotalElements() > totalGotElement){//需要分页的情况
								Paging paging = new Paging();
								paging.setBeginDate(beginDate);
								paging.setEndDate(endDate);
								paging.setPageable(pageable);
								paging.setTotalGotElements(totalGotElement);
								pagings.add(paging);
							}
							break;
						case 2://下拉刷新最新数据
							int totalGotElement2 = momentsResponse.getSharings() == null ? 0 : momentsResponse.getSharings().size();
							if(momentsResponse.getPageable().getTotalElements() > totalGotElement2){//获取的新数据需要分页
								Paging paging = new Paging();
								paging.setBeginDate(beginDate);
								paging.setEndDate(endDate);
								paging.setPageable(momentsResponse.getPageable());
								paging.setTotalGotElements(totalGotElement2);
								pagings.add(0, paging);
							}
							break;
						case 3://分页加载更多，补完中间的空缺
							//该次所获取到的动态条数
							boolean hasThisPaging = false;
							int gotElementNum = momentsResponse.getSharings() == null ? 0 : momentsResponse.getSharings().size();
							for(int i= 0; i < pagings.size(); i++){
								if(pagings.get(i).getBeginDate() == beginDate && pagings.get(i).getEndDate() == endDate){
									hasThisPaging = true;
									pagings.get(i).setTotalGotElements(pagings.get(i).getTotalGotElements() + gotElementNum);
									if(pagings.get(i).getTotalGotElements() >= pagings.get(i).getPageable().getTotalElements()){
										pagings.remove(i);
									}
									break;
								}
							}
							if(!hasThisPaging && momentsResponse.getPageable().getTotalElements() > gotElementNum){//没有找到该分页情况，并且后续还有分页
								Paging paging = new Paging();
								paging.setBeginDate(beginDate);
								paging.setEndDate(endDate);
								paging.setPageable(momentsResponse.getPageable());
								paging.setTotalGotElements(gotElementNum);
								pagings.add(paging);
							}
							break;
						default:
							break;
						}
						//更新页面
						new MergeDataTask().setSharings(momentsResponse.getSharings()).execute();
					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						listview.stopRefresh();
						listview.stopLoadMore();
						if (!NetWorkManager.getInstance(mContext)
								.isNetConnected()) {
							ShowToast("当前网络不可用，请稍后重试");
						} else if (error.networkResponse == null) {
							ShowToast("获取数据失败，请稍后重试");
						} else if (error.networkResponse.statusCode == 401) {
							ErrorCodeUtil.ErrorCode401(mContext);
						}
					}
				});
	}

	/**
	 * 归并数据，并更新界面
	 */
	private class MergeDataTask extends
			AsyncTask<Void, Void, List<SharingWithComments>> {
		List<SharingWithComments> sharings;
		
		public MergeDataTask setSharings(List<SharingWithComments> sharings) {
			this.sharings = sharings;
			return this;
		}

		@Override
		protected List<SharingWithComments> doInBackground(
				Void... params) {
			return mergeData(sharings);
		}

		@Override
		protected void onPostExecute(List<SharingWithComments> result) {
			super.onPostExecute(result);
			synchronized (asyncLock) {
				ArrayList<SharingWithComments> tt = null;
				if(result != null){
					tt = new ArrayList<SharingWithComments>(result);
				}
				adapter.setSharingWithCommentsList(tt);
				listview.stopRefresh();
				listview.stopLoadMore();
			}
		}
	}
	
	//跟现有数据进行归并
	private List<SharingWithComments> mergeData(List<SharingWithComments> sharings){
		// 如果返回的数据为空，看请求的是之前还是之后的数据，如果是之前的数据，就返回existing
		/*
		 * 分为4中情况，（0x11）代表现有数据: not null，返回数据: not null （0x10）代表现有数据:not
		 * null，返回数据：null（0x01）代表现有数据:null，返回数据：not null
		 * （0x00）代表现有数据：null，返回数据：null
		 */
		byte state = 0x00;
		if (sharings == null || sharings.size() == 0
				|| sharings.get(0).getSharing() == null) {
			state = (byte) (state | 0x00);
		} else {
			state = (byte) (state | 0x01);
		}

		// 插到现有list的前段或者后端
		List<SharingWithComments> existingActiveWithComments = adapter
				.getSharingWithCommentsWithOutTop();
		if (existingActiveWithComments == null
				|| existingActiveWithComments.size() == 0
				|| existingActiveWithComments.get(0).getSharing() == null) {
			state = (byte) (state | 0x00);
		} else {
			state = (byte) (state | 0x10);
		}

		switch (state) {
		case 0x00:// 返回null
		default:
			return null;
		case 0x01:// 现有为空，返回不为空
			return sharings;
		case 0x10:// 现有不为空，返回为空，返回现有的
			return existingActiveWithComments;
		case 0x11://现有不为空，返回不为空
			//抽出置顶的部分
			int start = 0;
			for (SharingWithComments sharingWithComments : sharings) {
				if(sharingWithComments.getSharing().isOnTop()){
					start ++;
					continue;
				}
				break;
			}
			if(start > 0){//有需要置顶的
				TreeSet<SharingWithComments> set = new TreeSet<SharingWithComments>();
				set.addAll(sharings.subList(start, sharings.size()));//首先保证服务器的数据不被冲掉
				set.addAll(existingActiveWithComments);
				SharingWithComments[] ss = set.toArray(new SharingWithComments[set.size()]);
				List<SharingWithComments> result = new ArrayList<SharingWithComments>(Arrays.asList(ss));
				result.addAll(0, sharings.subList(0, start));
				return result;
			}else{//无需要置顶的
				TreeSet<SharingWithComments> set = new TreeSet<SharingWithComments>();
				set.addAll(sharings);//首先保证服务器的数据不被冲掉
				set.addAll(existingActiveWithComments);
				SharingWithComments[] ss = set.toArray(new SharingWithComments[set.size()]);
				return Arrays.asList(ss);
			}
		}
	}
	
	/**
	 * 两个日期相比，> 0 ：dateEnd在dateStart之后 < 0：dateEnd在dateStart之前
	 * 
	 * @param year
	 * @param month
	 * @param dayOfMonth
	 * @return
	 * @throws ParseException
	 */
	public static int compareDate(String dateEnd, String dateStart)
			throws ParseException {
		FastDateFormat dateFormat = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");
		Date dateA = dateFormat.parse(dateEnd);
		Date dateB = dateFormat.parse(dateStart);
		int i = dateA.compareTo(dateB);
		return i;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if(resultCode != RESULT_OK){
			return;
		}
		if(requestCode == ChatConstants.REQUESTCODE_UPLOADAVATAR_CAMERA){//拍照后返回
			ArrayList<PickedImage> imageResult = new ArrayList<PickedImage>();
			imageResult.add(new PickedImage(0, mPhotoPath));
			Intent inten = new Intent(mContext,MomentsPublishActivity.class);
			inten.putParcelableArrayListExtra(CustomGalleryActivity2.INTENT_IMG_PATH_ARRAY, imageResult);
			startActivityForResult(inten, REQUEST_CODE_SEND_ACTIVE);
			return;
		}
		if(requestCode == ChatConstants.REQUESTCODE_UPLOADAVATAR_LOCATION){//本地图库选择图片返回
			Uri uri = intent.getData();
			String imagePath = PickPicture.getPath(mContext, uri);
			if(PictureUtil.isImage(imagePath)){
				ArrayList<PickedImage> imageResult = new ArrayList<PickedImage>();
				imageResult.add(new PickedImage(0, imagePath));
				Intent inten = new Intent(mContext,MomentsPublishActivity.class);
				inten.putParcelableArrayListExtra(CustomGalleryActivity2.INTENT_IMG_PATH_ARRAY, imageResult);
				startActivityForResult(inten, REQUEST_CODE_SEND_ACTIVE);
			}else{
				ShowToast("所选文件非图片");
				Log.i("选的非图片","imagePath = "+imagePath);
			}
			return;
		}
//		if(requestCode == REQUEST_CODE_SEND_ACTIVE ){//发动态后的结果
//			boolean result = intent.getBooleanExtra(MomentsPublishActivity.INTENT_PUBLISH_ACTIVE_FOR_RESULT, false);
//			if(result){
//				Active active = intent.getParcelableExtra(MomentsPublishActivity.INTENT_PUBLISH_ACTIVE_FOR_RESULT_ACTIVE);
//				if(active == null){
//					//取最新数据
//					String timeStamp = null;
//					String timeDir = null;
//					if (adapter.getActiveWithComments() == null
//							|| adapter.getActiveWithComments().size() == 0
//							|| adapter.getActiveWithComments().get(0).getActive() == null) {
//					} else {
//						// 取第一个动态的时间戳
//						timeStamp = adapter.getActiveWithComments().get(0)
//								.getActive().getCreateTime();
//						timeDir = "latter";
//					}
//					// 获取更多我的动态
////					new AsyncNetworkManager().getActiveTimeLine(mContext, mobile,
////							isGetMyActive ? 1 : 0, timeStamp, timeDir,
////							new getActiveTimeLineCallBack() {
////
////								@Override
////								public void onGetActiveTimeLineCallBack(int rtn,
////										ArrayList<ActiveWithComments> activeList) {
////									switch (rtn) {
////									case 1:// 成功 ------>处理取回的数据
////										new MergeDataTask().execute(activeList);
////										break;
////									case 0:
////									default:
////										listview.onRefreshComplete();
////										break;
////									}
////								}
////							});
//				}else{//保存active，刷新界面
////					try {
////						db.saveOrUpdate(active);
////					} catch (DbException e) {
////						e.printStackTrace();
////					}
//					ActiveWithComments activeWithComments = new ActiveWithComments();
//					activeWithComments.setActive(active);
//					adapter.addFirstActiveWithCommentsList(activeWithComments);
//				}
//			}else{
//				Toast.makeText(mContext, "发送失败", Toast.LENGTH_LONG).show();
//			}
//			//删除目录下图片缓存文件
//			FileUtil.delete(new File(JConstants.IMAGE_CACHE));
//		}
	}
	
	/**
	 * 发布朋友圈动态的事件
	 * @param genMomentsSharingEvent
	 */
	public void onEventMainThread(GenMomentsSharingEvent genMomentsSharingEvent){
		SharingWithComments sharingWithComment = new SharingWithComments();
		sharingWithComment.setSharing(genMomentsSharingEvent.getSharing());
		sharingWithComment.setComments(new ArrayList<Comment>());
		
		//异步保存到数据库
		List<SharingWithComments> sharingWithComments = new ArrayList<SharingWithComments>();
		sharingWithComments.add(sharingWithComment);
		adapter.addFirstSharingWithCommentsList(sharingWithComment);
		listview.smoothScrollToPosition(0);//滚到顶部
	}
	
	/**
	 * 对朋友圈动态点赞或评论 --- 其他来源
	 */
	public void onEventMainThread(CommentSharingEvent commentSharingEvent){
		String sharingUuid = commentSharingEvent.getSharingUuid();
		if(TextUtils.isEmpty(sharingUuid))
			return;
		for(SharingWithComments item : adapter.getSharingWithCommentsWithOutTop()){
			if(sharingUuid.equals(item.getSharing().getUuid())){
				item.getComments().add(commentSharingEvent.getComment());
				adapter.notifyDataSetChanged();
				break;
			}
		}
	}
	
	/**
	 * 删除朋友圈评论的事件
	 * @param deleteCommentEvent
	 */
	public void onEventMainThread(DeleteCommentEvent deleteCommentEvent){
		String sharingUuid = deleteCommentEvent.getSharingUuid();
		if(TextUtils.isEmpty(sharingUuid) || TextUtils.isEmpty(deleteCommentEvent.getCommentUuid()))
			return;
		for(SharingWithComments item : adapter.getSharingWithCommentsWithOutTop()){
			if(sharingUuid.equals(item.getSharing().getUuid())){
				int size = item.getComments().size();
				for(int i = 0; i < size; i++){
					if(deleteCommentEvent.getCommentUuid().equals(item.getComments().get(i).getUuid())){
						item.getComments().remove(i);
						break;
					}
				}
				break;
			}
		}
		adapter.notifyDataSetChanged();
	}
	
	/**
	 * 删除朋友圈动态的事件
	 */
	public void onEventMainThread(DeleteSharingEvent deleteSharingEvent){
		String sharingUuid = deleteSharingEvent.getSharingUuid();
		if(TextUtils.isEmpty(sharingUuid)){
			return;
		}
		List<SharingWithComments> sharingLists = adapter.getSharingWithCommentsWithOutTop();
		int size = sharingLists.size();
		for(int i = 0; i < size; i++){
			if(sharingUuid.equals(sharingLists.get(i).getSharing().getUuid())){
				sharingLists.remove(i);
				break;
			}
		}
		adapter.notifyDataSetChanged();
	}
	
	/**
	 * 朋友圈评论推送消息
	 * @param sharingCommentedPushMessageEvent
	 */
	public void onEventMainThread(SharingCommentedPushMessageEvent sharingCommentedPushMessageEvent){
		//获取该动态的最新消息,,
		String sharingUuid = null;
		try{
			sharingUuid = sharingCommentedPushMessageEvent.getSharingCommentedPM().getContent().getComment().getSharingUuid();
		}catch(NullPointerException e){ }
		if(!TextUtils.isEmpty(sharingUuid)){
			final String targetSharingUuid = sharingUuid;
			NetWorkManager.getInstance(mContext).getSharingComments(sharingUuid, UserManager.getInstance(mContext).getCurrentUser().getToken(),
					new Listener<JSONArray>() {
	
						@Override
						public void onResponse(JSONArray response) {
							Gson gson = new Gson();
							List<Comment> comments = gson.fromJson(response.toString(), new TypeToken<List<Comment>>(){}.getType());
							if(comments == null || comments.size() == 0){
								return;
							}
							//从list中找到该sharing
							List<SharingWithComments> temp = adapter.getSharingWithCommentsWithTop();
							if(temp != null){
								for(SharingWithComments item : temp){
									if(item.getSharing() != null && targetSharingUuid.equals(item.getSharing().getUuid())){
										item.setComments(comments);
										adapter.setSharingWithCommentsList(temp);
										
										break;
									}
								}
							}
						}
					}, new ErrorListener(){
	
						@Override
						public void onErrorResponse(VolleyError error) {
							if (error.networkResponse.statusCode == 401) {
								ErrorCodeUtil.ErrorCode401(mContext);
							} else if (error.networkResponse.statusCode == 404){
								ShowToast("没有找到该动态");
							}
						}
					});
		}
		new CountMomentsPushMessageTask().execute();
	}
	
	/**
	 * 异步统计数据库数据
	 */
	class CountMomentsPushMessageTask extends AsyncTask<Void, Void, ArrayList<SharingCommentedPM>> {

		@Override
		protected ArrayList<SharingCommentedPM> doInBackground(Void... arg0) {
			QueryBuilder<PushMessage> qb1 = CustomApplcation.getDaoSession(null)
					.getPushMessageDao().queryBuilder();
			qb1.where(PushMessageDao.Properties.Type.eq(PushMessageEnum.SHARING_COMMENTED.getValue()),PushMessageDao.Properties.HasRead.eq(false)).orderDesc(PushMessageDao.Properties.Date);
			pushMessageList = qb1.list();//转为SharingCommentedPM
			ArrayList<SharingCommentedPM> sharingCommentedPMList = new ArrayList<SharingCommentedPM>();
			String targetType = PushMessageEnum.SHARING_COMMENTED.getValue();
			for(PushMessage pushMessage : pushMessageList){
				if(targetType.equals(pushMessage.getType())){//保证类型相同，其实可以不用该判断
					SharingCommentedPM sharingCommentedPM = new SharingCommentedPM();
					sharingCommentedPM.setType(targetType);
					sharingCommentedPM.setDate(pushMessage.getDate());
					sharingCommentedPM.setContent(JSONUtils.fromJson(pushMessage.getContent(), SharingCommentedMC.class));
					sharingCommentedPMList.add(sharingCommentedPM);
				}
			}
			return sharingCommentedPMList;//球队的消息统计
		}

		@Override
		protected void onPostExecute(final ArrayList<SharingCommentedPM> result) {
			super.onPostExecute(result);
			if(result == null || result.isEmpty()){
				return;
			}
			//显示条数和最后一个人
			if(headView != null){
				headView.findViewById(R.id.item_container).setVisibility(View.VISIBLE);
				headView.findViewById(R.id.llt_comment_hint).setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						headView.findViewById(R.id.item_container).setVisibility(View.GONE);
						for(PushMessage item : pushMessageList){
							item.setHasRead(true);
							CustomApplcation.getDaoSession(null).getPushMessageDao().insertOrReplace(item);
						}
						EventBus.getDefault().post(new MomentsHasReadPushMessageEvent());
						Intent intent = new Intent(mContext, MomentsLookOverCommentActivity.class);
						intent.putParcelableArrayListExtra(MomentsLookOverCommentActivity.INTENT_SHARING_COMMENT_LIST, result);
						startActivity(intent);
					}
				});
				com.kinth.football.view.RoundImageView icon = (com.kinth.football.view.RoundImageView) headView.findViewById(R.id.iv_head_icon);
				TextView hint = (TextView) headView.findViewById(R.id.tv_comment_num_hint);
				hint.setText(result.size() + "条新消息");
				String playerIconUrl = null;
				if(result.get(0) !=  null && result.get(0).getContent() != null && result.get(0).getContent().getComment() != null && result.get(0).getContent().getComment().getPlayer() != null){
					playerIconUrl = result.get(0).getContent().getComment().getPlayer().getPicture();
				}
				if(TextUtils.isEmpty(playerIconUrl)){//自身没有设置图片
					ImageLoader.getInstance().displayImage(null, icon, new DisplayImageOptions.Builder()
							.showImageOnLoading(
									R.drawable.icon_head)
							.showImageForEmptyUri(R.drawable.icon_head)
							.showImageOnFail(R.drawable.icon_head) // 默认头像
							.cacheInMemory(true).cacheOnDisk(true).build());
				}else {
					PictureUtil.UILShowImage(mContext, icon, PhotoUtil.getAllPhotoPath(playerIconUrl));
				}
			}
		}
	}
}
