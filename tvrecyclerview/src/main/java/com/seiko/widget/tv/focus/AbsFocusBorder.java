package com.seiko.widget.tv.focus;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Build;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewPropertyAnimator;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public abstract class AbsFocusBorder extends FrameLayout implements FocusBorder, ViewTreeObserver.OnGlobalFocusChangeListener {
    private static final long DEFAULT_ANIM_DURATION_TIME = 300;
    private static final long DEFAULT_TITLE_ANIM_DURATION_TIME = 400;
    private static final long DEFAULT_SHIMMER_DURATION_TIME = 1000;
    private static final long DEFAULT_BREATHING_DURATION_TIME = 3000;

    protected Builder mBuilder;
    protected RectF mFrameRectF = new RectF();
    protected RectF mPaddingRectF = new RectF();
    protected RectF mTempRectF = new RectF();

    private LinearGradient mShimmerLinearGradient;
    private Matrix mShimmerGradientMatrix;
    private Paint mShimmerPaint;
    private float mShimmerTranslate = 0;
    // 闪光动画是否正在执行
    private boolean mShimmerAnimating = false;
    // 修复RecyclerView焦点临时标记
    private boolean mReAnim = false;

    private AnimatorSet mAnimatorSet;

    private RecyclerViewScrollListener mRecyclerViewScrollListener;
    private WeakReference<RecyclerView> mWeakRecyclerView;
    private WeakReference<View> mOldFocusView;
    private OnFocusCallback mOnFocusCallback;
    private boolean mIsVisible = false;

    private float mScaleX;
    private float mScaleY;

    protected TextView mTitleView;
    private AnimatorHelper mAnimatorHelper;

    protected AbsFocusBorder(Context context, Builder builder) {
        super(context);
        setWillNotDraw(false);

        mBuilder = builder;

        //关闭硬件加速
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        setVisibility(VISIBLE);

        setClipChildren(false);
        setClipToPadding(false);

        //绘制闪光相关
        mShimmerPaint = new Paint();
        mShimmerGradientMatrix = new Matrix();

        initTitleView();

        mAnimatorHelper = new AnimatorHelper(this);
    }

    private void initTitleView() {
        //标题
        if (null == mTitleView) {
            mTitleView = new TextView(getContext());
            mTitleView.setSingleLine();
            mTitleView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            mTitleView.setSelected(true);
            //关闭硬件加速
            mTitleView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            mTitleView.setTextSize(mBuilder.mTitleTextSize);
            mTitleView.setTextColor(mBuilder.mTitleTextColor);
            mTitleView.setBackgroundResource(mBuilder.mTitleBackgroundRes);
            mTitleView.setText(" ");
            mTitleView.setGravity(Gravity.CENTER);
            if (null != mBuilder.mTitlePaddingRect) {
                mTitleView.setPadding(mBuilder.mTitlePaddingRect.left, mBuilder.mTitlePaddingRect.top,
                        mBuilder.mTitlePaddingRect.right, mBuilder.mTitlePaddingRect.bottom);
            }
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(mBuilder.mTitleWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
            if (null != mBuilder.mTitleMarginRect) {
                params.bottomMargin += mBuilder.mTitleMarginRect.bottom;
                params.leftMargin += mBuilder.mTitleMarginRect.left;
                params.rightMargin += mBuilder.mTitleMarginRect.right;
            }
            addView(mTitleView, params);
        }
    }

    @Override
    public boolean isInEditMode() {
        return true;
    }

    /**
     * 绘制闪光
     */
    protected void onDrawShimmer(Canvas canvas) {
        if (mShimmerAnimating) {
            canvas.save();
            mTempRectF.set(mFrameRectF);
            mTempRectF.intersect(mBuilder.mPaddingOffsetRectF);
            float shimmerTranslateX = mTempRectF.width() * mShimmerTranslate;
            float shimmerTranslateY = mTempRectF.height() * mShimmerTranslate;
            mShimmerGradientMatrix.setTranslate(shimmerTranslateX, shimmerTranslateY);
            mShimmerLinearGradient.setLocalMatrix(mShimmerGradientMatrix);
            canvas.drawRoundRect(mTempRectF, getRoundRadius(), getRoundRadius(), mShimmerPaint);
            canvas.restore();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w != oldw || h != oldh) {
            mFrameRectF.set(mPaddingRectF.left, mPaddingRectF.top, w - mPaddingRectF.right, h - mPaddingRectF.bottom);
            if (null != mTitleView) {
                int maxWidth = (int) (mFrameRectF.width() - mBuilder.mPaddingOffsetRectF.left - mBuilder.mPaddingOffsetRectF.right);
                mTitleView.setMaxWidth(maxWidth - (null != mBuilder.mTitleMarginRect ? (mBuilder.mTitleMarginRect.left + mBuilder.mTitleMarginRect.right) : 0));
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        onDrawShimmer(canvas);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mBuilder.mTitleMarginBottomAutoAlignBorder) {
            ((LayoutParams) mTitleView.getLayoutParams()).bottomMargin += (int) (mPaddingRectF.bottom + mBuilder.mPaddingOffsetRectF.bottom);
        }
        ((LayoutParams) mTitleView.getLayoutParams()).leftMargin += (int) (mPaddingRectF.left + mBuilder.mPaddingOffsetRectF.left);
        ((LayoutParams) mTitleView.getLayoutParams()).rightMargin += (int) (mPaddingRectF.right + mBuilder.mPaddingOffsetRectF.right);
    }

    @Override
    protected void onDetachedFromWindow() {
        unBoundGlobalFocusListener();
        super.onDetachedFromWindow();
    }

    void setShimmerAnimating(boolean shimmerAnimating) {
        mShimmerAnimating = shimmerAnimating;
        if (mShimmerAnimating) {
            mTempRectF.set(mFrameRectF);
            mTempRectF.left += mBuilder.mPaddingOffsetRectF.left;
            mTempRectF.top += mBuilder.mPaddingOffsetRectF.top;
            mTempRectF.right -= mBuilder.mPaddingOffsetRectF.right;
            mTempRectF.bottom -= mBuilder.mPaddingOffsetRectF.bottom;
            mShimmerLinearGradient = new LinearGradient(
                    0, 0, mTempRectF.width(), mTempRectF.height(),
                    new int[]{0x00FFFFFF, 0x1AFFFFFF, mBuilder.mShimmerColor, 0x1AFFFFFF, 0x00FFFFFF},
                    new float[]{0f, 0.2f, 0.5f, 0.8f, 1f}, Shader.TileMode.CLAMP);
            mShimmerPaint.setShader(mShimmerLinearGradient);
        }
    }

    protected void setShimmerTranslate(float shimmerTranslate) {
        if (mBuilder.mRunShimmerAnim && mShimmerTranslate != shimmerTranslate) {
            mShimmerTranslate = shimmerTranslate;
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    protected float getShimmerTranslate() {
        return mShimmerTranslate;
    }

    protected void setWidth(int width) {
        if (getLayoutParams().width != width) {
            getLayoutParams().width = width;
            requestLayout();
        }
    }

    protected void setHeight(int height) {
        if (getLayoutParams().height != height) {
            getLayoutParams().height = height;
            requestLayout();
        }
    }

    @Override
    public void setVisible(boolean visible) {
        setVisible(visible, true);
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void setVisible(boolean visible, boolean anim) {
        if (mIsVisible != visible) {
            mIsVisible = visible;

            if (null != mAnimatorSet) {
                mAnimatorSet.cancel();
            }

            boolean hasOldFocus = null != mOldFocusView && null != mOldFocusView.get();

            if(anim) {
                ViewPropertyAnimator animator = animate().alpha(visible ? 1f : 0f).setDuration(mBuilder.mAnimDuration);
                if (!visible && hasOldFocus) {
                    animator.scaleX(1f / mOldFocusView.get().getScaleX())
                            .scaleY(1f / mOldFocusView.get().getScaleY());
                } else {
                    setScaleX(1f);
                    setScaleY(1f);
                }
                animator.start();
            } else {
                setAlpha(visible ? 1f : 0f);
                setScaleX(1f);
                setScaleY(1f);
            }

            if (!visible && hasOldFocus) {
                runFocusScaleAnimation(mOldFocusView.get(), 1f, 1f);
                mOldFocusView.clear();
                mOldFocusView = null;
            }
        }
    }

    @Override
    public boolean isVisible() {
        return mIsVisible;
    }

    private void registerScrollListener(RecyclerView recyclerView) {
        if (null != mWeakRecyclerView && mWeakRecyclerView.get() == recyclerView) {
            return;
        }

        if (null == mRecyclerViewScrollListener) {
            mRecyclerViewScrollListener = new RecyclerViewScrollListener(this);
        }

        if (null != mWeakRecyclerView && null != mWeakRecyclerView.get()) {
            mWeakRecyclerView.get().removeOnScrollListener(mRecyclerViewScrollListener);
            mWeakRecyclerView.clear();
        }

        recyclerView.removeOnScrollListener(mRecyclerViewScrollListener);
        recyclerView.addOnScrollListener(mRecyclerViewScrollListener);
        mWeakRecyclerView = new WeakReference<>(recyclerView);
    }

    protected Rect findLocationWithView(View view) {
        return findOffsetDescendantRectToMyCoords(view);
    }

    protected Rect findOffsetDescendantRectToMyCoords(View descendant) {
        final ViewGroup root = (ViewGroup) getParent();
        final Rect rect = new Rect();
        mReAnim = false;
        if (descendant == root) {
            return rect;
        }

        final View srcDescendant = descendant;

        ViewParent theParent = descendant.getParent();
        Point point = null;
        Point gridPoint = null;

        // search and offset up to the parent
        while (theParent instanceof View && theParent != root) {

            rect.offset(descendant.getLeft() - descendant.getScrollX(),
                    descendant.getTop() - descendant.getScrollY());

            //兼容TvRecyclerView
            if (theParent instanceof RecyclerView && point == null) {
                final RecyclerView rv = (RecyclerView) theParent;
                registerScrollListener(rv);
                Object tag = rv.getTag();
                if (tag instanceof Point) {
                    point = (Point) tag;
                    rect.offset(-point.x, -point.y);
                }
                if (null == tag && rv.getScrollState() != RecyclerView.SCROLL_STATE_IDLE
                        && (mRecyclerViewScrollListener.mScrolledX != 0 || mRecyclerViewScrollListener.mScrolledY != 0)) {
                    mReAnim = true;
                }
            }

            //兼容TvGridLayout
            if(theParent instanceof GridLayout && gridPoint == null) {
                Object tag = ((GridLayout) theParent).getTag();
                if(tag instanceof Point) {
                    gridPoint = (Point) tag;
                    rect.offset(-gridPoint.x, -gridPoint.y);
                    ((GridLayout) theParent).setTag(null);
                }
            }

            descendant = (View) theParent;
            theParent = descendant.getParent();
        }

        // now that we are up to this view, need to offset one more time
        // to get into our coordinate space
        if (theParent == root) {
            rect.offset(descendant.getLeft() - descendant.getScrollX(),
                    descendant.getTop() - descendant.getScrollY());
        }

        rect.right = rect.left + srcDescendant.getMeasuredWidth();
        rect.bottom = rect.top + srcDescendant.getMeasuredHeight();

        return rect;
    }

    public View getOldFocusView() {
        return null != mOldFocusView ? mOldFocusView.get() : null;
    }

    @Override
    public void onFocus(@NonNull View focusView, @Nullable FocusBorder.Options options) {
        View oldFocus = getOldFocusView();
        if (null != oldFocus) {
            runFocusScaleAnimation(oldFocus, 1f, 1f);
            mOldFocusView.clear();
        }

        if (null == options) {
            options = Options.get();
        }

        if (options instanceof Options) {
            restoreFocusBorder(oldFocus, focusView);
            setVisible(true);
            runFocusAnimation(focusView, (Options) options);
            mOldFocusView = new WeakReference<>(focusView);
        }
    }

    /**
     * 复原焦点框，在oldFocus为空时则认为当前焦点框是第一次或者再次显示出来了，
     * 所以需要将焦点框的位置和大小直接跟焦点view保持一致。
     */
    private void restoreFocusBorder(@Nullable View oldFocus, @NonNull View newFocus) {
        if (null == oldFocus) {
            final float paddingWidth = mPaddingRectF.left + mPaddingRectF.right + mBuilder.mPaddingOffsetRectF.left + mBuilder.mPaddingOffsetRectF.right;
            final float paddingHeight = mPaddingRectF.top + mPaddingRectF.bottom + mBuilder.mPaddingOffsetRectF.top + mBuilder.mPaddingOffsetRectF.bottom;
            final Rect toRect = findLocationWithView(newFocus);
            toRect.inset((int) (-paddingWidth / 2), (int) (-paddingHeight / 2));
            setWidth(toRect.width());
            setHeight(toRect.height());
            setTranslationX(toRect.left);
            setTranslationY(toRect.top);
        }
    }

    @Override
    public void boundGlobalFocusListener(@NonNull OnFocusCallback callback) {
        mOnFocusCallback = callback;
        getViewTreeObserver().addOnGlobalFocusChangeListener(this);
    }

    @Override
    public void unBoundGlobalFocusListener() {
        if (null != mOnFocusCallback) {
            mOnFocusCallback = null;
            getViewTreeObserver().removeOnGlobalFocusChangeListener(this);
        }
    }

    @Override
    public void onGlobalFocusChanged(View oldFocus, View newFocus) {
        final Options options = null != mOnFocusCallback ? (Options) mOnFocusCallback.onFocus(oldFocus, newFocus) : null;
        if (null != options) {
            onFocus(newFocus, options);
        }
    }

    private void runFocusAnimation(View focusView, Options options) {
        mScaleX = options.scaleX;
        mScaleY = options.scaleY;

        getBorderView().setAlpha(1f);
        mTitleView.setAlpha(0);
        mTitleView.setText(options.title);
        mTitleView.setTranslationY(mTitleView.getHeight());
        mTitleView.bringToFront();

        // 边框的动画
        runBorderAnimation(focusView, options, false);
    }

    protected void runBorderAnimation(View focusView, Options options, boolean isReAnim) {
        if (null == focusView) {
            return;
        }
        if (null != mAnimatorSet) {
            mAnimatorSet.cancel();
        }
        mAnimatorSet = createBorderAnimation(focusView, options, isReAnim);
        mAnimatorSet.start();
    }

    /**
     * 焦点VIEW缩放动画
     */
    protected void runFocusScaleAnimation(@Nullable final View oldOrNewFocusView, final float scaleX, final float scaleY) {
        if (null == oldOrNewFocusView) {
            return;
        }
        oldOrNewFocusView.animate().scaleX(scaleX).scaleY(scaleY).setDuration(mBuilder.mAnimDuration).start();
    }

    protected AnimatorSet createBorderAnimation(View focusView, Options options, boolean isReAnim) {
        final int paddingWidth = (int) (mPaddingRectF.left + mPaddingRectF.right + mBuilder.mPaddingOffsetRectF.left + mBuilder.mPaddingOffsetRectF.right);
        final int paddingHeight = (int) (mPaddingRectF.top + mPaddingRectF.bottom + mBuilder.mPaddingOffsetRectF.top + mBuilder.mPaddingOffsetRectF.bottom);
        final int offsetWidth = (int) (focusView.getMeasuredWidth() * (options.scaleX - 1f));
        final int offsetHeight = (int) (focusView.getMeasuredHeight() * (options.scaleY - 1f));

        final Rect fromRect = findLocationWithView(this);
        final Rect toRect = findLocationWithView(focusView);
        toRect.inset(-paddingWidth / 2, -paddingHeight / 2);

        int newWidth = toRect.width();
        int newHeight = toRect.height();
        int newX = toRect.left - fromRect.left;
        int newY = toRect.top - fromRect.top;

        final List<Animator> together = new ArrayList<>();
        final List<Animator> sequentially = new ArrayList<>();

        if (mBuilder.mAnimMode == Mode.TOGETHER) {
            toRect.inset(-offsetWidth / 2, -offsetHeight / 2);
            newWidth = toRect.width();
            newHeight = toRect.height();
            newX = toRect.left - fromRect.left;
            newY = toRect.top - fromRect.top;

            together.add(mAnimatorHelper.getBorderAnimatorWithScale(focusView, options, newX, newY, newWidth, newHeight, mBuilder.mAnimDuration, 0));

        } else if (mBuilder.mAnimMode == Mode.SEQUENTIALLY) {
            if(!isReAnim) {
                AnimatorSet anim = mAnimatorHelper.getBorderAnimator(newX, newY, newWidth, newHeight, mBuilder.mAnimDuration / 2);
                anim.setInterpolator(new DecelerateInterpolator());
                together.add(anim);
            }

            toRect.inset(-offsetWidth / 2, -offsetHeight / 2);
            newWidth = toRect.width();
            newHeight = toRect.height();
            newX = toRect.left - fromRect.left;
            newY = toRect.top - fromRect.top;

            if(!isReAnim) {
                sequentially.add(mAnimatorHelper.getBorderAnimatorWithScale(focusView, options, newX, newY, newWidth, newHeight, mBuilder.mAnimDuration / 2, 200));
            } else {
                together.add(mAnimatorHelper.getBorderAnimatorWithScale(focusView, options, newX, newY, newWidth, newHeight, mBuilder.mAnimDuration / 2, 0));
            }
        } else {
            if(!isReAnim) {
                setTranslationX(newX);
                setTranslationY(newY);
                setWidth(newWidth);
                setHeight(newHeight);
            }

            if (options.isScale()) {
                toRect.inset(-offsetWidth / 2, -offsetHeight / 2);
                newWidth = toRect.width();
                newHeight = toRect.height();
                newX = toRect.left - fromRect.left;
                newY = toRect.top - fromRect.top;

                together.add(mAnimatorHelper.getBorderAnimatorWithScale(focusView, options, newX, newY, newWidth, newHeight, mBuilder.mAnimDuration, 0));
            }
        }

        final List<Animator> appendTogether = getTogetherAnimators(newX, newY, newWidth, newHeight, options);
        if (null != appendTogether && !appendTogether.isEmpty()) {
            together.addAll(appendTogether);
        }

        if(isReAnim) {
            together.add(mAnimatorHelper.getShimmerAndTitleAnimator(options, isReAnim));
        } else {
            sequentially.add(mAnimatorHelper.getShimmerAndTitleAnimator(options, isReAnim));
        }

        final List<Animator> appendSequentially = getSequentiallyAnimators(newX, newY, newWidth, newHeight, options);
        if (null != appendSequentially && !appendSequentially.isEmpty()) {
            sequentially.addAll(appendSequentially);
        }
        if (mBuilder.mRunBreathingAnim) {
            sequentially.add(mAnimatorHelper.getBreathingLampAnimator());
        }

        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.setInterpolator(new DecelerateInterpolator(1));
        mAnimatorSet.playTogether(together);
        mAnimatorSet.playSequentially(sequentially);
        //增加延迟是为了让view重新布局（宽高等）
        mAnimatorSet.setStartDelay(50);

        return mAnimatorSet;
    }

    @NonNull
    public abstract View getBorderView();

    abstract float getRoundRadius();

    abstract List<Animator> getTogetherAnimators(float newX, float newY, int newWidth, int newHeight, Options options);

    abstract List<Animator> getSequentiallyAnimators(float newX, float newY, int newWidth, int newHeight, Options options);

    private static class RecyclerViewScrollListener extends RecyclerView.OnScrollListener {
        private WeakReference<AbsFocusBorder> mFocusBorder;
        private int mScrolledX = 0, mScrolledY = 0;

        public RecyclerViewScrollListener(AbsFocusBorder border) {
            mFocusBorder = new WeakReference<>(border);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            mScrolledX = Math.abs(dx) == 1 ? 0 : dx;
            mScrolledY = Math.abs(dy) == 1 ? 0 : dy;
        }

        private View getFocused(RecyclerView recyclerView) {
            View focused = recyclerView.getFocusedChild();
            if (focused instanceof RecyclerView) {
                return getFocused((RecyclerView) focused);
            }
            return focused;
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                final AbsFocusBorder border = mFocusBorder.get();
                final View focused = getFocused(recyclerView);
                if (null != border && null != focused && !(focused instanceof RecyclerView)) {
                    if (border.mReAnim || mScrolledX != 0 || mScrolledY != 0) {
                        border.runBorderAnimation(focused, Options.get(border.mScaleX, border.mScaleY), true);
                    }
                }
                mScrolledX = mScrolledY = 0;
            }
        }
    }

    public static class Options extends FocusBorder.Options {
        protected float scaleX = 1f, scaleY = 1f;
        protected String title;

        Options() {
        }

        private static class OptionsHolder {
            private static final Options INSTANCE = new Options();
        }

        public static Options get() {
            return get(1.0f, 1.0f);
        }

        public static Options get(float scaleX, float scaleY) {
            return get(scaleX, scaleY, null);
        }

        public static Options get(float scaleX, float scaleY, String title) {
            OptionsHolder.INSTANCE.scaleX = scaleX;
            OptionsHolder.INSTANCE.scaleY = scaleY;
            OptionsHolder.INSTANCE.title = title;
            return OptionsHolder.INSTANCE;
        }

        public boolean isScale() {
            return scaleX != 1f || scaleY != 1f;
        }
    }

    public enum Mode {
        TOGETHER,
        SEQUENTIALLY,
        NOLL
    }

    public static abstract class Builder {
        protected int mShimmerColor = 0x66FFFFFF;
        protected boolean mRunShimmerAnim = true;
        protected boolean mRunBreathingAnim = true;
        protected Mode mAnimMode = Mode.TOGETHER;
        protected long mAnimDuration = AbsFocusBorder.DEFAULT_ANIM_DURATION_TIME;
        protected long mShimmerDuration = AbsFocusBorder.DEFAULT_SHIMMER_DURATION_TIME;
        protected long mBreathingDuration = AbsFocusBorder.DEFAULT_BREATHING_DURATION_TIME;
        protected RectF mPaddingOffsetRectF = new RectF();

        protected Rect mTitlePaddingRect;
        protected Rect mTitleMarginRect;
        protected boolean mTitleMarginBottomAutoAlignBorder;
        protected float mTitleTextSize = 20;
        protected int mTitleTextColor = 0x66FFFFFF;
        protected int mTitleWidth = ViewGroup.LayoutParams.WRAP_CONTENT;
        protected int mTitleBackgroundRes;
        protected long mTitleAnimDuration = AbsFocusBorder.DEFAULT_TITLE_ANIM_DURATION_TIME;

        public Builder titlePadding(int left, int top, int right, int bottom) {
            this.mTitlePaddingRect = new Rect(left, top, right, bottom);
            return this;
        }

        public Builder titlePadding(int padding) {
            return titlePadding(padding, padding, padding, padding);
        }

        public Builder titleMargin(int left, int top, int right, int bottom) {
            this.mTitleMarginRect = new Rect(left, top, right, bottom);
            return this;
        }

        public Builder titleMargin(int margin) {
            return titleMargin(margin, margin, margin, margin);
        }

        /**
         * 标题自动对齐焦点框底部
         *
         * @return
         */
        public Builder titleMarginBottomAutoAlignBorder() {
            this.mTitleMarginBottomAutoAlignBorder = true;
            return this;
        }

        public Builder titleTextSize(float size) {
            this.mTitleTextSize = size;
            return this;
        }

        public Builder titleTextColor(@ColorInt int color) {
            this.mTitleTextColor = color;
            return this;
        }

        public Builder titleTextColor(@ColorRes int colorRes, Context context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                this.shimmerColor(ContextCompat.getColor(context, colorRes));
            } else {
                this.titleTextColor(context.getResources().getColor(colorRes));
            }
            return this;
        }

        public Builder titleWidth(int width) {
            this.mTitleWidth = width;
            return this;
        }

        public Builder titleBackgroundRes(@DrawableRes int drawableRes) {
            this.mTitleBackgroundRes = drawableRes;
            return this;
        }

        public Builder titleAnimDuration(long duration) {
            this.mTitleAnimDuration = duration;
            return this;
        }

        public Builder breathingDuration(long duration) {
            this.mBreathingDuration = duration;
            return this;
        }

        public Builder noBreathing() {
            this.mRunBreathingAnim = false;
            return this;
        }

        public Builder shimmerColor(@ColorInt int color) {
            this.mShimmerColor = color;
            return this;
        }

        public Builder shimmerColorRes(@ColorRes int colorId, Context context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                this.shimmerColor(context.getColor(colorId));
            } else {
                this.shimmerColor(context.getResources().getColor(colorId));
            }
            return this;
        }

        public Builder shimmerDuration(long duration) {
            this.mShimmerDuration = duration;
            return this;
        }

        public Builder noShimmer() {
            this.mRunShimmerAnim = false;
            return this;
        }

        public Builder animDuration(long duration) {
            this.mAnimDuration = duration;
            return this;
        }

        public Builder animMode(Mode mode) {
            this.mAnimMode = mode;
            return this;
        }

        public Builder padding(float padding) {
            return padding(padding, padding, padding, padding);
        }

        public Builder padding(float left, float top, float right, float bottom) {
            this.mPaddingOffsetRectF.left = left;
            this.mPaddingOffsetRectF.top = top;
            this.mPaddingOffsetRectF.right = right;
            this.mPaddingOffsetRectF.bottom = bottom;
            return this;
        }

//        public FocusBorder build(Fragment fragment) {
//            if (null != fragment.getActivity()) {
//                return build(fragment.getActivity());
//            }
//            return build((ViewGroup) fragment.getView());
//        }
//
//        public FocusBorder build(Fragment fragment) {
//            if (null != fragment.getActivity()) {
//                return build(fragment.getActivity());
//            }
//            return build((ViewGroup) fragment.getView());
//        }
//
        public abstract FocusBorder build(Activity activity);

        public abstract FocusBorder build(ViewGroup viewGroup);
    }
}
