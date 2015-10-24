package com.kinth.football.friend;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kinth.football.CustomApplcation;
import com.kinth.football.R;
import com.kinth.football.bean.message.SharingCommentedPM;
import com.kinth.football.bean.moments.Comment;
import com.kinth.football.bean.moments.Sharing;
import com.kinth.football.bean.moments.SharingWithComments;
import com.kinth.football.dao.PlayerDao;
import com.kinth.football.dao.SharingDB;
import com.kinth.football.dao.SharingDBDao;
import com.kinth.football.manager.UserManager;
import com.kinth.football.network.NetWorkManager;
import com.kinth.football.singleton.SingletonBitmapClass;
import com.kinth.football.singleton.ViewCompat;
import com.kinth.football.ui.BaseActivity;
import com.kinth.football.util.DBUtil;
import com.kinth.football.util.ErrorCodeUtil;
import com.kinth.football.util.PhotoUtil;
import com.kinth.football.util.PictureUtil;
import com.kinth.football.util.TimeUtil;
import com.kinth.football.util.ViewHolder;
import com.kinth.football.view.RoundImageView;
import com.kinth.football.view.xlist.XListView;
import com.kinth.football.view.xlist.XListView.IXListViewListener;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import de.greenrobot.dao.query.Query;

/**
 * 查看评论的详情
 * @author Sola
 * 
 */
@ContentView(R.layout.activity_moments_look_over_comment)
public class MomentsLookOverCommentActivity extends BaseActivity{
	public static final String INTENT_SHARING_COMMENT_LIST = "INTENT_SHARING_COMMENT_LIST";
	private int HANDLER_WHAT = 10;
	
	private List<SharingCommentedPM> sharingCommentedPMList;
	private MomentsLookOverCommentAdapter adapter;
	
	@ViewInject(R.id.viewGroup)
	private LinearLayout viewGroup;
	
	@ViewInject(R.id.nav_left)
	private ImageButton back;
	
	@ViewInject(R.id.nav_title)
	private TextView title;
	
	@ViewInject(R.id.listview_moments_sharing_comment)
	private XListView listview;
	
	@OnClick(R.id.nav_left)
	public void fun_1(View v){
		finish();
	}

	private Handler handler = new Handler(new Handler.Callback() {
		
		@Override
		public boolean handleMessage(Message msg) {
			if(msg.what == HANDLER_WHAT){
				Log.e("handleMessage","handleMessage");
				String sharingUuid = (String) msg.obj;
				NetWorkManager.getInstance(mContext).getSharingByUuid(sharingUuid, UserManager.getInstance(mContext).getCurrentUser().getToken(),
						new Listener<JSONObject>() {

							@Override
							public void onResponse(JSONObject response) {
								Gson gson = new Gson();
								Sharing sharing = gson.fromJson(response.toString(), new TypeToken<Sharing>(){}.getType());
								if(sharing == null){
									return;
								}
								adapter.updateSharing(sharing);
							}
						}, new ErrorListener(){

							@Override
							public void onErrorResponse(VolleyError error) {
								if (!NetWorkManager.getInstance(mContext)
										.isNetConnected()) {
									ShowToast("当前网络不可用，请稍后重试");
								} else if (error.networkResponse == null) {
									ShowToast("获取数据失败，请稍后重试");
								} else if (error.networkResponse.statusCode == 401) {
									ErrorCodeUtil.ErrorCode401(mContext);
								} else if (error.networkResponse.statusCode == 404){
									ShowToast("没有找到该动态");
								}
							}
						});
			}
			return false;
		}
	});
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		
		Bitmap background = SingletonBitmapClass.getInstance()
				.getBackgroundBitmap();
		ViewCompat.setBackground(viewGroup, new BitmapDrawable(getResources(),
				background));
		
		title.setText("消息");
		sharingCommentedPMList = getIntent().getParcelableArrayListExtra(INTENT_SHARING_COMMENT_LIST);
		new LoadSharingWithCommentsTask().execute();
		listview.setPullRefreshEnable(false);//不给加载更多
		listview.setPullLoadEnable(false);
		
