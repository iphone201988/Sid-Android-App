package com.tech.sid.ui.dashboard.start_practicing

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.tech.sid.BR
import com.tech.sid.CommonFunctionClass
import com.tech.sid.R
import com.tech.sid.base.BaseActivity
import com.tech.sid.base.BaseViewModel
import com.tech.sid.base.SimpleRecyclerViewAdapter
import com.tech.sid.base.utils.BindingUtils
import com.tech.sid.base.utils.Status
import com.tech.sid.base.utils.showErrorToast
import com.tech.sid.data.api.Constants
import com.tech.sid.databinding.ActivityNextBestStepBinding
import com.tech.sid.databinding.ActivityStartPracticingBinding
import com.tech.sid.databinding.ItemCountryBinding
import com.tech.sid.databinding.StartPracticingItemBinding
import com.tech.sid.ui.auth.CountryModel
import com.tech.sid.ui.dashboard.CreatingBaseLine
import com.tech.sid.ui.dashboard.choose_situation.ChooseSituation
import com.tech.sid.ui.dashboard.next_best_step.NextBestStepVm
import com.tech.sid.ui.dashboard.result_screen.ResultActivity
import com.tech.sid.ui.onboarding_ques.OnboardingModel
import com.tech.sid.ui.onboarding_ques.OnboardingQuestion
import com.tech.sid.ui.onboarding_ques.PostOnboardingModel
import com.tech.sid.ui.onboarding_ques.StartPracticingModel
import com.tech.sid.ui.onboarding_ques.StepperOnboardingModel
import com.tech.sid.ui.onboarding_ques.StepperPageModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StartPracticing : BaseActivity<ActivityStartPracticingBinding>() {
    private val viewModel: StartPracticingVm by viewModels()
    private lateinit var adapter: SimpleRecyclerViewAdapter<StartPracticingModel, StartPracticingItemBinding>
    override fun getLayoutResource(): Int {
        return R.layout.activity_start_practicing
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView() {
        BindingUtils.screenFillView(this)
        initOnClick()
        apiObserver()
        viewModel.getAiCoachFunction()
        initRecyclerview(binding.rvGetStarted)
    }

    private fun initRecyclerview(view: RecyclerView) {
        adapter =
            SimpleRecyclerViewAdapter(
                R.layout.start_practicing_item, BR.bean
            ) { v, m, pos ->
                when (v.id) {
                    R.id.mainLayout -> {
                        CommonFunctionClass.singleSelectionRV(
                            list = adapter.list,
                            selectedId = m.id,
                            getId = { it.id },
                            isSelectedGetter = { it.iselected == true },
                            isSelectedSetter = { item, isSelected, selectedModel ->
                                item.iselected = isSelected
                                if (isSelected) {

                                    BindingUtils.interactionModelPost?.momentId =
                                        selectedModel?.id ?: ""
                                }
                            },
                            notifyChanged = { adapter.notifyItemChanged(it) }
                        )
                    }
                }
            }
        view.adapter = adapter
        view.isNestedScrollingEnabled = true
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
                        Constants.GET_EMPATHY_OPTIONS_API -> {
                            try {
                                val getModelStartPracticing: ModelStartPracticing? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (getModelStartPracticing?.success == true) {

                                    val itemListData = ArrayList<StartPracticingModel>()
                                    itemListData.add(
                                        StartPracticingModel(
                                            "Conflict",
                                            "#E9FFFF",
                                            R.drawable.icon_heartbreak
                                        )
                                    )
                                    itemListData.add(
                                        StartPracticingModel(
                                            "Boundaries",
                                            "#FFFFFF",
                                            R.drawable.icon_divide_solid,
                                            "Boundaries"
                                        )
                                    )
                                    itemListData.add(
                                        StartPracticingModel(
                                            "Ghosting or \nEmotional Distance",
                                            "#FFEEEE",
                                            R.drawable.ghost_icon,
                                            "Ghosting or Emotional Distance"
                                        )
                                    )
                                    itemListData.add(
                                        StartPracticingModel(
                                            "Criticism &\n" + "Judgment",
                                            "#F0EBFF",
                                            R.drawable.thumb_down,
                                            "Criticism and Judgment"
                                        )
                                    )
                                    itemListData.add(
                                        StartPracticingModel(
                                            "Guilt or " + "Emotional Pressure",
                                            "#FFFFFF",
                                            R.drawable.sad_face,
                                            "Guilt or Emotional Pressure"
                                        )
                                    )
                                    itemListData.add(
                                        StartPracticingModel(
                                            "Miscommunication",
                                            "#E9FFFF",
                                            R.drawable.line_md_heart,
                                            "Miscommunication"
                                        )
                                    )
                                    itemListData.add(
                                        StartPracticingModel(
                                            "Closeness &\n" + "Reassurance",
                                            "#F0EBFF",
                                            R.drawable.smile_facing,
                                            "Closeness & Reassurance"
                                        )
                                    )
                                    itemListData.add(
                                        StartPracticingModel(
                                            "Feeling\n" + "Undervalued",
                                            "#FFEEEE",
                                            R.drawable.line_graph,
                                            "Feeling Undervalued"
                                        )
                                    )
                                    for (i in getModelStartPracticing.data.indices) {
                                        for (j in itemListData.indices) {
                                            if (itemListData[j].exactText == getModelStartPracticing.data[i].title) {
                                                itemListData[j].id =
                                                    getModelStartPracticing.data[i]._id ?: "0"
                                            }
                                        }
                                    }

                                    adapter.list = itemListData
                                } else {
                                    getModelStartPracticing?.message?.let { it1 ->
                                        showErrorToast(
                                            it1
                                        )
                                    }
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

    private fun initOnClick() {
        viewModel.onClick.observe(this) {
            when (it?.id) {
                R.id.button -> {
                    if (binding.somethingIMGoingThrough.text.toString().trim().isEmpty()) {
                        showErrorToast("please enter notes")
                        return@observe
                    }
                    if (BindingUtils.interactionModelPost?.momentId.toString().trim().isEmpty()) {
                        showErrorToast("please select empathy coach")
                        return@observe
                    }

                    startActivity(Intent(this, ChooseSituation::class.java))
                }

                R.id.back_button -> {
                    finish()
                }
            }
        }
    }
}
