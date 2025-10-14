package com.tech.sid.ui.dashboard.next_best_step
import android.content.Intent
import androidx.activity.viewModels
import com.tech.sid.R
import com.tech.sid.base.BaseActivity
import com.tech.sid.base.BaseViewModel
import com.tech.sid.base.utils.BindingUtils
import com.tech.sid.databinding.ActivityNextBestStepBinding
import com.tech.sid.ui.dashboard.dashboard_with_fragment.DashboardActivity
import com.tech.sid.ui.dashboard.journal_folder.TodayJournal
import com.tech.sid.ui.dashboard.start_practicing.StartPracticing
import com.tech.sid.ui.onboarding_ques.OnboardingQuestion
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NextBestStep : BaseActivity<ActivityNextBestStepBinding>() {
    private val viewModel: NextBestStepVm by viewModels()
    override fun getLayoutResource(): Int {
        return R.layout.activity_next_best_step
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
                R.id.start_journalingLL -> {
                    val intent =Intent(this, DashboardActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
//                    startActivity(Intent(this, TodayJournal::class.java))
                }    R.id.button -> {
                    startActivity(Intent(this, StartPracticing::class.java))
                }

                R.id.back_button -> {
                    finish()
                }
            }
        }
    }
}
