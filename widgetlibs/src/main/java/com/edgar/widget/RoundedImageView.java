package com.edgar.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

/**
 * Created by Edgar on 2018/12/29.
 */
public class RoundedImageView extends AppCompatImageView {

    private static final ScaleType CENTER_CROP = ScaleType.CENTER_CROP;
    private static final int COLOR_DRAWABLE_SIZE = 2;
    private static final Bitmap.Config BITMAP_CONFIG = Bitmap.Config.ARGB_8888;
    private static final int TOP_LEFT = 0;
    private static final int TOP_RIGHT = 2;
    private static final int BOTTOM_LEFT = 4;
    private static final int BOTTOM_RIGHT = 6;

    private boolean mHaveFrame = false;
    private Bitmap mBitmap;
    private BitmapShader mBitmapShader;
    private Paint mBitmapPaint;
    private Matrix mBitmapMatrix;
    private Path mShaderPath;
    private int mBorderSize;
    private final RectF mBitmapRectF;
    private final RectF mBorderRectF;
    private final Paint mBorderPaint;
    private float mCircleBorderRadius;
    private float mBitmapRadius;
    private float[] mRoundRadiusArray;
    private boolean mIsCircle;  //圆形
    private boolean mSupportRounded;

    public RoundedImageView(Context context) {
        this(context, null);
    }

