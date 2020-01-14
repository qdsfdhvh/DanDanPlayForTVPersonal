package news.androidtv.filepicker;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.leanback.app.GuidedStepSupportFragment;
import androidx.leanback.widget.GuidanceStylist;
import androidx.leanback.widget.GuidedAction;

import java.util.List;

import news.androidtv.filepicker.filters.FilterInterface;
import news.androidtv.filepicker.model.AbstractFile;

/**
 * <p>This class extends the {@link GuidedStepSupportFragment} to provide a simple file browser that is
 * native to Android TV and a DPAD interface. In your activity, you should initialize the picker
 * by calling {@link #newInstance(FragmentActivity, AbstractFile, FilterInterface, SelectionCallback)}.
 * </p>
 *
 * <p>This method is robust, allowing for a wide range of customization depending on the
 * implementation. It can display an array of {@link AbstractFile} that can have any origin. To
 * match with a certain type of file, or just selecting a folder, a class implementing
 * {@link FilterInterface} can be provided. Then, the developer can customize the response by
 * providing a {@link SelectionCallback}.
 * </p>
 */
public class FilePickerFragment extends GuidedStepSupportFragment {
    private AbstractFile mFiles;
    private FragmentActivity mActivity;
    private FilterInterface mFilterInterface;
    private SelectionCallback mSelectionCallback;

    @SuppressLint("ValidFragment")
    private FilePickerFragment() {
    }

    /**
     * Creates a new instance of the {@link FilePickerFragment} with a set of parameters.
     *
     * @param attachedActivity The activity that owns this fragment.
     * @param files A list of {@link AbstractFile} that will be shown.
     * @param filterInterface A filter to indicate which types of files should be shown.
     * @param callback A callback which will be run when a user selects a particular item.
     * @return A root FilePickerFragment.
     */
    public static FilePickerFragment newInstance(FragmentActivity attachedActivity,
                                                 AbstractFile files,
                                                 @NonNull FilterInterface filterInterface,
                                                 @NonNull SelectionCallback callback) {
        FilePickerFragment filePickerFragment = new FilePickerFragment();
        filePickerFragment.mActivity = attachedActivity;
        filePickerFragment.mFiles = files;
        filePickerFragment.mFilterInterface = filterInterface;
        filePickerFragment.mSelectionCallback = callback;
        return filePickerFragment;
    }

    /**
     * Creates a list of Folders.
     *
     * @param actions The list of {@link GuidedAction} that will be created.
     * @param savedInstanceState We don't use this.
     */
    @Override
    public void onCreateActions(@NonNull List<GuidedAction> actions, Bundle savedInstanceState) {
        if (mFiles.getParent() != null) {
            actions.add(new GuidedAction.Builder(mActivity)
                    .icon(mActivity.getDrawable(R.drawable.ic_folder_white_24dp))
                    .id(-1)
                    .title("../") // Go up one step
                    .build());
        }
        if (mFiles.getFiles() != null) {
            for (int i = 0; i < mFiles.getFiles().length; i++) {
                AbstractFile file = mFiles.getFiles()[i];
                if (file.isDirectory() && mFilterInterface.matches(file)) {
                    actions.add(new GuidedAction.Builder(mActivity)
                            .icon(mActivity.getDrawable(R.drawable.ic_folder_white_24dp))
                            .id(i)
                            .title(file.getTitle())
                            .build());
                }
            }
        }
    }

    @Override
    public void onGuidedActionClicked(GuidedAction action) {
        if (action.getId() == -1) {
            // Selected the up directory
            explore(AbstractFile.fromLocalAbstractFile(mFiles.getParent()));
            return;
        }
        AbstractFile selectedFile = mFiles.getFiles()[(int) action.getId()];
        if (selectedFile.isDirectory()) {
            if (mSelectionCallback.onDirectoryPicked(this, selectedFile)) {
                closeFilePickerFragment();
            }
        } else {
            if (mSelectionCallback.onFilePicked(this, selectedFile)) {
                closeFilePickerFragment();
            }
        }
    }

    /**
     * Create a list of Files.
     *
     * @param actions The list of {@link GuidedAction} that will be created.
     * @param savedInstanceState We don't use this.
     */
    @Override
    public void onCreateButtonActions(@NonNull List<GuidedAction> actions,
            Bundle savedInstanceState) {
        if (mFiles.getFiles() != null) {
            for (int i = 0; i < mFiles.getFiles().length; i++) {
                AbstractFile file = mFiles.getFiles()[i];
                if (!file.isDirectory() && mFilterInterface.matches(file)) {
                    actions.add(new GuidedAction.Builder(mActivity)
                            .id(i)
                            .title(file.getTitle())
                            .build());
                }
            }
        }
    }

    @Override
    public int onProvideTheme() {
        return R.style.Theme_Leanback_GuidedStep;
    }

    @Override
    public void setUiStyle(int style) {
        super.setUiStyle(UI_STYLE_ACTIVITY_ROOT);
    }

    @NonNull
    @Override
    public GuidanceStylist.Guidance onCreateGuidance(Bundle savedInstanceState) {
        return new GuidanceStylist.Guidance(
                "File Browser",
                mFiles.getUri().getPath(),
                mFilterInterface.getDescription(),
                mActivity.getApplication().getApplicationInfo()
                .loadIcon(mActivity.getPackageManager()));

    }

    /**
     * Closes the file picker.
     */
    public void closeFilePickerFragment() {
        finishGuidedStepSupportFragments();
    }

    /**
     * Adds another layer to the FilePicker layer instead of creating an entirely new instance. This
     * keeps the same filter and callback.
     *
     * @param files The new list of files to browse.
     * @return Returns false to be used in a simple, single line in
     * {@link SelectionCallback#onDirectoryPicked(FilePickerFragment, AbstractFile)}.
     */
    public boolean explore(AbstractFile files) {
        GuidedStepSupportFragment.add(mActivity.getSupportFragmentManager(), newInstance(mActivity, files,
                mFilterInterface, mSelectionCallback));
        return false;
    }

    public interface SelectionCallback {
        /**
         * This method is called when the user has selected a file in the file picker.
         *
         * @param filePickerFragment The current fragment, to access helper methods such as
         *     {@link #explore(AbstractFile)}
         * @param abstractFile The file that was selected.
         * @return true if the file picker should be closed. False if it should remain open.
         */
        boolean onFilePicked(FilePickerFragment filePickerFragment, AbstractFile abstractFile);
        /**
         * This method is called when the user has selected a directory in the file picker.
         *
         * @param filePickerFragment The current fragment, to access helper methods such as
         *     {@link #explore(AbstractFile)}
         * @param abstractDirectory The directory that was selected.
         * @return true if the file picker should be closed. False if it should remain open.
         */
        boolean onDirectoryPicked(FilePickerFragment filePickerFragment,
                AbstractFile abstractDirectory);
    }
}
