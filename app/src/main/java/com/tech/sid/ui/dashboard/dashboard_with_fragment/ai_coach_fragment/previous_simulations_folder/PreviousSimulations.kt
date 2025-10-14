package com.tech.sid.ui.dashboard.dashboard_with_fragment.ai_coach_fragment.previous_simulations_folder

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.tech.sid.R
import com.tech.sid.base.BaseActivity
import com.tech.sid.base.BaseViewModel
import com.tech.sid.base.utils.BindingUtils
import com.tech.sid.base.utils.Status
import com.tech.sid.base.utils.showErrorToast
import com.tech.sid.data.api.Constants
import com.tech.sid.databinding.ActivityDashboardBinding
import com.tech.sid.databinding.ActivityPreviousSimulationsBinding
import com.tech.sid.ui.dashboard.dashboard_with_fragment.DashboardActivityVm
import com.tech.sid.ui.dashboard.dashboard_with_fragment.FragmentNavRoute
import com.tech.sid.ui.dashboard.dashboard_with_fragment.home_fragment.HomeGraphModel
import com.tech.sid.ui.dashboard.dashboard_with_fragment.home_fragment.HomeModel
import com.tech.sid.ui.dashboard.dashboard_with_fragment.insights_fragment.InsightsModel
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
        viewModel.simulationsFunction()
        apiObserver()
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

                        Constants.GET_SIMULATION_ACCOUNT -> {
                            try {
                                val signUpModel: SimulationModel? =
                                    BindingUtils.parseJson(it.data.toString())
                                if(signUpModel!=null){
                                    binding.bean = signUpModel

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