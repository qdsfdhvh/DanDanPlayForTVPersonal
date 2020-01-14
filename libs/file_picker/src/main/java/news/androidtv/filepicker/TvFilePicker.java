package news.androidtv.filepicker;

import android.app.Activity;
import android.content.Intent;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

public class TvFilePicker {

    private FragmentActivity mActivity;
    private Fragment mFragment;
    private final int mRequestCode;

    private String mRootPath;
    private String mFilterName;

    private TvFilePicker(FragmentActivity activity, int requestCode) {
        this.mActivity = activity;
        this.mRequestCode = requestCode;
    }

    private TvFilePicker(Fragment fragment, int requestCode) {
        this.mFragment = fragment;
        this.mRequestCode = requestCode;
    }

    public static TvFilePicker with(FragmentActivity activity, int requestCode) {
        return new TvFilePicker(activity, requestCode);
    }

    public static TvFilePicker with(Fragment fragment, int requestCode) {
        return new TvFilePicker(fragment, requestCode);
    }

    public TvFilePicker setRootPath(String rootPath) {
        mRootPath = rootPath;
        return this;
    }

    public TvFilePicker setFilterName(String filterName) {
        mFilterName = filterName;
        return this;
    }

    private Intent getIntent() {
        Activity activity;
        if (mActivity != null) {
            activity = mActivity;
        } else if (mFragment != null) {
            activity = mFragment.getActivity();
        } else {
            throw new RuntimeException("You must pass Activity/Fragment by calling withActivity/withFragment method");
        }

        Intent intent = new Intent(activity, FilePickerActivity.class);

        if (mRootPath != null) {
            intent.putExtra(FilePickerActivity.ARGS_ROOT_PATH, mRootPath);
        }
        if (mFilterName != null) {
            intent.putExtra(FilePickerActivity.ARG_FILTER_NAME, mFilterName);
        }
        return intent;
    }

    public void start() {
        if (mActivity != null) {
            mActivity.startActivityForResult(getIntent(), mRequestCode);
        } else if (mFragment != null) {
            mFragment.startActivityForResult(getIntent(), mRequestCode);
        }
    }

}
