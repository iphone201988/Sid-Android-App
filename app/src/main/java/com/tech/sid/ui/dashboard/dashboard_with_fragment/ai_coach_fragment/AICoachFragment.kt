package com.tech.sid.ui.dashboard.dashboard_with_fragment.ai_coach_fragment

import android.content.Intent
import android.view.View
import androidx.fragment.app.viewModels
import com.tech.sid.R
import com.tech.sid.base.BaseFragment
import com.tech.sid.base.BaseViewModel
import com.tech.sid.databinding.FragmentAICoachBinding
import com.tech.sid.ui.dashboard.dashboard_with_fragment.ai_coach_fragment.previous_simulations_folder.PreviousSimulations
import com.tech.sid.ui.dashboard.dashboard_with_fragment.change_password.ChangePassword
import com.tech.sid.ui.dashboard.dashboard_with_fragment.notification.NotificationActivity
import com.tech.sid.ui.dashboard.dashboard_with_fragment.profile_edit.EditProfile
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AICoachFragment : BaseFragment<FragmentAICoachBinding>() {
    private val viewModel: AICoachFragmentVm by viewModels()
    override fun onCreateView(view: View) {
        initOnClick()
    }
    override fun getLayoutResource(): Int {
        return R.layout.fragment_a_i_coach
    }
    override fun getViewModel(): BaseViewModel {
        return viewModel
    }
    private fun initOnClick() {
        viewModel.onClick.observe(requireActivity()) {
            when (it?.id) {
                R.id.button -> {
                    startActivity(Intent(requireActivity(), PreviousSimulations::class.java))
                }
            }
        }
    }
}
