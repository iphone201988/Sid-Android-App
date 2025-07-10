package com.tech.sid.ui.dashboard.simulation_insights

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
import com.tech.sid.databinding.ActivityChatBinding
import com.tech.sid.databinding.ActivitySimulationInsightsBinding
import com.tech.sid.ui.dashboard.chat_screen.ChatActivityVm
import com.tech.sid.ui.dashboard.chat_screen.ChatAdapter
import com.tech.sid.ui.dashboard.chat_screen.ChatMessage
import com.tech.sid.ui.dashboard.result_screen.ResultActivity
import com.tech.sid.ui.dashboard.subscription_package.SubscriptionActivity
import com.tech.sid.ui.onboarding_ques.OnboardingQuestion
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SimulationInsights : BaseActivity<ActivitySimulationInsightsBinding>() {
    private val viewModel: SimulationInsightsVm by viewModels()
    override fun getLayoutResource(): Int {
        return R.layout.activity_simulation_insights
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
                    startActivity(Intent(this, SubscriptionActivity::class.java))
                }
                R.id.back_button -> {
                    finish()
                }

            }
        }
    }
}