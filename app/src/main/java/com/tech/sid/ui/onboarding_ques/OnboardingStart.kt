package com.tech.sid.ui.onboarding_ques

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.activity.viewModels
import com.tech.sid.R
import com.tech.sid.base.BaseActivity
import com.tech.sid.base.BaseViewModel
import com.tech.sid.base.utils.BindingUtils
import com.tech.sid.databinding.ActivityOnboardingStartBinding
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
        animateText(
            binding.tvYourIn, getString(R.string.you_re_in_let_s_explore_nyour_emotional_world)
        )
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

    fun animateText(textView: TextView, text: String, delay: Long = 100) {

        textView.post {
            textView.text = text
            textView.alpha = 0f

            // Measure full screen height to start animation completely off-screen
            val screenHeight = textView.rootView.height.toFloat()
            textView.translationY = screenHeight  // start below the visible area

            textView.animate()
                .translationY(0f) // move to its normal position
                .alpha(1f)
                .setDuration(1500)
                .setInterpolator(android.view.animation.DecelerateInterpolator())
                .start()
        }

        /*textView.text = text
        textView.alpha = 0f
        textView.translationY = 300f  // start below

        textView.animate()
            .translationY(0f)
            .alpha(1f)
            .setDuration(1500)
            .setInterpolator(android.view.animation.OvershootInterpolator())
            .start()*/

//        textView.text = ""
//        var i = 0
//        val handler = Handler(Looper.getMainLooper())
//        val runnable = object : Runnable {
//            override fun run() {
//                if (i <= text.length) {
//                    textView.text = text.substring(0, i)
//                    i++
//                    handler.postDelayed(this, delay)
//                }
//            }
//        }
//        handler.post(runnable)
    }
}