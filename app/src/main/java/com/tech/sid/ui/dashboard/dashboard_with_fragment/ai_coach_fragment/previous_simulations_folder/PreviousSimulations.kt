package com.tech.sid.ui.dashboard.dashboard_with_fragment.ai_coach_fragment.previous_simulations_folder

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
import com.tech.sid.databinding.ActivityDashboardBinding
import com.tech.sid.databinding.ActivityPreviousSimulationsBinding
import com.tech.sid.ui.dashboard.dashboard_with_fragment.DashboardActivityVm
import com.tech.sid.ui.dashboard.dashboard_with_fragment.FragmentNavRoute
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class PreviousSimulations : BaseActivity<ActivityPreviousSimulationsBinding>() {
    private val viewModel: PreviousSimulationsVm by viewModels()


    override fun getLayoutResource(): Int {
        return R.layout.activity_previous_simulations
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
                R.id.back_button -> {
                    finish()
                }
            }
        }
    }

}