package com.seiko.common.util.extensions

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel

inline fun <reified T : ViewModel> Fragment.parentViewModels() =
    viewModels<T>(ownerProducer = { requireParentFragment() })