		listview.setXListViewListener(new IXListViewListener() {
			
			@Override
			public void onRefresh() {
				
			}
			
			@Override
			public void onLoadMore() {
				
			}
		});
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
			}
		});
		adapter = new MomentsLookOverCommentAdapter(null);
		listview.setAdapter(adapter);
	}
	
	/**
	 * 异步统计数据库数据
	 */
	class LoadSharingWithCommentsTask extends AsyncTask<Void, Void, List<SharingWithComments>> {

		@Override
		protected List<SharingWithComments> doInBackground(Void... arg0) {
			List<SharingWithComments> result = new ArrayList<SharingWithComments>();
			SharingDBDao sharingDBDao = CustomApplcation.getDaoSession(null).getSharingDBDao();
			PlayerDao playerDao = CustomApplcation.getDaoSession(null).getPlayerDao();
			Query<SharingDB> query = sharingDBDao.queryBuilder().where(SharingDBDao.Properties.Uuid.eq("")).build();
			for(SharingCommentedPM item : sharingCommentedPMList){
				SharingWithComments sharingWithComment = new SharingWithComments();
				List<Comment> aa = new ArrayList<Comment>();
				aa.add(item.getContent().getComment());
				sharingWithComment.setComments(aa);
				
				query.setParameter(0, item.getContent().getComment().getSharingUuid());
				SharingDB sharingDB = query.unique();
				Sharing sharing = DBUtil.transSharingDB2Sharing(sharingDB, playerDao);
				if(sharing == null){//本地没有该动态
					sharing = new Sharing();
					sharing.setUuid(item.getContent().getComment().getSharingUuid());//网络获取该sharing
					Message msg = handler.obtainMessage(HANDLER_WHAT);
					msg.obj = item.getContent().getComment().getUuid();
					handler.sendMessage(msg);
				}
				sharingWithComment.setSharing(sharing);
				result.add(sharingWithComment);
			}
			return result;//球队的消息统计
		}

		@Override
		protected void onPostExecute(final List<SharingWithComments> result) {
			super.onPostExecute(result);
			adapter.setSharingComments(result);
		}
	}
	
	public class MomentsLookOverCommentAdapter extends BaseAdapter{
		private LayoutInflater inflater;
		private List<SharingWithComments> sharingComments = new ArrayList<SharingWithComments> ();

		public MomentsLookOverCommentAdapter(
				List<SharingWithComments> sharingComments) {
			super();
			this.sharingComments = sharingComments;
			this.inflater = LayoutInflater.from(mContext);
		}

		public void updateSharing(Sharing sharing) {
			for(SharingWithComments item : sharingComments){
				if(item.getSharing().getUuid().equals(sharing.getUuid())){
					item.setSharing(sharing);
					Log.e("fjdsojf","32323");
					notifyDataSetChanged();
					break;
				}
			}
		}

		public List<SharingWithComments> getSharingComments() {
			return sharingComments;
		}

		public void setSharingComments(List<SharingWithComments> sharingComments) {
			this.sharingComments = sharingComments;
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return sharingComments == null ? 0 : sharingComments.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = inflater.inflate(
						R.layout.item_moments_look_over_comment, parent, false);
			}
			RoundImageView icon = ViewHolder.get(convertView, R.id.iv_user_icon);
			TextView userName = ViewHolder.get(convertView, R.id.tv_user_name);
			TextView content = ViewHolder.get(convertView, R.id.tv_comment_content);
			TextView date = ViewHolder.get(convertView, R.id.tv_comment_date);
//			MyImageButton textImage = ViewHolder.get(convertView, R.id.ib_text_image_button);
			ImageView img = ViewHolder.get(convertView, R.id.ib_text_image);
			TextView tv = ViewHolder.get(convertView, R.id.ib_text_tv);
//			SharingCommentedPM sharingCommentedPM = sharingCommentedPMList.get(position);
			SharingWithComments sharingWithComments = sharingComments.get(position);
			// 加载头像
			String playerIconUrl = null;
			if(sharingWithComments.getComments() != null && sharingWithComments.getComments().size() != 0){
				playerIconUrl = sharingWithComments.getComments().get(0).getPlayer().getPicture();
				if(TextUtils.isEmpty(playerIconUrl)){//自身没有设置图片
					ImageLoader.getInstance().displayImage(null, icon,new DisplayImageOptions.Builder()
							.showImageOnLoading(
									R.drawable.icon_head)
							.showImageForEmptyUri(R.drawable.icon_head)
							.showImageOnFail(R.drawable.icon_head) // 默认头像
							.cacheInMemory(true).cacheOnDisk(true).build());
				}else {
					PictureUtil.UILShowImage(mContext, icon, PhotoUtil.getAllPhotoPath(playerIconUrl));
				}
				
				if("COMMENT".equals(sharingWithComments.getComments().get(0).getType())){
					content.setText(StringUtils.defaultIfEmpty(sharingWithComments.getComments().get(0).getComment(), ""));
				}else{
					content.setText(sharingCommentedPMList.get(position).getContent().getDescription());
				}
				userName.setText(sharingWithComments.getComments().get(0).getPlayer().getName());
				String type = sharingWithComments.getSharing().getType();
				if(type != null){  
					if("TEXT".equals(type)){
                        tv.setVisibility(View.VISIBLE);
                        img.setVisibility(View.GONE);
						tv.setText(sharingWithComments.getSharing().getComment());
						
//						textImage.setText(sharingWithComments.getSharing().getComment());
//						textImage.setTextSize(20);
//						
//						textImage.setColor(mContext.getResources().getColor(android.R.color.white));
					}else if("IMAGE".equals(type)){
						    tv.setVisibility(View.GONE);
	                        img.setVisibility(View.VISIBLE);
						final String url = sharingWithComments.getSharing().getImageUrls() == null ? null : sharingWithComments.getSharing().getImageUrls().get(0);
						if(TextUtils.isEmpty(url)){//自身没有设置图片
							ImageLoader.getInstance().displayImage(null, img,new DisplayImageOptions.Builder()
									.showImageOnLoading(
											R.drawable.icon_head)
									.showImageForEmptyUri(R.drawable.icon_head)
									.showImageOnFail(R.drawable.icon_head) // 默认头像
									.cacheInMemory(true).cacheOnDisk(true).build());
						}else {
							PictureUtil.UILShowImage(mContext, img, PhotoUtil.getAllPhotoPath(url));
						}
					}
				}
			}
			
			date.setText(TimeUtil.TransTime(sharingCommentedPMList.get(position).getDate(), true));
			return convertView;
		}

	}
}
