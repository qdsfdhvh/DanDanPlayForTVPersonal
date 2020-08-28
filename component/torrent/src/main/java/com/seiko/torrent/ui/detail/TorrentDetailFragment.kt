package com.seiko.torrent.ui.detail

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.fragment.app.activityViewModels
import androidx.leanback.widget.OnChildViewHolderSelectedListener
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.seiko.common.ui.adapter.OnItemClickListener
import com.seiko.common.util.extensions.lazyAndroid
import com.seiko.common.util.toast.toast
import com.seiko.torrent.R
import com.seiko.torrent.databinding.TorrentFragmentDetailBinding
import com.seiko.torrent.service.TorrentTaskService
import com.seiko.torrent.ui.adapter.TabTitleAdapter
import com.seiko.torrent.vm.TorrentViewModel
import dagger.hilt.android.AndroidEntryPoint

private const val NUM_FRAGMENTS = 2

private const val FILES_FRAG_POS = 0
private const val INFO_FRAG_POS = 1

@AndroidEntryPoint
class TorrentDetailFragment : Fragment(R.layout.torrent_fragment_detail)
    , OnItemClickListener {

    companion object {
        const val TAG = "TorrentDetailFragment"
        private const val ARGS_DETAIL_TAB_SELECTED_POSITION = "ARGS_DETAIL_TAB_SELECTED_POSITION"

        fun newInstance(): TorrentDetailFragment {
            return TorrentDetailFragment()
        }
    }

    private val viewModel: TorrentViewModel by activityViewModels()

    private val binding: TorrentFragmentDetailBinding by viewBinding()

    private lateinit var tabAdapter: TabTitleAdapter

    /**
     * 记录位置
     */
    private var tabSelectPosition = -1


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkSelectPosition(savedInstanceState)
        setupUI()
        bindViewModel()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.torrentTab.adapter = tabAdapter
    }

    override fun onDestroyView() {
        binding.torrentTab.removeOnChildViewHolderSelectedListener(mItemSelectedListener)
        super.onDestroyView()
    }

    /**
     * 保存视图状态
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(ARGS_DETAIL_TAB_SELECTED_POSITION, tabSelectPosition)
    }

    private fun checkSelectPosition(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(ARGS_DETAIL_TAB_SELECTED_POSITION)) {
                tabSelectPosition = savedInstanceState.getInt(ARGS_DETAIL_TAB_SELECTED_POSITION)
            }
        }
        if (tabSelectPosition >= 0) {
            tabAdapter.setSelectPosition(tabSelectPosition)
        }
    }

    private fun setupUI() {
        tabAdapter = TabTitleAdapter(NUM_FRAGMENTS) { tab, position ->
            tab.setText(
                when (position) {
                    FILES_FRAG_POS -> getString(R.string.torrent_files)
                    INFO_FRAG_POS -> getString(R.string.torrent_info)
                    else -> ""
                }
            )
        }
        tabAdapter.setOnItemClickListener(this)
        binding.torrentTab.setPadding(25, 0, 25, 0)
        binding.torrentTab.setItemSpacing(25)
        binding.torrentTab.addOnChildViewHolderSelectedListener(mItemSelectedListener)

        // ViewPager2
        binding.torrentViewPager.adapter = DetailPagerAdapter(this)
    }

    private fun bindViewModel() {

    }

    override fun onClick(holder: RecyclerView.ViewHolder, item: Any, position: Int) {
        when(holder) {
            is TabTitleAdapter.ViewHolder -> {
                toast("position = $position")
            }
        }
    }

    /**
     * Item选择监听回调
     */
    private val mItemSelectedListener : OnChildViewHolderSelectedListener by lazyAndroid {
        object : OnChildViewHolderSelectedListener() {
            override fun onChildViewHolderSelected(
                parent: RecyclerView?,
                child: RecyclerView.ViewHolder?,
                position: Int,
                subposition: Int
            ) {
                when(parent) {
                    binding.torrentTab -> {
                        if (tabSelectPosition == position) return
                        tabSelectPosition = position
                        tabAdapter.setSelectPosition(position)
                        binding.torrentViewPager.currentItem = position
                    }
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.delete_torrent_menu -> {
                val hash = viewModel.torrentItem.value?.hash
                if (hash != null) {
                    TorrentTaskService.delTorrent(requireActivity(), hash, true)
                }
            }
        }
        return true
    }

}

private class DetailPagerAdapter(
    fragment: Fragment
) : FragmentStatePagerAdapter(
    fragment.childFragmentManager,
    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
) {

    override fun getCount(): Int = NUM_FRAGMENTS

    override fun getItem(position: Int): Fragment {
        return when(position) {
            FILES_FRAG_POS -> TorrentDetailFilesFragment.newInstance()
            INFO_FRAG_POS -> TorrentDetailInfoFragment.newInstance()
            else -> throw RuntimeException("Can't create fragment with position=$position.")
        }
    }
}