package com.kinth.football.chat.ui;

import android.app.Activity;
import android.os.Bundle;
import android.text.SpannableString;
import android.widget.TextView;

import com.kinth.football.R;
import com.kinth.football.chat.ChatConstants;
import com.kinth.football.chat.util.FaceTextUtils;

public class BiggerTextSizeActivity extends Activity{

	private TextView tvBiggerTextSize = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自动生成的方法存根
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_blacklist);
		SpannableString spannableString = FaceTextUtils
				.toSpannableString(this, getIntent().getStringExtra(ChatConstants.INTENT_TEXT_MESSAGE));//转为可显示表情的字符串
		tvBiggerTextSize = (TextView) findViewById(R.id.tv_bigger_text_size);
		tvBiggerTextSize.setText(spannableString);
	}
	
}
