package com.tech.sid.ui.dashboard.dashboard_with_fragment.profile_edit

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
import com.tech.sid.databinding.ActivityEditProfileBinding
import com.tech.sid.databinding.ActivitySignUpBinding
import com.tech.sid.ui.auth.AuthCommonVM
import com.tech.sid.ui.auth.OtpVerify
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditProfile : BaseActivity<ActivityEditProfileBinding>() {

    private val viewModel: EditProfileVm by viewModels()
    override fun getLayoutResource(): Int {
        return R.layout.activity_edit_profile
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