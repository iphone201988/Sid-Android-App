package com.tech.sid.ui.dashboard.dashboard_with_fragment.ai_coach_fragment
import android.view.View
import androidx.fragment.app.viewModels
import com.tech.sid.R
import com.tech.sid.base.BaseFragment
import com.tech.sid.base.BaseViewModel
import com.tech.sid.databinding.FragmentAICoachBinding
import dagger.hilt.android.AndroidEntryPoint
@AndroidEntryPoint
class AICoachFragment : BaseFragment<FragmentAICoachBinding>() {
    private val viewModel: AICoachFragmentVm by viewModels()
    override fun onCreateView(view: View) {

    }

    override fun getLayoutResource(): Int {
        return R.layout.fragment_a_i_coach
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }
}
