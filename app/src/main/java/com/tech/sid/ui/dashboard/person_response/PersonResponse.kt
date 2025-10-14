package com.tech.sid.ui.dashboard.person_response

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
import com.tech.sid.databinding.ActivityPersonResponseBinding
import com.tech.sid.databinding.StartPracticingItem2Binding
import com.tech.sid.ui.dashboard.chat_screen.ChatActivity
import com.tech.sid.ui.dashboard.want_to_talk.WantToTalk
import com.tech.sid.ui.onboarding_ques.StartPracticingModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PersonResponse : BaseActivity<ActivityPersonResponseBinding>() {
    private val viewModel: PersonResponseVm by viewModels()
    private lateinit var adapter: SimpleRecyclerViewAdapter<StartPracticingModel, StartPracticingItem2Binding>
    override fun getLayoutResource(): Int {
        return R.layout.activity_person_response
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView() {
        BindingUtils.screenFillView(this)
        initOnClick()

        initRecyclerview(binding.rvgetStarted)
        apiObserver()
        viewModel.getResponseStylesFunction()
        binding.tvScenarioText.text = "Scenario: "+BindingUtils.choosenSatuation
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
                        Constants.GET_EMPATHY_OPTIONS_RESPONSE_STYLES_API -> {
                            try {
                                val getModelScenarious: PersonResponseModel? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (getModelScenarious?.success == true) {
                                    val itemListData = ArrayList<StartPracticingModel>()
                                    itemListData.add(
                                        StartPracticingModel(
                                            "Supportive", "#E9FFFF", R.drawable.hand_icon_blue
                                        )
                                    )
                                    itemListData.add(
                                        StartPracticingModel(
                                            "Direct", "#FFFFFF", R.drawable.dart_icon
                                        )
                                    )
                                    itemListData.add(
                                        StartPracticingModel(
                                            "Balanced", "#FFEEEE", R.drawable.equality
                                        )
                                    )
                                    itemListData.add(
                                        StartPracticingModel(
                                            "Challenging", "#F0EBFF", R.drawable.search
                                        )
                                    )
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

                        Constants.POST_EMPATHY_INTERACTIONS_API -> {
                            try {
                                val postPersonResponseModel: PostPersonResponseModel? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (postPersonResponseModel?.success == true) {
                                    ChatActivity.scenarioId =
                                        postPersonResponseModel.interaction._id
                                    startActivity(Intent(this, ChatActivity::class.java))
                                } else {
                                    postPersonResponseModel?.message?.let { it1 ->
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

    private fun initRecyclerview(view: RecyclerView) {
        adapter = SimpleRecyclerViewAdapter(
            R.layout.start_practicing_item_2, BR.bean
        ) { v, m, pos ->
            when (v.id) {
                R.id.mainLayout -> {
                    CommonFunctionClass.singleSelectionRV(
                        list = adapter.list,
                        selectedId = m.titleValue,
                        getId = { it.titleValue },
                        isSelectedGetter = { it.iselected == true },
                        isSelectedSetter = { item, isSelected, selectedModel ->
                            item.iselected = isSelected
                            if (isSelected) {
                                BindingUtils.interactionModelPost?.responseStyle =
                                    selectedModel?.titleValue ?: ""
                            }
                        },
                        notifyChanged = { adapter.notifyItemChanged(it) })
                }
            }
        }
        view.adapter = adapter
        view.isNestedScrollingEnabled = true
    }

    private fun initOnClick() {
        viewModel.onClick.observe(this) {
            when (it?.id) {
                R.id.button -> {
                    if (BindingUtils.interactionModelPost?.responseStyle.toString().trim()
                            .isEmpty()
                    ) {
                        showErrorToast("Please select scenario coach")
                        return@observe
                    }
                    startActivity(Intent(this, WantToTalk::class.java))
                }

                R.id.back_button -> {
                    finish()
                }
            }
        }
    }
}
