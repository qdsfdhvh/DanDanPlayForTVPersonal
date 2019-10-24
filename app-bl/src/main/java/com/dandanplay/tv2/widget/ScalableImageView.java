package com.dandanplay.tv2.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;

import com.dandanplay.tv2.R;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;


import static android.util.TypedValue.COMPLEX_UNIT_DIP;

/* compiled from: BL */
public class ScalableImageView extends SimpleDraweeView implements ForceListener {

    private ForceViewHelper helper;


    public int scaleViewType = 0;
    public int b;
    public int c;
    private double heightRatio;

//    public ScalableImageView(Context context, GenericDraweeHierarchy hierarchy) {
//        super(context, hierarchy);
//        init(context, null);
//    }

    public ScalableImageView(Context context) {
        this(context, null);
    }

    public ScalableImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScalableImageView(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs, defStyle, 0);
    }

    public ScalableImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
        helper = new ForceViewHelper(context, this);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (canvas != null) {
            helper.onDraw(canvas, 0, 0, getWidth(), getHeight());
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (this.heightRatio > 0.0D && (this.scaleViewType == 1 || this.scaleViewType == 0)) {
            widthMeasureSpec = MeasureSpec.getSize(widthMeasureSpec);
            setMeasuredDimension(widthMeasureSpec, (int) (widthMeasureSpec * heightRatio));
            return;
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Drawable drawable = getDrawable();
        if (this.scaleViewType != 0) {
            if (drawable == null) return;
            //TODO 反编译得的结果，待运行确认。
            heightMeasureSpec = getMeasuredWidth();
            widthMeasureSpec = getMeasuredHeight();
            int i = getPaddingLeft() + getPaddingRight();
            int j = getPaddingTop() + getPaddingBottom();
            if (heightMeasureSpec > i) {
                double d1;
                if (widthMeasureSpec <= j) return;
                if (this.b != 0 && this.c != 0) {
                    d1 = this.b / this.c;
                } else if (this.heightRatio > 0.0D) {
                    d1 = 1.0D / this.heightRatio;
                } else {
                    int k = drawable.getIntrinsicWidth();
                    int m = drawable.getIntrinsicHeight();
                    if (m != 0) {
                        if (k == 0) return;
                        d1 = k / m;
                    } else {
                        return;
                    }
                }
                switch (this.scaleViewType) {
                    case 2:
                        heightMeasureSpec = (int) Math.floor((widthMeasureSpec - j) * d1 + i);
                        break;
                    case 1:
                        widthMeasureSpec = (int) Math.floor((heightMeasureSpec - i) / d1 + j);
                        break;
                }
                setMeasuredDimension(heightMeasureSpec, widthMeasureSpec);
            }

        }

//        int intrinsicHeight;
//        if (this.l <= 0.0d || !(this.b == 1 || this.b == 0)) {
//            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//            Drawable drawable = getDrawable();
//            if (this.b != 0 && drawable != null) {
//                int measuredWidth = getMeasuredWidth();
//                int measuredHeight = getMeasuredHeight();
//                int paddingRight = getPaddingRight() + getPaddingLeft();
//                int paddingBottom = getPaddingBottom() + getPaddingTop();
//                if (measuredWidth > paddingRight && measuredHeight > paddingBottom) {
//                    double d;
//                    int intrinsicWidth;
//                    if (this.c != 0 && this.d != 0) {
//                        d = ((double) this.c) / ((double) this.d);
//                    } else if (this.l > 0.0d) {
//                        d = 1.0d / this.l;
//                    } else {
//                        intrinsicWidth = drawable.getIntrinsicWidth();
//                        intrinsicHeight = drawable.getIntrinsicHeight();
//                        if (intrinsicHeight != 0 && intrinsicWidth != 0) {
//                            d = ((double) intrinsicWidth) / ((double) intrinsicHeight);
//                        } else {
//                            return;
//                        }
//                    }
//                    switch (this.b) {
//                        case TencentLocationRequest.REQUEST_LEVEL_NAME /*1*/:
//                            intrinsicHeight = (int) Math.floor((((double) (measuredWidth - paddingRight)) / d) + ((double) paddingBottom));
//                            intrinsicWidth = measuredWidth;
//                            break;
//                        case QQShare.SHARE_TO_QQ_TYPE_AUDIO /*2*/:
//                            intrinsicWidth = (int) Math.floor((d * ((double) (measuredHeight - paddingBottom))) + ((double) paddingRight));
//                            intrinsicHeight = measuredHeight;
//                            break;
//                        default:
//                            intrinsicHeight = measuredHeight;
//                            intrinsicWidth = measuredWidth;
//                            break;
//                    }
//                    setMeasuredDimension(intrinsicWidth, intrinsicHeight);
//                    return;
//                }
//                return;
//            }
//            return;
//        }
//        intrinsicHeight = MeasureSpec.getSize(widthMeasureSpec);
//        setMeasuredDimension(intrinsicHeight, (int) (((double) intrinsicHeight) * this.l));
    }

    public void setHeightRatio(double ratio) {
        if (this.heightRatio != ratio) {
            this.heightRatio = ratio;
            setAspectRatio(1.0F / (float) this.heightRatio);
        }
    }

    public void setRoundAsCircle(boolean roundAsCircle) {
        RoundingParams param = getHierarchy().getRoundingParams();
        if (param == null) param = new RoundingParams();
        param.setRoundAsCircle(roundAsCircle);
        param.setRoundingMethod(RoundingParams.RoundingMethod.BITMAP_ONLY);
        getHierarchy().setRoundingParams(param);
    }

    public void setRoundRadius(int radius) {
        if (radius > 0) {
            RoundingParams param = getHierarchy().getRoundingParams();
            if (param == null) param = new RoundingParams();
            float value = TypedValue.applyDimension(COMPLEX_UNIT_DIP, (float) radius, getResources().getDisplayMetrics());
            param.setCornersRadius(value);
            getHierarchy().setRoundingParams(param);
        }
    }

    public void setCornersRadii(float topLeft, float topRight, float bottomRight, float bottomLeft) {
        RoundingParams param = getHierarchy().getRoundingParams();
        if (param == null) param = new RoundingParams();
        param.setCornersRadii(topLeft, topRight, bottomRight, bottomLeft);
        getHierarchy().setRoundingParams(param);
    }

    public void setScaleViewType(int type) {
        this.scaleViewType = type;
    }

    public double getHeightRatio() {
        return this.heightRatio;
    }


    public void setUpDrawable(Drawable drawable) {
        helper.setUpDrawable(drawable);
    }

    @Override
    public void setUpDrawable(int drawableId) {
        helper.setUpDrawable(drawableId);
    }

    @Override
    public void setUpEnabled(boolean bool) {
        helper.setUpEnabled(bool);
    }

    public void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray gdhAttrs = context.obtainStyledAttributes(attrs, R.styleable.SimpleDraweeView);
            this.scaleViewType = gdhAttrs.getInt(R.styleable.SimpleDraweeView_placeholderImageScaleType, this.scaleViewType);
//            this.b = gdhAttrs.getInt(1, this.b);
//            this.c = gdhAttrs.getInt(0, this.c);
            if (this.b > 0 && this.c > 0) {
                this.heightRatio = (double) (((float) this.c) / ((float) this.b));
                this.scaleViewType = 1;
            }
            gdhAttrs.recycle();
        }
    }

}