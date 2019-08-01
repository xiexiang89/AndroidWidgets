package com.edgar.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import androidx.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.edgar.span.BulletSpanCompat;

/**
 * Created by Edgar on 2018/12/25.
 * 基于BulletSpan的封装.
 */
public class DotTextView extends androidx.appcompat.widget.AppCompatTextView {

    private int mDotColor;
    private int mDotSize;
    private int mDotGapWidth;
    private SpannableStringBuilder mDotSpannable;

    public DotTextView(Context context) {
        this(context,null);
    }

    public DotTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public DotTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Resources res = getResources();
        TypedArray ta = context.obtainStyledAttributes(attrs,R.styleable.DotTextView);
        int dotColor = ta.getColor(R.styleable.DotTextView_dotColor,Color.TRANSPARENT);
        int dotSize = ta.getDimensionPixelSize(R.styleable.DotTextView_dotSize,res.getDimensionPixelOffset(R.dimen.default_dot_size));
        mDotGapWidth = ta.getDimensionPixelOffset(R.styleable.DotTextView_dotGapWidth,0);
        setDotSize(dotSize);
        setDotColor(dotColor);
        ta.recycle();
    }

    private CharSequence getRawText() {
        CharSequence text = getText();
        return text != null ? text.toString() : "";
    }

    public void setDotColor(int dotColor) {
        if (mDotColor != dotColor) {
            mDotColor = dotColor;
            setText(getRawText());
        }
    }

    public void setDotSize(int dotSize) {
        if (mDotSize != dotSize) {
            mDotSize = dotSize;
            setText(getRawText());
        }
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(getDotSpan(text), type);
    }

    private CharSequence getDotSpan(CharSequence text) {
        if (TextUtils.isEmpty(text) || mDotColor == Color.TRANSPARENT || mDotSize <= 0) {
            return text;
        }
        if (mDotSpannable == null) {
            mDotSpannable = new SpannableStringBuilder();
        } else {
            mDotSpannable.clear();
            mDotSpannable.clearSpans();
        }
        mDotSpannable.append(text);
        mDotSpannable.setSpan(new BulletSpanCompat(mDotGapWidth,mDotColor,mDotSize),0,text.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return mDotSpannable;
    }
}