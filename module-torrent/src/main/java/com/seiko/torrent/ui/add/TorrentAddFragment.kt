package com.seiko.torrent.ui.add

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.activity.addCallback
import androidx.activity.requireDispatchKeyEventDispatcher
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.recyclerview.widget.RecyclerView
import com.seiko.common.ui.dialog.setLoadFragment
import com.seiko.common.util.extensions.checkPermissions
import com.seiko.common.util.extensions.lazyAndroid
import com.seiko.common.util.toast.toast
import com.seiko.common.ui.adapter.OnItemClickListener
import com.seiko.common.data.Result
import com.seiko.torrent.R
import com.seiko.torrent.databinding.TorrentFragmentAddBinding
import com.seiko.torrent.service.TorrentTaskService
import com.seiko.torrent.vm.AddTorrentViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

private const val PERMISSION_REQUEST = 1

private val PERMISSIONS = arrayOf(
    Manifest.permission.WRITE_EXTERNAL_STORAGE
)

class AddTorrentFragment : Fragment()
    , OnItemClickListener
    , View.OnClickListener {

    companion object {
        const val TAG = "AddTorrentFragment"
        private const val ARGS_TORRENT_URI = "ARGS_TORRENT_URI"

        fun newInstance(uri: Uri): AddTorrentFragment {
            val bundle = Bundle()
            bundle.putParcelable(ARGS_TORRENT_URI, uri)

            val fragment = AddTorrentFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    private val uri by lazyAndroid {
        arguments!!.getParcelable<Uri>(ARGS_TORRENT_URI)!!
    }

    private val viewModel by viewModel<AddTorrentViewModel>()

    private lateinit var binding: TorrentFragmentAddBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = TorrentFragmentAddBinding.inflate(inflater, container, false)
        setupUI()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViewModel()
        checkPermissions()
        registerKeyEvent()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        unBindViewModel()
    }

    /**
     * 绑定按键监听到Activity
     */
    private fun registerKeyEvent() {
        requireDispatchKeyEventDispatcher().getDispatchKeyEventDispatcher()
            .addCallback(this, this::dispatchKeyEvent)
    }

    private fun setupUI() {
        binding.torrentBtnCenter.setOnClickListener(this)
        binding.torrentBtnCenter.requestFocus()
    }

    private fun bindViewModel() {
        viewModel.state.observe(this) { result ->
            when(result) {
                is Result.Success -> updateStateUI(result.data)
                is Result.Error -> handleException(result.exception)
            }
        }
    }

    private fun unBindViewModel() {
        viewModel.state.removeObservers(this)
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
//                toast("position = $position")
            }
        }
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.torrent_btn_center -> {
                when (val result = viewModel.buildTorrentTask()) {
                    is Result.Success -> {
                        // 添加任务，添加完成后悔发送通知，TorrentAddActivity接收通知后会关闭
                        TorrentTaskService.addTorrent(requireActivity(), result.data)
                    }
                    is Result.Error -> {
                        toast(result.exception.message)
                    }
                }
            }
        }
    }

    private fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        if (event?.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_BACK) {
            if (!binding.torrentBtnCenter.hasFocus()) {
                binding.torrentBtnCenter.requestFocus()
                return true
            }
        }
        return false
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