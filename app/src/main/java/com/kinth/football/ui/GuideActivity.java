package com.kinth.football.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.kinth.football.R;
import com.kinth.football.view.MyScrollLayout;
import com.kinth.football.view.OnViewChangeListener;

public class GuideActivity extends Activity implements
		OnViewChangeListener {

	private MyScrollLayout mScrollLayout;
	private ImageView[] imgs;
	private int count;
	private int currentItem;
	private LinearLayout pointLLayout;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guide);
		initView();
	}

	private void initView() {
		/*if(getIntent().getStringExtra("status")!=null){
			if(getIntent().getStringExtra("status").equals("only_guide"))
				findViewById(R.id.rl_btns).setVisibility(View.GONE);
		}*/
		
		mScrollLayout = (MyScrollLayout) findViewById(R.id.ScrollLayout);
		pointLLayout = (LinearLayout) findViewById(R.id.llayout);
		count = mScrollLayout.getChildCount();
		imgs = new ImageView[count];
		for (int i = 0; i < count; i++) {
			imgs[i] = (ImageView) pointLLayout.getChildAt(i);
			imgs[i].setEnabled(true);
			imgs[i].setTag(i);
		}
		currentItem = 0;
		imgs[currentItem].setEnabled(false);
		mScrollLayout.SetOnViewChangeListener(this);
	}
	
	@Override
	public void OnViewChange(int position) {
		setcurrentPoint(position);
	}

	private void setcurrentPoint(int position) {
		if (position < 0 || position > count - 1 || currentItem == position) {
			return;
		}
		imgs[currentItem].setEnabled(true);
		imgs[position].setEnabled(false);
		currentItem = position;
	}

	public void skipOnClick(View v) {
		Intent intent = new Intent(GuideActivity.this, LoginActivity.class);
		startActivity(intent);
		finish();
	}

	@Override
	public void OnViewAllRead() {
		// TODO 自动生成的方法存根
		Intent intent = new Intent(GuideActivity.this, LoginActivity.class);
		startActivity(intent);
		finish();
	}
	
}