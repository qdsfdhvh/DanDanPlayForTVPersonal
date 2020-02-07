package com.seiko.torrent.ui.detail

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.seiko.common.router.Navigator
import com.seiko.common.util.extensions.lazyAndroid
import com.seiko.common.util.toast.toast
import com.seiko.torrent.data.model.filetree.BencodeFileTree
import com.seiko.torrent.data.model.filetree.FileNode
import com.seiko.torrent.databinding.TorrentFragmentDetailFileBinding
import com.seiko.torrent.util.FileUtil
import com.seiko.torrent.util.extensions.fixItemAnim
import com.seiko.torrent.util.extensions.toFileTree
import com.seiko.torrent.vm.MainViewModel
import org.koin.android.viewmodel.ext.android.sharedViewModel
import timber.log.Timber
import java.io.File

class TorrentDetailFilesFragment : Fragment(), TorrentDetailFilesAdapter.OnItemClickListener {

    companion object {
        fun newInstance(): TorrentDetailFilesFragment {
            return TorrentDetailFilesFragment()
        }
    }

    private val viewModel: MainViewModel by sharedViewModel()

    private lateinit var binding: TorrentFragmentDetailFileBinding

    private val adapter by lazyAndroid { TorrentDetailFilesAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = TorrentFragmentDetailFileBinding.inflate(inflater, container, false)
        setupUI()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViewModel()
    }

    override fun onDestroyView() {
        adapter.setOnItemClickListener(null)
        super.onDestroyView()
    }

    private fun setupUI() {
        adapter.setOnItemClickListener(this)
        binding.fileList.fixItemAnim()
        binding.fileList.adapter = adapter
    }

    private fun bindViewModel() {
        viewModel.torrentMetaInfo.observe(this::getLifecycle) { info ->
            if (info == null) {
                return@observe
            }
            adapter.setFileTree(info.fileList.toFileTree())
        }
    }

    override fun onItemClicked(node: BencodeFileTree) {
        val item = viewModel.torrentItem.value ?: return
        val file = File(item.downloadPath, node.path)
        val filePath = file.absolutePath
        if (!file.exists()) {
            toast("文件不存在：${filePath}")
            return
        }
        if (!FileUtil.isMediaFile(filePath)) {
            toast("非视频文件：${filePath}")
            return
        }
        Navigator.navToPlayer(this, Uri.fromFile(file), node.name, item.hash)
    }

}