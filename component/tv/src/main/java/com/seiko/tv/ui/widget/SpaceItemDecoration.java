package com.seiko.tv.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Item 分割
 *
 * @Author: Dicks.yang
 * @Date: 2019.04.15
 */
public class SpaceItemDecoration extends RecyclerView.ItemDecoration {

    private int mRightSpace;
    private int mTopSpace;

    private Paint mPaint;
    private Drawable mDivider;
    private int mDividerHeight = 1;//分割线高度，默认为1px
    private static final int[] ATTRS = new int[]{android.R.attr.listDivider};
    private int mOrientation; //0->水平 1->竖直

    /**
     * 默认分割线：高度为2px，颜色为灰色
     *
     * @param context context
     * @param orientation 分割线方向 0->水平 1->竖直
     */
    public SpaceItemDecoration(Context context, int orientation) {
        if (orientation != 0 && orientation != 1) {
            throw new IllegalArgumentException("请输入正确的参数！");
        }
        mOrientation = orientation;
        final TypedArray a = context.obtainStyledAttributes(ATTRS);
        mDivider = a.getDrawable(0);
        a.recycle();
    }

    /**
     * 自定义分割线
     *
     * @param context context
     * @param orientation 分割线方向 0->水平 1->竖直
     * @param drawableId  分割线图片
     */
    public SpaceItemDecoration(Context context, int orientation, @DrawableRes int drawableId) {
        this(context, orientation);
        mDivider = ContextCompat.getDrawable(context, drawableId);
        if (mDivider != null) {
            mDividerHeight = mDivider.getIntrinsicHeight();
        }
    }

    /**
     * 自定义分割线
     *
     * @param context context
     * @param orientation 分割线方向 0->水平 1->竖直
     * @param dividerHeight 分割线高度
     * @param dividerColor  分割线颜色
     */
    public SpaceItemDecoration(Context context, int orientation, int dividerHeight, @ColorInt int dividerColor) {
        this(context, orientation);
        mDividerHeight = dividerHeight;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(dividerColor);
        mPaint.setStyle(Paint.Style.FILL);
    }

    /**
     * 分隔间隔
     *
     * @param rightSpace 右方间隔
     * @param topSpace 上方间隔
     */

    public SpaceItemDecoration(int rightSpace, int topSpace) {
        this.mRightSpace = rightSpace;
        this.mTopSpace = topSpace;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.set(0, 0, 0, mDividerHeight);
        //不是第一个的格子都设一个左边和底部的间距
        outRect.right = mRightSpace;
        outRect.top = mTopSpace;
    }


    //绘制分割线
    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);

        if(mOrientation==0)
        {
            drawHorizontal(c, parent);
        }else
        {
            drawVertical(c, parent);
        }

    }

    //绘制横向 item 分割线
    private void drawHorizontal(Canvas canvas, RecyclerView parent) {
        final int left = parent.getPaddingLeft();
        final int right = parent.getMeasuredWidth() - parent.getPaddingRight();
        final int childSize = parent.getChildCount();
        for (int i = 0; i < childSize; i++) {
            final View child = parent.getChildAt(i);
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int top = child.getBottom() + layoutParams.bottomMargin;
            final int bottom = top + mDividerHeight;
            if (mDivider != null) {
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(canvas);
            }
            if (mPaint != null) {
                canvas.drawRect(left, top, right, bottom, mPaint);
            }
        }
    }



        //绘制纵向 item 分割线
    private void drawVertical(Canvas canvas, RecyclerView parent) {
        final int top = parent.getPaddingTop();
        final int bottom = parent.getMeasuredHeight() - parent.getPaddingBottom();
        final int childSize = parent.getChildCount();
        for (int i = 0; i < childSize; i++) {
            final View child = parent.getChildAt(i);
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int left = child.getRight() + layoutParams.rightMargin;
            final int right = left + mDividerHeight;
            if (mDivider != null) {
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(canvas);
            }
            if (mPaint != null) {
                canvas.drawRect(left, top, right, bottom, mPaint);
            }
        }
    }

}
