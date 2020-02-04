package androidx.leanback.app

abstract class FixBrowseSupportFragment : BrowseSupportFragment() {

    override fun onDestroyView() {
        super.onDestroyView()
        mHeadersSupportFragment = null
    }
}