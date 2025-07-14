package com.tech.sid.ui.dashboard.choose_situation

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
import com.tech.sid.databinding.ActivityChooseSituationBinding
import com.tech.sid.databinding.ActivityStartPracticingBinding
import com.tech.sid.ui.dashboard.person_response.PersonResponse
import com.tech.sid.ui.dashboard.start_practicing.StartPracticingVm
import com.tech.sid.ui.dashboard.want_to_talk.WantToTalk
import com.tech.sid.ui.onboarding_ques.OnboardingQuestion
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChooseSituation : BaseActivity<ActivityChooseSituationBinding>() {
    private val viewModel: ChooseSituationVm by viewModels()
    override fun getLayoutResource(): Int {
        return R.layout.activity_choose_situation
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
//                    startActivity(Intent(this, PersonResponse::class.java))
                    startActivity(Intent(this, WantToTalk::class.java))
                }
                R.id.back_button -> {
                    finish()
                }
            }
        }
    }
}
