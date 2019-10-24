package com.seiko.widget.tv;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.FocusFinder;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;

import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

/**
 * @author ZhouSuQiang
 * @date 2017/11/6
 */
public class TvRecyclerView extends RecyclerView implements View.OnClickListener, View.OnFocusChangeListener{
    private static final int DEFAULT_LOAD_MORE_BEFOREHAND_COUNT = 4;
    private static final Class<?>[] LAYOUT_MANAGER_CONSTRUCTOR_SIGNATURE =
            new Class[]{Context.class, AttributeSet.class, int.class};

    public int mVerticalSpacingWithMargins = 0;
    public int mHorizontalSpacingWithMargins = 0;
    private int mOldVerticalSpacingWithMargins = 0;
    private int mOldHorizontalSpacingWithMargins = 0;

    private int mSelectedItemOffsetStart;
    private int mSelectedItemOffsetEnd;
    private boolean mSelectedItemCentered;

    private boolean mIsMemoryFocus;
    private boolean mIsMenu;

    private boolean mHasMoreData = true;
    private boolean mLoadingMore = false;
    private int mLoadMoreBeforehandCount;

    private int mSelectedPosition = NO_POSITION;
    private boolean mHasFocusWithPrevious = false;

    private OnItemListener mOnItemListener;
    private OnInBorderKeyEventListener mOnInBorderKeyEventListener;
    private OnLoadMoreListener mOnLoadMoreListener;

    private final Rect mTempRect = new Rect();
    private final IRecyclerViewDataObserver mDataObserver = new IRecyclerViewDataObserver();
    private boolean mShouldReverseLayout = true;
    private boolean mOptimizeLayout;

    protected int mScrollX, mScrollY;

    public TvRecyclerView(Context context) {
        this(context, null);
    }

    public TvRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TvRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init(context);

        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TvRecyclerView, defStyle, 0);

//        final String name = a.getString(R.styleable.TvRecyclerView_tv_layoutManager);
//        if (!TextUtils.isEmpty(name)) {
//            createLayoutManager(context, name, attrs, defStyle);
//        }
        mSelectedItemCentered = a.getBoolean(R.styleable.TvRecyclerView_tv_selectedItemIsCentered, false);
        mIsMenu = a.getBoolean(R.styleable.TvRecyclerView_tv_isMenu, false);
        mIsMemoryFocus = a.getBoolean(R.styleable.TvRecyclerView_tv_isMemoryFocus, true);
        mLoadMoreBeforehandCount = a.getInt(R.styleable.TvRecyclerView_tv_loadMoreBeforehandCount, DEFAULT_LOAD_MORE_BEFOREHAND_COUNT);
        mSelectedItemOffsetStart = a.getDimensionPixelOffset(R.styleable.TvRecyclerView_tv_selectedItemOffsetStart, 0);
        mSelectedItemOffsetEnd = a.getDimensionPixelOffset(R.styleable.TvRecyclerView_tv_selectedItemOffsetEnd, 0);
        mOptimizeLayout = a.getBoolean(R.styleable.TvRecyclerView_tv_optimizeLayout, false);

        int vm = a.getDimensionPixelOffset(R.styleable.TvRecyclerView_tv_verticalSpacingWithMargins, 0);
        int hm = a.getDimensionPixelOffset(R.styleable.TvRecyclerView_tv_horizontalSpacingWithMargins, 0);
        setSpacingWithMargins(vm, hm);

        a.recycle();
    }

    private void init(Context context){
        setChildrenDrawingOrderEnabled(true);
        setWillNotDraw(true); // 自身不作onDraw处理
        setHasFixedSize(true);
        setOverScrollMode(View.OVER_SCROLL_NEVER);

        setClipChildren(false);
        setClipToPadding(false);

//        setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
        setFocusable(true);
        setFocusableInTouchMode(true);

        //修复adapter.notifyItemChanged时焦点闪烁的问题
        if(null != getItemAnimator()) {
            ((SimpleItemAnimator) getItemAnimator()).setSupportsChangeAnimations(false);
        }
    }

