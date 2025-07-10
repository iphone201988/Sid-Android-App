package com.tech.sid.ui.auth

import android.content.Intent
import androidx.activity.viewModels
import com.tech.sid.R
import com.tech.sid.base.BaseActivity
import com.tech.sid.base.BaseViewModel
import com.tech.sid.base.utils.BindingUtils
import com.tech.sid.databinding.ActivityLoginBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : BaseActivity<ActivityLoginBinding>() {
    private val viewModel: AuthCommonVM by viewModels()
    override fun getLayoutResource(): Int {
        return R.layout.activity_login
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }
    override fun onCreateView() {
        BindingUtils.screenFillView(this)
        initOnClick()
    }

    private fun initOnClick() {
        viewModel.onClick.observe(this) {
            when (it?.id) {
                R.id.button -> {
                    startActivity(Intent(this, LoginActivity::class.java))
                }

                R.id.back_button -> {
                    finish()
                }
                R.id.sign_up -> {
                    startActivity(Intent(this, SignUpActivity::class.java))
                }
            }
        }

    }

}