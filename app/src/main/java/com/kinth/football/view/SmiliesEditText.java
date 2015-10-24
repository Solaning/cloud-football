
package com.kinth.football.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * 可显示图片、表情的输入框
 * 
 * @author Sola
 */
public class SmiliesEditText extends EditText {

    public SmiliesEditText(Context context) {
        super(context);
    }

    public SmiliesEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void insertIcon(String title, int id) {
        // SpannableString连续的字符串，长度不可变，同时可以附加一些object;可变的话使用SpannableStringBuilder，参考sdk文档
        SpannableString ss = new SpannableString("[" + title + "]");
        // 得到要显示图片的资源
        Drawable d = getResources().getDrawable(id);
        // 设置高度
        d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
        // 跨度底部应与周围文本的基线对齐
        ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BASELINE);
        // 附加图片，最后一个参数类似数学中的集合,[5,12)表示从5到12，包括5但不包括12
        ss.setSpan(span, 0, title.length() + 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        // this.append(ss);//追加的方式才能显示多个图片
        int start = this.getSelectionStart();
        int end = this.getSelectionEnd();
        if (start == end) {//
            this.getText().insert(start, ss);
        } else {// 有内容选中
            this.getText().replace(start, end, ss);
        }
        // 光标移到最后。
        this.setSelection(start + ss.length());
    }
}
