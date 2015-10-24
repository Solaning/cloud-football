package com.kinth.football.chat.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.kinth.football.chat.bean.FaceText;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;

public class FaceTextUtils {

	public static List<FaceText> faceTexts = new ArrayList<FaceText>();
	static {
		faceTexts.add(new FaceText("[f001]"));
		faceTexts.add(new FaceText("[f002]"));
		faceTexts.add(new FaceText("[f003]"));
		faceTexts.add(new FaceText("[f004]"));
		faceTexts.add(new FaceText("[f005]"));
		faceTexts.add(new FaceText("[f007]"));
		faceTexts.add(new FaceText("[f008]"));
		faceTexts.add(new FaceText("[f009]"));
		faceTexts.add(new FaceText("[f011]"));
		faceTexts.add(new FaceText("[f012]"));
		faceTexts.add(new FaceText("[f013]"));
		faceTexts.add(new FaceText("[f014]"));
		faceTexts.add(new FaceText("[f015]"));
		faceTexts.add(new FaceText("[f016]"));
		faceTexts.add(new FaceText("[f018]"));
		faceTexts.add(new FaceText("[f019]"));
		faceTexts.add(new FaceText("[f020]"));
		faceTexts.add(new FaceText("[f021]"));
		faceTexts.add(new FaceText("[f022]"));
		faceTexts.add(new FaceText("[f023]"));
		faceTexts.add(new FaceText("[f025]"));
		faceTexts.add(new FaceText("[f026]"));
		faceTexts.add(new FaceText("[f035]"));
		faceTexts.add(new FaceText("[f036]"));
		faceTexts.add(new FaceText("[f037]"));
		faceTexts.add(new FaceText("[f038]"));
		faceTexts.add(new FaceText("[f039]"));
		faceTexts.add(new FaceText("[f040]"));
		faceTexts.add(new FaceText("[f041]"));
		faceTexts.add(new FaceText("[f042]"));
		faceTexts.add(new FaceText("[f045]"));
		faceTexts.add(new FaceText("[f046]"));
		faceTexts.add(new FaceText("[f047]"));
		faceTexts.add(new FaceText("[f048]"));
	}

/*	public static String parse(String s) {
		for (FaceText faceText : faceTexts) {
			s = s.replace("\\[" + faceText.text + "]", faceText.text);
			s = s.replace(faceText.text, "\\[" + faceText.text + "]");
		}
		return s;
	}*/

	/** 
	  * toSpannableString
	  * @return SpannableString
	  * @throws
	  */
	public static SpannableString toSpannableString(Context context, String text) {
		if (!TextUtils.isEmpty(text)) {
			SpannableString spannableString = new SpannableString(text);
			int start = 0;
			Pattern pattern = Pattern.compile("\\[f[0-9]{3}]", Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(text);
			while (matcher.find()) {
				String faceText = matcher.group();
				String key = faceText.substring(1);
				key = key.substring(key.indexOf("[")+1, key.indexOf("]"));
				BitmapFactory.Options options = new BitmapFactory.Options();
				Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
						context.getResources().getIdentifier(key, "drawable", context.getPackageName()), options);
				ImageSpan imageSpan = new ImageSpan(context, bitmap);
				int startIndex = text.indexOf(faceText, start);
				int endIndex = startIndex + faceText.length();
				if (startIndex >= 0)
					spannableString.setSpan(imageSpan, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				start = (endIndex - 1);
			}
			return spannableString;
		} else {
			return new SpannableString("");
		}
	}

	public static SpannableString toSpannableString(Context context, String text, SpannableString spannableString) {

		int start = 0;
		Pattern pattern = Pattern.compile("\\[f[0-9]{3}]", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		while (matcher.find()) {
			String faceText = matcher.group();
			String key = faceText.substring(1);
			BitmapFactory.Options options = new BitmapFactory.Options();
//			options.inSampleSize = 2;
			Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), context.getResources()
					.getIdentifier(key, "drawable", context.getPackageName()), options);
			ImageSpan imageSpan = new ImageSpan(context, bitmap);
			int startIndex = text.indexOf(faceText, start);
			int endIndex = startIndex + faceText.length();
			if (startIndex >= 0)
				spannableString.setSpan(imageSpan, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			start = (endIndex - 1);
		}

		return spannableString;
	}

}
