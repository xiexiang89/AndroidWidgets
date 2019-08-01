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
    //顺时针方向
    private static final int TOP_LEFT = 0;
    private static final int TOP_RIGHT = 2;
    private static final int BOTTOM_RIGHT = 4;
    private static final int BOTTOM_LEFT = 6;

    private boolean mHaveFrame = false;
    private Bitmap mBitmap;
    private BitmapShader mBitmapShader;
    private Paint mDrawablePaint;
    private Matrix mDrawableMatrix;
    private Path mShaderPath;
    private int mBorderSize;
    private final RectF mDrawableRectF;
    private final RectF mBorderRectF;
    private final Paint mBorderPaint;
    private float mCircleBorderRadius;
    private float mCircleDrawableRadius;
    private float[] mDrawableRadii;
    private float[] mBorderRadii;
    private boolean mIsCircle;  //圆形
    private boolean mSupportRounded;
    private boolean mBorderOverlay;

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
        mDrawablePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBorderRectF = new RectF();
        mDrawableRectF = new RectF();
        mDrawableRadii = new float[8];
        mBorderRadii = mDrawableRadii.clone();
        mShaderPath = new Path();
        TypedArray ta = context.obtainStyledAttributes(attrs,R.styleable.RoundedImageView,defStyleAttr,0);
        int borderColor = ta.getColor(R.styleable.RoundedImageView_borderColor,res.getColor(R.color.default_circle_border_color));
        mBorderSize = ta.getDimensionPixelSize(R.styleable.RoundedImageView_borderSize,0);
        mBorderOverlay = ta.getBoolean(R.styleable.RoundedImageView_borderOverlay,true);
        mIsCircle = ta.getBoolean(R.styleable.RoundedImageView_isCircle,false);
        mSupportRounded = ta.getBoolean(R.styleable.RoundedImageView_supportRounded,true);
        mCircleDrawableRadius = ta.getDimension(R.styleable.RoundedImageView_roundRadius,0);
        float topLeftRadius = ta.getDimension(R.styleable.RoundedImageView_roundTopLeftRadius, mCircleDrawableRadius);
        float topRightRadius = ta.getDimension(R.styleable.RoundedImageView_roundTopRightRadius, mCircleDrawableRadius);
        float bottomLeftRadius = ta.getDimension(R.styleable.RoundedImageView_roundBottomLeftRadius, mCircleDrawableRadius);
        float bottomRightRadius = ta.getDimension(R.styleable.RoundedImageView_roundBottomRightRadius, mCircleDrawableRadius);
        setCornerRadii(topLeftRadius,topRightRadius,bottomLeftRadius,bottomRightRadius);
        ta.recycle();
        setBorderColor(borderColor);
        mBorderPaint.setStrokeWidth(mBorderSize);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        initBitmap();
    }

    public void setTopLeftRadii(float radii) {
        mBorderRadii[TOP_LEFT] = mBorderRadii[TOP_LEFT+1] = radii;
        mDrawableRadii[TOP_LEFT] = mDrawableRadii[TOP_LEFT+1] = radii;
        updateDrawable();
    }

    public void setTopRightRadii(float radii) {
        mBorderRadii[TOP_RIGHT] = mBorderRadii[TOP_RIGHT+1] = radii;
        mDrawableRadii[TOP_RIGHT] = mDrawableRadii[TOP_RIGHT+1] = radii;
        updateDrawable();
    }

    public void setBottomLeftRadii(float radii) {
        mBorderRadii[BOTTOM_LEFT] = mBorderRadii[BOTTOM_LEFT+1] = radii;
        mDrawableRadii[BOTTOM_LEFT] = mDrawableRadii[BOTTOM_LEFT+1] = radii;
        updateDrawable();
    }

    public void setBottomRightRadii(float radii) {
        mBorderRadii[BOTTOM_RIGHT] = mBorderRadii[BOTTOM_RIGHT+1] = radii;
        mDrawableRadii[BOTTOM_RIGHT] = mDrawableRadii[BOTTOM_RIGHT+1] = radii;
        updateDrawable();
    }

    public void setCornerRadii(float topLeft, float topRight, float bottomRight, float bottomLeft) {
        updateRadii(mBorderRadii,topLeft,topRight,bottomRight,bottomLeft);
        updateRadii(mDrawableRadii,topLeft,topRight,bottomRight,bottomLeft);
        updateDrawable();
    }

    private void updateRadii(float[] cornerRadii, float topLeft, float topRight, float bottomRight, float bottomLeft) {
        cornerRadii[TOP_LEFT] = cornerRadii[TOP_LEFT+1] = topLeft;
        cornerRadii[TOP_RIGHT] = cornerRadii[TOP_RIGHT+1] = topRight;
        cornerRadii[BOTTOM_RIGHT] = cornerRadii[BOTTOM_RIGHT+1] = bottomRight;
        cornerRadii[BOTTOM_LEFT] = cornerRadii[BOTTOM_LEFT+1] = bottomLeft;
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
            mBorderPaint.setStrokeWidth(mBorderSize);
            updateDrawable();
        }
    }

    public void setCircle(boolean circle) {
        if (mIsCircle != circle) {
            mIsCircle = circle;
            updateDrawable();
        }
    }

    @Override
    public void setScaleType(ScaleType scaleType) {
        if (!mSupportRounded) {
            super.setScaleType(scaleType);
        }
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
        updateDrawable();
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
            mBitmap = null;
            invalidate();
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
        updateDrawable();
    }

    private RectF calculateBounds() {
        int availableWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        int availableHeight = getHeight() - getPaddingTop() - getPaddingBottom();

        int sideLength = Math.min(availableWidth, availableHeight);

        float left = getPaddingLeft() + (availableWidth - sideLength) / 2f;
        float top = getPaddingTop() + (availableHeight - sideLength) / 2f;

        return new RectF(left, top, left + sideLength, top + sideLength);
    }

    private void updateDrawable() {
        if (mBitmap == null || !mHaveFrame) {
            return;
        }
        if (mBitmapShader == null) {
            mBitmapShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        }
        //set bitmap and border bounds
        mBorderRectF.set(calculateBounds());
        mDrawableRectF.set(mBorderRectF);
        if (!mBorderOverlay && mBorderSize > 0) {
            if (!mIsCircle) {
                float offset = mBorderSize/2f;
                mDrawableRectF.inset(offset,offset);
                updateRadii(mDrawableRadii,
                        mBorderRadii[TOP_LEFT]-offset, mBorderRadii[TOP_RIGHT]-offset,
                        mBorderRadii[BOTTOM_RIGHT]-offset,mBorderRadii[BOTTOM_LEFT]-offset);
            } else {
                mDrawableRectF.inset(mBorderSize,mBorderSize);
            }
        }
        if (mIsCircle) {
            mCircleBorderRadius = Math.min((mBorderRectF.width()-mBorderSize)/2f,(mBorderRectF.height()-mBorderSize)/2f);
            mCircleDrawableRadius = Math.min(mDrawableRectF.width()/2f, mDrawableRectF.height()/2f);
        }
        //update image matrix
        updateDrawableMatrix(mDrawableRectF.width(), mDrawableRectF.height());
        invalidate();
    }

    private void updateDrawableMatrix(float fwidth, float fheight) {
        int bitmapWidth = mBitmap.getWidth();
        int bitmapHeight = mBitmap.getHeight();
        if (bitmapWidth <= 0 || bitmapHeight <= 0) {
            return;
        }
        if (mDrawableMatrix == null) {
            mDrawableMatrix = new Matrix();
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

        mDrawableMatrix.set(null);
        mDrawableMatrix.setScale(scale, scale);
        mDrawableMatrix.postTranslate((int) (dx + 0.5f) + mDrawableRectF.left, (int) (dy + 0.5f) + mDrawableRectF.top);
        mBitmapShader.setLocalMatrix(mDrawableMatrix);
        mDrawablePaint.setShader(mBitmapShader);
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
            canvas.drawCircle(mDrawableRectF.centerX(), mDrawableRectF.centerY(), mCircleDrawableRadius, mDrawablePaint);
            if (mBorderSize > 0) {
                canvas.drawCircle(mBorderRectF.centerX(),mBorderRectF.centerY(), mCircleBorderRadius, mBorderPaint);
            }
        } else {
            drawRoundImage(canvas);
        }
    }

    private void drawRoundImage(Canvas canvas) {
        mShaderPath.reset();
        mShaderPath.addRoundRect(mDrawableRectF, mDrawableRadii, Path.Direction.CW);
        canvas.drawPath(mShaderPath, mDrawablePaint);
        mShaderPath.reset();
        if (mBorderSize > 0) {
            mShaderPath.addRoundRect(mBorderRectF, mBorderRadii, Path.Direction.CW);
            canvas.drawPath(mShaderPath,mBorderPaint);
        }
    }
}