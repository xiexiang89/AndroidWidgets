package com.edgar.span;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Parcel;
import android.support.annotation.ColorInt;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.Px;
import android.text.Layout;
import android.text.ParcelableSpan;
import android.text.Spanned;
import android.text.style.BulletSpan;
import android.text.style.LeadingMarginSpan;

/**
 * Created by Edgar on 2018/12/26.
 */
public class BulletSpanCompat implements LeadingMarginSpan, ParcelableSpan {

    // Bullet is slightly bigger to avoid aliasing artifacts on mdpi devices.
    private static final int STANDARD_BULLET_RADIUS = 4;
    public static final int STANDARD_GAP_WIDTH = 2;
    private static final int STANDARD_COLOR = 0;

    @Px
    private final int mGapWidth;
    @Px
    private final int mBulletRadius;
    private Path mBulletPath = null;
    @ColorInt
    private final int mColor;
    private final boolean mWantColor;

    /**
     * Creates a {@link BulletSpan} with the default values.
     */
    public BulletSpanCompat() {
        this(STANDARD_GAP_WIDTH, STANDARD_COLOR, false, STANDARD_BULLET_RADIUS);
    }

    /**
     * Creates a {@link BulletSpanCompat} based on a gap width
     *
     * @param gapWidth the distance, in pixels, between the bullet point and the paragraph.
     */
    public BulletSpanCompat(int gapWidth) {
        this(gapWidth, STANDARD_COLOR, false, STANDARD_BULLET_RADIUS);
    }

    /**
     * Creates a {@link BulletSpanCompat} based on a gap width and a color integer.
     *
     * @param gapWidth the distance, in pixels, between the bullet point and the paragraph.
     * @param color    the bullet point color, as a color integer
     * @see android.content.res.Resources#getColor(int, Resources.Theme)
     */
    public BulletSpanCompat(int gapWidth, @ColorInt int color) {
        this(gapWidth, color, true, STANDARD_BULLET_RADIUS);
    }

    /**
     * Creates a {@link BulletSpanCompat} based on a gap width and a color integer.
     *
     * @param gapWidth     the distance, in pixels, between the bullet point and the paragraph.
     * @param color        the bullet point color, as a color integer.
     * @param bulletRadius the radius of the bullet point, in pixels.
     * @see android.content.res.Resources#getColor(int, Resources.Theme)
     */
    public BulletSpanCompat(int gapWidth, @ColorInt int color, @IntRange(from = 0) int bulletRadius) {
        this(gapWidth, color, true, bulletRadius);
    }

    private BulletSpanCompat(int gapWidth, @ColorInt int color, boolean wantColor,
                       @IntRange(from = 0) int bulletRadius) {
        mGapWidth = gapWidth;
        mBulletRadius = bulletRadius;
        mColor = color;
        mWantColor = wantColor;
    }

    /**
     * Creates a {@link BulletSpanCompat} from a parcel.
     */
    public BulletSpanCompat(@NonNull Parcel src) {
        mGapWidth = src.readInt();
        mWantColor = src.readInt() != 0;
        mColor = src.readInt();
        mBulletRadius = src.readInt();
    }

    @Override
    public int getSpanTypeId() {
        return getSpanTypeIdInternal();
    }

    public int getSpanTypeIdInternal() {
        return 8;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        writeToParcelInternal(dest, flags);
    }

    public void writeToParcelInternal(@NonNull Parcel dest, int flags) {
        dest.writeInt(mGapWidth);
        dest.writeInt(mWantColor ? 1 : 0);
        dest.writeInt(mColor);
        dest.writeInt(mBulletRadius);
    }

    @Override
    public int getLeadingMargin(boolean first) {
        return 2 * mBulletRadius + mGapWidth;
    }

    /**
     * Get the distance, in pixels, between the bullet point and the paragraph.
     *
     * @return the distance, in pixels, between the bullet point and the paragraph.
     */
    public int getGapWidth() {
        return mGapWidth;
    }

    /**
     * Get the radius, in pixels, of the bullet point.
     *
     * @return the radius, in pixels, of the bullet point.
     */
    public int getBulletRadius() {
        return mBulletRadius;
    }

    /**
     * Get the bullet point color.
     *
     * @return the bullet point color
     */
    public int getColor() {
        return mColor;
    }

    @Override
    public void drawLeadingMargin(@NonNull Canvas canvas, @NonNull Paint paint, int x, int dir,
                                  int top, int baseline, int bottom,
                                  @NonNull CharSequence text, int start, int end,
                                  boolean first, @Nullable Layout layout) {
        if (((Spanned) text).getSpanStart(this) == start) {
            Paint.Style style = paint.getStyle();
            int oldcolor = 0;

            if (mWantColor) {
                oldcolor = paint.getColor();
                paint.setColor(mColor);
            }

            paint.setStyle(Paint.Style.FILL);

            if (layout != null && layout.getLineCount() > 1) {
                // "bottom" position might include extra space as a result of line spacing
                // configuration. Subtract extra space in order to show bullet in the vertical
                // center of characters.
                bottom = (int) (bottom - layout.getSpacingAdd());
            }

            final float yPosition = (top + bottom) / 2f;
            final float xPosition = x + dir * mBulletRadius;

            if (canvas.isHardwareAccelerated()) {
                if (mBulletPath == null) {
                    mBulletPath = new Path();
                    mBulletPath.addCircle(0.0f, 0.0f, mBulletRadius, Path.Direction.CW);
                }

                canvas.save();
                canvas.translate(xPosition, yPosition);
                canvas.drawPath(mBulletPath, paint);
                canvas.restore();
            } else {
                canvas.drawCircle(xPosition, yPosition, mBulletRadius, paint);
            }

            if (mWantColor) {
                paint.setColor(oldcolor);
            }

            paint.setStyle(style);
        }
    }
}
