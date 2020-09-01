package com.seiko.tv.ui.widget;

import android.content.Context;
import android.util.AttributeSet;

import androidx.transition.Slide;

public class SlideNoPropagation extends Slide {

    public SlideNoPropagation() {
    }

    public SlideNoPropagation(int slideEdge) {
        super(slideEdge);
    }

    public SlideNoPropagation(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setSlideEdge(int slideEdge) {
        super.setSlideEdge(slideEdge);
        setPropagation(null);
    }
}
