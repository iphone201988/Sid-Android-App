package com.tech.sid.ui.dashboard.dashboard_with_fragment.forget_password

import com.tech.sid.ui.dashboard.dashboard_with_fragment.change_password.ChangePasswordVm



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
import com.tech.sid.databinding.ActivityEditProfileBinding
import com.tech.sid.ui.dashboard.dashboard_with_fragment.profile_edit.EditProfileVm
import dagger.hilt.android.AndroidEntryPoint



@AndroidEntryPoint
class ForgotPassword : BaseActivity<ActivityChangePasswordBinding>() {

    private val viewModel: ChangePasswordVm by viewModels()
    override fun getLayoutResource(): Int {
        return R.layout.activity_forgot_password
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

                }
                R.id.back_button -> {
                    finish()
                }
            }
        }

    }
}