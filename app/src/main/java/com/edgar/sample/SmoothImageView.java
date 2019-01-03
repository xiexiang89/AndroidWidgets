package com.edgar.sample;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by Edgar on 2019/1/2.
 */
public class SmoothImageView extends ImageView {

    private static final int STATE_NONE = 0;
    private static final int STATE_IN = 1;
    private static final int STATE_OUT = 2;

    private final Matrix mSmoothMatrix = new Matrix();
    private Bitmap mBitmap;
    private int mState = STATE_NONE;
    private Transform mAnimationTransform;

    public SmoothImageView(Context context) {
        this(context,null);
    }

    public SmoothImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SmoothImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        initBitmap();
    }

    private void initBitmap() {
        Drawable drawable = getDrawable();
        if (drawable == null) {
            return;
        }
        if (drawable instanceof BitmapDrawable) {
            mBitmap = ((BitmapDrawable) drawable).getBitmap();
        } else {
            try {
                Bitmap bitmap;
                if (drawable instanceof ColorDrawable) {
                    bitmap = Bitmap.createBitmap(getMeasuredWidth(),getMeasuredHeight(), Bitmap.Config.ARGB_8888);
                } else {
                    bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),drawable.getIntrinsicHeight(),Bitmap.Config.ARGB_8888);
                }
                Canvas canvas = new Canvas(bitmap);
                drawable.setBounds(0,0,canvas.getWidth(),canvas.getHeight());
                drawable.draw(canvas);
                mBitmap = bitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mBitmap == null) {
            return;
        }
        super.onDraw(canvas);
    }

    private class Transform implements Cloneable {
        float left, top, width, height;
        int alpha;
        float scale;

        @Override
        public Transform clone() {
            Transform obj = null;
            try {
                obj = (Transform) super.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
            return obj;
        }
    }
}