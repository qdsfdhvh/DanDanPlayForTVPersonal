package com.seiko.common.ui.adapter;

import android.animation.TimeAnimator;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

import androidx.leanback.graphics.ColorOverlayDimmer;
import androidx.leanback.widget.ShadowOverlayContainer;
import androidx.leanback.widget.ShadowOverlayHelper;

public class FocusAnimator implements TimeAnimator.TimeListener {
    private final View mView;
    private final int mDuration;
    private final ShadowOverlayContainer mWrapper;
    private final float mScaleDiff;
    private float mFocusLevel = 0f;
    private float mFocusLevelStart;
    private float mFocusLevelDelta;
    private final TimeAnimator mAnimator = new TimeAnimator();
    private final Interpolator mInterpolator = new AccelerateDecelerateInterpolator();
    private final ColorOverlayDimmer mDimmer;

    public void animateFocus(boolean select, boolean immediate) {
        endAnimation();
        final float end = select ? 1 : 0;
        if (immediate) {
            setFocusLevel(end);
        } else if (mFocusLevel != end) {
            mFocusLevelStart = mFocusLevel;
            mFocusLevelDelta = end - mFocusLevelStart;
            mAnimator.start();
        }
    }

    public FocusAnimator(View view, float scale, boolean useDimmer, int duration) {
        mView = view;
        mDuration = duration;
        mScaleDiff = scale - 1f;
        if (view instanceof ShadowOverlayContainer) {
            mWrapper = (ShadowOverlayContainer) view;
        } else {
            mWrapper = null;
        }
        mAnimator.setTimeListener(this);
        if (useDimmer) {
            mDimmer = ColorOverlayDimmer.createDefault(view.getContext());
        } else {
            mDimmer = null;
        }
    }

    void setFocusLevel(float level) {
        mFocusLevel = level;
        float scale = 1f + mScaleDiff * level;
        mView.setScaleX(scale);
        mView.setScaleY(scale);
        if (mWrapper != null) {
            mWrapper.setShadowFocusLevel(level);
        } else {
            ShadowOverlayHelper.setNoneWrapperShadowFocusLevel(mView, level);
        }
        if (mDimmer != null) {
            mDimmer.setActiveLevel(level);
            int color = mDimmer.getPaint().getColor();
            if (mWrapper != null) {
                mWrapper.setOverlayColor(color);
            } else {
                ShadowOverlayHelper.setNoneWrapperOverlayColor(mView, color);
            }
        }
    }

    float getFocusLevel() {
        return mFocusLevel;
    }

    public void endAnimation() {
        mAnimator.end();
    }

    @Override
    public void onTimeUpdate(TimeAnimator animation, long totalTime, long deltaTime) {
        float fraction;
        if (totalTime >= mDuration) {
            fraction = 1;
            mAnimator.end();
        } else {
            fraction = (float) (totalTime / (double) mDuration);
        }
        if (mInterpolator != null) {
            fraction = mInterpolator.getInterpolation(fraction);
        }
        setFocusLevel(mFocusLevelStart + fraction * mFocusLevelDelta);
    }
}
