package com.dandanplay.tv2.widget;

//
// Decompiled by Procyon - 2837ms
//

import android.content.Context;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;

public class PagerSlidingTabStrip extends HorizontalScrollView {
//    public ViewPager.OnPageChangeListener a;
//    private LinearLayout.LayoutParams b;
//    private LinearLayout.LayoutParams c;
//    private final PagerSlidingTabStrip.OnPageChangeListener d;
//    private PagerSlidingTabStrip.PageReselectedListener e;
//    private LinearLayout f;
//    private ViewPager g;
//    private int h;
//    private int i;
//    private float j;
//    private Paint k;
//    private int l;
//    private boolean m;
//    private boolean n;
//    private int o;
//    private int p;
//    private int q;
//    private int r;
//    private int s;
//    private int t;
//    private int u;
//    private Locale v;
//    private Bitmap w;
//    private View.OnClickListener x;

    public PagerSlidingTabStrip(final Context context) {
        this(context, null);
    }

    public PagerSlidingTabStrip(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }

    public PagerSlidingTabStrip(final Context context, AttributeSet obtainStyledAttributes, int dimensionPixelSize) {
        super(context, obtainStyledAttributes, dimensionPixelSize);
//        this.d = new PagerSlidingTabStrip.OnPageChangeListener(this);
//        this.i = 0;
//        this.j = 0.0f;
//        this.l = -10066330;
//        this.m = false;
//        this.n = true;
//        this.o = 52;
//        this.p = 8;
//        this.q = 24;
//        this.r = Integer.MAX_VALUE;
//        this.t = 0;
//        this.u = 0x7f0700b6;
//        this.x = (View$OnClickListener)new PagerSlidingTabStrip$2(this);
//        this.setFillViewport(true);
//        this.setWillNotDraw(false);
//        (this.f = new LinearLayout(context)).setOrientation(0);
//        this.f.setLayoutParams((ViewGroup$LayoutParams)new FrameLayout$LayoutParams(-1, -1));
//        this.addView((View)this.f);
//        final DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
//        this.o = (int)TypedValue.applyDimension(1, (float)this.o, displayMetrics);
//        this.p = (int)TypedValue.applyDimension(1, (float)this.p, displayMetrics);
//        this.q = (int)TypedValue.applyDimension(1, (float)this.q, displayMetrics);
//        if (this.isInEditMode()) {
//            return;
//        }
//        obtainStyledAttributes = (AttributeSet)context.obtainStyledAttributes(obtainStyledAttributes, a.d);
//        try {
//            ((TypedArray)obtainStyledAttributes).getResourceId(1, 0);
//            this.p = ((TypedArray)obtainStyledAttributes).getDimensionPixelSize(2, this.p);
//            this.q = ((TypedArray)obtainStyledAttributes).getDimensionPixelSize(8, this.q);
//            this.u = ((TypedArray)obtainStyledAttributes).getResourceId(5, this.u);
//            this.m = ((TypedArray)obtainStyledAttributes).getBoolean(4, this.m);
//            this.o = ((TypedArray)obtainStyledAttributes).getDimensionPixelSize(3, this.o);
//            this.n = ((TypedArray)obtainStyledAttributes).getBoolean(9, this.n);
//            this.r = ((TypedArray)obtainStyledAttributes).getDimensionPixelSize(7, this.r);
//            this.s = ((TypedArray)obtainStyledAttributes).getResourceId(0, 12);
//            dimensionPixelSize = ((TypedArray)obtainStyledAttributes).getDimensionPixelSize(6, 0);
//            this.f.setPadding(dimensionPixelSize, 0, dimensionPixelSize, 0);
//            ((TypedArray)obtainStyledAttributes).recycle();
//            (this.k = new Paint()).setAntiAlias(true);
//            this.k.setStyle(Paint.Style.FILL);
//            this.b = new LinearLayout.LayoutParams(-2, -1);
//            this.c = new LinearLayout.LayoutParams(0, -1, 1.0f);
//            if (this.v == null) {
//                this.v = this.getResources().getConfiguration().locale;
//            }
//            this.w = BitmapFactory.decodeResource(this.getResources(), 0x7f0700a8);
//        }
//        finally {
//            ((TypedArray)obtainStyledAttributes).recycle();
//        }
    }

//    private void a(final int n, final int imageResource) {
//        final ImageButton imageButton = new ImageButton(this.getContext());
//        imageButton.setImageResource(imageResource);
//        this.a(n, (View)imageButton);
//    }
//
//    private void a(final int n, final View view) {
//        view.setFocusable(true);
//        view.setTag((Object)n);
//        view.setOnClickListener(this.x);
//        final LinearLayout f = this.f;
//        LinearLayout.LayoutParams linearLayout$LayoutParams;
//        if (this.m) {
//            linearLayout$LayoutParams = this.c;
//        }
//        else {
//            linearLayout$LayoutParams = this.b;
//        }
//        f.addView(view, n, (ViewGroup.LayoutParams)linearLayout$LayoutParams);
//    }
//
//    private void a(final ViewGroup viewGroup) {
//        for (int childCount = viewGroup.getChildCount(), i = 0; i < childCount; ++i) {
//            final View child = viewGroup.getChildAt(i);
//            if (child instanceof TextView) {
//                this.a((TextView)child);
//            }
//            else if (child instanceof ViewGroup) {
//                this.a((ViewGroup)child);
//            }
//        }
//    }
//
//    private void a(final TextView textView) {
//        textView.setTextAppearance(textView.getContext(), this.s);
//        if (this.n) {
////            if (Build.VERSION.SDK_INT >= 14) {
//                textView.setAllCaps(true);
////            }
//            else {
//                textView.setText((CharSequence)textView.getText().toString().toUpperCase(this.v));
//            }
//        }
//    }
//
//    private void b() {
//        for (int i = 0; i < this.h; ++i) {
//            final View child = this.f.getChildAt(i);
//            child.setBackgroundResource(this.u);
//            child.setPadding(this.q, 0, this.q, 0);
//            if (child instanceof TextView) {
//                this.a((TextView)child);
//            }
//            else if (child instanceof ViewGroup) {
//                this.a((ViewGroup)child);
//            }
//        }
//    }
//
//    private void b(int t, final int n) {
//        if (this.h == 0) {
//            return;
//        }
//        final int n2 = this.f.getChildAt(t).getLeft() + n;
//        Label_0039: {
//            if (t <= 0) {
//                t = n2;
//                if (n <= 0) {
//                    break Label_0039;
//                }
//            }
//            t = n2 - this.o;
//        }
//        if (t != this.t) {
//            this.scrollTo(this.t = t, 0);
//        }
//    }
//
//    private void b(final int n, final CharSequence charSequence) {
//        this.a(n, this.a(n, charSequence));
//    }
//
//    public View a(final int n) {
//        if (this.f != null && this.f.getChildCount() > n) {
//            return this.f.getChildAt(n);
//        }
//        return null;
//    }
//
//    protected View a(final int n, final CharSequence text) {
//        final TextView textView = new TextView(this.getContext());
//        textView.setText(text);
//        textView.setMaxWidth(this.r);
//        textView.setGravity(17);
//        textView.setEllipsize(TextUtils.TruncateAt.END);
//        textView.setSingleLine();
//        textView.setFocusable(true);
//        return (View)textView;
//    }
//
//    public void a() {
//        this.f.removeAllViews();
//        this.h = this.g.getAdapter().getCount();
//        for (int i = 0; i < this.h; ++i) {
//            if (this.g.getAdapter() instanceof PagerSlidingTabStrip.PagerSlidingTabStrip$a) {
//                this.a(i, ((PagerSlidingTabStrip.PagerSlidingTabStrip$a)this.g.getAdapter()).a(i));
//            }
//            else {
//                this.b(i, this.g.getAdapter().getPageTitle(i));
//            }
//        }
//        this.b();
//        this.getViewTreeObserver().addOnGlobalLayoutListener(this);
//    }
//
//    public int getIndicatorColor() {
//        return this.l;
//    }
//
//    public int getIndicatorHeight() {
//        return this.p;
//    }
//
//    public int getScrollOffset() {
//        return this.o;
//    }
//
//    public boolean getShouldExpand() {
//        return this.m;
//    }
//
//    public int getTabBackground() {
//        return this.u;
//    }
//
//    public int getTabPaddingLeftRight() {
//        return this.q;
//    }
//
//    protected void onDraw(final Canvas canvas) {
//        super.onDraw(canvas);
//        if (!this.isInEditMode() && this.h != 0) {
//            final int height = this.getHeight();
//            this.k.setColor(this.l);
//            final View child = this.f.getChildAt(this.i);
//            final int left = this.f.getLeft();
//            final float n = child.getLeft() + left;
//            final float n2 = child.getRight() + left;
//            float n3 = n;
//            float n4 = n2;
//            if (this.j > 0.0f) {
//                n3 = n;
//                n4 = n2;
//                if (this.i < this.h - 1) {
//                    final View child2 = this.f.getChildAt(this.i + 1);
//                    final float n5 = child2.getLeft() + left;
//                    final float n6 = child2.getRight() + left;
//                    n3 = this.j * n5 + (1.0f - this.j) * n;
//                    n4 = this.j * n6 + (1.0f - this.j) * n2;
//                }
//            }
//            float n7;
//            if ((n7 = n3 + child.getWidth() / 2 - this.w.getWidth() / 2) > n4) {
//                n7 = n4;
//            }
//            canvas.drawBitmap(this.w, n7, (float)(height - this.w.getHeight() + 2), this.k);
//        }
//    }
//
//    @Override
//    public boolean onInterceptTouchEvent(final MotionEvent motionEvent) {
//        return this.isEnabled() && super.onInterceptTouchEvent(motionEvent);
//    }
//
//    @Override
//    protected void onRestoreInstanceState(Parcelable state) {
////        final PagerSlidingTabStrip.SavedState pagerSlidingTabStrip$SavedState = (PagerSlidingTabStrip.PagerSlidingTabStrip$SavedState)superState;
////        this.i = pagerSlidingTabStrip$SavedState.a;
////        superState = pagerSlidingTabStrip$SavedState.getSuperState();
////        while (true) {
////            try {
////                superState.getClass().getDeclaredField("isLayoutRtl").setBoolean(superState, false);
////                super.onRestoreInstanceState(superState);
////                this.requestLayout();
////            }
////            catch (Exception ex) {
////                continue;
////            }
////            break;
////        }
//        super.onRestoreInstanceState(state);
//    }
//
//    @Override
//    protected Parcelable onSaveInstanceState() {
//        return super.onSaveInstanceState();
//    }
//
//    //    public Parcelable onSaveInstanceState() {
////        final PagerSlidingTabStrip.PagerSlidingTabStrip.SavedState pagerSlidingTabStrip$SavedState = new PagerSlidingTabStrip.PagerSlidingTabStrip$SavedState(super.onSaveInstanceState());
////        pagerSlidingTabStrip$SavedState.a = this.i;
////        return (Parcelable)pagerSlidingTabStrip$SavedState;
////    }
//
//    @Override
//    public boolean onTouchEvent(final MotionEvent motionEvent) {
//        return this.isEnabled() && super.onTouchEvent(motionEvent);
//    }
//
//    public void setAllCaps(final boolean n) {
//        this.n = n;
//    }
//
//    public void setEnabled(final boolean b) {
//        if (this.isEnabled() == b) {
//            return;
//        }
//        for (int i = 0; i < this.h; ++i) {
//            this.f.getChildAt(i).setEnabled(b);
//        }
//        super.setEnabled(b);
//    }
//
//    public void setIndicatorColor(final int l) {
//        this.l = l;
//        this.invalidate();
//    }
//
//    public void setIndicatorColorResource(final int n) {
//        this.l = this.getResources().getColor(n);
//        this.invalidate();
//    }
//
//    public void setIndicatorHeight(final int p) {
//        this.p = p;
//        this.invalidate();
//    }
//
//    public void setOnPageChangeListener(final ViewPager.OnPageChangeListener a) {
//        this.a = a;
//    }
//
//    public void setOnPageReselectedListener(final PagerSlidingTabStrip.PageReselectedListener e) {
//        this.e = e;
//    }
//
//    public void setScrollOffset(final int o) {
//        this.o = o;
//        this.invalidate();
//    }
//
//    public void setShouldExpand(final boolean m) {
//        this.m = m;
//        this.requestLayout();
//    }
//
//    public void setTabBackground(final int u) {
//        this.u = u;
//    }
//
//    public void setTabPaddingLeftRight(final int q) {
//        this.q = q;
//        this.b();
//    }
//
//    public void setTabTextAppearance(final int s) {
//        this.s = s;
//        this.b();
//    }
//
//    public void setViewPager(final ViewPager g) {
//        this.g = g;
//        if (g.getAdapter() == null) {
//            throw new IllegalStateException("ViewPager does not have adapter getInstance.");
//        }
//        g.addOnPageChangeListener(this.d);
//        this.a();
//    }
//
//    class SavedState
//
//    public interface PageReselectedListener {
//
//    }
//
//    public class OnPageChangeListener implements ViewPager.OnPageChangeListener {
//
//        public OnPageChangeListener() {
//
//        }
//
//        @Override
//        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//        }
//
//        @Override
//        public void onPageSelected(int position) {
//
//        }
//
//        @Override
//        public void onPageScrollStateChanged(int state) {
//
//        }
//    }
}