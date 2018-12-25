package com.edgar.switchbutton;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
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
    private GradientDrawable mTrackDrawable;
    private GradientDrawable mTrackCheckDrawable;
    private int mTrackWidth;
    private int mTrackHeight;
    private int mUnCheckColor;
    private int mCheckColor;
    private int mTrackRadius;
    private int mThumbPadding;
    private float mLastTouchX;
    private float mLastTouchY;
    private int mTouchSlop;
    private int mTouchMode = TOUCH_MODE_IDLE;
    private ValueAnimator mThumbAnimation;
    private int mThumbPosition;

    public SwitchButton(Context context) {
        this(context, null);
    }

    public SwitchButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwitchButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs,R.attr.SwitchTheme);
        Resources res = getResources();
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SwitchButton, defStyleAttr, 0);
        mThumbDrawable = ta.getDrawable(R.styleable.SwitchButton_android_thumb);
        mTrackWidth = ta.getDimensionPixelOffset(R.styleable.SwitchButton_trackWidth, res.getDimensionPixelOffset(R.dimen.default_track_width));
        mTrackHeight = ta.getDimensionPixelOffset(R.styleable.SwitchButton_trackHeight, res.getDimensionPixelOffset(R.dimen.default_track_height));
        mUnCheckColor = ta.getColor(R.styleable.SwitchButton_track_uncheck_color, res.getColor(R.color.default_unchecked_color));
        mCheckColor = ta.getColor(R.styleable.SwitchButton_track_check_color, res.getColor(R.color.default_checked_color));
        mTrackRadius = ta.getDimensionPixelOffset(R.styleable.SwitchButton_track_radius, res.getDimensionPixelOffset(R.dimen.default_track_radius));
        mThumbPadding = ta.getDimensionPixelOffset(R.styleable.SwitchButton_thumbPadding,
                res.getDimensionPixelOffset(R.dimen.default_thumb_padding));
        ta.recycle();
        final ViewConfiguration config = ViewConfiguration.get(context);
        mTouchSlop = config.getScaledTouchSlop();
        mTrackDrawable = createTrackDrawable(mUnCheckColor);
        mTrackCheckDrawable = createTrackDrawable(mCheckColor);
        setDrawableCallback(mThumbDrawable);
        setDrawableCallback(mTrackDrawable);
        setDrawableCallback(mTrackCheckDrawable);
        mTrackCheckDrawable.setAlpha(0);
    }

    private GradientDrawable createTrackDrawable(@ColorInt int color) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setSize(mTrackWidth, mTrackHeight);
        drawable.setCornerRadius(mTrackRadius);
        return drawable;
    }

    private void setDrawableCallback(Drawable drawable) {
        if (drawable != null) {
            drawable.setCallback(this);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int trackWidth = mTrackWidth;
        final int trackHeight = mTrackHeight;
        final int thumbWidth = getThumbWidth();
        final int thumbHeight = getThumbHeight();
        int height = Math.max(thumbHeight, trackHeight);
        setMeasuredDimension(trackWidth, height);
        int thumbTop = (trackHeight - thumbHeight) / 2;
        mThumbPosition = isChecked() ? getSwitchEndLeft() : mThumbPadding;
        mThumbDrawable.setBounds(mThumbPosition, thumbTop, mThumbPosition + thumbWidth, thumbTop + thumbHeight);
        mTrackDrawable.setBounds(0, 0, trackWidth, trackHeight);
        mTrackCheckDrawable.setBounds(mTrackDrawable.copyBounds());
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

    private int getHorizontalTotalPadding() {
        return mThumbPadding * 2;
    }

    private int getAvailableWidth() {
        return getMeasuredWidth() - mThumbPadding * 2 - getThumbWidth() / 2;
    }

    private int getSwitchEndLeft() {
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
                if (isEnabled() && hitThumb(x, y)) {
                    mTouchMode = TOUCH_MODE_DOWN;
                    mLastTouchX = x;
                    mLastTouchY = y;
                }
                break;
            }
            case MotionEvent.ACTION_MOVE:
                switch (mTouchMode) {
                    case TOUCH_MODE_IDLE: {
                        break;
                    }

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
                        startDrag(event.getX(), event.getY());
                        return true;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                if (mTouchMode == TOUCH_MODE_DRAGGING) {
                    stopDrag(event);
                    super.onTouchEvent(event);
                    return true;
                }
                mTouchMode = TOUCH_MODE_IDLE;
                break;
            }
        }
        return super.onTouchEvent(event);
    }

    private void startDrag(float x, float y) {
        final float thumbScrollOffset = x - mLastTouchX;
        int newPosition = (int) (thumbScrollOffset + mThumbDrawable.getBounds().left);
        if (newPosition < mThumbPadding) {
            newPosition = mThumbPadding;
        }
        final int switchEndLeft = getSwitchEndLeft();
        if (newPosition > switchEndLeft) {
            newPosition = switchEndLeft;
        }
        setThumbPosition(newPosition, Math.abs(x - mLastTouchX) > Math.abs(y - mLastTouchY));
        mLastTouchX = x;
        mLastTouchY = y;
    }

    private void stopDrag(MotionEvent ev) {
        mTouchMode = TOUCH_MODE_IDLE;

        final boolean commitChange = ev.getAction() == MotionEvent.ACTION_UP && isEnabled();
        final boolean oldState = isChecked();
        final boolean newState;
        if (commitChange) {
            final Rect bounds = mThumbDrawable.getBounds();
            int centerX = bounds.centerX();
            newState = centerX >= getMeasuredWidth() / 2;
        } else {
            newState = oldState;
        }

        if (newState != oldState) {
            playSoundEffect(SoundEffectConstants.CLICK);
        }
        setChecked(newState);
        cancelSuperTouch(ev);
    }

    private void cancelSuperTouch(MotionEvent ev) {
        MotionEvent cancel = MotionEvent.obtain(ev);
        cancel.setAction(MotionEvent.ACTION_CANCEL);
        super.onTouchEvent(cancel);
        cancel.recycle();
    }

    private void animationChecked(final boolean checked) {
        final int targetPosition = checked ? getSwitchEndLeft() : mThumbPadding;
        if (mThumbAnimation == null) {
            mThumbAnimation = ValueAnimator.ofInt();
            mThumbAnimation.setDuration(ANIMATION_DURATION);
            mThumbAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    setThumbPosition((Integer) animation.getAnimatedValue(), true);
                }
            });
        }
        mThumbAnimation.setIntValues(mThumbPosition, targetPosition);
        mThumbAnimation.start();
    }

    private void setThumbPosition(int position, boolean changeAlpha) {
        mThumbPosition = position;
        final Rect bounds = mThumbDrawable.getBounds();
        mThumbDrawable.setBounds(position, bounds.top, position + getThumbWidth(), bounds.bottom);
        if (changeAlpha) {
            int width = getMeasuredWidth() - getHorizontalTotalPadding() - getThumbWidth();
            float alpha = Math.max(0f, Math.min((float) bounds.left / width, 1f));
            mTrackCheckDrawable.setAlpha((int) (255 * alpha));
        }
    }

    @Override
    protected boolean verifyDrawable(@NonNull Drawable who) {
        boolean verify = super.verifyDrawable(who);
        if (!verify) {
            verify = who == mThumbDrawable || who == mTrackDrawable || who == mTrackCheckDrawable;
        }
        return verify;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mTrackDrawable.draw(canvas);
        mTrackCheckDrawable.draw(canvas);
        mThumbDrawable.draw(canvas);
    }
}