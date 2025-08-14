package com.tech.sid.ui.auth

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.viewModels
import com.tech.sid.R
import com.tech.sid.base.BaseActivity
import com.tech.sid.base.BaseViewModel
import com.tech.sid.base.utils.BindingUtils
import com.tech.sid.databinding.ActivityMySplashBinding
import com.tech.sid.ui.dashboard.dashboard_with_fragment.DashboardActivity
import com.tech.sid.ui.dashboard.journal_folder.AudioListening
import com.tech.sid.ui.dashboard.person_response.PersonResponse
import com.tech.sid.ui.dashboard.start_practicing.StartPracticing
import com.tech.sid.ui.onboarding_ques.OnboardingStart
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MySplashActivity : BaseActivity<ActivityMySplashBinding>() {
    private val viewModel: AuthCommonVM by viewModels()
    override fun getLayoutResource(): Int {
        return R.layout.activity_my_splash
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView() {
        BindingUtils.screenFillView(this)
        initOnClick()
        if (sharedPrefManager.getTokenFromPref()) {
            binding.button.visibility = View
                .GONE
            Handler(Looper.getMainLooper()).postDelayed({
                startActivity(Intent(this, DashboardActivity::class.java))
            }, 1000)
        }
    }

    private fun initOnClick() {
        viewModel.onClick.observe(this) {
            when (it?.id) {
                R.id.button -> {
                    startActivity(Intent(this, LoginActivity::class.java))
                }
            }
        }

    }
}