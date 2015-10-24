//package com.kinth.football.ui.team;
//
//import android.os.Bundle;
//import android.view.MotionEvent;
//import android.widget.ImageView;
//
//import com.kinth.football.R;
//import com.kinth.football.ui.BaseActivity;
//import com.kinth.football.util.PhotoUtil;
//import com.nostra13.universalimageloader.core.ImageLoader;
//
//public class ShowImageActivity extends BaseActivity {
//
//	private ImageView show_img = null;
//	
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		// TODO Auto-generated method stub
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_showimage);
//		
//		show_img = (ImageView)this.findViewById(R.id.show_img);
//		
//		String img_URL = getIntent().getExtras().getString("img_URL");
//		String badgeOrFamily = getIntent().getExtras().getString("badgeOrFamily");
//		if(!img_URL.equals("null")){
//			ImageLoader.getInstance().displayImage(PhotoUtil.getAllPhotoPath(img_URL), show_img);
//		}else{
//			if(badgeOrFamily.equals("isBadge"))
//				show_img.setImageResource(R.drawable.icon_default_head);
//			else
//				show_img.setImageResource(R.drawable.default_family_photo);
//		}
//	}
//	
//	@Override
//	public boolean onTouchEvent(MotionEvent event) {
//		// TODO Auto-generated method stub
//		finish();
//		return true;
//	}
//}
