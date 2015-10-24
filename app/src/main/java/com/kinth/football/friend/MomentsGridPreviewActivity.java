package com.kinth.football.friend;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.kinth.football.R;
import com.kinth.football.friend.touchgallery.GalleryWidget.BasePagerAdapter.OnItemChangeListener;
import com.kinth.football.friend.touchgallery.GalleryWidget.GalleryViewPager;
import com.kinth.football.friend.touchgallery.GalleryWidget.GalleryViewPager.OnItemClickListener;
import com.kinth.football.friend.touchgallery.GalleryWidget.UrlPagerAdapter;
import com.kinth.football.ui.BaseActivity;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

/**
 * 朋友圈主页面预览图片
 * @author Sola
 */
@ContentView(R.layout.activity_moments_preview)
public class MomentsGridPreviewActivity extends BaseActivity{
	public static final String INTENT_IMAGES_TO_PREVIEW = "INTENT_IMAGES_TO_PREVIEW";//图片的Path数据
	public static final String INTENT_IMAGE_POSITION = "INTENT_IMAGE_POSITION";//点击图片的position
	private Context mContext;
	private UrlPagerAdapter pagerAdapter;//适配器
	private ArrayList<String> urls;//
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
	public void fun_1(View v){
		finish();
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		ViewUtils.inject(this);
		right.setVisibility(View.GONE);
		
		urls = getIntent().getStringArrayListExtra(INTENT_IMAGES_TO_PREVIEW);
		clickPosition = getIntent().getIntExtra(INTENT_IMAGE_POSITION, 0);
		if(urls == null || urls.size() == 0){
			return;
		}
		title.setText(clickPosition+"/"+urls.size());
		pagerAdapter = new UrlPagerAdapter(mContext, urls);
        pagerAdapter.setOnItemChangeListener(new OnItemChangeListener()
		{
			@Override
			public void onItemChange(int position)
			{
				//图片切换完时的回调
				currentPosition = position;
				title.setText((currentPosition+1)+"/"+urls.size());
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
