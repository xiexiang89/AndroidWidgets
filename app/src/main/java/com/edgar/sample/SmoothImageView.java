package com.edgar.sample;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

/**
 * Created by Edgar on 2019/1/2.
 */
public class SmoothImageView extends ImageView {

    private final Matrix mSmoothMatrix = new Matrix();
    private Bitmap mBitmap;
    private Transform mStartTransForm;
    private Transform mEndTransform;
    private Transform mAnimationTransform;
    private boolean mAnimatorPaying;
    private int mBitmapWidth;
    private int mBitmapHeight;
    private Rect mThumbRect;

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

    public void showImage(Bitmap bitmap) {
        mBitmap = bitmap;
        setImageBitmap(bitmap);
        ViewTreeObserver viewTreeObserver = getViewTreeObserver();
        viewTreeObserver.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (getWidth() > 0 && getHeight() > 0) {
                    initTransform();
                }
                return false;
            }
        });
    }

    private void initTransform() {
        mStartTransForm = new Transform();
        mStartTransForm.alpha = 0;
        if (mThumbRect == null) {
            mThumbRect = new Rect();
        }
        mStartTransForm.left = mThumbRect.left;
        mStartTransForm.top = mThumbRect.top;
        mStartTransForm.width = mThumbRect.width();
        mStartTransForm.height = mThumbRect.height();
        //开始时以CenterCrop方式显示，缩放图片使图片的一边等于起始区域的一边，另一边大于起始区域
        float startScaleX = (float) mThumbRect.width() / mBitmapWidth;
        float startScaleY = (float) mThumbRect.height() / mBitmapHeight;
        mStartTransForm.scale = startScaleX > startScaleY ? startScaleX : startScaleY;

        //结束时以fitCenter方式显示，缩放图片使图片的一边等于View的一边，另一边大于View
        float endScaleX = (float) getWidth() / mBitmapWidth;
        float endScaleY = (float) getHeight() / mBitmapHeight;
        mEndTransform = new Transform();
        mEndTransform.scale = endScaleX < endScaleY ? endScaleX : endScaleY;
        mEndTransform.alpha = 255;
        int endBitmapWidth = (int) (mEndTransform.scale * mBitmapWidth);
        int endBitmapHeight = (int) (mEndTransform.scale * mBitmapHeight);
        mEndTransform.left = (getWidth() - endBitmapWidth) / 2.f;
        mEndTransform.top = (getHeight() - endBitmapHeight) / 2.f;
        mEndTransform.width = endBitmapWidth;
        mEndTransform.height = endBitmapHeight;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mBitmap == null) {
            return;
        }
        if (!mAnimatorPaying) {
            super.onDraw(canvas);
        }
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