package com.tech.sid.ui.dashboard

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.TextView
import androidx.activity.viewModels
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
        animateText(
            binding.tvEmotionalProfile,
            getString(R.string.creating_your_nemotional_profile)
        )

        val fadeAnim = AlphaAnimation(0.3f, 1f).apply {
            duration = 800
            repeatMode = Animation.REVERSE
            repeatCount = Animation.INFINITE
        }
        binding.tvImage.startAnimation(fadeAnim)

    }

    private fun handlerNext() {
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, ResultActivity::class.java))
        }, 5000)

    }

    private fun initOnClick() {
        viewModel.onClick.observe(this) {
            when (it?.id) {
                R.id.back_button -> {
                    finish()
                }
            }
        }
    }

    private fun animateText(textView: TextView, text: String, delay: Long = 80) {
        textView.text = ""
        var i = 0
        val handler = Handler(Looper.getMainLooper())
        val runnable = object : Runnable {
            override fun run() {
                if (i <= text.length) {
                    textView.text = text.substring(0, i)
                    i++
                    handler.postDelayed(this, delay)
                }
            }
        }
        handler.post(runnable)
    }
}