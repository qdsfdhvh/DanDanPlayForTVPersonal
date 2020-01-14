package news.androidtv.filepicker;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import java.io.File;

import news.androidtv.filepicker.filters.EmptyFilter;
import news.androidtv.filepicker.filters.FileExtensionFilter;
import news.androidtv.filepicker.filters.FilterInterface;
import news.androidtv.filepicker.model.AbstractFile;

public class FilePickerActivity extends FragmentActivity {

    static String ARGS_ROOT_PATH = "ARGS_ROOT_PATH";
    static String ARG_FILTER_NAME = "ARG_FILTER_NAME";

    private String mRootPath;
    private String mFilterName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter_picker_acitvity);
        initArguments();
        startFilePickerFragment();
    }

    private void initArguments() {
        if (getIntent().hasExtra(ARGS_ROOT_PATH)) {
            mRootPath = getIntent().getStringExtra(ARGS_ROOT_PATH);
        }
        if (getIntent().hasExtra(ARG_FILTER_NAME)) {
            mFilterName = getIntent().getStringExtra(ARG_FILTER_NAME);
        }

    }

    private void startFilePickerFragment() {

        File root;
        if (mRootPath != null && !mRootPath.isEmpty()) {
            root = new File(mRootPath);
            if (!root.exists()) {
                root = Environment.getExternalStorageDirectory();
            }
        } else {
            root = Environment.getExternalStorageDirectory();
        }

        FilterInterface filter;
        if (mFilterName != null && !mFilterName.isEmpty()) {
            filter = new FileExtensionFilter(mFilterName);
        } else {
            filter = new EmptyFilter();
        }


        FilePickerFragment filePickerFragment = FilePickerFragment.newInstance(this,
                AbstractFile.fromLocalPath(root), filter,
                new FilePickerFragment.SelectionCallback() {
                    @Override
                    public boolean onFilePicked(FilePickerFragment filePickerFragment, AbstractFile abstractFile) {
                        setResultAndFinish(abstractFile.getUri());
                        return false;
                    }

                    @Override
                    public boolean onDirectoryPicked(FilePickerFragment filePickerFragment,
                            AbstractFile abstractDirectory) {
                        return filePickerFragment.explore(
                                AbstractFile.fromLocalAbstractFile(abstractDirectory));
                    }
        });
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.container, filePickerFragment, "root");
        transaction.commit();
    }

    private void setResultAndFinish(Uri uri) {
        Intent data = new Intent();
        data.setData(uri);
        setResult(RESULT_OK, data);
        finish();
    }

}
