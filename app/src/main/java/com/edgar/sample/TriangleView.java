package com.edgar.sample;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Edgar on 2019/1/5.
 */
public class TriangleView extends View {

    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path mPath = new Path();
    private int mTriangleWidth;
    private int mTriangleHeight;

    public TriangleView(Context context) {
        this(context,null);
    }

    public TriangleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public TriangleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final Resources res = getResources();
        mTriangleWidth = res.getDimensionPixelOffset(R.dimen.triangle_width);
        mTriangleHeight = res.getDimensionPixelOffset(R.dimen.triangle_height);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.rotate(90,mTriangleWidth/2,mTriangleHeight/2);
        int start = mTriangleWidth/2;
        mPath.moveTo(start,0);  //起始位置点
        mPath.lineTo(0,mTriangleHeight);  //终止位置点
        mPath.lineTo(mTriangleWidth,mTriangleHeight);  //上一个点移动到目标点
        mPath.close();
        canvas.drawPath(mPath,mPaint);
        canvas.restore();
    }
}