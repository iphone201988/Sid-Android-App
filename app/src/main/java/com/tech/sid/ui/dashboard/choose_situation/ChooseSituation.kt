package com.tech.sid.ui.dashboard.choose_situation

import android.content.Intent
import androidx.activity.viewModels
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
import com.tech.sid.databinding.ActivityChooseSituationBinding
import com.tech.sid.databinding.ChooseSituationCardItemBinding
import com.tech.sid.ui.dashboard.person_response.PersonResponse
import com.tech.sid.ui.onboarding_ques.ChooseSituationModel
import com.tech.sid.ui.onboarding_ques.describe.DescribeYourScenarioActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChooseSituation : BaseActivity<ActivityChooseSituationBinding>() {
    private val viewModel: ChooseSituationVm by viewModels()
    var storePos = 0
    private lateinit var adapter: SimpleRecyclerViewAdapter<ChooseSituationModel, ChooseSituationCardItemBinding>
    override fun getLayoutResource(): Int {
        return R.layout.activity_choose_situation
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView() {
        BindingUtils.screenFillView(this)
        initOnClick()
        apiObserver()
        viewModel.getAIEmpathyCoachFunction()
        initRecyclerview(binding.rvGetStarted)
    }

    private fun initRecyclerview(view: RecyclerView) {
        adapter = SimpleRecyclerViewAdapter(
            R.layout.choose_situation_card_item, BR.bean
        ) { v, m, pos ->
            when (v.id) {
                R.id.mainLayout -> {
                    if (pos == adapter.list.size - 1) {
                        storePos = pos
                    } else {
                        storePos = 0
                    }
                    CommonFunctionClass.singleSelectionRV(
                        list = adapter.list,
                        selectedId = m.id,
                        getId = { it.id },
                        isSelectedGetter = { it.iselected == true },
                        isSelectedSetter = { item, isSelected, selectedModel ->
                            item.iselected = isSelected
                            if (isSelected) {
                                BindingUtils.interactionModelPost?.scenarioId =
                                    selectedModel?.id ?: ""
                                BindingUtils.choosenSatuation = selectedModel?.titleValue
                            }
                        },
                        notifyChanged = { adapter.notifyItemChanged(it) })
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
                        Constants.GET_EMPATHY_OPTIONS_SCENARIOS_API -> {
                            try {
                                val getModelScenarious: ModelGetScenariousOption? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (getModelScenarious?.success == true) {
                                    val itemListData = ArrayList<ChooseSituationModel>()
                                    val colors = listOf(
                                        "#FFEEEE",
                                        "#E9FFFF",
                                        "#F0EBFF",
                                    )
                                    for (i in getModelScenarious.data.indices) {
                                        val colorIndex = i % colors.size
                                        itemListData.add(
                                            ChooseSituationModel(
                                                titleValue = getModelScenarious.data[i].title,
                                                colorsValue = colors[colorIndex],
                                                id = getModelScenarious.data[i]._id,
                                            )
                                        )
                                    }
                                    adapter.list = itemListData
                                } else {
                                    getModelScenarious?.message?.let { it1 ->
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
                    if (storePos == 5) {
                        val intent = Intent(this, DescribeYourScenarioActivity::class.java)
                        startActivity(intent)

                    } else {
                        if (BindingUtils.interactionModelPost?.scenarioId.toString().trim()
                                .isEmpty()
                        ) {
                            showErrorToast("Please select scenario coach")
                            return@observe
                        }
                        startActivity(Intent(this, PersonResponse::class.java))
                    }
                }

                R.id.back_button -> {
                    finish()
                }
            }
        }
    }
}
