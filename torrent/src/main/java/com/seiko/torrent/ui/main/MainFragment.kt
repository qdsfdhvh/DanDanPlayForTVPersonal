package com.seiko.torrent.ui.main

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.webkit.URLUtil
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import com.blankj.utilcode.util.ToastUtils
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.seiko.torrent.R
import com.seiko.torrent.constants.HTTPS_PREFIX
import com.seiko.torrent.constants.HTTP_PREFIX
import com.seiko.torrent.constants.INFOHASH_PREFIX
import com.seiko.torrent.extensions.getClipboard
import com.seiko.torrent.extensions.isHash
import com.seiko.torrent.extensions.isMagnet
import com.seiko.torrent.ui.base.BaseFragment
import com.seiko.torrent.ui.dialog.BaseAlertDialog
import kotlinx.android.synthetic.main.torrent_fragment_main.*
import news.androidtv.filepicker.TvFilePicker
import java.io.File
import java.util.*

class MainFragment : BaseFragment(),
    BaseAlertDialog.OnClickListener,
    BaseAlertDialog.OnDialogShowListener {

    override fun getLayoutId(): Int {
        return R.layout.torrent_fragment_main
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        bindViewModel()
    }

    private fun setupUI() {
        add_torrent_button.setClosedOnTouchOutside(true)

        open_file_button.setOnClickListener {
            add_torrent_button.close(true)
            startFilePickerActivity()
        }

        add_link_button.setOnClickListener {
            add_torrent_button.close(true)
            addLinkDialog()
        }
    }

    private fun bindViewModel() {

    }

    override fun onShow(dialog: AlertDialog?) {
        if (dialog != null) {
            val fm = fragmentManager ?: return

            if (fm.findFragmentByTag(TAG_ADD_LINK_DIALOG) != null) {
                initAddDialog(dialog)
            }
        }
    }

    override fun onPositiveClicked(v: View?) {

    }

    override fun onNegativeClicked(v: View?) {

    }

    override fun onNeutralClicked(v: View?) {
        /* Nothing */
    }


    private fun initAddDialog(dialog: AlertDialog) {
        val field = dialog.findViewById<TextInputEditText>(R.id.text_input_dialog)!!
        val fieldLayout = dialog.findViewById<TextInputLayout>(R.id.layout_text_input_dialog)!!

        /* Dismiss error label if user has changed the text */
        field.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                /* Nothing */
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                fieldLayout.isErrorEnabled = false
                fieldLayout.error = null
            }

            override fun afterTextChanged(s: Editable) {
                /* Nothing */
            }
        })

        /*
         * It is necessary in order to the dialog is not closed by
         * pressing positive button if the text checker gave a false result
         */
        val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)

        positiveButton.setOnClickListener {
            if (field.text != null) {
                val link = field.text!!.toString()

                if (checkEditTextField(link, fieldLayout)) {
                    val uri = buildTorrentUri(link)
                    if (uri == null) {
                        ToastUtils.showShort("无效的连接：$link")
                        return@setOnClickListener
                    }
                    dialog.dismiss()
                    addTorrentDialog(uri)
                }
            }
        }

        /* Inserting a link from the clipboard */
        val clipboard = requireActivity().getClipboard() ?: return
        val text = clipboard.toLowerCase(Locale.US)
        if (text.isMagnet() || text.isHash()
            || text.startsWith(HTTP_PREFIX)
            || text.startsWith(HTTPS_PREFIX)) {
            field.setText(clipboard)
            return
        }
    }

    private fun addLinkDialog() {
        val fm = fragmentManager
        if (fm != null && fm.findFragmentByTag(TAG_ADD_LINK_DIALOG) == null) {
            val addLinkDialog = BaseAlertDialog.newInstance(
                getString(R.string.torrent_dialog_add_link_title), null,
                R.layout.torrent_dialog_text_input,
                getString(R.string.ok),
                getString(R.string.cancel), null,
                this
            )
            addLinkDialog.show(fm,
                TAG_ADD_LINK_DIALOG
            )
        }
    }

    private fun checkEditTextField(s: String?, layout: TextInputLayout?): Boolean {
        if (s == null || layout == null)
            return false

        if (s.isNullOrEmpty()) {
            layout.isErrorEnabled = true
            layout.error = getString(R.string.torrent_error_empty_link)
            layout.requestFocus()
            return false
        }

        layout.isErrorEnabled = false
        layout.error = null

        return true
    }

    private fun addTorrentDialog(uri: Uri) {
        findNavController().navigate(MainFragmentDirections.actionMainFragmentToAddTorrentFragment(uri))
    }

    private fun startFilePickerActivity() {
        TvFilePicker.with(this, FilePickerRequestCode)
            .setFilterName("torrent")
            .start()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        when(requestCode) {
            FilePickerRequestCode -> {
                if (resultCode == Activity.RESULT_OK) {
                    val uri = intent?.data ?: return
                    addTorrentDialog(uri)
                }
            }
        }
    }

    private fun buildTorrentUri(source: String?): Uri? {
        if (source.isNullOrEmpty()) return null

        if (source.isMagnet()) return Uri.parse(source)

        if (source.isHash()) return Uri.parse(INFOHASH_PREFIX + source)

        if (URLUtil.isNetworkUrl(source)) return Uri.parse(source)

        if (URLUtil.isFileUrl(source)) return Uri.fromFile(File(source))

        if (URLUtil.isContentUrl(source)) return Uri.parse(source)

        return null
    }

    companion object {
        private const val TAG_ADD_LINK_DIALOG = "add_link_dialog"

        private const val FilePickerRequestCode = 6906
    }
}