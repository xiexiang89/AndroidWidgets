package com.edgar.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by Edgar on 2018/12/29.
 */
public class CircleImageView extends ImageView {

    private static final ScaleType CENTER_CROP = ScaleType.CENTER_CROP;
    private static final int COLOR_DRAWABLE_SIZE = 2;
    private static final Bitmap.Config BITMAP_CONFIG = Bitmap.Config.ARGB_8888;

    private boolean mHaveFrame = false;
    private Bitmap mBitmap;
    private BitmapShader mBitmapShader;
    private Paint mBitmapPaint;
    private Matrix mBitmapMatrix;
    private int mBorderSize;
    private int mBorderColor;
    private final RectF mBitmapRectF;
    private final RectF mBorderRectF;
    private final Paint mBorderPaint;
    private float mBorderRadius;
    private float mBitmapRadius;
    private boolean mBorderOverly;

    public CircleImageView(Context context) {
        this(context, null);
    }

    public CircleImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        super.setScaleType(CENTER_CROP);
        final Resources res = getResources();
        mBitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBorderRectF = new RectF();
        mBitmapRectF = new RectF();
        TypedArray ta = context.obtainStyledAttributes(attrs,R.styleable.CircleImageView,defStyleAttr,0);
        mBorderColor = ta.getColor(R.styleable.CircleImageView_borderColor,res.getColor(R.color.default_circle_border_color));
        mBorderSize = ta.getColor(R.styleable.CircleImageView_borderSize,res.getDimensionPixelOffset(R.dimen.default_circle_border_size));
        mBorderOverly = ta.getBoolean(R.styleable.CircleImageView_borderOverly,false);
        ta.recycle();
        mBorderPaint.setColor(mBorderColor);
        mBorderPaint.setStrokeWidth(mBorderSize);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        initBitmap();
    }

    @Override
    public ScaleType getScaleType() {
        return CENTER_CROP;
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
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mHaveFrame = true;
        updateCircleImage();
    }

    private void initBitmap() {
        Drawable drawable = getDrawable();
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
        mBitmapShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

        //update border bounds
        mBorderRectF.set(pLeft,pTop,pLeft + availableWidth, pTop + availableHeight);
        mBorderRadius = Math.min((mBorderRectF.width()-mBorderSize)/2f,(mBorderRectF.height()-mBorderSize)/2f);
        mBitmapRectF.set(mBorderRectF);
        if (!mBorderOverly && mBorderSize > 0) {
            mBitmapRectF.inset(mBorderSize,mBorderSize);
        }
        mBitmapRadius = Math.min(mBitmapRectF.width()/2f,mBitmapRectF.height()/2f);
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
        if (mBitmap == null) {
            return;
        }
        canvas.drawCircle(mBitmapRectF.centerX(), mBitmapRectF.centerY(),mBitmapRadius, mBitmapPaint);
        if (mBorderSize > 0) {
            canvas.drawCircle(mBorderRectF.centerX(),mBorderRectF.centerY(),mBorderRadius, mBorderPaint);
        }
    }
}