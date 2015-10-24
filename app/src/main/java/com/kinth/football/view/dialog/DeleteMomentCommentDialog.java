package com.kinth.football.view.dialog;

import com.kinth.football.R;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;

/**
 * 删除评论对话框
 * @author Sola
 *
 */
public class DeleteMomentCommentDialog extends DialogFragment {

	OnClickListener listener;//点击事件
	
	public void setListener(OnClickListener listener) {
		this.listener = listener;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		View view = inflater.inflate(R.layout.moment_delete_comment_layout, container);
		view.findViewById(R.id.tv_delete_comment).setOnClickListener(listener);
		return view;
	}
}
