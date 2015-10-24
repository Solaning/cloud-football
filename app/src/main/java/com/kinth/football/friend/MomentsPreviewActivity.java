package com.kinth.football.friend;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kinth.football.R;
import com.kinth.football.friend.touchgallery.GalleryWidget.BasePagerAdapter.OnItemChangeListener;
import com.kinth.football.friend.touchgallery.GalleryWidget.FilePagerAdapter;
import com.kinth.football.friend.touchgallery.GalleryWidget.GalleryViewPager;
import com.kinth.football.friend.touchgallery.GalleryWidget.GalleryViewPager.OnItemClickListener;
import com.kinth.football.ui.BaseActivity;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

/**
 * 朋友圈编辑页面预览图片
 * @author Sola
 */
@ContentView(R.layout.activity_moments_preview)
public class MomentsPreviewActivity extends BaseActivity{
	public static final String INTENT_IMAGES_TO_PREVIEW = "INTENT_IMAGES_TO_PREVIEW";//图片的Path数据
	public static final String INTENT_IMAGE_POSITION = "INTENT_IMAGE_POSITION";//点击图片的position
	public static final String INTENT_PREVIEW_IMAGES_FOR_RESULT = "INTENT_IMAGES_FOR_RESULT";//携带数据返回
	private FilePagerAdapter pagerAdapter;//适配器
	private ArrayList<PickedImage> pickedImages;//
	private int currentPosition;//当前显示的页面为准
	private int clickPosition;//过来时的图片位置
	
	@ViewInject(R.id.title_bar)
	private View bar;
	
	@ViewInject(R.id.nav_left)
	private View back;
	
	@ViewInject(R.id.nav_right_image)
	private ImageView right;
	
	@ViewInject(R.id.nav_title)
	private TextView title;

	@ViewInject(R.id.gallery_viewer)
	private GalleryViewPager galleryViewPager;//显示图片
	
	@OnClick(R.id.nav_left)
	public void fun_1(View v){//携带数据
		Intent data = new Intent();
		data.putParcelableArrayListExtra(INTENT_PREVIEW_IMAGES_FOR_RESULT, pickedImages);
		setResult(RESULT_OK, data);
		finish();
	}
	
	@OnClick(R.id.nav_right_image)
	public void fun_2(View v){//删除该图片
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle("提示");
		builder.setMessage("要删除这张照片吗？");
		builder.setNegativeButton("取消",
				new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		builder.setPositiveButton("确定",
				new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						if(pickedImages.size() > currentPosition){
							pickedImages.remove(currentPosition);//删掉该图
						}
						if(pickedImages.size() == 0){//删除完了返回 
							Intent data = new Intent();
							data.putParcelableArrayListExtra(INTENT_PREVIEW_IMAGES_FOR_RESULT, pickedImages);
							setResult(RESULT_OK, data);
							finish();
							return;
						}

						if(currentPosition == pickedImages.size()){//删除位于最后一张图时
							currentPosition --;
						}
						title.setText((currentPosition+1)+"/"+pickedImages.size());
						List<String> urls = new ArrayList<String>();//取路径
						for(PickedImage item : pickedImages){
							urls.add(item.getPath());
						}
						pagerAdapter.setResources(urls);
						pagerAdapter.notifyDataSetChanged();
					}
				});
		builder.create().show();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		right.setImageResource(R.drawable.delete);
		pickedImages = getIntent().getParcelableArrayListExtra(INTENT_IMAGES_TO_PREVIEW);
		clickPosition = getIntent().getIntExtra(INTENT_IMAGE_POSITION, 0);
		if(pickedImages == null || pickedImages.size() == 0){
			return;
		}
		title.setText(clickPosition+"/"+pickedImages.size());
		List<String> urls = new ArrayList<String>();//取路径
		for(PickedImage item : pickedImages){
			urls.add(item.getPath());
		}
		pagerAdapter = new FilePagerAdapter(mContext,urls);
        pagerAdapter.setOnItemChangeListener(new OnItemChangeListener()
		{
			@Override
			public void onItemChange(int position)
			{
				//图片切换完时的回调
				currentPosition = position;
				title.setText((currentPosition+1)+"/"+pickedImages.size());
			}
		});
        
        //单击隐藏title bar
        galleryViewPager.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClicked(View view, int position) {
				if(bar.getVisibility() == View.VISIBLE){//可见下隐藏
					bar.setVisibility(View.GONE);
				}else{//隐藏下则可见
					bar.setVisibility(View.VISIBLE);
				}
			}
		});
        galleryViewPager.setOffscreenPageLimit(1);
        galleryViewPager.setAdapter(pagerAdapter);
        galleryViewPager.setCurrentItem(clickPosition);
	}
	
	/**
	 * 返回键 默认，返回向右退出
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			Intent data = new Intent();
			data.putParcelableArrayListExtra(INTENT_PREVIEW_IMAGES_FOR_RESULT, pickedImages);
			setResult(RESULT_OK, data);
			finish();
			return true;
		}
		return false;
	}
}
