package com.kinth.football.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageButton;

public class MyImageButton extends ImageButton {
    private String text = "";  //要显示的文字
    private int color; //文字的颜色
    private float textSize;//文字大小
    
    public MyImageButton(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
	}

	public MyImageButton(Context context, AttributeSet attrs) {
        super(context,attrs);
    }
	
	public MyImageButton(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
      
    public void setText(String text){
        this.text = text;       //设置文字
    }
      
    public void setColor(int color){
        this.color = color;    //设置文字颜色
    }
    
    public void setTextSize(float textSize){
    	this.textSize = textSize;
    }
      
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(color);
        paint.setTextSize(textSize);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        canvas.drawText(text, width/2, height/2, paint);  //绘制文字
    }
  
}
