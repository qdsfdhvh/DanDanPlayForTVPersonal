package com.dandanplay.tv2.ui.account

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.blankj.utilcode.util.ToastUtils
import com.dandanplay.tv2.R
import com.dandanplay.tv2.ui.base.BaseFragment
import com.dandanplay.tv2.ui.base.hideSoftInput
import com.dandanplay.tv2.vm.LoginViewModel
import kotlinx.android.synthetic.main.fragment_login.*
import org.koin.android.viewmodel.ext.android.viewModel

class LoginFragment: BaseFragment() {

    private val viewModel by viewModel<LoginViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {
        login_button.setOnClickListener { login() }
    }

    private fun login() {
        val userName = userName.text.toString()
        val password = password.text.toString()

        if (TextUtils.isEmpty(userName)) {
            ToastUtils.showShort("用户名不能为空")
            return
        }
        if (TextUtils.isEmpty(password)) {
            ToastUtils.showShort("密码不能为空")
            return
        }

        // 关闭软键盘
        hideSoftInput()

        // 开始登陆，请求服务
        viewModel.login(userName, password)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_login
    }

    companion object {
        fun newInstance(): LoginFragment {
            return LoginFragment()
        }
    }
}