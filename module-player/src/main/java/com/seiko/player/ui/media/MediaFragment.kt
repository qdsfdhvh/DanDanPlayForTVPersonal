package com.seiko.player.ui.media

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.leanback.app.BackgroundManager
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.recyclerview.widget.RecyclerView
import com.seiko.common.ui.adapter.OnItemClickListener
import com.seiko.common.util.extensions.getScreenWidth
import com.seiko.common.util.extensions.lazyAndroid
import com.seiko.player.R
import com.seiko.player.data.model.PlayParam
import com.seiko.player.databinding.PlayerFragmentBrowserBinding
import com.seiko.player.ui.adapter.MediaTvListAdapter
import com.seiko.player.ui.video.VlcVideoPlayerActivity
import com.seiko.player.util.RecyclerViewUtils
import com.seiko.player.util.bitmap.ImageLoader
import com.seiko.player.vm.VideosViewModel
import kotlinx.coroutines.yield
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import org.videolan.medialibrary.interfaces.media.MediaWrapper
import org.videolan.medialibrary.media.MediaLibraryItem

class MediaFragment : Fragment(), MediaTvListAdapter.OnItemFocusListener, OnItemClickListener {

    private val viewModel: VideosViewModel by sharedViewModel()

    private lateinit var binding: PlayerFragmentBrowserBinding
    private lateinit var backgroundManager: BackgroundManager

    private val imageLoader: ImageLoader by inject()
    private val adapter by lazyAndroid { MediaTvListAdapter(requireActivity(), imageLoader, lifecycleScope) }

    private var setFocus = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        backgroundManager = BackgroundManager.getInstance(requireActivity())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = PlayerFragmentBrowserBinding.inflate(inflater, container, false)
        setupUI()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupGridNum()
        super.onViewCreated(view, savedInstanceState)
        bindViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        unBindViewModel()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.list.adapter = adapter
        if (!backgroundManager.isAttached) {
            backgroundManager.attachToView(view)
        }
    }

    override fun onStart() {
        // 重置or恢复 背景
        val item = adapter.getSelectItem()
        if (item == null) {
            imageLoader.clearBackground(backgroundManager)
        } else {
            lifecycleScope.launchWhenStarted {
                yield()
                imageLoader.updateBackground(requireActivity(), backgroundManager, item)
            }
        }
        super.onStart()
        if (setFocus) {
            setFocus = false
            lifecycleScope.launchWhenStarted {
                yield()
                binding.list.requestFocus()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        backgroundManager.release()
    }

    private fun setupUI() {
        binding.title.text = "媒体库"
        adapter.setOnItemFocusListener(this)
        adapter.setOnItemClickListener(this)
    }

    private fun setupGridNum() {
        val spacing = resources.getDimensionPixelSize(R.dimen.kl_small)
        val screenWidth = requireActivity().getScreenWidth()
        val itemWidth = requireContext().resources.getDimension(R.dimen.tv_grid_card_thumb_width).toInt()
        val count = RecyclerViewUtils.getColumns(screenWidth - binding.list.paddingStart - binding.list.paddingEnd, itemWidth, spacing)
        binding.list.setItemSpacing(spacing)
        binding.list.setNumColumns(count)
    }

    private fun bindViewModel() {
        viewModel.provider.pagedList.observe(this) { list ->
            adapter.submitList(list)
        }
    }

    private fun unBindViewModel() {
        viewModel.provider.pagedList.removeObservers(this)
    }

    override fun onClick(holder: RecyclerView.ViewHolder, item: Any, position: Int) {
        when(item) {
            is MediaWrapper  -> {
                VlcVideoPlayerActivity.launch(requireActivity(), PlayParam(
                    item.uri.path!!,
                    item.title
                ))
            }
        }
    }

    override fun onItemFocused(view: View, item: MediaLibraryItem) {
        lifecycleScope.launchWhenStarted {
            imageLoader.updateBackground(requireActivity(), backgroundManager, item)
        }
    }

}