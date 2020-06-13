package com.seiko.torrent.ui.main

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.seiko.common.router.Navigator
import com.seiko.common.util.toast.toast
import com.seiko.torrent.R
import com.seiko.torrent.databinding.TorrentFragmentMainBinding
import com.seiko.torrent.ui.detail.TorrentDetailFragment
import com.seiko.torrent.ui.dialog.DialogInputFragment
import com.seiko.torrent.util.buildTorrentUri
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import news.androidtv.filepicker.TvFilePicker
import timber.log.Timber

class TorrentMainFragment : Fragment(), View.OnClickListener {

    companion object {
        private const val FilePickerRequestCode = 6906
    }

    private lateinit var binding: TorrentFragmentMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.tag("Navigator").d("TorrentMainFragment - onCreate")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Timber.tag("Navigator").d("TorrentMainFragment - onActivityCreated")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Timber.tag("Navigator").d("TorrentMainFragment - onCreateView")
        binding = TorrentFragmentMainBinding.inflate(inflater, container, false)
        setupUI()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.tag("Navigator").d("TorrentMainFragment - onViewCreated")
        bindViewModel()
    }

    private fun setupUI() {
        binding.torrentBtnAdd.setOnClickListener(this)
        binding.torrentBtnOpenFile.setOnClickListener(this)
        binding.torrentBtnAdd.requestFocus()
    }

    private fun bindViewModel() {

    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.torrent_btn_add -> {
                openAddTorrentDialog()
            }
            R.id.torrent_btn_open_file -> {
                startFilePickerActivity()
            }
        }
    }

    private fun openAddTorrentDialog() {
        if (childFragmentManager.findFragmentByTag(DialogInputFragment.TAG) == null) {
            DialogInputFragment.Builder()
                .setHint(getString(R.string.torrent_dialog_add_link_title))
                .setConfirmText(getString(R.string.ok))
                .setCancelText(getString(R.string.cancel))
                .setConfirmClickListener { source ->
                    val uri = buildTorrentUri(source)
                    if (uri == null) {
                        toast("无效的连接：$source")
                    } else {
                        startAddTorrentDialog(uri)
                    }
                }
                .build()
                .show(childFragmentManager)
        }
    }

    private fun startAddTorrentDialog(uri: Uri) {
        Navigator.navToAddTorrent(requireActivity(), uri)
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
                    startAddTorrentDialog(uri)
                }
            }
        }
    }
}