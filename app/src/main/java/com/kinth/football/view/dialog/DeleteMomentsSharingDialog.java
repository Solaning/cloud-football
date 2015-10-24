package com.kinth.football.view.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * 删除朋友圈动态对话框
 * 
 * @author Sola
 *
 */
public class DeleteMomentsSharingDialog extends DialogFragment {
	
	DialogInterface.OnClickListener listener;//点击事件
	
	public void setListener(DialogInterface.OnClickListener listener) {
		this.listener = listener;
	}

	public static DeleteMomentsSharingDialog newInstance(String title, String message) {
		DeleteMomentsSharingDialog adf = new DeleteMomentsSharingDialog();
		Bundle bundle = new Bundle();
		bundle.putString("alert-title", title);
		bundle.putString("alert-message", message);
		adf.setArguments(bundle);
		return adf;
	}

	private String getTitle() {
		return getArguments().getString("alert-title");
	}

	private String getMessage() {
		return getArguments().getString("alert-message");
	}

	/*
	 * 【步骤2】创建view可以通过两个途径，一是fragment中的onCreateView()，
	 * 二是DialogFragment中的onCreateDialog()。 前者适合对自定义的layout进行设置，具有更大的灵活性
	 * 而后者适合对简单dialog进行处理，可以利用Dialog.Builder直接返回Dialog对象
	 * 从生命周期的顺序而言，先执行onCreateDialog()，后执行oonCreateView()，我们不应同时使用两者。
	 */
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder b = new AlertDialog.Builder(getActivity()).setTitle(getTitle()).setMessage(getMessage())
				.setPositiveButton("确定", listener) // 设置回调函数
				.setNegativeButton("取消", new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						
					}});
		return b.create();
	}

}
