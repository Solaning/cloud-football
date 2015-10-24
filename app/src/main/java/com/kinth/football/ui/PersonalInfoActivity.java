package com.kinth.football.ui;

import com.kinth.football.R;

import android.os.Bundle;
import android.view.View;

public class PersonalInfoActivity extends BaseActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personal_info);
		
	}
	
	public void btnOnClick(View v) {
		switch (v.getId()) {
		case R.id.rlt_nick:
			
			break;
		case R.id.rlt_sex:
			
			break;
		case R.id.rlt_email:
			
			break;
		default:
			break;
		}
	}
	
	
}