    public RoundedImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundedImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        super.setScaleType(CENTER_CROP);
        final Resources res = getResources();
        mBitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBorderRectF = new RectF();
        mBitmapRectF = new RectF();
        mRoundRadiusArray = new float[8];
        mShaderPath = new Path();
        TypedArray ta = context.obtainStyledAttributes(attrs,R.styleable.RoundedImageView,defStyleAttr,0);
        int borderColor = ta.getColor(R.styleable.RoundedImageView_borderColor,res.getColor(R.color.default_circle_border_color));
        mBorderSize = ta.getDimensionPixelSize(R.styleable.RoundedImageView_borderSize,0);
        mIsCircle = ta.getBoolean(R.styleable.RoundedImageView_isCircle,false);
        mSupportRounded = ta.getBoolean(R.styleable.RoundedImageView_supportRounded,true);
        mBitmapRadius = ta.getDimension(R.styleable.RoundedImageView_roundRadius,0);
        float topLeftRadius = ta.getDimension(R.styleable.RoundedImageView_roundTopLeftRadius,mBitmapRadius);
        float topRightRadius = ta.getDimension(R.styleable.RoundedImageView_roundTopRightRadius,mBitmapRadius);
        float bottomLeftRadius = ta.getDimension(R.styleable.RoundedImageView_roundBottomLeftRadius,mBitmapRadius);
        float bottomRightRadius = ta.getDimension(R.styleable.RoundedImageView_roundBottomRightRadius,mBitmapRadius);
        setRadius(topLeftRadius,topRightRadius,bottomLeftRadius,bottomRightRadius);
        ta.recycle();
        setBorderColor(borderColor);
        mBorderPaint.setStrokeWidth(mBorderSize);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        initBitmap();
    }

    public void setRadius(float topLeft, float topRight, float bottomRight, float bottomLeft) {
        mRoundRadiusArray[TOP_LEFT] = mRoundRadiusArray[TOP_LEFT+1] = topLeft;
        mRoundRadiusArray[TOP_RIGHT] = mRoundRadiusArray[TOP_RIGHT+1] = topRight;
        mRoundRadiusArray[BOTTOM_RIGHT] = mRoundRadiusArray[BOTTOM_RIGHT+1] = bottomRight;
        mRoundRadiusArray[BOTTOM_LEFT] = mRoundRadiusArray[BOTTOM_LEFT+1] = bottomLeft;
        updateCircleImage();
    }

    public void setBorderColor(@ColorInt int borderColor) {
        if (mBorderPaint.getColor() != borderColor) {
            mBorderPaint.setColor(borderColor);
            invalidate();
        }
    }

    public void setBorderSize(int borderSize) {
        if (mBorderSize != borderSize) {
            mBorderSize = borderSize;
            updateCircleImage();
            invalidate();
        }
    }

    public void setCircle(boolean circle) {
        mIsCircle = circle;
        updateCircleImage();
    }

    @Override
    public ScaleType getScaleType() {
        if (mSupportRounded) {
            return CENTER_CROP;
        } else {
            return super.getScaleType();
        }
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        initBitmap();
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        initBitmap();
    }

    @Override
    public void setImageDrawable(@Nullable Drawable drawable) {
        super.setImageDrawable(drawable);
        initBitmap();
    }

    @Override
    protected boolean setFrame(int l, int t, int r, int b) {
        boolean change = super.setFrame(l,t,r,b);
        mHaveFrame = true;
        updateCircleImage();
        return change;
    }

    @Override
    protected void onDetachedFromWindow() {
        recycleBitmap();
        super.onDetachedFromWindow();
    }

    private void recycleBitmap() {
        if (mBitmap != null && mBitmap.isMutable() &&!mBitmap.isRecycled()) {
            mBitmap.recycle();
        }
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
                    bitmap = Bitmap.createBitmap(COLOR_DRAWABLE_SIZE,COLOR_DRAWABLE_SIZE, BITMAP_CONFIG);
                } else {
                    bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),drawable.getIntrinsicHeight(),BITMAP_CONFIG);
                }
                Canvas canvas = new Canvas(bitmap);
                drawable.setBounds(0,0,canvas.getWidth(),canvas.getHeight());
                drawable.draw(canvas);
                mBitmap = bitmap;
                mBitmapShader = null;
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
        updateCircleImage();
    }

    private void updateCircleImage() {
        if (mBitmap == null || !mHaveFrame) {
            return;
        }
        int pLeft = getPaddingLeft();
        int pTop = getPaddingTop();
        int availableWidth = getMeasuredWidth() - pLeft - getPaddingRight();
        int availableHeight = getMeasuredHeight() - pTop - getPaddingBottom();
        if (mBitmapShader == null) {
            mBitmapShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        }
        //set bitmap and border bounds
        mBorderRectF.set(pLeft,pTop,pLeft + availableWidth, pTop + availableHeight);
        mBitmapRectF.set(mBorderRectF);
        if (mIsCircle) {
            mCircleBorderRadius = Math.min((mBorderRectF.width()-mBorderSize)/2f,(mBorderRectF.height()-mBorderSize)/2f);
            mBitmapRadius = Math.min(mBitmapRectF.width()/2f,mBitmapRectF.height()/2f);
        }
        //update image matrix
        updateImageMatrix(mBitmapRectF.width(),mBitmapRectF.height());
    }

    private void updateImageMatrix(float fwidth, float fheight) {
        int bitmapWidth = mBitmap.getWidth();
        int bitmapHeight = mBitmap.getHeight();
        if (bitmapWidth <= 0 || bitmapHeight <= 0) {
            return;
        }
        if (mBitmapMatrix == null) {
            mBitmapMatrix = new Matrix();
        }
        float scale;
        float dx = 0, dy = 0;
        if (bitmapWidth * fheight > fwidth * bitmapHeight) {
            scale = fheight / (float) bitmapHeight;
            dx = (fwidth - bitmapWidth * scale) * 0.5f;
        } else {
            scale = fwidth / (float) bitmapWidth;
            dy = (fheight - bitmapHeight * scale) * 0.5f;
        }

        mBitmapMatrix.set(null);
        mBitmapMatrix.setScale(scale, scale);
        mBitmapMatrix.postTranslate(Math.round(dx), Math.round(dy));
        mBitmapShader.setLocalMatrix(mBitmapMatrix);
        mBitmapPaint.setShader(mBitmapShader);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!mSupportRounded) {
            super.onDraw(canvas);
            return;
        }
        if (mBitmap == null) {
            return;
        }
        if (mIsCircle) {
            canvas.drawCircle(mBitmapRectF.centerX(), mBitmapRectF.centerY(),mBitmapRadius, mBitmapPaint);
            if (mBorderSize > 0) {
                canvas.drawCircle(mBorderRectF.centerX(),mBorderRectF.centerY(), mCircleBorderRadius, mBorderPaint);
            }
        } else {
            drawRoundImage(canvas);
        }
    }

    private void drawRoundImage(Canvas canvas) {
        mShaderPath.reset();
        mShaderPath.addRoundRect(mBitmapRectF, mRoundRadiusArray, Path.Direction.CW);
        canvas.drawPath(mShaderPath,mBitmapPaint);
        mShaderPath.reset();
        if (mBorderSize > 0) {
            mShaderPath.addRoundRect(mBorderRectF,mRoundRadiusArray, Path.Direction.CW);
            canvas.drawPath(mShaderPath,mBorderPaint);
        }
    }
}