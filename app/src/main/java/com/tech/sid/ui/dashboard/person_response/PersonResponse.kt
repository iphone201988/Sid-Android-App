package com.tech.sid.ui.dashboard.person_response

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
import com.tech.sid.databinding.ActivityPersonResponseBinding
import com.tech.sid.databinding.ActivityStartPracticingBinding
import com.tech.sid.ui.dashboard.chat_screen.ChatActivity
import com.tech.sid.ui.dashboard.choose_situation.ChooseSituationVm
import com.tech.sid.ui.onboarding_ques.OnboardingQuestion
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PersonResponse : BaseActivity<ActivityPersonResponseBinding>() {
    private val viewModel: PersonResponseVm by viewModels()
    override fun getLayoutResource(): Int {
        return R.layout.activity_person_response
    }
    override fun getViewModel(): BaseViewModel {
        return viewModel
    }
    override fun onCreateView() {
        BindingUtils.screenFillView(this)
        initOnClick()
    }
    private fun initOnClick() {
        viewModel.onClick.observe(this) {
            when (it?.id) {
                R.id.button -> {
                    startActivity(Intent(this, ChatActivity::class.java))
                }
                R.id.back_button -> {
                    finish()
                }
            }
        }
    }
}
