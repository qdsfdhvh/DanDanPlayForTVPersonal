package androidx.leanback.app

abstract class FixDetailsSupportFragment : DetailsSupportFragment() {

    override fun onDestroyView() {
        super.onDestroyView()
        mRowsSupportFragment = null
    }
}