package com.tech.sid.ui.dashboard.want_to_talk

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
import com.tech.sid.databinding.ActivityWantToTalkBinding
import com.tech.sid.databinding.RvWantToTalkItemViewBinding
import com.tech.sid.ui.dashboard.chat_screen.ChatActivity
import com.tech.sid.ui.dashboard.person_response.PostPersonResponseModel
import com.tech.sid.ui.onboarding_ques.WantToTalkModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class WantToTalk : BaseActivity<ActivityWantToTalkBinding>() {
    private val viewModel: WantToTalkVm by viewModels()
    private lateinit var adapter: SimpleRecyclerViewAdapter<WantToTalkModel, RvWantToTalkItemViewBinding>
    override fun getLayoutResource(): Int {
        return R.layout.activity_want_to_talk
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView() {
        BindingUtils.screenFillView(this)
        initOnClick()
        initRecyclerview(binding.rvgetStarted)
        apiObserver()
        viewModel.getWantToTalkFunction()
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
                        Constants.GET_RELATION_API -> {
                            try {
                                val getModelClassWatchApi: ModelClassWatchApi? =
                                    BindingUtils.parseJson(it.data.toString())
                                val itemListData = ArrayList<WantToTalkModel>()
                                if (getModelClassWatchApi?.relations == null || getModelClassWatchApi.relations.isEmpty()) {
                                    return@observe
                                }

                                val colors = listOf(
                                    "#FFEEEE",
                                    "#F0EBFF",
                                    "#E9FFFF",
                                    "#FFFFFF",
                                )
                                for (i in getModelClassWatchApi.relations.indices) {

                                    val colorIndex = i % colors.size
                                    itemListData.add(
                                        WantToTalkModel(
                                            titleValue = getModelClassWatchApi.relations[i],
                                            colorsValue = colors[colorIndex],
                                        )
                                    )
                                }
                                adapter.list = itemListData
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
            R.layout.rv_want_to_talk_item_view, BR.bean
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
                                BindingUtils.interactionModelPost?.relation =
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
                    if (binding.somethingIMGoingThrough.text.toString().trim().isNotEmpty()) {
                        BindingUtils.interactionModelPost?.relation =
                            binding.somethingIMGoingThrough.text.toString().trim()
                    } else {
                        showErrorToast("please enter name or role")
                        return@observe
                    }
                    if (BindingUtils.interactionModelPost?.relation.toString().trim().isEmpty()) {
                        showErrorToast("please select scenario coach")
                        return@observe
                    }

                    val data: HashMap<String, Any> = hashMapOf(
                        "momentId" to (BindingUtils.interactionModelPost?.momentId ?: ""),
                        "relation" to (BindingUtils.interactionModelPost?.relation ?: ""),
                        "responseStyle" to (BindingUtils.interactionModelPost?.responseStyle ?: ""),
                        "scenarioId" to (BindingUtils.interactionModelPost?.scenarioId ?: ""),
                        "customMomentText" to (BindingUtils.interactionModelPost?.customMomentText
                            ?: ""),
                        "customScenarioText" to (BindingUtils.interactionModelPost?.customScenarioText
                            ?: "")
                    )
                    viewModel.postEmpathyInteractionsFunction(data)
                }

                R.id.back_button -> {
                    finish()
                }
            }
        }
    }

    data class ModelClassWatchApi(
        val __v: Int, val _id: String, val relations: List<String>, val type: Int
    )
}