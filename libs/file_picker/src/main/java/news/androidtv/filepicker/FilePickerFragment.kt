package news.androidtv.filepicker

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.GuidanceStylist.Guidance
import androidx.leanback.widget.GuidedAction
import news.androidtv.filepicker.FilePickerFragment
import news.androidtv.filepicker.FilePickerFragment.SelectionCallback
import news.androidtv.filepicker.filters.FilterInterface
import news.androidtv.filepicker.model.AbstractFile

/**
 *
 * This class extends the [GuidedStepSupportFragment] to provide a simple file browser that is
 * native to Android TV and a DPAD interface. In your activity, you should initialize the picker
 * by calling [.newInstance].
 *
 *
 *
 * This method is robust, allowing for a wide range of customization depending on the
 * implementation. It can display an array of [AbstractFile] that can have any origin. To
 * match with a certain type of file, or just selecting a folder, a class implementing
 * [FilterInterface] can be provided. Then, the developer can customize the response by
 * providing a [SelectionCallback].
 *
 */
class FilePickerFragment private constructor(): GuidedStepSupportFragment() {

    companion object {
        /**
         * Creates a new instance of the [FilePickerFragment] with a set of parameters.
         *
         * @param attachedActivity The activity that owns this fragment.
         * @param files A list of [AbstractFile] that will be shown.
         * @param filterInterface A filter to indicate which types of files should be shown.
         * @param callback A callback which will be run when a user selects a particular item.
         * @return A root FilePickerFragment.
         */
        @JvmStatic
        fun newInstance(
            attachedActivity: FragmentActivity,
            files: AbstractFile,
            filterInterface: FilterInterface,
            callback: SelectionCallback
        ): FilePickerFragment {
            val filePickerFragment = FilePickerFragment()
            filePickerFragment.mActivity = attachedActivity
            filePickerFragment.mFiles = files
            filePickerFragment.mFilterInterface = filterInterface
            filePickerFragment.mSelectionCallback = callback
            return filePickerFragment
        }
    }

    private lateinit var mFiles: AbstractFile
    private lateinit var mActivity: FragmentActivity
    private lateinit var mFilterInterface: FilterInterface
    private lateinit var mSelectionCallback: SelectionCallback

    /**
     * Creates a list of Folders.
     *
     * @param actions The list of [GuidedAction] that will be created.
     * @param savedInstanceState We don't use this.
     */
    override fun onCreateActions(
        actions: MutableList<GuidedAction>,
        savedInstanceState: Bundle?
    ) {
        if (mFiles.parent != null) {
            actions.add(
                GuidedAction.Builder(mActivity)
                    .icon(mActivity.getDrawable(R.drawable.ic_folder_white_24dp))
                    .id(-1)
                    .title("../") // Go up one step
                    .build()
            )
        }
        if (mFiles.files != null) {
            for (i in mFiles.files.indices) {
                val file = mFiles.files[i]
                if (file.isDirectory && mFilterInterface.matches(file)) {
                    actions.add(
                        GuidedAction.Builder(mActivity)
                            .icon(mActivity.getDrawable(R.drawable.ic_folder_white_24dp))
                            .id(i.toLong())
                            .title(file.title)
                            .build()
                    )
                }
            }
        }
    }

    override fun onGuidedActionClicked(action: GuidedAction) {
        if (action.id == -1L) { // Selected the up directory
            explore(AbstractFile.fromLocalAbstractFile(mFiles.parent))
            return
        }
        val selectedFile = mFiles.files[action.id.toInt()]
        if (selectedFile.isDirectory) {
            if (mSelectionCallback.onDirectoryPicked(this@FilePickerFragment, selectedFile)) {
                closeFilePickerFragment()
            }
        } else {
            if (mSelectionCallback.onFilePicked(this@FilePickerFragment, selectedFile)) {
                closeFilePickerFragment()
            }
        }
    }

    /**
     * Create a list of Files.
     *
     * @param actions The list of [GuidedAction] that will be created.
     * @param savedInstanceState We don't use this.
     */
    override fun onCreateButtonActions(
        actions: MutableList<GuidedAction>,
        savedInstanceState: Bundle?
    ) {
        if (mFiles.files != null) {
            for (i in mFiles.files.indices) {
                val file = mFiles.files[i]
                if (!file.isDirectory && mFilterInterface.matches(file)) {
                    actions.add(
                        GuidedAction.Builder(mActivity)
                            .id(i.toLong())
                            .title(file.title)
                            .build()
                    )
                }
            }
        }
    }

    override fun onProvideTheme(): Int {
        return R.style.Theme_Leanback_GuidedStep
    }

    override fun setUiStyle(style: Int) {
        super.setUiStyle(UI_STYLE_ACTIVITY_ROOT)
    }

    override fun onCreateGuidance(savedInstanceState: Bundle?): Guidance {
        return Guidance(
            "File Browser",
            mFiles.uri.path,
            mFilterInterface.description,
            mActivity.application.applicationInfo
                .loadIcon(mActivity.packageManager)
        )
    }

    /**
     * Closes the file picker.
     */
    fun closeFilePickerFragment() {
        finishGuidedStepSupportFragments()
    }

    /**
     * Adds another layer to the FilePicker layer instead of creating an entirely new instance. This
     * keeps the same filter and callback.
     *
     * @param files The new list of files to browse.
     * @return Returns false to be used in a simple, single line in
     * [SelectionCallback.onDirectoryPicked].
     */
    fun explore(files: AbstractFile): Boolean {
        add(mActivity.supportFragmentManager,
            newInstance(mActivity, files, mFilterInterface, mSelectionCallback)
        )
        return false
    }

    interface SelectionCallback {
        /**
         * This method is called when the user has selected a file in the file picker.
         *
         * @param filePickerFragment The current fragment, to access helper methods such as
         * [.explore]
         * @param abstractFile The file that was selected.
         * @return true if the file picker should be closed. False if it should remain open.
         */
        fun onFilePicked(
            filePickerFragment: FilePickerFragment?,
            abstractFile: AbstractFile?
        ): Boolean

        /**
         * This method is called when the user has selected a directory in the file picker.
         *
         * @param filePickerFragment The current fragment, to access helper methods such as
         * [.explore]
         * @param abstractDirectory The directory that was selected.
         * @return true if the file picker should be closed. False if it should remain open.
         */
        fun onDirectoryPicked(
            filePickerFragment: FilePickerFragment?,
            abstractDirectory: AbstractFile?
        ): Boolean
    }

}