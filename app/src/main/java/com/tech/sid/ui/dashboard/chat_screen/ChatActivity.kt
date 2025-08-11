package com.tech.sid.ui.dashboard.chat_screen

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.tech.sid.CommonFunctionClass
import com.tech.sid.R
import com.tech.sid.base.BaseActivity
import com.tech.sid.base.BaseViewModel
import com.tech.sid.base.utils.BindingUtils
import com.tech.sid.base.utils.Status
import com.tech.sid.base.utils.showErrorToast
import com.tech.sid.data.api.Constants
import com.tech.sid.databinding.ActivityChatBinding
import com.tech.sid.databinding.ActivityPersonResponseBinding
import com.tech.sid.ui.dashboard.person_response.PersonResponseModel
import com.tech.sid.ui.dashboard.person_response.PersonResponseVm
import com.tech.sid.ui.dashboard.person_response.PostPersonResponseModel
import com.tech.sid.ui.dashboard.simulation_insights.SimulationInsights
import com.tech.sid.ui.onboarding_ques.StartPracticingModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ChatActivity : BaseActivity<ActivityChatBinding>() {
    private val viewModel: ChatActivityVm by viewModels()
    override fun getLayoutResource(): Int {
        return R.layout.activity_chat
    }

    companion object {
        var scenarioId: String? = ""
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView() {
        BindingUtils.screenFillView(this)
        initOnClick()
        chatAdapter()
        apiObserver()
    }

    private fun apiObserver() {
        viewModel.observeCommon.observe(this) {
            when (it?.status) {
                Status.LOADING -> {
//                    showLoading("Loading")
                }

                Status.SUCCESS -> {

                    hideLoading()
                    when (it.message) {

                        Constants.POST_CHAT_API -> {
                            try {
                                val chatApiResposeModelModel: ChatApiResposeModel? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (chatApiResposeModelModel?.success == true) {

                                    /*     adapter.setListData(
                                             ChatMessage(
                                                 chatApiResposeModelModel.newMessage,
                                                 false
                                             )
                                         )*/
                                    binding.sendButtom.isEnabled = true
                                    CommonFunctionClass.updateItemInRecyclerView(
                                        recyclerView = binding.chatRv,
                                        list = adapter.getList(),
                                        position = adapter.getList().size - 1,
                                        updateItem = { /*it.message = "Updated message!"*/ },
                                        updateView = { holder, item ->
                                            when (holder) {
//                                            is ChatAdapter.SentMessageViewHolder -> holder.messageText.text = item.message
                                                is ChatAdapter.ReceivedMessageViewHolder -> {
                                                    holder.loadingDotsView.visibility = View.GONE
                                                    holder.messageText.visibility = View.VISIBLE
                                                    holder.messageText.text =
                                                        chatApiResposeModelModel.newMessage
                                                }
                                            }
                                        },
                                        notifyFallback = { /*adapter.notifyItemChanged(it) */ }
                                    )
                                    Handler(Looper.getMainLooper()).postDelayed({
                                        SimulationInsights.isChatRoute = true
                                        SimulationInsights.simulationInsightsId = scenarioId?:""
                                        startActivity(Intent(this, SimulationInsights::class.java))
                                    }, 100)


                                } else {
                                    chatApiResposeModelModel?.message?.let { it1 ->
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

    var adapter = ChatAdapter()

    private fun chatAdapter() {
//        val messages = listOf(
//            ChatMessage("Hey there! How's your day going so far?", false),
//            ChatMessage("Hiii, Doing Good", true),
//            )
        binding.chatRv.adapter = adapter
    }

    private fun initOnClick() {
        viewModel.onClick.observe(this) {
            when (it?.id) {
                R.id.sendButtom -> {
                    if (binding.enterEmail.text.toString().trim().isEmpty()) {
                        showErrorToast("please enter the message")
                        return@observe
                    }
                    val data: HashMap<String, Any> = hashMapOf(
                        "message" to binding.enterEmail.text.toString().trim(),
                        "simulationId" to scenarioId!!,
                    )
                    binding.sendButtom.isEnabled = false
                    adapter.setListData(
                        ChatMessage(
                            binding.enterEmail.text.toString().trim(),
                            true
                        )
                    )
                    adapter.setListData(
                        ChatMessage(
                            "",
                            false
                        )
                    )
                    CommonFunctionClass.updateItemInRecyclerView(
                        recyclerView = binding.chatRv,
                        list = adapter.getList(),
                        position = adapter.getList().size - 1,
                        updateItem = { /*it.message = "Updated message!"*/ },
                        updateView = { holder, item ->
                            Log.i("dfgjkkdlfjgkldfjglkfhusdfmlshijdh", "initOnClick: ")
                            when (holder) {

//                                is ChatAdapter.SentMessageViewHolder -> holder.messageText.text = item.message
                                is ChatAdapter.ReceivedMessageViewHolder -> {
                                    holder.loadingDotsView.visibility = View.VISIBLE
                                    holder.messageText.visibility = View.GONE
                                }
                            }
                        },
                        notifyFallback = { /*adapter.notifyItemChanged(it) */ }
                    )


                    binding.enterEmail.text?.clear()

                    viewModel.postChatFunction(data)

//                    startActivity(Intent(this, SimulationInsights::class.java))
                }

                R.id.back_button -> {
                    finish()
                }
            }
        }
    }
}
