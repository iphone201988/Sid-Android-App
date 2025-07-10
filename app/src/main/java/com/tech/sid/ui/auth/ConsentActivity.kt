package com.tech.sid.ui.auth

import android.content.Intent
import androidx.activity.viewModels
import com.tech.sid.R
import com.tech.sid.base.BaseActivity
import com.tech.sid.base.BaseViewModel
import com.tech.sid.base.utils.BindingUtils
import com.tech.sid.databinding.ActivityConsentBinding
import com.tech.sid.databinding.ActivityOtpVerifyBinding
import com.tech.sid.ui.onboarding_ques.OnboardingStart
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ConsentActivity : BaseActivity<ActivityConsentBinding>() {
    private val viewModel: AuthCommonVM by viewModels()
    override fun getLayoutResource(): Int {
        return R.layout.activity_consent
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
                    startActivity(Intent(this, OnboardingStart::class.java))
                }
                R.id.back_button -> {
                    finish()
                }
                R.id.firstCheckBoxLL -> {
                    binding.firstCheckBox = !(binding.firstCheckBox ?: false)
                }
                R.id.secondCheckBoxLL -> {
                    binding.secondCheckBox = !(binding.secondCheckBox ?: false)
                }
                R.id.thirdCheckBoxLL -> {
                    binding.thirdCheckBox = !(binding.thirdCheckBox ?: false)
                }
            }
        }
    }
}