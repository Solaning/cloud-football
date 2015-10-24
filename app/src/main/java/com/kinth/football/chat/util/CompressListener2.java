package com.kinth.football.chat.util;

import android.graphics.Bitmap;

public abstract class CompressListener2 {
	public abstract void CompressSuccess(Bitmap bitmap);
	public abstract void CompressFail(String failReason);
}
