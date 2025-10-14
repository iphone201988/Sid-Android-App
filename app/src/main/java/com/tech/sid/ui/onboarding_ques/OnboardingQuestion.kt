package com.tech.sid.ui.onboarding_ques

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.PagerSnapHelper
import com.google.gson.Gson
import com.tech.sid.BR
import com.tech.sid.R
import com.tech.sid.base.BaseActivity
import com.tech.sid.base.BaseViewModel
import com.tech.sid.base.SimpleRecyclerViewAdapter
import com.tech.sid.base.utils.BindingUtils
import com.tech.sid.base.utils.Status
import com.tech.sid.base.utils.showErrorToast
import com.tech.sid.data.api.Constants
import com.tech.sid.databinding.ActivityOnboardingQuestionBinding
import com.tech.sid.databinding.StepperOnboardingRvItemBinding
import com.tech.sid.ui.auth.AuthCommonVM
import com.tech.sid.ui.dashboard.CreatingBaseLine
import com.tech.sid.ui.dashboard.result_screen.ResultActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnboardingQuestion : BaseActivity<ActivityOnboardingQuestionBinding>() {
    private val viewModel: AuthCommonVM by viewModels()
    private lateinit var onboardingAdapter: SimpleRecyclerViewAdapter<StepperPageModel, StepperOnboardingRvItemBinding>
    var stepper: Int = 1
    var tagList: List<GetTagsData>? = listOf()
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
        binding.selectedDote = stepper
        apiObserver()
        viewModel.getTagsApi()
        //  getOnboardingFunction()
    }


    private fun initOnClick() {
        viewModel.onClick.observe(this) {
            when (it?.id) {
                R.id.button -> {
                    if (stepper <= 10) {
                        stepper++
                        adapterInit(stepper)
                        binding.selectedDote = stepper
                    } else {
                        getPostOnboarding()
                    }

                }

                R.id.back_button -> {
                    finish()
                }

            }
        }
    }

    private fun getPostOnboarding() {
        val finalAnswers = mutableListOf<Any>()

        // Loop through pages for answers
        onboardingAdapter.list.forEachIndexed { index, page ->
            when {
                index in 0..9 -> {
                    var selectedIndex = page.stepperOnboardingModel.indexOfFirst { it.select }
                    if (selectedIndex == 0 || selectedIndex == -1) {
                        selectedIndex = 1
                    }
                    finalAnswers.add(selectedIndex)
                }

                index == 10 -> {
                    finalAnswers.add(page.textLastText.get() ?: "")
                }
            }
        }

        val selectedTags = tagList?.filter { it.iselected == true }

        val tagsCategories =
            selectedTags?.groupBy { it.key }?.mapValues { entry -> entry.value.size }

        val tags = selectedTags?.mapNotNull { it.word }
        if (finalAnswers[10].toString().isNullOrBlank() || finalAnswers[10].toString().isNullOrEmpty()) {
            showErrorToast("Please enter the usually respond")
            return
        }
        val data = HashMap<String, Any>().apply {
            put("answers", finalAnswers)
            if (!tagsCategories.isNullOrEmpty()) {
                put("tags_categories", tagsCategories)
            }
            if (!tags.isNullOrEmpty()) {
                put("tags", tags)
            }
        }
        viewModel.postOnboardingApi(data)
    }


    private fun apiObserver() {
        viewModel.observeCommon.observe(this) {
            when (it?.status) {
                Status.LOADING -> {
                    showLoading("Loading")
                }

                Status.SUCCESS -> {

                    hideLoading()
                    when (it.message) {
                        Constants.GET_ONBOARDING_API -> {
                            try {
                                val getOnboardingModel: OnboardingModel? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (getOnboardingModel?.success == true) {
                                    val itemListData = ArrayList<StepperPageModel>()
                                    for (i in getOnboardingModel.questions.indices) {
                                        val itemListData2 = ArrayList<StepperOnboardingModel>()
                                        for (j in getOnboardingModel.questions[i].options.indices) {
                                            if (j == 0) {
                                                itemListData2.add(
                                                    StepperOnboardingModel(
                                                        true,
                                                        getOnboardingModel.questions[i].options[j]
                                                    )
                                                )
                                            } else {
                                                itemListData2.add(
                                                    StepperOnboardingModel(
                                                        false,
                                                        getOnboardingModel.questions[i].options[j]
                                                    )
                                                )
                                            }
                                        }

                                        itemListData.add(
                                            StepperPageModel(
                                                itemListData2,
                                                getOnboardingModel.questions[i].text,
                                                tagData = tagList
                                            )
                                        )
                                    }
                                    onboardingAdapter.list = itemListData
                                } else {
                                    getOnboardingModel?.message?.let { it1 -> showErrorToast(it1) }
                                }
                            } catch (e: Exception) {
                                showErrorToast(e.toString())
                            }
                        }

                        Constants.POST_ONBOARDING_API -> {
                            try {
                                val postOnboardingModel: PostOnboardingModel? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (postOnboardingModel?.success == true) {
                                    ResultActivity.onboardingResultModel = postOnboardingModel.data
                                    val userData = sharedPrefManager.getProfileData()
                                    if(userData!=null){
                                        userData.user.isOnboardingCompleted=true
                                        sharedPrefManager.setLoginData(userData)
                                    }
                                    startActivity(Intent(this, CreatingBaseLine::class.java))
                                } else {
                                    postOnboardingModel?.message?.let { it1 -> showErrorToast(it1) }
                                }
                            } catch (e: Exception) {
                                showErrorToast(e.toString())
                            }
                        }

                        Constants.GET_TAGS -> {
                            try {
                                Log.i("qgqg", "apiObserver: " + Gson().toJson(it.data))
                                val getTagsModel: GetTagsResponse? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (getTagsModel != null && getTagsModel.success == true) {
                                    tagList = getTagsModel.data
                                    getOnboardingFunction()

                                } else {
                                    showErrorToast("Model parse exception")
                                }

                            } catch (e: Exception) {
                                showErrorToast(e.toString())
                            }
                        }
                    }
                }

                Status.ERROR -> {
                    hideLoading()
                    showErrorToast(it.message.toString())
                }

                Status.UN_AUTHORIZE -> {
                    hideLoading()
                    showUnauthorised()
                }

                else -> {
                    hideLoading()
                }
            }
        }
    }

    private fun getOnboardingFunction() {
        viewModel.getOnboardingApi()
    }

    private fun adapterInit(stepper: Int) {


//        settingAdapter.list = getStepperItemList(this, stepper)
        binding.rvOnboarding.smoothScrollToPosition(stepper - 1)
    }

    private fun adapterLoad() {
        onboardingAdapter = SimpleRecyclerViewAdapter(
            R.layout.stepper_onboarding_rv_item, BR.bean
        ) { v, m, pos ->
        }
        binding.rvOnboarding.adapter = onboardingAdapter
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(binding.rvOnboarding)
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


}