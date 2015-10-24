package com.kinth.football.ui.team;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.kinth.football.R;
import com.kinth.football.ui.BaseActivity;
import com.lidroid.xutils.view.annotation.ViewInject;

public class MemberActivity extends BaseActivity {
	@ViewInject(R.id.nav_left)
	private ImageButton back;

	@ViewInject(R.id.nav_title)
	private TextView nav_title;

	@ViewInject(R.id.nav_right_image)
	private ImageView right;
  @Override
protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	right.setImageResource(R.drawable.add);//
	nav_title.setText("球队成员");
}
}
