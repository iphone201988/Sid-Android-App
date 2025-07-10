package com.tech.sid.ui.onboarding_ques

import android.content.Context
import android.content.Intent
import android.widget.LinearLayout
import android.window.SplashScreen
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.recyclerview.widget.PagerSnapHelper
import com.tech.sid.R
import com.tech.sid.BR
import com.tech.sid.base.BaseActivity
import com.tech.sid.base.BaseViewModel
import com.tech.sid.base.SimpleRecyclerViewAdapter
import com.tech.sid.base.utils.BindingUtils
import com.tech.sid.base.utils.GlowCircleView
import com.tech.sid.base.utils.GlowCircleView2
import com.tech.sid.base.utils.dpToPx
import com.tech.sid.databinding.ActivityOnboardingQuestionBinding
import com.tech.sid.databinding.ActivityOnboardingStartBinding
import com.tech.sid.databinding.StepperOnboardingRvItemBinding
import com.tech.sid.databinding.StepperRvItemBinding
import com.tech.sid.ui.auth.AuthCommonVM
import com.tech.sid.ui.auth.MySplashActivity
import com.tech.sid.ui.dashboard.CreatingBaseLine
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnboardingQuestion : BaseActivity<ActivityOnboardingQuestionBinding>() {
    private val viewModel: AuthCommonVM by viewModels()
    override fun getLayoutResource(): Int {
        return R.layout.activity_onboarding_question
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView() {
        BindingUtils.screenFillView(this)
        initOnClick()
        adapterLoad()
        adapterInit(stepper)

    }

    private fun adapterLoad() {
        settingAdapter = SimpleRecyclerViewAdapter(
            R.layout.stepper_rv_item, BR.bean
        ) { v, m, pos ->

        }
        binding.rvStepper.adapter = settingAdapter



        onboardingAdapter = SimpleRecyclerViewAdapter(
            R.layout.stepper_onboarding_rv_item, BR.bean
        ) { v, m, pos ->

        }
        binding.rvOnboarding.adapter = onboardingAdapter
//        binding.rvOnboarding.suppressLayout(true)
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(binding.rvOnboarding)
        onboardingAdapter.list = getOnboardingStepperItemList()

    }

    private lateinit var settingAdapter: SimpleRecyclerViewAdapter<StepperModel, StepperRvItemBinding>
    private lateinit var onboardingAdapter: SimpleRecyclerViewAdapter<StepperPageModel, StepperOnboardingRvItemBinding>
    private fun adapterInit(stepper: Int) {


        settingAdapter.list = getStepperItemList(this, stepper)
        binding.rvOnboarding.smoothScrollToPosition(stepper-1)
    }

    private fun getStepperItemList(context: Context, stepper: Int): List<StepperModel> {
        val totalCount = 11
        val colors = listOf(
            R.color.stepper_color_1 to R.color.stepper_color_1_30,
            R.color.stepper_color_2 to R.color.stepper_color_2_30,
            R.color.stepper_color_3 to R.color.stepper_color_3_30
        )

        return List(totalCount) { index ->
            val colorPair = colors[index % colors.size]
            StepperModel(
                index >= stepper,
                ContextCompat.getColor(context, colorPair.first),
                ContextCompat.getColor(context, colorPair.second)
            )
        }
    }

    private fun getOnboardingStepperItemList(): List<StepperPageModel> {
        val itemListData = ArrayList<StepperPageModel>()
        val itemListData2 = ArrayList<StepperOnboardingModel>()
        itemListData2.add(StepperOnboardingModel(true, "Strongly Disagree"))
        itemListData2.add(StepperOnboardingModel(false, "Slightly Disagree"))
        itemListData2.add(StepperOnboardingModel(false, "Neutral"))
        itemListData2.add(StepperOnboardingModel(false, "Slightly Agree"))
        itemListData2.add(StepperOnboardingModel(false, "Agree"))
        itemListData2.add(StepperOnboardingModel(false, "Strongly Agree"))
        itemListData2.add(StepperOnboardingModel(true, "Strongly Disagree"))
        itemListData2.add(StepperOnboardingModel(false, "Slightly Disagree"))
        itemListData2.add(StepperOnboardingModel(false, "Neutral"))
        itemListData2.add(StepperOnboardingModel(false, "Slightly Agree"))
        itemListData2.add(StepperOnboardingModel(false, "Agree"))
        itemListData2.add(StepperOnboardingModel(false, "Strongly Agree"))


        itemListData2.add(StepperOnboardingModel(true, "Strongly Disagree"))
        itemListData2.add(StepperOnboardingModel(false, "Slightly Disagree"))
        itemListData2.add(StepperOnboardingModel(false, "Neutral"))
        itemListData2.add(StepperOnboardingModel(false, "Slightly Agree"))
        itemListData2.add(StepperOnboardingModel(false, "Agree"))
        itemListData2.add(StepperOnboardingModel(false, "Strongly Agree"))


        itemListData2.add(StepperOnboardingModel(true, "Strongly Disagree"))
        itemListData2.add(StepperOnboardingModel(false, "Slightly Disagree"))
        itemListData2.add(StepperOnboardingModel(false, "Neutral"))
        itemListData2.add(StepperOnboardingModel(false, "Slightly Agree"))
        itemListData2.add(StepperOnboardingModel(false, "Agree"))
        itemListData2.add(StepperOnboardingModel(false, "Strongly Agree"))



        itemListData2.add(StepperOnboardingModel(true, "Strongly Disagree"))
        itemListData2.add(StepperOnboardingModel(false, "Slightly Disagree"))
        itemListData2.add(StepperOnboardingModel(false, "Neutral"))
        itemListData2.add(StepperOnboardingModel(false, "Slightly Agree"))
        itemListData2.add(StepperOnboardingModel(false, "Agree"))
        itemListData2.add(StepperOnboardingModel(false, "Strongly Agree"))



        itemListData2.add(StepperOnboardingModel(true, "Strongly Disagree"))
        itemListData2.add(StepperOnboardingModel(false, "Slightly Disagree"))
        itemListData2.add(StepperOnboardingModel(false, "Neutral"))
        itemListData2.add(StepperOnboardingModel(false, "Slightly Agree"))
        itemListData2.add(StepperOnboardingModel(false, "Agree"))
        itemListData2.add(StepperOnboardingModel(false, "Strongly Agree"))




        itemListData2.add(StepperOnboardingModel(true, "Strongly Disagree"))
        itemListData2.add(StepperOnboardingModel(false, "Slightly Disagree"))
        itemListData2.add(StepperOnboardingModel(false, "Neutral"))
        itemListData2.add(StepperOnboardingModel(false, "Slightly Agree"))
        itemListData2.add(StepperOnboardingModel(false, "Agree"))
        itemListData2.add(StepperOnboardingModel(false, "Strongly Agree"))



        itemListData2.add(StepperOnboardingModel(true, "Strongly Disagree"))
        itemListData2.add(StepperOnboardingModel(false, "Slightly Disagree"))
        itemListData2.add(StepperOnboardingModel(false, "Neutral"))
        itemListData2.add(StepperOnboardingModel(false, "Slightly Agree"))
        itemListData2.add(StepperOnboardingModel(false, "Agree"))
        itemListData2.add(StepperOnboardingModel(false, "Strongly Agree"))//8*6=48


        itemListData2.add(StepperOnboardingModel(true, "1. Not at all"))
        itemListData2.add(StepperOnboardingModel(false, "2 – Very slightly"))
        itemListData2.add(StepperOnboardingModel(false, "3 – Slightly"))
        itemListData2.add(StepperOnboardingModel(false, "4 – Somewhat"))
        itemListData2.add(StepperOnboardingModel(false, "5 – Moderately"))
        itemListData2.add(StepperOnboardingModel(false, "6 – Fairly"))
        itemListData2.add(StepperOnboardingModel(false, "7 – Considerably"))
        itemListData2.add(StepperOnboardingModel(false, "8 – Mostly"))
        itemListData2.add(StepperOnboardingModel(false, "9 – Almost completely"))
        itemListData2.add(StepperOnboardingModel(false, "10 – Completely"))//48+10=58


        itemListData2.add(StepperOnboardingModel(false, "\$0.05"))
        itemListData2.add(StepperOnboardingModel(false, "\$0.10"))
        itemListData2.add(StepperOnboardingModel(false, "\$0.50"))
        itemListData2.add(StepperOnboardingModel(false, "\$1.0"))//58+4=62
        itemListData2.size
        itemListData.add(
            StepperPageModel(
                itemListData2.take(5),
                "I enjoy exploring new ideas and unfamiliar experiences."
            )
        )
        itemListData.add(
            StepperPageModel(
                itemListData2.subList(6, 12), // 2nd set
                "I often feel anxious, tense, or emotionally overwhelmed"
            )
        )
        itemListData.add(
            StepperPageModel(
                itemListData2.subList(12, 18), // 3rd set
                "I try to see the good in others, even when I feel frustrated or disappointed."
            )
        )
        itemListData.add(
            StepperPageModel(
                itemListData2.subList(18, 24), // 4th set
                "I take time to reflect on the good things in my life"
            )
        )
        itemListData.add(
            StepperPageModel(
                itemListData2.subList(24, 30), // 5th set
                "I find it easy to get emotionally close to others"
            )
        )
        itemListData.add(
            StepperPageModel(
                itemListData2.subList(30, 36), // 6th set
                "I often worry that people I care about may not feel the same way about me."
            )
        )
        itemListData.add(
            StepperPageModel(
                itemListData2.subList(36, 42), // 7th set
                "I feel unsettled when I sense emotional distance in close relationships."
            )
        )
        itemListData.add(
            StepperPageModel(
                itemListData2.subList(42, 48), // 8th set
                "I like to plan ahead and follow through on my goals."
            )
        )
        itemListData.add(
            StepperPageModel(
                itemListData2.subList(48, 58), // 9th set: scale from 1–10
                "On a scale from 1 to 10 how satisfied are you with your life right now?"
            )
        )
        itemListData.add(
            StepperPageModel(
                itemListData2.takeLast(4),
                "A bat and a ball cost \$1.10 in total. The bat costs \$1 more than the ball. How much does the ball cost?"
            )
        )
        itemListData.add(
            StepperPageModel(
                itemListData2.takeLast(4),
                "When you feel hurt or let down by someone close, how do you usually respond?"
            )
        )
        return itemListData
    }

    var stepper: Int = 1
    private fun initOnClick() {
        viewModel.onClick.observe(this) {
            when (it?.id) {
                R.id.button -> {
                    if(stepper<=11){
                        stepper++
                        adapterInit(stepper)
                    }
                    else{
                        startActivity(Intent(this, CreatingBaseLine::class.java))

                    }

                }
                R.id.back_button -> {
                    finish()
                }

            }
        }
    }
}