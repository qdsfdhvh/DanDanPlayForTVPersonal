package com.seiko.widget.tv.focus;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

import java.util.List;

/**
 * Created by owen on 2017/7/25.
 */

public class DrawableFocusBorder extends AbsFocusBorder {

    private View mBorderView;

    private DrawableFocusBorder(Context context, Builder builder) {
        super(context, builder);

        Drawable mBorderDrawable = builder.mBorderDrawable;
        final Rect paddingRect = new Rect();
        mBorderDrawable.getPadding(paddingRect);
        mPaddingRectF.set(paddingRect);
        
        mBorderView = new View(context);
        //关闭硬件加速
        mBorderView.setLayerType(LAYER_TYPE_SOFTWARE, null);
//        if(Build.VERSION.SDK_INT >= 16) {
        mBorderView.setBackground(mBorderDrawable);
//        } else {
//            mBorderView.setBackgroundDrawable(mBorderDrawable);
//        }
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(mBorderView, params);
    }

    @NonNull
    @Override
    public View getBorderView() {
        return mBorderView;
    }

    @Override
    public float getRoundRadius() {
        return 0;
    }

    @Override
    List<Animator> getTogetherAnimators(float newX, float newY, int newWidth, int newHeight, AbsFocusBorder.Options options) {
        return null;
    }

    @Override
    List<Animator> getSequentiallyAnimators(float newX, float newY, int newWidth, int newHeight, AbsFocusBorder.Options options) {
        return null;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public final static class Builder extends AbsFocusBorder.Builder{
        private int mBorderResId = 0;
        private Drawable mBorderDrawable = null;

        public Builder borderDrawableRes(@DrawableRes int resId) {
            mBorderResId = resId;
            return this;
        }
        
        public Builder borderDrawable(Drawable drawable) {
            mBorderDrawable = drawable;
            return this;
        }

        @Override
        public FocusBorder build(Activity activity) {
            if(null == activity) {
                throw new NullPointerException("The activity cannot be null");
            }
            if(null == mBorderDrawable && mBorderResId == 0) {
                throw new RuntimeException("The border Drawable or ResId cannot be null");
            }
            final ViewGroup parent = activity.findViewById(android.R.id.content);
            return build(parent);
        }

        @Override
        public FocusBorder build(ViewGroup parent) {
            if(null == parent) {
                throw new NullPointerException("The FocusBorder parent cannot be null");
            }
            mBorderDrawable = null != mBorderDrawable ? mBorderDrawable :
                    Build.VERSION.SDK_INT >= 21 ? parent.getContext().getDrawable(mBorderResId)
                            : parent.getContext().getResources().getDrawable(mBorderResId);
            final DrawableFocusBorder borderView = new DrawableFocusBorder(parent.getContext(), this);
            final ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(1,1);
            parent.addView(borderView, lp);
            return borderView;
        }
    }
}
