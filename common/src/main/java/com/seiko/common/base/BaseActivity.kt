package com.seiko.common.base

import androidx.annotation.LayoutRes
import androidx.fragment.app.FragmentActivity

abstract class BaseActivity : FragmentActivity {

    constructor() : super()

    constructor(@LayoutRes contentLayoutId: Int) : super(contentLayoutId)

}