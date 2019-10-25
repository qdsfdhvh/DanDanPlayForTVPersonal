package com.dandanplay.tv.ui

import android.graphics.Color
import android.os.Bundle
import android.util.SparseArray
import android.view.View
import androidx.core.content.ContextCompat
import androidx.leanback.widget.*
import com.blankj.utilcode.util.ToastUtils
import com.dandanplay.tv.R
import com.dandanplay.tv.bean.MyBean
import com.dandanplay.tv.ui.base.SupportBrowerFragment
import com.dandanplay.tv.ui.dialog.setLoadFragment
import com.dandanplay.tv.ui.presenter.MainAreaPresenter
import com.dandanplay.tv.ui.presenter.MainMyPresenter
import com.dandanplay.tv.utils.AnimeRow
import com.dandanplay.tv.vm.BangumiAViewModel
import com.seiko.common.ResultData
import com.seiko.common.Status
import com.seiko.common.lazyAndroid
import com.seiko.domain.entities.BangumiIntro
import org.koin.android.viewmodel.ext.android.viewModel

class MainFragment : SupportBrowerFragment(), OnItemViewClickedListener, View.OnClickListener {

    private val viewModel by viewModel<BangumiAViewModel>()

    private lateinit var adapterRows: SparseArray<AnimeRow>

    private val leftItems by lazyAndroid {
        listOf(
            MyBean(ID_AREA, "番剧区", R.drawable.ic_bangumi_area),
            MyBean(ID_FAVOURITE, "追 番", R.drawable.ic_bangumi_favourite),
            MyBean(ID_TIME, "放送表", R.drawable.ic_bangumi_time),
            MyBean(ID_INDEX, "索引", R.drawable.ic_bangumi_index)
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.mainState.observe(this::getLifecycle, this::updateUI)

        headersState = HEADERS_ENABLED
        isHeadersTransitionOnBackEnabled = true
        title = "弹弹Play"
        brandColor = Color.parseColor("#424242")

        // About Search
        searchAffordanceColor = ContextCompat.getColor(context!!, R.color.colorAccent)
        setOnSearchClickedListener(this)

        // Create Rows Adapter
        adapterRows = SparseArray(2)
        adapterRows.put(ROW_AREA, AnimeRow(ROW_AREA)
            .setAdapter(MainAreaPresenter())
            .setTitle("今日更新"))
        adapterRows.put(ROW_MY, AnimeRow(ROW_MY)
            .setAdapter(MainMyPresenter())
            .setTitle("个人中心"))

        // Create True Rows Adapter
        val rowsAdapter = ArrayObjectAdapter(ListRowPresenter())
        for (i in 0 until adapterRows.size()) {
            val row = adapterRows.valueAt(i)
            val headerItem = HeaderItem(row.getId(), row.getTitle())
            val listRow = ListRow(headerItem, row.getAdapter())
            rowsAdapter.add(listRow)
        }

        // Bind Adapter
        adapter = rowsAdapter
        onItemViewClickedListener = this
        prepareEntranceTransition()

        //
        adapterRows.get(ROW_MY)?.setList(leftItems)
    }

    override fun onStart() {
        super.onStart()
        viewModel.getBangumiList()
    }

    private fun updateUI(data: ResultData<List<BangumiIntro>>) {
        when(data.responseType) {
            Status.LOADING -> {
                setLoadFragment(true)
            }
            Status.ERROR -> {
                setLoadFragment(false)
                ToastUtils.showShort(data.error.toString())
            }
            Status.SUCCESSFUL -> {
                setLoadFragment(false)
                adapterRows.get(ROW_AREA)?.setList(data.data ?: return)
                startEntranceTransition()
            }
        }
    }

    /**
     * @link [androidx.leanback.widget.TitleView]
     */
    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.title_orb -> {
                ToastUtils.showShort("搜索")
            }
        }
    }

    override fun onItemClicked(holder: Presenter.ViewHolder?, item: Any?, rowViewHolder: RowPresenter.ViewHolder?, row: Row?) {
        when(item) {
            is BangumiIntro -> {
                ToastUtils.showShort(item.animeTitle)
            }
            is MyBean -> {
                ToastUtils.showShort(item.name)
            }
        }
    }

    companion object {
        const val TAG = "MainFragment"

        private const val ROW_AREA = 0
        private const val ROW_MY = 1

        private const val ID_AREA = 0
        private const val ID_FAVOURITE = 1
        private const val ID_TIME = 2
        private const val ID_INDEX = 3

        fun newInstance() = MainFragment()
    }
}