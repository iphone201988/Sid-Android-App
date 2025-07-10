package com.tech.sid.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.tech.sid.R
import com.tech.sid.base.BaseActivity
import com.tech.sid.base.BaseViewModel
import com.tech.sid.base.utils.BindingUtils
import com.tech.sid.databinding.ActivityCreatingBaseLineBinding
import com.tech.sid.ui.dashboard.result_screen.ResultActivity
import com.tech.sid.ui.onboarding_ques.OnboardingQuestion
import dagger.hilt.android.AndroidEntryPoint
@AndroidEntryPoint
class CreatingBaseLine : BaseActivity<ActivityCreatingBaseLineBinding>() {
    private val viewModel: CreatingBaseLineVm by viewModels()
    override fun getLayoutResource(): Int {
        return R.layout.activity_creating_base_line
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }
    override fun onCreateView() {
        BindingUtils.screenFillView(this)
        initOnClick()
        handlerNext()
    }
    private fun handlerNext() {
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, ResultActivity::class.java))
        }, 1000)
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