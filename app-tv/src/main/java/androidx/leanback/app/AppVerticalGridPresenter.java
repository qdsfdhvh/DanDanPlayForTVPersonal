package androidx.leanback.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.leanback.widget.FocusHighlight;
import androidx.leanback.widget.VerticalGridPresenter;

import com.dandanplay.tv.R;

public class AppVerticalGridPresenter extends VerticalGridPresenter {

    public AppVerticalGridPresenter() {
        super(FocusHighlight.ZOOM_FACTOR_LARGE);
    }

    public AppVerticalGridPresenter(int focusZoomFactor, boolean useFocusDimmer) {
        super(focusZoomFactor, useFocusDimmer);
    }

    public AppVerticalGridPresenter(int focusZoomFactor) {
        super(focusZoomFactor, true);
    }

    @Override
    protected ViewHolder createGridViewHolder(ViewGroup parent) {
        View root = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.widget_gride_center_view, parent, false);
        return new ViewHolder(root.findViewById(R.id.browse_grid));
    }
}
