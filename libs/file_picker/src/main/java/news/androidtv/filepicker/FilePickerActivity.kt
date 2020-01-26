package news.androidtv.filepicker

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import news.androidtv.filepicker.FilePickerFragment.Companion.newInstance
import news.androidtv.filepicker.FilePickerFragment.SelectionCallback
import news.androidtv.filepicker.filters.EmptyFilter
import news.androidtv.filepicker.filters.FileExtensionFilter
import news.androidtv.filepicker.filters.FilterInterface
import news.androidtv.filepicker.model.AbstractFile
import java.io.File

class FilePickerActivity : FragmentActivity() {

    companion object {
        private const val TAG = "FilePickerActivity"
        const val ARGS_ROOT_PATH = "ARGS_ROOT_PATH"
        const val ARG_FILTER_NAME = "ARG_FILTER_NAME"
    }

    private var mRootPath: String? = null
    private var mFilterName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.filter_picker_acitvity)
        initArguments()
        startFilePickerFragment()
    }

    private fun initArguments() {
        if (intent.hasExtra(ARGS_ROOT_PATH)) {
            mRootPath = intent.getStringExtra(ARGS_ROOT_PATH)
        }
        if (intent.hasExtra(ARG_FILTER_NAME)) {
            mFilterName = intent.getStringExtra(ARG_FILTER_NAME)
        }
    }

    private fun startFilePickerFragment() {
        var root: File
        if (!mRootPath.isNullOrEmpty()) {
            root = File(mRootPath!!)
            if (!root.exists()) {
                root = Environment.getExternalStorageDirectory()
            }
        } else {
            root = Environment.getExternalStorageDirectory()
        }

        val filter: FilterInterface = if (!mFilterName.isNullOrEmpty()) {
            FileExtensionFilter(mFilterName)
        } else {
            EmptyFilter()
        }

        val filePickerFragment = newInstance(this,
            AbstractFile.fromLocalPath(root), filter,
            object : SelectionCallback {
                override fun onFilePicked(
                    filePickerFragment: FilePickerFragment?,
                    abstractFile: AbstractFile?
                ): Boolean {
                    setResultAndFinish(abstractFile!!.uri)
                    return false
                }

                override fun onDirectoryPicked(
                    filePickerFragment: FilePickerFragment?,
                    abstractDirectory: AbstractFile?
                ): Boolean {
                    return filePickerFragment!!.explore(
                        AbstractFile.fromLocalAbstractFile(abstractDirectory)
                    )
                }
            }
        )

        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.container, filePickerFragment, TAG)
        transaction.commit()
    }

    private fun setResultAndFinish(uri: Uri) {
        val data = Intent()
        data.data = uri
        setResult(Activity.RESULT_OK, data)
        finish()
    }

}