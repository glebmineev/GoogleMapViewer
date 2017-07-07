package com.evelopers.mapviewer.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.evelopers.mapviewer.R;

/**
 * Created by gmineev on 06.07.17.
 */

public class ProfileImageView extends AppCompatImageView {

    private Paint mBlackPaint;
    private Paint mMaskedPaint;

    private Rect mBounds;
    private RectF mBoundsF;

    private Drawable mBorderDrawable;
    private Drawable mMaskDrawable;
    private String number;
    private String complete;
    private String description;

    private ColorMatrixColorFilter mDesaturateColorFilter;
    private boolean mDesaturateOnPress = false;

    private boolean mCacheValid = false;
    private Bitmap mCacheBitmap;
    private int mCachedWidth;
    private int mCachedHeight;

    private Paint numberPaint;

    public ProfileImageView(Context context) {
        this(context, null);
    }

    public ProfileImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProfileImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        // Attribute initialization.
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ProfileImageView,
                defStyle, 0);

        mMaskDrawable = a.getDrawable(R.styleable.ProfileImageView_maskDrawable);
        if (mMaskDrawable != null) {
            mMaskDrawable.setCallback(this);
        }

        mBorderDrawable = a.getDrawable(R.styleable.ProfileImageView_borderDrawable);
        if (mBorderDrawable != null) {
            mBorderDrawable.setCallback(this);
        }

        mDesaturateOnPress = a.getBoolean(R.styleable.ProfileImageView_desaturateOnPress,
                mDesaturateOnPress);

        mBorderDrawable = a.getDrawable(R.styleable.ProfileImageView_borderDrawable);
        if (mBorderDrawable != null) {
            mBorderDrawable.setCallback(this);
        }

        number = a.getString(R.styleable.ProfileImageView_number);
        complete = a.getString(R.styleable.ProfileImageView_complete);

        a.recycle();

        // Other initialization.
        mBlackPaint = new Paint();
        mBlackPaint.setColor(0xff000000);

        mMaskedPaint = new Paint();
        mMaskedPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        // Always want a cache allocated.
        mCacheBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);

        if (mDesaturateOnPress) {
            // Create a desaturate color filter for pressed state.
            ColorMatrix cm = new ColorMatrix();
            cm.setSaturation(0);
            mDesaturateColorFilter = new ColorMatrixColorFilter(cm);
        }

        fillNumberPaint();

    }

    private void fillNumberPaint() {
        numberPaint = new Paint();
        numberPaint.setColor(Color.BLACK);
        numberPaint.setAntiAlias(true);
        numberPaint.setFakeBoldText(false);
        numberPaint.setStyle(Paint.Style.FILL);
        numberPaint.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
        numberPaint.setTextAlign(Paint.Align.CENTER);
        numberPaint.setStrokeWidth(0);
    }

    @Override
    protected boolean setFrame(int l, int t, int r, int b) {
        final boolean changed = super.setFrame(l, t, r, b);
        mBounds = new Rect(0, 0, r - l, b - t);
        mBoundsF = new RectF(mBounds);

        if (mBorderDrawable != null) {
            mBorderDrawable.setBounds(mBounds);
        }
        if (mMaskDrawable != null) {
            mMaskDrawable.setBounds(mBounds);
        }

        if (changed) {
            mCacheValid = false;
        }

        return changed;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mBounds == null) {
            return;
        }

        int width = mBounds.width();
        int height = mBounds.height();

        if (width == 0 || height == 0) {
            return;
        }

        if (!mCacheValid || width != mCachedWidth || height != mCachedHeight) {
            // Need to redraw the cache.
            if (width == mCachedWidth && height == mCachedHeight) {
                // Have a correct-sized bitmap cache already allocated. Just erase it.
                mCacheBitmap.eraseColor(0);
            } else {
                // Allocate a new bitmap with the correct dimensions.
                mCacheBitmap.recycle();
                //noinspection AndroidLintDrawAllocation
                mCacheBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                mCachedWidth = width;
                mCachedHeight = height;
            }

            Canvas cacheCanvas = new Canvas(mCacheBitmap);
            if (mMaskDrawable != null) {
                int sc = cacheCanvas.save();
                mMaskDrawable.draw(cacheCanvas);
                mMaskedPaint.setColorFilter((mDesaturateOnPress && isPressed())
                        ? mDesaturateColorFilter : null);
                cacheCanvas.saveLayer(mBoundsF, mMaskedPaint,
                        Canvas.HAS_ALPHA_LAYER_SAVE_FLAG | Canvas.FULL_COLOR_LAYER_SAVE_FLAG);
                super.onDraw(cacheCanvas);
                cacheCanvas.restoreToCount(sc);
            } else if (mDesaturateOnPress && isPressed()) {
                int sc = cacheCanvas.save();
                cacheCanvas.drawRect(0, 0, mCachedWidth, mCachedHeight, mBlackPaint);
                mMaskedPaint.setColorFilter(mDesaturateColorFilter);
                cacheCanvas.saveLayer(mBoundsF, mMaskedPaint,
                        Canvas.HAS_ALPHA_LAYER_SAVE_FLAG | Canvas.FULL_COLOR_LAYER_SAVE_FLAG);
                super.onDraw(cacheCanvas);
                cacheCanvas.restoreToCount(sc);
            } else {
                super.onDraw(cacheCanvas);
            }

            if (mBorderDrawable != null) {
                mBorderDrawable.draw(cacheCanvas);
            }
        }

        // Draw from cache.
        canvas.drawBitmap(mCacheBitmap, mBounds.left, mBounds.top, null);

        // draw description
        //int fontSize = (Math.min(width, height) / 2);
        int titleFontSize = getResources().getDimensionPixelSize(R.dimen.title_font_size);
        int middleFontSize = getResources().getDimensionPixelSize(R.dimen.middle_font_size);
        int smallFontSize = getResources().getDimensionPixelSize(R.dimen.small_font_size);
        int paddingButtom = getResources().getDimensionPixelSize(R.dimen.profile_image_buttom_padding);
        if (number != null && !number.isEmpty()) {
            numberPaint.setTextSize(titleFontSize);
            numberPaint.setColor(getResources().getColor(R.color.colorWhite));
            canvas.drawText(number, width / 2, height / 2 - ((numberPaint.descent() + numberPaint.ascent()) / 2), numberPaint);
        }
        if (complete != null && !complete.isEmpty()) {
            numberPaint.setTextSize(smallFontSize);
            numberPaint.setColor(getResources().getColor(R.color.colorWhite));
            canvas.drawText(complete, width / 2, height - paddingButtom, numberPaint);
        }
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if (mBorderDrawable != null && mBorderDrawable.isStateful()) {
            mBorderDrawable.setState(getDrawableState());
        }
        if (mMaskDrawable != null && mMaskDrawable.isStateful()) {
            mMaskDrawable.setState(getDrawableState());
        }
        if (isDuplicateParentStateEnabled()) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getComplete() {
        return complete;
    }

    public void setComplete(String complete) {
        this.complete = complete;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public void invalidateDrawable(Drawable who) {
        if (who == mBorderDrawable || who == mMaskDrawable) {
            invalidate();
        } else {
            super.invalidateDrawable(who);
        }
    }

    @Override
    protected boolean verifyDrawable(Drawable who) {
        return who == mBorderDrawable || who == mMaskDrawable || super.verifyDrawable(who);
    }

}
