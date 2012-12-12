package com.tylerjchesley.creatures.ui.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import com.tylerjchesley.creatures.R;

/**
 * Author: Tyler Chesley
 */
public class CreatureThumbnailImageView extends ImageView {

    private final BitmapDrawable mIsNewDrawable;

    private final BitmapDrawable mIsFavoriteDrawable;

    private boolean mIsFavorite = false;

    private boolean mIsNew = false;

    private boolean mBlockLayout = false;

    public CreatureThumbnailImageView(Context context) {
        super(context);

        final Resources resources = getResources();

        final float scale = getResources().getDisplayMetrics().density;
        final int padding = (int) (1 * scale + 0.5f);

        mIsNewDrawable = (BitmapDrawable) resources.getDrawable(R.drawable.creature_new_indicator);
        mIsNewDrawable.setBounds(0, 0,
                mIsNewDrawable.getIntrinsicWidth(),
                mIsNewDrawable.getIntrinsicHeight());
        mIsFavoriteDrawable = (BitmapDrawable) resources.getDrawable(R.drawable.creature_favorite_indicator);
        mIsFavoriteDrawable.setBounds(0, 0,
                mIsFavoriteDrawable.getIntrinsicWidth(),
                mIsFavoriteDrawable.getIntrinsicHeight());

        setBackgroundResource(R.drawable.creature_image_placeholder);

        setPadding(padding, padding, padding, padding);

        setScaleType(ScaleType.CENTER_CROP);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mIsFavorite) {
            mIsFavoriteDrawable.draw(canvas);
        }

        if (mIsNew) {
            mIsNewDrawable.draw(canvas);
        }
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        mBlockLayout = true;
        super.setImageDrawable(drawable);
        mBlockLayout = false;
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        mBlockLayout = true;
        super.setImageBitmap(bm);
        mBlockLayout = false;
    }

    @Override
    public void requestLayout() {
        if (!mBlockLayout) {
            super.requestLayout();
        }
    }

    public void setIsNew(boolean isNew) {
        mIsNew = isNew;
    }

    public void setIsFavorite(boolean isFavorite) {
        mIsFavorite = isFavorite;
    }

}
