package androidx.leanback.app;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.leanback.widget.BrowseFrameLayout;
import androidx.leanback.widget.ObjectAdapter;
import androidx.leanback.widget.OnChildLaidOutListener;
import androidx.leanback.widget.OnItemViewClickedListener;
import androidx.leanback.widget.OnItemViewSelectedListener;
import androidx.leanback.widget.Presenter;
import androidx.leanback.widget.Row;
import androidx.leanback.widget.RowPresenter;
import androidx.leanback.widget.VerticalGridPresenter;

import com.dandanplay.tv.R;

public class AppVerticalGridFragment extends BrandedSupportFragment {

    private static final String TAG = "VerticalGF";
    private static final boolean DEBUG = false;

    private ObjectAdapter mAdapter;
    private AppVerticalGridPresenter mGridPresenter;
    private AppVerticalGridPresenter.ViewHolder mGridViewHolder;
    private OnItemViewSelectedListener mOnItemViewSelectedListener;
    private OnItemViewClickedListener mOnItemViewClickedListener;
    private int mSelectedPosition = -1;

    /**
     * Sets the grid presenter.
     */
    public void setGridPresenter(AppVerticalGridPresenter gridPresenter) {
        if (gridPresenter == null) {
            throw new IllegalArgumentException("Grid presenter may not be null");
        }
        mGridPresenter = gridPresenter;
        mGridPresenter.setOnItemViewSelectedListener(mViewSelectedListener);
        if (mOnItemViewClickedListener != null) {
            mGridPresenter.setOnItemViewClickedListener(mOnItemViewClickedListener);
        }
    }

    /**
     * Returns the grid presenter.
     */
    public VerticalGridPresenter getGridPresenter() {
        return mGridPresenter;
    }

    /**
     * Sets the object adapter for the fragment.
     */
    public void setAdapter(ObjectAdapter adapter) {
        mAdapter = adapter;
        updateAdapter();
    }

    /**
     * Returns the object adapter.
     */
    public ObjectAdapter getAdapter() {
        return mAdapter;
    }

    final private OnItemViewSelectedListener mViewSelectedListener =
            new OnItemViewSelectedListener() {
                @Override
                public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item,
                                           RowPresenter.ViewHolder rowViewHolder, Row row) {
                    int position = mGridViewHolder.getGridView().getSelectedPosition();
                    if (DEBUG) Log.v(TAG, "grid selected position " + position);
                    gridOnItemSelected(position);
                    if (mOnItemViewSelectedListener != null) {
                        mOnItemViewSelectedListener.onItemSelected(itemViewHolder, item,
                                rowViewHolder, row);
                    }
                }
            };

    final private OnChildLaidOutListener mChildLaidOutListener =
            new OnChildLaidOutListener() {
                @Override
                public void onChildLaidOut(ViewGroup parent, View view, int position, long id) {
                    if (position == 0) {
                        showOrHideTitle();
                    }
                }
            };

    /**
     * Sets an item selection listener.
     */
    public void setOnItemViewSelectedListener(OnItemViewSelectedListener listener) {
        mOnItemViewSelectedListener = listener;
    }

    private void gridOnItemSelected(int position) {
        if (position != mSelectedPosition) {
            mSelectedPosition = position;
            showOrHideTitle();
        }
    }

    private void showOrHideTitle() {
//        if (mGridViewHolder.getGridView().findViewHolderForAdapterPosition(mSelectedPosition)
//                == null) {
//            return;
//        }
//        if (!mGridViewHolder.getGridView().hasPreviousViewInSameRow(mSelectedPosition)) {
//            showTitle(true);
//        } else {
//            showTitle(false);
//        }
    }

    /**
     * Sets an item clicked listener.
     */
    public void setOnItemViewClickedListener(OnItemViewClickedListener listener) {
        mOnItemViewClickedListener = listener;
        if (mGridPresenter != null) {
            mGridPresenter.setOnItemViewClickedListener(mOnItemViewClickedListener);
        }
    }

    /**
     * Returns the item clicked listener.
     */
    public OnItemViewClickedListener getOnItemViewClickedListener() {
        return mOnItemViewClickedListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.lb_vertical_grid_fragment,
                container, false);
        ViewGroup gridFrame = (ViewGroup) root.findViewById(R.id.grid_frame);
        installTitleView(inflater, gridFrame, savedInstanceState);

        ViewGroup gridDock = (ViewGroup) root.findViewById(R.id.browse_grid_dock);
        mGridViewHolder = mGridPresenter.onCreateViewHolder(gridDock);
        gridDock.addView(mGridViewHolder.view);
        mGridViewHolder.getGridView().setOnChildLaidOutListener(mChildLaidOutListener);
        updateAdapter();
        return root;
    }

    private void setupFocusSearchListener() {
        if (getView() == null) return;
        BrowseFrameLayout browseFrameLayout = (BrowseFrameLayout) getView().findViewById(
                R.id.grid_frame);
        browseFrameLayout.setOnFocusSearchListener(getTitleHelper().getOnFocusSearchListener());
    }

    @Override
    public void onStart() {
        super.onStart();
        setupFocusSearchListener();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mGridViewHolder = null;
    }

    /**
     * Sets the selected item position.
     */
    public void setSelectedPosition(int position) {
        mSelectedPosition = position;
        if(mGridViewHolder != null && mGridViewHolder.getGridView().getAdapter() != null) {
            mGridViewHolder.getGridView().setSelectedPositionSmooth(position);
        }
    }

    private void updateAdapter() {
        if (mGridViewHolder != null) {
            mGridPresenter.onBindViewHolder(mGridViewHolder, mAdapter);
            if (mSelectedPosition != -1) {
                mGridViewHolder.getGridView().setSelectedPosition(mSelectedPosition);
            }
        }
    }

    void setEntranceTransitionState(boolean afterTransition) {
        mGridPresenter.setEntranceTransitionState(mGridViewHolder, afterTransition);
    }
}
