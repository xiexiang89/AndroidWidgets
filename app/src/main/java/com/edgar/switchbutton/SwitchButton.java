package com.edgar.switchbutton;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.CompoundButton;

/**
 * Created by Edgar on 2018/12/24.
 */
public class SwitchButton extends CompoundButton {

    private static final String TAG = "SwitchButton";
    private static final int ANIMATION_DURATION = 200;
    private static final int TOUCH_MODE_IDLE = 0;
    private static final int TOUCH_MODE_DOWN = 1;
    private static final int TOUCH_MODE_DRAGGING = 2;

    private Drawable mThumbDrawable;
    private Drawable mTrackDrawable;
    private int mThumbPadding;
    private float mLastTouchX;
    private float mLastTouchY;
    private int mTouchSlop;
    private int mTouchMode = TOUCH_MODE_IDLE;
    private ValueAnimator mThumbAnimation;
    private int mThumbPosition;

    public SwitchButton(Context context) {
        this(context,null);
    }

    public SwitchButton(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SwitchButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Resources res = getResources();
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SwitchButton, defStyleAttr,0);
        mThumbDrawable = ta.getDrawable(R.styleable.SwitchButton_android_thumb);
        mTrackDrawable = ta.getDrawable(R.styleable.SwitchButton_android_track);
        mThumbPadding = ta.getDimensionPixelOffset(R.styleable.SwitchButton_thumbPadding,res.getDimensionPixelOffset(R.dimen.default_thumb_padding));
        ta.recycle();
        final ViewConfiguration config = ViewConfiguration.get(context);
        mTouchSlop = config.getScaledTouchSlop();
        mThumbDrawable.setCallback(this);
        mTrackDrawable.setCallback(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int trackWidth = mTrackDrawable.getIntrinsicWidth();
        final int trackHeight = mTrackDrawable.getIntrinsicHeight();
        final int thumbWidth = getThumbWidth();
        final int thumbHeight = getThumbHeight();
        int height = Math.max(thumbHeight,trackHeight);
        setMeasuredDimension(trackWidth, height);
        int thumbTop = (trackHeight - thumbHeight)/2;
        mThumbPosition = isChecked() ? getSwitchLeft() : mThumbPadding;
        mThumbDrawable.setBounds(mThumbPosition,thumbTop,mThumbPosition + thumbWidth, thumbTop + thumbHeight);
        mTrackDrawable.setBounds(0,0,trackWidth,trackHeight);
    }

    private int getThumbWidth() {
        return mThumbDrawable.getIntrinsicWidth();
    }

    private int getThumbHeight() {
        return mThumbDrawable.getIntrinsicHeight();
    }

    private boolean hitThumb(float x, float y) {
        if (mThumbDrawable == null) {
            return false;
        }

        final Rect bounds = mThumbDrawable.getBounds();
        final int thumbTop = bounds.top;
        final int thumbLeft = bounds.left;
        final int thumbRight = bounds.right;
        final int thumbBottom = bounds.bottom;
        return x > thumbLeft && x < thumbRight && y > thumbTop && y < thumbBottom;
    }

    private int getAvailableWidth() {
        return getMeasuredWidth() - mThumbPadding*2 - getThumbWidth();
    }

    private int getSwitchLeft() {
        return getMeasuredWidth() - mThumbPadding - getThumbWidth();
    }

    @Override
    public void toggle() {
        setChecked(!isChecked());
    }

    @Override
    public void setChecked(boolean checked) {
        super.setChecked(checked);
        checked = isChecked();
        if (ViewCompat.isAttachedToWindow(this) && ViewCompat.isLaidOut(this)) {
            animationChecked(checked);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN: {
                final float x = event.getX();
                final float y = event.getY();
                if (hitThumb(x, y)) {
                    mTouchMode = TOUCH_MODE_DOWN;
                    mLastTouchX = x;
                    mLastTouchY = y;
                    return true;
                }
                break;
            }
            case MotionEvent.ACTION_MOVE:
                switch (mTouchMode) {
                    case TOUCH_MODE_DOWN: {
                        final float x = event.getX();
                        final float y = event.getY();
                        if (Math.abs(x - mLastTouchX) > mTouchSlop ||
                                Math.abs(y - mLastTouchY) > mTouchSlop) {
                            mTouchMode = TOUCH_MODE_DRAGGING;
                            getParent().requestDisallowInterceptTouchEvent(true);
                            mLastTouchX = x;
                            mLastTouchY = y;
                            return true;
                        }
                        break;
                    }
                    case TOUCH_MODE_DRAGGING: {
                        startDragging(event.getX());
                        return true;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                if (mTouchMode == TOUCH_MODE_DRAGGING) {
                    final Rect bounds = mThumbDrawable.getBounds();
                    int centerX = bounds.centerX();
                    setChecked(centerX >= getMeasuredWidth()/2);
                    super.onTouchEvent(event);
                    return true;
                }
                mTouchMode = TOUCH_MODE_IDLE;
                break;
            }
        }
        return super.onTouchEvent(event);
    }

    private void startDragging(float x) {
        final float thumbScrollOffset = x - mLastTouchX;
        int newPosition = (int) (thumbScrollOffset+mThumbDrawable.getBounds().left);
        if (newPosition < mThumbPadding) {
            newPosition = mThumbPadding;
        }
        final int switchLeft = getSwitchLeft();
        if (newPosition > switchLeft) {
            newPosition = switchLeft;
        }
        setThumbPosition(newPosition);
        mLastTouchX = x;
    }

    private void animationChecked(boolean checked) {
        final int targetPosition = checked ? getSwitchLeft() : mThumbPadding;
        if (mThumbAnimation == null) {
            mThumbAnimation = ValueAnimator.ofInt();
            mThumbAnimation.setDuration(ANIMATION_DURATION);
            mThumbAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    setThumbPosition((Integer) animation.getAnimatedValue());
                }
            });
        }
        mThumbAnimation.setIntValues(mThumbPosition,targetPosition);
        mThumbAnimation.start();
    }

    private void setThumbPosition(int position) {
        mThumbPosition = position;
        final Rect bounds = mThumbDrawable.getBounds();
        mThumbDrawable.setBounds(position,bounds.top,position+getThumbWidth(),bounds.bottom);
    }

    @Override
    protected boolean verifyDrawable(@NonNull Drawable who) {
        boolean verify = super.verifyDrawable(who);
        if (!verify) {
            verify = who == mThumbDrawable || who == mTrackDrawable;
        }
        return verify;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mTrackDrawable.draw(canvas);
        mThumbDrawable.draw(canvas);
    }
}