//    /**
//     * Instantiate and set a LayoutManager, if specified in the attributes.
//     */
//    private void createLayoutManager(Context context, String className, AttributeSet attrs,
//                                     int defStyleAttr) {
//        if (className != null) {
//            className = className.trim();
//            if (className.length() != 0) {  // Can't use isEmpty since it was added in API 9.
//                className = getFullClassName(context, className);
//                try {
//                    ClassLoader classLoader;
//                    if (isInEditMode()) {
//                        // Stupid layoutlib cannot handle simple class loaders.
//                        classLoader = this.getClass().getClassLoader();
//                    } else {
//                        classLoader = context.getClassLoader();
//                    }
//                    Class<? extends LayoutManager> layoutManagerClass =
//                            classLoader.loadClass(className).asSubclass(LayoutManager.class);
//                    Constructor<? extends LayoutManager> constructor;
//                    Object[] constructorArgs = null;
//                    try {
//                        constructor = layoutManagerClass
//                                .getConstructor(LAYOUT_MANAGER_CONSTRUCTOR_SIGNATURE);
//                        constructorArgs = new Object[]{context, attrs, defStyleAttr};
//                    } catch (NoSuchMethodException e) {
//                        try {
//                            constructor = layoutManagerClass.getConstructor();
//                        } catch (NoSuchMethodException e1) {
//                            e1.initCause(e);
//                            throw new IllegalStateException(attrs.getPositionDescription() +
//                                    ": Error creating LayoutManager " + className, e1);
//                        }
//                    }
//                    constructor.setAccessible(true);
//                    setLayoutManager(constructor.newInstance(constructorArgs));
//                } catch (ClassNotFoundException e) {
//                    throw new IllegalStateException(attrs.getPositionDescription()
//                            + ": Unable to find LayoutManager " + className, e);
//                } catch (InvocationTargetException e) {
//                    throw new IllegalStateException(attrs.getPositionDescription()
//                            + ": Could not instantiate the LayoutManager: " + className, e);
//                } catch (InstantiationException e) {
//                    throw new IllegalStateException(attrs.getPositionDescription()
//                            + ": Could not instantiate the LayoutManager: " + className, e);
//                } catch (IllegalAccessException e) {
//                    throw new IllegalStateException(attrs.getPositionDescription()
//                            + ": Cannot access non-public constructor " + className, e);
//                } catch (ClassCastException e) {
//                    throw new IllegalStateException(attrs.getPositionDescription()
//                            + ": Class is not a LayoutManager " + className, e);
//                }
//            }
//        }
//    }
//
//    private String getFullClassName(Context context, String className) {
//        if (className.charAt(0) == '.') {
//            return context.getPackageName() + className;
//        }
//        if (className.contains(".")) {
//            return className;
//        }
//        return TvRecyclerView.class.getPackage().getName() + '.' + className;
//    }


    public int getSelectedPosition() {
        return mSelectedPosition;
    }

    public void setMemoryFocus(boolean memoryFocus) {
        mIsMemoryFocus = memoryFocus;
    }

    public boolean isMemoryFocus() {
        return mIsMemoryFocus;
    }

    public void setMenu(boolean menu) {
        mIsMenu = menu;
    }

    public boolean isMenu() {
        return mIsMenu;
    }

    public void setLoadMoreBeforehandCount(int loadMoreBeforehandCount) {
        mLoadMoreBeforehandCount = loadMoreBeforehandCount;
    }

    public int getLoadMoreBeforehandCount() {
        return mLoadMoreBeforehandCount;
    }

    public boolean hasMoreData() {
        return mHasMoreData;
    }

    public void setHasMoreData(boolean hasMoreData) {
        mHasMoreData = hasMoreData;
    }

    public void finishLoadMoreWithNoMore() {
        mLoadingMore = false;
        setHasMoreData(false);
    }

    public void finishLoadMore() {
        mLoadingMore = false;
    }

    public boolean isLoadingMore() {
        return mLoadingMore;
    }

    /**
     * 设置选中的Item距离开始或结束的偏移量；
     * 与滚动方向有关；
     * 与setSelectedItemAtCentered()方法二选一
     * @param offsetStart
     * @param offsetEnd
     */
    public void setSelectedItemOffset(int offsetStart, int offsetEnd) {
        this.mSelectedItemOffsetStart = offsetStart;
        this.mSelectedItemOffsetEnd = offsetEnd;
    }

    public int getSelectedItemOffsetStart() {
        return mSelectedItemOffsetStart;
    }

    public int getSelectedItemOffsetEnd() {
        return mSelectedItemOffsetEnd;
    }

    /**
     * 设置选中的Item居中；
     * 与setSelectedItemOffset()方法二选一
     * @param isCentered
     */
    public void setSelectedItemAtCentered(boolean isCentered) {
        this.mSelectedItemCentered = isCentered;
    }

    public boolean isSelectedItemCentered() {
        return mSelectedItemCentered;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final long startMillis = System.currentTimeMillis();
        mHasFocusWithPrevious = mHasFocusWithPrevious || hasFocus();
        Loger.i("onLayout...start hasFocus()="+mHasFocusWithPrevious + " changed="+changed + " ,mShouldReverseLayout="+ mShouldReverseLayout);

        final boolean requestLayout = !mOptimizeLayout || (changed || mShouldReverseLayout);
        final boolean layoutAfterFocus;
        if(requestLayout) {
            super.onLayout(changed, l, t, r, b);
            mShouldReverseLayout = false;

            layoutAfterFocus = hasFocus();
            if(!layoutAfterFocus) {
                if(mSelectedPosition < 0) {
                    mSelectedPosition = getFirstVisibleAndFocusablePosition();
                } else if(mSelectedPosition >= getItemCount()) {
                    mSelectedPosition = getLastVisibleAndFocusablePosition();
                }
                if(mHasFocusWithPrevious && getPreserveFocusAfterLayout()) {
                    requestDefaultFocus();
                } else if(mIsMenu){
                    setItemActivated(mSelectedPosition);
                }
            }
        } else {
            layoutAfterFocus = hasFocus();
        }

        mHasFocusWithPrevious = false;
        Loger.i("onLayout...end layoutAfterFocus="+layoutAfterFocus+". used time " + (System.currentTimeMillis() - startMillis) / 1000f + "s");
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        long startMillis = System.currentTimeMillis();
        Loger.i("onMeasure...start");
        super.onMeasure(widthSpec, heightSpec);
        Loger.i("onMeasure...end. used time " + (System.currentTimeMillis() - startMillis) / 1000f + "s");
    }

    @Override
    public boolean isInEditMode() {
        return true;
    }

    public int getItemCount() {
        if(null != getAdapter()) {
            return getAdapter().getItemCount();
        }
        return 0;
    }

    @Override
    public void swapAdapter(Adapter adapter, boolean removeAndRecycleExistingViews) {
        if(null == adapter) {
            return;
        }
        resetAdapter(adapter, false);
        super.swapAdapter(adapter, removeAndRecycleExistingViews);
    }

    @Override
    public void setAdapter(final Adapter adapter) {
        if(null == adapter) {
            return;
        }
        resetAdapter(adapter, false);
        super.setAdapter(adapter);
    }

    public void setAdapter(final Adapter adapter, final boolean resetSelectedPosition) {
        if(null == adapter) {
            return;
        }
        resetAdapter(adapter, resetSelectedPosition);
        super.setAdapter(adapter);
    }

    private void resetAdapter(Adapter newAdapter, boolean resetSelectedPosition) {
        final Adapter oldAdapter = getAdapter();
        if(null != oldAdapter) {
            oldAdapter.unregisterAdapterDataObserver(mDataObserver);
            mShouldReverseLayout = true;
        }
        newAdapter.registerAdapterDataObserver(mDataObserver);
        //修复重新setAdapter后第一条被遮挡的问题
        View view = getChildAt(0);
        if(null != view && null != oldAdapter
                && getLayoutManager() instanceof BaseLayoutManager) {
            mHasFocusWithPrevious = hasFocus();
            int start = getLayoutManager().canScrollVertically() ? getLayoutManager().getDecoratedTop(view) : getLayoutManager().getDecoratedLeft(view);
            start -= getLayoutManager().canScrollVertically() ? getPaddingTop() : getPaddingLeft();
            scrollBy(start, start);
        } else {
            mSelectedPosition = getFirstVisibleAndFocusablePosition();
        }

        if(resetSelectedPosition) {
            mSelectedPosition = NO_POSITION;
        }
    }

    @Override
    public void onClick(View itemView) {
        if(null != mOnItemListener && this != itemView) {
            mOnItemListener.onItemClick(TvRecyclerView.this, itemView, getChildAdapterPosition(itemView));
        }
    }

    @Override
    public void onFocusChange(final View itemView, boolean hasFocus) {
        if(null != itemView) {
            final int position = getChildAdapterPosition(itemView);
            final boolean isRv = itemView instanceof RecyclerView;
            if (!isRv) {
                itemView.setSelected(hasFocus);
            }
            if (hasFocus) {
                mSelectedPosition = position;
                if (!isRv) {
                    if (mIsMenu && itemView.isActivated()) {
                        itemView.setActivated(false);
                    }
                    if (null != mOnItemListener) {
                        mOnItemListener.onItemSelected(TvRecyclerView.this, itemView, position);
                    }
                }
            } else {
                itemView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(!hasFocus()) {
                            if(mIsMenu && !isRv) {
                                // 解决选中后无状态表达的问题，selector中使用activated代表选中后焦点移走
                                itemView.setActivated(true);
                            }
                            //模拟TvRecyclerView失去焦点
//                            onFocusChanged(false, FOCUS_DOWN, null);
                            if(null != getOnFocusChangeListener()) {
                                getOnFocusChangeListener().onFocusChange(TvRecyclerView.this, false);
                            }
                        }
                    }
                }, 6);

                if(null != mOnItemListener && !isRv) {
                    mOnItemListener.onItemPreSelected(TvRecyclerView.this, itemView, position);
                }
            }
        }
    }

    @Override
    protected boolean onRequestFocusInDescendants(int direction, Rect previouslyFocusedRect) {
        //焦点记忆
        final int position = mSelectedPosition == NO_POSITION || !mIsMemoryFocus ? getFirstVisibleAndFocusablePosition() : mSelectedPosition;
        View child = null != getLayoutManager() ? getLayoutManager().findViewByPosition(position) : null;
        if (null != child) {
            if(null != getOnFocusChangeListener()) {
                getOnFocusChangeListener().onFocusChange(this, true);
            }
            return child.requestFocus(direction, previouslyFocusedRect);
        }
        return super.onRequestFocusInDescendants(direction, previouslyFocusedRect);
    }

    @Override
    public void addFocusables(ArrayList<View> views, int direction, int focusableMode) {
        if (!hasFocus() && isFocusable()) {
            views.add(this);
            return;
        }
        super.addFocusables(views, direction, focusableMode);
    }

    /*@Override
    public boolean requestFocus(int direction, Rect previouslyFocusedRect) {
        Loger.i("direction..."+direction);
        if(null == getFocusedChild()) {
            //请求默认焦点
            requestDefaultFocus();
        }
        return false;
    }
    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, @Nullable Rect previouslyFocusedRect) {
        Loger.i("gainFocus="+gainFocus + " hasFocus="+hasFocus()+" direction="+direction);
        if(gainFocus) {
            setDescendantFocusability(FOCUS_BEFORE_DESCENDANTS);
        } else {
            setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
        }
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
    }*/

    @Override
    public void onScrollStateChanged(int state) {
        if(state == SCROLL_STATE_IDLE) {
            setScrollValue(0, 0);

            // 加载更多回调
            if(null != mOnLoadMoreListener && !mLoadingMore && mHasMoreData) {
                if(getLastVisiblePosition() >= getAdapter().getItemCount() - (1 + mLoadMoreBeforehandCount)) {
                    mLoadingMore = true;
                    mOnLoadMoreListener.onLoadMore();
                }
            }
        }
        super.onScrollStateChanged(state);
    }

    private Point mScrollPoint = new Point();
    void setScrollValue(int x, int y) {
        if(x != 0 || y != 0) {
            mScrollPoint.set(x, y);
            setTag(mScrollPoint);
        } else {
            setTag(null);
        }
    }

    private int getFreeHeight() {
        return getHeight() - getPaddingTop() - getPaddingBottom();
    }

    private int getFreeWidth() {
        return getWidth() - getPaddingLeft() - getPaddingRight();
    }

    @Override
    public boolean requestChildRectangleOnScreen(View child, Rect rect, boolean immediate) {
        if(null == child || null == getLayoutManager()) {
            return false;
        }

        if(mSelectedItemCentered) {
            getDecoratedBoundsWithMargins(child, mTempRect);
            mSelectedItemOffsetStart = (getLayoutManager().canScrollHorizontally() ? (getFreeWidth() - mTempRect.width())
                    : (getFreeHeight() - mTempRect.height())) / 2;
            mSelectedItemOffsetEnd = mSelectedItemOffsetStart;
        }

        int[] scrollAmount = getChildRectangleOnScreenScrollAmount2(child, rect, mSelectedItemOffsetStart, mSelectedItemOffsetEnd);
        int dx = scrollAmount[0];
        int dy = scrollAmount[1];
        Loger.i("dx="+dx+" dy="+dy);

        smoothScrollBy(dx, dy);

        if (dx != 0 || dy != 0) {
            return true;
        }

        // 重绘是为了选中item置顶，具体请参考getChildDrawingOrder方法
        postInvalidate();

        return false;
    }

    @Override
    public void smoothScrollBy(int dx, int dy, Interpolator interpolator) {
        setScrollValue(dx, dy);
        super.smoothScrollBy(dx, dy, interpolator);
    }

    private int[] getChildRectangleOnScreenScrollAmount2(View focusView, Rect rect, int offsetStart, int offsetEnd) {
        //横向滚动
        int dx = 0;
        int dy = 0;

        if(null != getLayoutManager()) {

            getDecoratedBoundsWithMargins(focusView, mTempRect);

            if (getLayoutManager().canScrollHorizontally()) {
                final int right =
                        mTempRect.right
                                + getPaddingRight()
                                - getWidth();
                final int left =
                        mTempRect.left
                                - getPaddingLeft();
                Loger.i("zsq left=" + left + " right=" + right);
                dx = computeScrollOffset(left, right, offsetStart, offsetEnd);
            }

            //竖向滚动
            if (getLayoutManager().canScrollVertically()) {
                final int bottom =
                        mTempRect.bottom
                                + getPaddingBottom()
                                - getHeight();
                final int top =
                        mTempRect.top
                                - getPaddingTop();
                Loger.i("zsq top=" + top + " bottom=" + bottom);
                dy = computeScrollOffset(top, bottom, offsetStart, offsetEnd);
            }

        }

        Loger.i("zsq dx="+dx+" dy="+dy);

        return new int[]{dx, dy};
    }

    private int computeScrollOffset(int start, int end, int offsetStart, int offsetEnd) {
        Loger.i("zsq start="+start+" end="+end+" offsetStart="+offsetStart+" offsetEnd="+offsetEnd);
        Loger.i("zsq canScrollHorizontally( -1)="+canScrollHorizontally( -1)+" canScrollVertically( -1)="+canScrollVertically( -1));

        // focusView超出下/右边界
        if (end > 0) {
            if(getLastVisiblePosition() != (getItemCount() - 1)) {
                return end + offsetEnd;
            } else {
                return end;
            }
        }
        // focusView超出上/左边界
        else if (start < 0) {
            if(getFirstVisiblePosition() != 0) {
                return start - offsetStart;
            } else {
                return start;
            }
        }
        // focusView未超出上/左边界，但边距小于指定offset
        else if(start > 0 && start < offsetStart
                && (canScrollHorizontally( -1) || canScrollVertically( -1))) {
            return start - offsetStart;
        }
        // focusView未超出下/右边界，但边距小于指定offset
        else if(Math.abs(end) > 0 && Math.abs(end) < offsetEnd
                && (canScrollHorizontally( 1) || canScrollVertically( 1))) {
            return offsetEnd - Math.abs(end);
        }

        return 0;
    }

    @Override
    public boolean canScrollHorizontally(int direction) {
        if(null != getLayoutManager() && getLayoutManager() instanceof  TwoWayLayoutManager) {
            final TwoWayLayoutManager lm = (TwoWayLayoutManager) getLayoutManager();
            return lm.canScrollHorizontally() && (direction > 0 ? !lm.cannotScrollBackward(direction) : !lm.cannotScrollForward(direction));
        }
        return super.canScrollHorizontally(direction);
    }

    @Override
    public boolean canScrollVertically(int direction) {
        if(null != getLayoutManager() && getLayoutManager() instanceof  TwoWayLayoutManager) {
            final TwoWayLayoutManager lm = (TwoWayLayoutManager) getLayoutManager();
            return lm.canScrollVertically() && (direction > 0 ? !lm.cannotScrollBackward(direction) : !lm.cannotScrollForward(direction));
        }
        return super.canScrollVertically(direction);
    }

    /**
     * 通过Margins来设置布局的横纵间距；
     * (与addItemDecoration()方法可二选一)
     * @param verticalSpacing
     * @param horizontalSpacing
     */
    public void setSpacingWithMargins(int verticalSpacing, int horizontalSpacing) {
        if(this.mVerticalSpacingWithMargins != verticalSpacing || this.mHorizontalSpacingWithMargins != horizontalSpacing) {
            this.mOldVerticalSpacingWithMargins = this.mVerticalSpacingWithMargins;
            this.mOldHorizontalSpacingWithMargins = this.mHorizontalSpacingWithMargins;
            this.mVerticalSpacingWithMargins = verticalSpacing;
            this.mHorizontalSpacingWithMargins = horizontalSpacing;
            adjustPadding();
        }
    }

    /**
     * 根据Margins调整Padding值
     */
    private void adjustPadding() {
        if((mVerticalSpacingWithMargins >= 0 || mHorizontalSpacingWithMargins >= 0)) {
            final int verticalSpacingHalf = mVerticalSpacingWithMargins / 2;
            final int horizontalSpacingHalf = mHorizontalSpacingWithMargins / 2;
            final int oldVerticalSpacingHalf = mOldVerticalSpacingWithMargins / 2;
            final int oldHorizontalSpacingHalf = mOldHorizontalSpacingWithMargins / 2;
            final int l = getPaddingLeft() + oldHorizontalSpacingHalf - horizontalSpacingHalf;
            final int t = getPaddingTop() + oldVerticalSpacingHalf - verticalSpacingHalf;
            final int r = getPaddingRight() +  oldHorizontalSpacingHalf - horizontalSpacingHalf;
            final int b = getPaddingBottom() + oldVerticalSpacingHalf - verticalSpacingHalf;
            setPadding(l, t, r, b);
        }
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        boolean result = super.checkLayoutParams(p);
        if(result && (mVerticalSpacingWithMargins >= 0 || mHorizontalSpacingWithMargins >= 0)) {
            final int verticalSpacingHalf = mVerticalSpacingWithMargins / 2;
            final int horizontalSpacingHalf = mHorizontalSpacingWithMargins / 2;
            final LayoutParams lp = (LayoutParams) p;
            lp.setMargins(horizontalSpacingHalf, verticalSpacingHalf, horizontalSpacingHalf, verticalSpacingHalf);
        }
        return result;
    }

    /**
     * 获取第一个可获取焦点的item
     * @return
     */
    public int getFirstVisibleAndFocusablePosition() {
        int position = getFirstVisiblePosition();
        for(; position < getChildCount(); position ++) {
            View item = getLayoutManager().findViewByPosition(position);
            if(null != item && item.isFocusable()) {
                return position;
            }
        }
        return NO_POSITION;
    }

    public int getFirstVisiblePosition() {
        if(getChildCount() == 0) {
            return 0;
        } else {
            if(getLayoutManager() instanceof LinearLayoutManager) {
                return ((LinearLayoutManager) getLayoutManager()).findFirstVisibleItemPosition();
            }
            return getChildAdapterPosition(getChildAt(0));
        }
    }

    /**
     * 获取最后一个可获取焦点的item
     * @return
     */
    public int getLastVisibleAndFocusablePosition() {
        int fPosition = getFirstVisiblePosition();
        int lPosition = getLastVisiblePosition();
        for(; lPosition >= fPosition; lPosition --) {
            View item = getLayoutManager().findViewByPosition(lPosition);
            if(null != item && item.isFocusable()) {
                return lPosition;
            }
        }
        return -1;
    }

    public int getLastVisiblePosition() {
        final int childCount = getChildCount();
        if(childCount == 0) {
            return 0;
        } else {
            if(getLayoutManager() instanceof LinearLayoutManager) {
                return ((LinearLayoutManager) getLayoutManager()).findLastVisibleItemPosition();
            }
            return getChildAdapterPosition(getChildAt(childCount - 1));
        }
    }

    /**
     * 请求默认焦点
     */
    public void requestDefaultFocus() {
        Loger.i("zqq requestDefaultFocus");
        if(mIsMenu || mIsMemoryFocus) {
            if(mSelectedPosition < 0) {
                mSelectedPosition = getFirstVisibleAndFocusablePosition();
            }
            setSelection(mSelectedPosition);
        } else {
            setSelection(getFirstVisibleAndFocusablePosition());
        }
    }

    /**
     * 选中指定项
     *
     * @param position 对应的位置索引
     */
    public void setSelection(final int position) {
        post(new Runnable() {
            @Override
            public void run() {
                if(null == getAdapter() || position < 0 || position >= getItemCount()) {
                    return;
                }
                mSelectedPosition = position;
                View view = null;
                if(null != getLayoutManager()) {
                    view = getLayoutManager().findViewByPosition(position);
                }
                if(null != view) {
                    if(!hasFocus()) {
                        //模拟TvRecyclerView获取焦点
                        onFocusChanged(true, FOCUS_DOWN, null);
                    }
                    view.requestFocus();
                }
                else {
                    TvSmoothScroller scroller = new TvSmoothScroller(getContext(), true, false, mSelectedItemOffsetStart);
                    scroller.setTargetPosition(position);
                    getLayoutManager().startSmoothScroll(scroller);
                }
            }
        });
    }

    /**
     * 选中指定项
     * 平滑的滚动到指定位置
     *
     * @param position 对应的位置索引
     */
    public void setSelectionWithSmooth(int position) {
        if(null == getAdapter() || position < 0 || position >= getItemCount()) {
            return;
        }
        mSelectedPosition = position;
        TvSmoothScroller scroller = new TvSmoothScroller(getContext(), true,
                true, mSelectedItemOffsetStart);
        scroller.setTargetPosition(position);
        getLayoutManager().startSmoothScroll(scroller);
    }

    @Override
    public void scrollToPosition(int position) {
        scrollToPositionWithOffset(position, mSelectedItemOffsetStart);
    }

    public void scrollToPositionWithOffset(int position, int offset) {
        scrollToPositionWithOffset(position, offset, false);
    }

    public void scrollToPositionWithOffset(int position, int offset, boolean isRequestFocus) {
        scrollToPosition(position, isRequestFocus, false, offset);
    }

    @Override
    public void smoothScrollToPosition(int position) {
        smoothScrollToPositionWithOffset(position, mSelectedItemOffsetStart);
    }

    public void smoothScrollToPositionWithOffset(int position, int offset) {
        smoothScrollToPositionWithOffset(position, offset, false);
    }

    public void smoothScrollToPositionWithOffset(int position, int offset, boolean isRequestFocus) {
        scrollToPosition(position, isRequestFocus, true, offset);
    }

    private void scrollToPosition(int position, boolean isRequestFocus, boolean isSmooth, int offset) {
        mSelectedPosition = position;
        TvSmoothScroller smoothScroller = new TvSmoothScroller(getContext(), isRequestFocus, isSmooth, offset);
        smoothScroller.setTargetPosition(position);
        getLayoutManager().startSmoothScroll(smoothScroller);
    }

    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        View focusedView = getFocusedChild();
        if(null != focusedView) {
            int position = indexOfChild(focusedView);
            if (i == childCount - 1) {
                //这是最后一个需要刷新的item
                if (position > i) {
                    position = i;
                }
                return position;
            }
            else if (position == i) {
                //这是原本要在最后一个刷新的item
                return childCount - 1;
            }
        }
        return i;
    }

    public boolean isScrolling() {
        return getScrollState() != SCROLL_STATE_IDLE;
    }

    /**
     * 查找下个可获取焦点的view
     * @param direction
     * @return
     */
    private View findNextFocus(int direction) {
        return FocusFinder.getInstance().findNextFocus(this, getFocusedChild(), direction);
    }

    @Override
    public boolean dispatchUnhandledMove(View focused, int direction) {
        return super.dispatchUnhandledMove(focused, direction);
    }

    @Override
    public View focusSearch(View focused, int direction) {
        final View nextFocusedView = findNextFocus(direction);
        if(hasInBorder(direction, focused, nextFocusedView)) {
            if(null == mOnInBorderKeyEventListener
                    || !mOnInBorderKeyEventListener.onInBorderKeyEvent(direction, focused)) {
                return super.focusSearch(focused, direction);
            }
            return null;
        } else {
            return nextFocusedView;
        }
    }

    /**
     * 判断选中的item是否到达边界
     */
    private boolean hasInBorder(int direction, View focused, View nextFocusedView) {
        switch (direction) {
            case FOCUS_DOWN: {
                boolean canScrollVertically = canScrollVertically(1);
                if(null != nextFocusedView && !canScrollVertically) {
                    if(null != focused) {
                        int offset = Math.abs(getHeight() - focused.getBottom()
                                - ((MarginLayoutParams)(focused.getLayoutParams())).bottomMargin - getPaddingBottom());
                        Loger.i("zsq offset="+offset);
                        return offset <= 2;
                    }
                    return false;
                }
                return !canScrollVertically;
            }
            case FOCUS_UP: {
                if(null != nextFocusedView) {
                    return false;
                }
                return !canScrollVertically(-1);
            }
            case FOCUS_LEFT: {
                if(null != nextFocusedView) {
                    return false;
                }
                return !canScrollHorizontally(-1);
            }
            case FOCUS_RIGHT: {
                boolean canScrollHorizontally = canScrollHorizontally(1);
                if(null != nextFocusedView && !canScrollHorizontally) {
                    if(null != focused) {
                        int offset = Math.abs(getWidth() - focused.getRight()
                                - ((MarginLayoutParams)(focused.getLayoutParams())).rightMargin - getPaddingRight());
                        Loger.i("zsq offset="+offset);
                        return offset <= 2;
                    }
                    return false;
                }
                return !canScrollHorizontally;
            }

            default:
                return false;
        }
    }

    @Override
    public void onChildAttachedToWindow(View child) {
        if(child.isClickable() && !ViewCompat.hasOnClickListeners(child)) {
            child.setOnClickListener(this);
        }
        if(child.isFocusable() && null == child.getOnFocusChangeListener()) {
            child.setOnFocusChangeListener(this);
        }
    }

    public void setItemActivated(int position) {
        ViewHolder holder;
        if(position != mSelectedPosition) {
            holder = findViewHolderForLayoutPosition(mSelectedPosition);
            if(null != holder && holder.itemView.isActivated()) {
                holder.itemView.setActivated(false);
            }
            mSelectedPosition = position;
        }
        holder = findViewHolderForLayoutPosition(position);
        if(null != holder && !holder.itemView.isActivated()) {
            holder.itemView.setActivated(true);
        }
    }

    public void setOnItemListener(OnItemListener onItemListener) {
        mOnItemListener = onItemListener;
    }

    public void setOnInBorderKeyEventListener(OnInBorderKeyEventListener onInBorderKeyEventListener) {
        mOnInBorderKeyEventListener = onInBorderKeyEventListener;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        mOnLoadMoreListener = onLoadMoreListener;
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public interface OnInBorderKeyEventListener {
        boolean onInBorderKeyEvent(int direction, View focused);
    }

    public interface OnItemListener {
        void onItemPreSelected(TvRecyclerView parent, View itemView, int position);

        void onItemSelected(TvRecyclerView parent, View itemView, int position);

        void onItemClick(TvRecyclerView parent, View itemView, int position);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        RecyclerView.SavedState superSavedState = (RecyclerView.SavedState) super.onSaveInstanceState();
        ISavedState savedState = new ISavedState(null != superSavedState ? superSavedState.getSuperState() : null);
        savedState.mISuperState = superSavedState;
        savedState.mSelectedPosition = mSelectedPosition;
        savedState.mVerticalSpacingWithMargins = mVerticalSpacingWithMargins;
        savedState.mHorizontalSpacingWithMargins = mHorizontalSpacingWithMargins;
        savedState.mOldVerticalSpacingWithMargins = mOldVerticalSpacingWithMargins;
        savedState.mOldHorizontalSpacingWithMargins = mOldHorizontalSpacingWithMargins;
        savedState.mSelectedItemOffsetStart = mSelectedItemOffsetStart;
        savedState.mSelectedItemOffsetEnd = mSelectedItemOffsetEnd;
        savedState.mSelectedItemCentered = mSelectedItemCentered;
        savedState.mIsMenu = mIsMenu;
        savedState.mHasMoreData = mHasMoreData;
        savedState.mIsSelectFirstVisiblePosition = mIsMemoryFocus;
        return savedState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if(null != state) {
            if(state instanceof ISavedState) {
                ISavedState savedState = (ISavedState) state;
                mSelectedPosition = savedState.mSelectedPosition;
                mVerticalSpacingWithMargins = savedState.mVerticalSpacingWithMargins;
                mHorizontalSpacingWithMargins = savedState.mHorizontalSpacingWithMargins;
                mOldVerticalSpacingWithMargins = savedState.mOldVerticalSpacingWithMargins;
                mOldHorizontalSpacingWithMargins = savedState.mOldHorizontalSpacingWithMargins;
                mSelectedItemOffsetStart = savedState.mSelectedItemOffsetStart;
                mSelectedItemOffsetEnd = savedState.mSelectedItemOffsetEnd;
                mSelectedItemCentered = savedState.mSelectedItemCentered;
                mIsMenu = savedState.mIsMenu;
                mHasMoreData = savedState.mHasMoreData;
                mIsMemoryFocus = savedState.mIsSelectFirstVisiblePosition;
                try {
                    super.onRestoreInstanceState(savedState.mISuperState);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                super.onRestoreInstanceState(state);
            }
        }
    }

    protected static class ISavedState extends android.view.View.BaseSavedState {
        private int mSelectedPosition;
        private int mVerticalSpacingWithMargins;
        private int mOldVerticalSpacingWithMargins;
        private int mHorizontalSpacingWithMargins;
        private int mOldHorizontalSpacingWithMargins;
        private int mSelectedItemOffsetStart;
        private int mSelectedItemOffsetEnd;
        private boolean mSelectedItemCentered;
        private boolean mIsMenu;
        private boolean mHasMoreData;
        private boolean mIsSelectFirstVisiblePosition;
        private Parcelable mISuperState;

        protected ISavedState(Parcelable superState) {
            super(superState);
        }

        protected ISavedState(Parcel in) {
            super(in);
            mISuperState = in.readParcelable(RecyclerView.class.getClassLoader());
            mSelectedPosition = in.readInt();
            mVerticalSpacingWithMargins = in.readInt();
            mHorizontalSpacingWithMargins = in.readInt();
            mOldVerticalSpacingWithMargins = in.readInt();
            mOldHorizontalSpacingWithMargins = in.readInt();
            mSelectedItemOffsetStart = in.readInt();
            mSelectedItemOffsetEnd = in.readInt();
            boolean[] booleens = new boolean[4];
            in.readBooleanArray(booleens);
            mSelectedItemCentered = booleens[0];
            mIsMenu = booleens[1];
            mHasMoreData = booleens[2];
            mIsSelectFirstVisiblePosition = booleens[3];
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeParcelable(mISuperState, 0);
            out.writeInt(mSelectedPosition);
            out.writeInt(mVerticalSpacingWithMargins);
            out.writeInt(mHorizontalSpacingWithMargins);
            out.writeInt(mOldVerticalSpacingWithMargins);
            out.writeInt(mOldHorizontalSpacingWithMargins);
            out.writeInt(mSelectedItemOffsetStart);
            out.writeInt(mSelectedItemOffsetEnd);
            boolean[] booleens = {mSelectedItemCentered, mIsMenu, mHasMoreData, mIsSelectFirstVisiblePosition};
            out.writeBooleanArray(booleens);
        }

        public static final Creator<ISavedState> CREATOR
                = new Creator<ISavedState>() {
            @Override
            public ISavedState createFromParcel(Parcel in) {
                return new ISavedState(in);
            }

            @Override
            public ISavedState[] newArray(int size) {
                return new ISavedState[size];
            }
        };
    }

    private class IRecyclerViewDataObserver extends AdapterDataObserver {
        @Override
        public void onChanged() {
            Loger.i("RecyclerView Data Changed!!!");
            mShouldReverseLayout = true;
        }
    }

    private class TvSmoothScroller extends LinearSmoothScroller {
        private boolean mRequestFocus;
        private boolean mIsSmooth;
        private int mOffset;

        public TvSmoothScroller(Context context, boolean isRequestFocus, boolean isSmooth, int offset) {
            super(context);
            mRequestFocus = isRequestFocus;
            mIsSmooth = isSmooth;
            mOffset = offset;
        }

        @Override
        protected int calculateTimeForScrolling(int dx) {
            return mIsSmooth ? super.calculateTimeForScrolling(dx) :
                    ((int) Math.ceil(Math.abs(dx) * (4f / getContext().getResources().getDisplayMetrics().densityDpi)));
        }

        @Override
        protected void onTargetFound(View targetView, State state, Action action) {
            if(mSelectedItemCentered && null != getLayoutManager()) {
                getDecoratedBoundsWithMargins(targetView, mTempRect);
                mOffset = (getLayoutManager().canScrollHorizontally() ? (getFreeWidth() - mTempRect.width())
                        : (getFreeHeight() - mTempRect.height())) / 2;
            }
            super.onTargetFound(targetView, state, action);
        }

        @Override
        public int calculateDtToFit(int viewStart, int viewEnd, int boxStart, int boxEnd, int snapPreference) {
            Loger.i("zsq viewStart="+viewStart+" boxStart="+boxStart + " mOffset="+mOffset);
            int dt = boxStart - viewStart + mOffset;
            Loger.i("zsq dt="+dt);
            return dt;
        }

        @Override
        protected void onStop() {
            Loger.i("zzssqq SmoothScroller onStop");
            if(mRequestFocus) {
                final int position = getTargetPosition();
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        final View itemView = TvRecyclerView.this.getLayoutManager().findViewByPosition(position);
                        if (null != itemView) {
                            Loger.i("zzssqq position="+position);
                            if (!hasFocus()) {
                                onFocusChanged(true, FOCUS_DOWN, null);
                            }
                            itemView.requestFocus();
                        } else {
                            Loger.i("zzssqq itemView is null position="+position);
                        }
                    }
                }, mIsSmooth ? 400 : 100);
            }
            super.onStop();
        }
    }
}