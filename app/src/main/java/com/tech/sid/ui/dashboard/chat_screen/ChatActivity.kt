package com.tech.sid.ui.dashboard.chat_screen

import android.content.Intent
import android.os.Bundle
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
import com.tech.sid.databinding.ActivityPersonResponseBinding
import com.tech.sid.ui.dashboard.person_response.PersonResponseVm
import com.tech.sid.ui.dashboard.simulation_insights.SimulationInsights
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ChatActivity : BaseActivity<ActivityChatBinding>() {
    private val viewModel: ChatActivityVm by viewModels()
    override fun getLayoutResource(): Int {
        return R.layout.activity_chat
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView() {
        BindingUtils.screenFillView(this)
        initOnClick()
        chatAdapter()
    }

    private fun chatAdapter() {
        val messages = listOf(
            ChatMessage("Hey there! How's your day going so far?", false),
            ChatMessage("Hiii, Doing Good", true),
            )
        val adapter = ChatAdapter(messages)
        binding.chatRv.adapter = adapter
    }

    private fun initOnClick() {
        viewModel.onClick.observe(this) {
            when (it?.id) {
                R.id.sendButtom -> {
                    startActivity(Intent(this, SimulationInsights::class.java))
                }

                R.id.back_button -> {
                    finish()
                }
            }
        }
    }
}
