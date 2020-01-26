package com.seiko.torrent.ui.add

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.activity.addCallback
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.leanback.widget.OnChildViewHolderSelectedListener
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.seiko.common.ui.dialog.setLoadFragment
import com.seiko.common.extensions.checkPermissions
import com.seiko.common.extensions.lazyAndroid
import com.seiko.common.toast.toast
import com.seiko.common.ui.adapter.OnItemClickListener
import com.seiko.core.data.Result
import com.seiko.torrent.R
import com.seiko.torrent.databinding.TorrentFragmentAddBinding
import com.seiko.torrent.service.TorrentTaskService
import com.seiko.torrent.ui.adapter.TabTitleAdapter
import com.seiko.torrent.vm.AddTorrentViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

private const val PERMISSION_REQUEST = 1

private val PERMISSIONS = arrayOf(
    Manifest.permission.WRITE_EXTERNAL_STORAGE
)


/**
 * 详情、文件
 */
private const val NUM_FRAGMENTS = 2

private const val INFO_FRAG_POS = 0
private const val FILES_FRAG_POS = 1

class AddTorrentFragment : Fragment()
    , OnItemClickListener
    , View.OnClickListener {

    companion object {
        const val TAG = "AddTorrentFragment"
        private const val ARGS_TORRENT_URI = "ARGS_TORRENT_URI"
        private const val ARGS_ADD_TAB_SELECTED_POSITION = "ARGS_ADD_TAB_SELECTED_POSITION"

        fun newInstance(uri: Uri): AddTorrentFragment {
            val bundle = Bundle()
            bundle.putParcelable(ARGS_TORRENT_URI, uri)

            val fragment = AddTorrentFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

//    private val args by navArgs<AddTorrentFragmentArgs>()

    private val uri by lazyAndroid {
        arguments!!.getParcelable<Uri>(ARGS_TORRENT_URI)!!
    }

    private val viewModel by viewModel<AddTorrentViewModel>()

    private lateinit var binding: TorrentFragmentAddBinding


    private lateinit var tabAdapter: TabTitleAdapter
    private var tabSelectPosition = -1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = TorrentFragmentAddBinding.inflate(inflater, container, false)
        setupUI()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkSelectPosition(savedInstanceState)
        bindViewModel()
        checkPermissions()
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
        outState.putInt(ARGS_ADD_TAB_SELECTED_POSITION, tabSelectPosition)
    }

    private fun checkSelectPosition(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(ARGS_ADD_TAB_SELECTED_POSITION)) {
                tabSelectPosition = savedInstanceState.getInt(ARGS_ADD_TAB_SELECTED_POSITION)
            }
        }
        if (tabSelectPosition >= 0) {
            tabAdapter.setSelectPosition(tabSelectPosition)
        }
    }

    private fun setupUI() {
        tabAdapter = TabTitleAdapter(NUM_FRAGMENTS) { tab, position ->
            tab.setText(when(position) {
                INFO_FRAG_POS -> getString(R.string.torrent_info)
                FILES_FRAG_POS -> getString(R.string.torrent_files)
                else -> ""
            })
        }
        tabAdapter.setOnItemClickListener(this)
        binding.torrentTab.setPadding(25, 0, 25, 0)
        binding.torrentTab.setItemSpacing(25)
        binding.torrentTab.addOnChildViewHolderSelectedListener(mItemSelectedListener)
        binding.torrentTab.adapter = tabAdapter

        // ViewPager2
        binding.torrentViewPager.adapter = AddTorrentPagerAdapter(this)

        binding.torrentBtnCenter.setOnClickListener(this)
        binding.torrentBtnCenter.requestFocus()

//        requireActivity().onBackPressedDispatcher.addCallback(this) { onBackPressed() }
    }

    private fun bindViewModel() {
        viewModel.state.observe(this::getLifecycle) { result ->
            when(result) {
                is Result.Success -> updateStateUI(result.data)
                is Result.Error -> handleException(result.exception)

            }
        }
        viewModel.loadData()
    }

    /**
     * 更新状态
     */
    private fun updateStateUI(state: Int) {
        when(state) {
            State.DECODE_TORRENT_FILE -> {
                setLoadFragment(true, getString(R.string.torrent_decode_torrent_default_message))
            }
            State.FETCHING_HTTP -> {
                setLoadFragment(true, getString(R.string.torrent_decode_torrent_downloading_torrent_message))
            }
            State.DECODE_TORRENT_COMPLETED,
            State.FETCHING_HTTP_COMPLETED -> {
                setLoadFragment(false)
            }
            State.FETCHING_MAGNET -> {
                binding.fetchMagnetProgress.visibility = View.VISIBLE
                toast(getString(R.string.torrent_decode_torrent_fetch_magnet_message))
            }
            State.FETCHING_MAGNET_COMPLETED -> {
                binding.fetchMagnetProgress.visibility = View.GONE
            }
        }
    }

    /**
     * 处理异常
     */
    private fun handleException(throwable: Throwable?) {
        setLoadFragment(false)
        //
        Timber.w(throwable)
        toast(throwable?.message)
    }

    override fun onClick(holder: RecyclerView.ViewHolder, item: Any, position: Int) {
        when(holder.itemView.id) {
            R.id.container -> {
                toast("position = $position")
            }
        }
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.torrent_btn_center -> {
                when (val result = viewModel.buildTorrentTask()) {
                    is Result.Success -> {
                        TorrentTaskService.addTorrent(requireActivity(), result.data)
                        onBackPressed()
                    }
                    is Result.Error -> {
                        toast(result.exception.message)
                    }
                }
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

    /************************************************
     *                   权限请求                    *
     ************************************************/

    private fun checkPermissions() {
        if (viewModel.state.value != null) return

        if (checkPermissions(PERMISSIONS)) {
            viewModel.decodeUri(uri)
        } else {
            requestPermissions(
                PERMISSIONS,
                PERMISSION_REQUEST
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_REQUEST) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                viewModel.decodeUri(uri)
            }
        }
    }

    /**
     * 返回
     */
    private fun onBackPressed() {
        Timber.d("onBackPressed")
        requireActivity().finish()
//        ActivityCompat.finishAffinity(requireActivity())
    }

}

/**
 * Fragment Pager
 */
private class AddTorrentPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = NUM_FRAGMENTS

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            INFO_FRAG_POS -> AddTorrentInfoFragment.newInstance()
            FILES_FRAG_POS -> AddTorrentFilesFragment.newInstance()
            else -> throw RuntimeException("Can't create fragment with position=$position.")
        }
    }
}

/**
 * 种子文件、磁力、Url解析状态
 */
@Retention(AnnotationRetention.SOURCE)
internal annotation class State {
    companion object {
        const val DECODE_TORRENT_FILE = 0
        const val DECODE_TORRENT_COMPLETED = 1
        const val FETCHING_MAGNET = 2
        const val FETCHING_HTTP = 3
        const val FETCHING_MAGNET_COMPLETED = 4
        const val FETCHING_HTTP_COMPLETED = 5
    }
}