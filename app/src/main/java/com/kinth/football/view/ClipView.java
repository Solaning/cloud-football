package com.kinth.football.view;

import com.kinth.football.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

/**
 * @author
 */
public class ClipView extends View {
	Paint mPaint = new Paint();        //绘制时所使用的画笔
	Path path = new Path();
	
	public ClipView(Context context) {
		super(context);
	}

	public ClipView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ClipView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int width = this.getWidth();
		int height = this.getHeight();
		
		mPaint.reset();
		mPaint.setAntiAlias(true);
		mPaint.setColor(0xaa000000);
		canvas.drawRect(0, 0, width, height / 4, mPaint);
		canvas.drawRect(0, height / 4, (width - height / 2) / 2,
				height * 3 / 4, mPaint);
		canvas.drawRect((width + height / 2) / 2, height / 4, width,
				height * 3 / 4, mPaint);
		canvas.drawRect(0, height * 3 / 4, width, height, mPaint);
		mPaint.setColor(getResources().getColor(R.color.white));
		canvas.drawRect((width - height / 2) / 2 - 1, height / 4 - 1,
				(width + height / 2) / 2 + 1, (height / 4), mPaint);
		canvas.drawRect((width - height / 2) / 2 - 1, height / 4,
				(width - height / 2) / 2, height * 3 / 4, mPaint);
		canvas.drawRect((width + height / 2) / 2, height / 4,
				(width + height / 2) / 2 + 1, height * 3 / 4, mPaint);
		canvas.drawRect((width - height / 2) / 2 - 1, height * 3 / 4,
				(width + height / 2) / 2 + 1, height * 3 / 4 + 1, mPaint);

//		mPaint.reset();
//		mPaint.setAntiAlias(true);
//		mPaint.setColor(0xaa000000);
//		path.moveTo(0,0);
//		path.lineTo(0, height);
//		path.lineTo(width, height);
//		path.lineTo(width, 0);
//		path.close();
//		path.addCircle(width/2, height/2, height/4, Path.Direction.CCW);
//		canvas.drawPath(path, mPaint);
	}
}
