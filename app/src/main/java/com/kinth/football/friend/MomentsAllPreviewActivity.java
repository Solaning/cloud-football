package com.kinth.football.friend;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;
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
 * 图片选择页面所有图片的预览
 * @author Sola
 */
@ContentView(R.layout.activity_moments_all_preview)
public class MomentsAllPreviewActivity extends BaseActivity{
	public static final String INTENT_ALL_IMAGES_TO_PREVIEW = "INTENT_IMAGES_TO_PREVIEW";//所有图片的Path数据
	public static final String INTENT_PICKED_IMAGES_TO_PREVIEW = "INTENT_PICKED_IMAGES_TO_PREVIEW";//已选图片的列表
	public static final String INTENT_IMAGE_POSITION = "INTENT_IMAGE_POSITION";//点击图片的position
	public static final String INTENT_PREVIEW_IMAGES_FOR_RESULT = "INTENT_IMAGES_FOR_RESULT";//携带数据返回
	public static final String INTENT_IS_FINISH_FOR_RESULT = "INTENT_IS_FINISH_FOR_RESULT";//是否完成选择
	
	private Context mContext;
	private FilePagerAdapter pagerAdapter;//适配器
	private ArrayList<CustomGalleryBean> data = new ArrayList<CustomGalleryBean>();//所有图片，第1项是照相机
	private ArrayList<PickedImage> pickedImages;//已经选好的图片
	private int currentPosition;//浏览时当前显示的页面
	private int clickPosition;//过来时的图片位置
	
	@ViewInject(R.id.title_bar)
	private View bar;//actionbar
	
	@ViewInject(R.id.nav_left)
	private View back;
	
	@ViewInject(R.id.nav_right_btn)
	private Button right;
	
	@ViewInject(R.id.nav_title)
	private TextView title;
	
	@ViewInject(R.id.rl_bottom_container)
	private View bottomContainer;//底部容器
	
	@ViewInject(R.id.checkbox_all_preview)
	private CheckBox checkbox;// 选中图片CheckBox

	@ViewInject(R.id.gallery_viewer)
	private GalleryViewPager galleryViewPager;// 显示图片
	
	@OnClick(R.id.nav_left)
	public void fun_1(View v){// 携带数据
		Intent data = new Intent();
		data.putParcelableArrayListExtra(INTENT_PREVIEW_IMAGES_FOR_RESULT, pickedImages);
		setResult(RESULT_OK, data);
		finish();
	}
	
	@OnClick(R.id.nav_right_btn)
	public void fun_2(View v){//选中的图片，完成按钮
		if(pickedImages.size() == 0){//默认选中当前的图片
			pickedImages.add(new PickedImage(currentPosition, data.get(currentPosition).getPath()));
		}
		Intent data = new Intent();
		data.putParcelableArrayListExtra(INTENT_PREVIEW_IMAGES_FOR_RESULT, pickedImages);
		data.putExtra(INTENT_IS_FINISH_FOR_RESULT, true);
		setResult(RESULT_OK, data);
		finish();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setBackgroundDrawable(null);
		mContext = this;
		ViewUtils.inject(this);
		right.setText("完成");
		
		data = getIntent().getParcelableArrayListExtra(INTENT_ALL_IMAGES_TO_PREVIEW);
		pickedImages = getIntent().getParcelableArrayListExtra(INTENT_PICKED_IMAGES_TO_PREVIEW);
		clickPosition = getIntent().getIntExtra(INTENT_IMAGE_POSITION, 1);//当前点击第几张
		if(data == null || data.size() == 0 || clickPosition > data.size()){//没有数据
			return;
		}
		//数据修正
		data.remove(0);//去掉第一项相机
		if(clickPosition > 0){
			clickPosition --;
		}
		if(pickedImages == null){
			pickedImages = new ArrayList<PickedImage>();
		}
		//页面显示
		title.setText(clickPosition+"/" + data.size());
		if(pickedImages.size() == 0){
			right.setText("完成");
		}else{
			right.setText("完成("+pickedImages.size() + "/9)");
		}
		checkbox.setChecked(false);
		for(PickedImage item : pickedImages){//查询当前是否已选
			if(item.getPath().equals(data.get(clickPosition).getPath())){
				checkbox.setChecked(true);
				break;
			}
		}
		checkbox.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(checkbox.isChecked()){//如果选中
					if(pickedImages.size() < 9){
						pickedImages.add(new PickedImage(currentPosition, data.get(currentPosition).getPath()));
						right.setText("完成(" + pickedImages.size() + "/9)");
					}else {
						Toast.makeText(mContext, "你最多只能选择9张照片", Toast.LENGTH_SHORT).show();
						checkbox.setChecked(false);
					}
				}else {//反选
					Iterator<PickedImage> iterator = pickedImages.iterator();
					while(iterator.hasNext()){
						if(data.get(currentPosition).getPath().equals(iterator.next().getPath())){
							iterator.remove();//删掉该项
						}
					}
					if (pickedImages.size() == 0) {
						right.setText("完成");
						return;
					}
					right.setText("完成(" + pickedImages.size() + "/9)");
				}
			}
		});
		
		List<String> urls = new ArrayList<String>();//取路径
		for(CustomGalleryBean item : data){
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
				title.setText((currentPosition+1)+"/"+data.size());
				//检查是否已选中
				checkbox.setChecked(false);
				for(PickedImage item : pickedImages){
					if(data.get(currentPosition).getPath().equals(item.getPath())){
						checkbox.setChecked(true);
						break;
					}
				}
			}
		});
        
        //单击隐藏title bar
        galleryViewPager.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClicked(View view, int position) {
				if(bar.getVisibility() == View.VISIBLE){//可见下隐藏
					bar.setVisibility(View.GONE);
					bottomContainer.setVisibility(View.GONE);
				}else{//隐藏下则可见
					bar.setVisibility(View.VISIBLE);
					bottomContainer.setVisibility(View.VISIBLE);
				}
			}
		});
        galleryViewPager.setOffscreenPageLimit(3);
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
	
	/**
	 * 隐藏显示状态栏
	 * @param enable
	 */
	@Deprecated
	private void full(boolean enable) {
        if (enable) {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            getWindow().setAttributes(lp);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        } else {
            WindowManager.LayoutParams attr = getWindow().getAttributes();
            attr.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setAttributes(attr);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }
}
