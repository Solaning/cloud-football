package com.kinth.football.view;

import com.kinth.football.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 具有图片和文字的按钮，可以控制文字的显示，图片全屏等
 * @author Sola
 *
 */
public class ImageTextButton extends LinearLayout {
	private Context mContext;
    private ImageView mImgView = null;
    private TextView mTextView = null;
   
    public ImageTextButton(Context context, AttributeSet attrs) {  
        super(context, attrs);  
        // TODO Auto-generated constructor stub  
        LayoutInflater.from(context).inflate(R.layout.image_text_button_layout, this, true);  
        mContext = context;  
        mImgView = (ImageView)findViewById(R.id.img);
        mTextView = (TextView)findViewById(R.id.text);
    }  
  
    public void init(int resId, String str){
    	mImgView.setImageResource(resId);
    	mTextView.setText(str); 
    }
    
   /*设置图片接口*/  
    public void setImageResource(int resId){  
        mImgView.setImageResource(resId);
    }  
    
    /*设置文字接口*/  
    public void setText(String str){
        mTextView.setText(str);  
    }  
    
    /*设置文字大小*/  
    public void setTextSize(float size){
        mTextView.setTextSize(size);
    }
    
    /*设置文字颜色*/
    public void setTextColor(int color){
    	mTextView.setTextColor(color);
    }
    
    /*设置文字控件可见性   */
    public void setTextVisible(){
    	mTextView.setVisibility(View.VISIBLE);
    }
    
    public void setTextInvisible(){
    	mTextView.setVisibility(View.INVISIBLE);
    }
    
    public void setTextGone(){
    	mTextView.setVisibility(View.GONE);
    }
    
    /**
     * 设置占满的图片资源
     */
    public void setFullScreenImage(int resId){
    	setTextGone();
    	mImgView.setPadding(0, 0, 0, 0);
    	mImgView.setImageResource(resId);
    }

	public ImageView getmImgView() {
		return mImgView;
	}

	public void setmImgView(ImageView mImgView) {
		this.mImgView = mImgView;
	}

	public TextView getmTextView() {
		return mTextView;
	}

	public void setmTextView(TextView mTextView) {
		this.mTextView = mTextView;
	}
    
//     /*设置触摸接口*/  
//    public void setOnTouch(OnTouchListener listen){  
//        mImgView.setOnTouchListener(listen);  
//        //mTextView.setOnTouchListener(listen);  
//    }  
  
}  