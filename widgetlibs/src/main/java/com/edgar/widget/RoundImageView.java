package com.edgar.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.Gravity;

/**
 * Created by Edgar on 2019/7/23.
 */
public class RoundImageView extends AppCompatImageView {

    public RoundImageView(Context context) {
        super(context);
    }

    public RoundImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RoundImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setImageDrawable(@Nullable Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            RoundedBitmapDrawable roundedBitmapDrawable =
                    RoundedBitmapDrawableFactory.create(getResources(),((BitmapDrawable) drawable).getBitmap());
            roundedBitmapDrawable.setCircular(true);
//            roundedBitmapDrawable.setCornerRadius(Math.max(roundedBitmapDrawable.getIntrinsicWidth(),roundedBitmapDrawable.getIntrinsicHeight())/2f);
            drawable = roundedBitmapDrawable;
        }
        super.setImageDrawable(drawable);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        if (bm == null) {
            super.setImageBitmap(bm);
            return;
        }
        RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(getResources(),bm);
        drawable.setCornerRadius(30);
        super.setImageDrawable(drawable);
    }
}
