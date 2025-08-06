package com.tech.sid.ui.dashboard.dashboard_with_fragment.forget_password

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.tech.sid.R
import com.tech.sid.base.BaseActivity
import com.tech.sid.base.BaseViewModel
import com.tech.sid.base.utils.BindingUtils
import com.tech.sid.databinding.ActivityChangePasswordBinding
import com.tech.sid.databinding.ActivityForgotEmailPasswordBinding
import com.tech.sid.ui.dashboard.dashboard_with_fragment.change_password.ChangePasswordVm
import com.tech.sid.ui.dashboard.dashboard_with_fragment.forget_password.verify_otp.OtpVerifyForgot
import dagger.hilt.android.AndroidEntryPoint




@AndroidEntryPoint
class ForgotEmailPassword : BaseActivity<ActivityForgotEmailPasswordBinding>() {

    private val viewModel: ChangePasswordVm by viewModels()
    override fun getLayoutResource(): Int {
        return R.layout.activity_forgot_email_password
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
                    startActivity(Intent(this, OtpVerifyForgot::class.java))

                }

                R.id.back_button -> {
                    finish()
                }
            }
        }

    }
}