package com.seiko.widget.tv.focus;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2018/12/13Ø
 */
class AnimatorHelper {

    private AbsFocusBorder mFocusBorder;
    private Map<Object, Animator> mCache = new HashMap<>();

    public AnimatorHelper(AbsFocusBorder border) {
        this.mFocusBorder = border;
    }


    private AnimatorSet getAnimatorSet(Object tag) {
        /**
         * 无法复用是因为Android低版本不兼容（api22测试无法兼容复用，其它版本未测试）
         * */
        AnimatorSet anim = new AnimatorSet();
        /*if(!mCache.containsKey(tag)) {
            anim = new AnimatorSet();
            mCache.put(tag, anim);
        } else {
            anim = (AnimatorSet) mCache.get(tag);
            if(null != anim) {
                anim.end();
            }
        }*/
        return anim;
    }

    @SuppressLint("ObjectAnimatorBinding")
    private ObjectAnimator getAnimatorsOfFloat(Object tag) {
        ObjectAnimator anim;
        if(!mCache.containsKey(tag)) {
            anim = ObjectAnimator.ofFloat(this, "dd", 1);
            mCache.put(tag, anim);
        } else {
            anim = (ObjectAnimator) mCache.get(tag);
        }
        return anim;
    }

    @SuppressLint("ObjectAnimatorBinding")
    private ObjectAnimator getAnimatorsOfInt(Object tag) {
        ObjectAnimator anim;
        if(!mCache.containsKey(tag)) {
            anim = ObjectAnimator.ofInt(this, "dd", 1);
            mCache.put(tag, anim);
        } else {
            anim = (ObjectAnimator) mCache.get(tag);
        }
        return anim;
    }

    public ObjectAnimator ofFloat(Object tag, Object target, String propertyName, float... values) {
        ObjectAnimator anim = getAnimatorsOfFloat(tag);
        anim.setTarget(target);
        anim.setPropertyName(propertyName);
        anim.setFloatValues(values);
        return anim;
    }

    public ObjectAnimator ofInt(Object tag, Object target, String propertyName, int... values) {
        ObjectAnimator anim = getAnimatorsOfInt(tag);
        anim.setTarget(target);
        anim.setPropertyName(propertyName);
        anim.setIntValues(values);
        return anim;
    }

    /**
     * 整个边框布局的位置和宽高动画,以及焦点View的缩放动画
     *
     * @param focusView
     * @param options
     * @param x
     * @param y
     * @param width
     * @param height
     * @param duration
     * @return
     */
    AnimatorSet getBorderAnimatorWithScale(View focusView, AbsFocusBorder.Options options, float x, float y, int width, int height, long duration, long startDelay) {
        AnimatorSet set = getAnimatorSet("BorderAnimator2");
        set.playTogether(
                ofFloat("mTranslationXAnimator2", mFocusBorder, "translationX", x),
                ofFloat("mTranslationYAnimator2", mFocusBorder, "translationY", y),
                ofInt("mWidthAnimator2", mFocusBorder, "width", width),
                ofInt("mHeightAnimator2", mFocusBorder, "height", height),
                ofFloat("mScaleXAnimator2", focusView, "scaleX", options.scaleX),
                ofFloat("mScaleYAnimator2", focusView, "scaleY", options.scaleY)
        );
        set.setDuration(duration);
        set.setStartDelay(startDelay);
        return set;
    }

    /**
     * 整个边框布局的位置和宽高动画(用于Mode.SEQUENTIALLY模式)
     *
     * @param x
     * @param y
     * @param width
     * @param height
     * @param duration
     * @return
     */
    AnimatorSet getBorderAnimator(float x, float y, int width, int height, long duration) {
        AnimatorSet set = getAnimatorSet("BorderAnimator");
        set.playTogether(
                ofFloat("mTranslationXAnimator", mFocusBorder, "translationX", x),
                ofFloat("mTranslationYAnimator", mFocusBorder, "translationY", y),
                ofInt("mWidthAnimator", mFocusBorder, "width", width),
                ofInt("mHeightAnimator", mFocusBorder, "height", height)
        );
        set.setDuration(duration);
        return set;
    }

    /**
     * 闪光动画及标题动画
     *
     * @param options
     * @return
     */
    Animator getShimmerAndTitleAnimator(AbsFocusBorder.Options options, boolean isReAnim) {
        AnimatorSet set = getAnimatorSet("ShimmerAndTitleAnimator");
        if (!TextUtils.isEmpty(mFocusBorder.mTitleView.getText())) {
            ObjectAnimator mTitleTranslationYAnimator = ofFloat("TitleTranslationYAnimator", mFocusBorder.mTitleView, "translationY",  mFocusBorder.mTitleView.getTranslationY(), 0f)
                    .setDuration(mFocusBorder.mBuilder.mTitleAnimDuration / (isReAnim ? 2 : 1));
            ObjectAnimator mTitleAlphaAnimator = ofFloat("TitleAlphaAnimator", mFocusBorder.mTitleView, "alpha", mFocusBorder.mTitleView.getAlpha(), 1f)
                    .setDuration(mFocusBorder.mBuilder.mTitleAnimDuration / (isReAnim ? 2 : 1));
            set.playTogether(mTitleTranslationYAnimator, mTitleAlphaAnimator);
        }
        if (mFocusBorder.mBuilder.mRunShimmerAnim) {
            set.playTogether(getShimmerAnimator());
        }
        if(!isReAnim) {
            if(mFocusBorder.mBuilder.mAnimMode == AbsFocusBorder.Mode.TOGETHER) {
                set.setStartDelay(300);
            } else if(mFocusBorder.mBuilder.mAnimMode == AbsFocusBorder.Mode.NOLL) {
                set.setStartDelay(200);
            } else {
                set.setStartDelay(100);
            }
        }
        return set;
    }

    /**
     * 闪光动画
     *
     * @return Animator
     */
    private ObjectAnimator getShimmerAnimator() {
        ObjectAnimator mShimmerAnimator = ofFloat("ShimmerAnimator", mFocusBorder, "shimmerTranslate", -1f, 1f);
        mShimmerAnimator.end();
        mShimmerAnimator.setInterpolator(new LinearInterpolator());
        mShimmerAnimator.setDuration(mFocusBorder.mBuilder.mShimmerDuration);
        mShimmerAnimator.setStartDelay(400);
        mShimmerAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mFocusBorder.setShimmerAnimating(true);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mFocusBorder.setShimmerAnimating(false);
            }
        });
        return mShimmerAnimator;
    }

    /**
     * 呼吸灯动画
     *
     * @return Animator
     */
    ObjectAnimator getBreathingLampAnimator() {
        ObjectAnimator mBreathingLampAnimator = ofFloat("BreathingLampAnimator", mFocusBorder.getBorderView(), "alpha", 1f, 0.22f, 1f);
        mBreathingLampAnimator.setDuration(mFocusBorder.mBuilder.mBreathingDuration);
        mBreathingLampAnimator.setStartDelay(400);
        mBreathingLampAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mBreathingLampAnimator.setRepeatCount(ValueAnimator.INFINITE);
        return mBreathingLampAnimator;
    }
}
