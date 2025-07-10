package com.tech.sid.ui.onboarding_ques

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
import com.tech.sid.databinding.ActivityConsentBinding
import com.tech.sid.databinding.ActivityOnboardingStartBinding
import com.tech.sid.databinding.ActivityOtpVerifyBinding
import com.tech.sid.ui.auth.AuthCommonVM
import dagger.hilt.android.AndroidEntryPoint




@AndroidEntryPoint
class OnboardingStart : BaseActivity<ActivityOnboardingStartBinding>() {
    private val viewModel: AuthCommonVM by viewModels()
    override fun getLayoutResource(): Int {
        return R.layout.activity_onboarding_start
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
                    startActivity(Intent(this, OnboardingQuestion::class.java))
                }
                R.id.back_button -> {
                    finish()
                }

            }
        }
    }
}