package com.seiko.torrent.ui.detail

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import com.seiko.common.router.Navigator
import com.seiko.common.util.extensions.lazyAndroid
import com.seiko.common.util.toast.toast
import com.seiko.torrent.data.model.filetree.BencodeFileTree
import com.seiko.torrent.databinding.TorrentFragmentDetailFileBinding
import com.seiko.torrent.util.FileUtils
import com.seiko.torrent.util.extensions.fixItemAnim
import com.seiko.torrent.util.extensions.toFileTree
import com.seiko.torrent.vm.TorrentViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class TorrentDetailFilesFragment : Fragment(), TorrentDetailFilesAdapter.OnItemClickListener {

    companion object {
        fun newInstance(): TorrentDetailFilesFragment {
            return TorrentDetailFilesFragment()
        }
    }

    private val viewModel: TorrentViewModel by activityViewModels()

    private lateinit var binding: TorrentFragmentDetailFileBinding

    private val adapter by lazyAndroid { TorrentDetailFilesAdapter() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
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
        unBindViewModel()
    }

    private fun setupUI() {
        adapter.setOnItemClickListener(this)
        binding.fileList.fixItemAnim()
        binding.fileList.adapter = adapter
    }

    private fun bindViewModel() {
        viewModel.torrentMetaInfo.observe(viewLifecycleOwner) { info ->
            if (info == null) {
                return@observe
            }
            adapter.setFileTree(info.fileList.toFileTree())
        }
    }

    private fun unBindViewModel() {
        viewModel.torrentMetaInfo.removeObservers(this)
    }

    override fun onItemClicked(node: BencodeFileTree) {
        val item = viewModel.torrentItem.value ?: return
        val file = File(item.downloadPath, node.path)
        val filePath = file.absolutePath
        if (!file.exists()) {
            toast("文件不存在：${filePath}")
            return
        }
        if (!FileUtils.isMediaFile(filePath)) {
            toast("非视频文件：${filePath}")
            return
        }
        Navigator.navToPlayer(this, Uri.fromFile(file), node.name)
    }

}