package com.tech.sid.ui.dashboard.simulation_insights

import android.content.Intent
import android.view.View
import androidx.activity.viewModels
import com.tech.sid.R
import com.tech.sid.base.BaseActivity
import com.tech.sid.base.BaseViewModel
import com.tech.sid.base.utils.BindingUtils
import com.tech.sid.base.utils.Status
import com.tech.sid.base.utils.showErrorToast
import com.tech.sid.data.api.Constants
import com.tech.sid.databinding.ActivitySimulationInsightsBinding
import com.tech.sid.ui.dashboard.dashboard_with_fragment.DashboardActivity
import com.tech.sid.ui.dashboard.journal_folder.TodayJournal
import com.tech.sid.ui.dashboard.start_practicing.StartPracticing
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SimulationInsights : BaseActivity<ActivitySimulationInsightsBinding>() {
    private val viewModel: SimulationInsightsVm by viewModels()
    override fun getLayoutResource(): Int {
        return R.layout.activity_simulation_insights
    }

    companion object {
        var simulationInsightsId: String = ""
        var isChatRoute: Boolean = false
        var simulationInsightsModel: SimulationInsightsModel? = null
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView() {
        if (!isChatRoute) {
            binding.startJournalingLL.visibility = View.GONE
            binding.button.visibility = View.GONE
            binding.llGoToHome.visibility=View.GONE
        }
        BindingUtils.screenFillView(this)
        initOnClick()
        apiObserver()
        if (simulationInsightsId.isNotEmpty()) {
            viewModel.getSimulationInsights(simulationInsightsId)
        } else {
            if (simulationInsightsModel != null) {
                binding.bean = simulationInsightsModel
                simulationInsightsModel = null
            }

        }

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
                        Constants.INSIGHTS_ACCOUNT -> {
                            try {
                                val getModelStartPracticing: SimulationInsightsResponse? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (getModelStartPracticing != null) {
                                    if(getModelStartPracticing.simulationInsightFound) {
                                        binding.bean = getModelStartPracticing.insight
                                    }else{
                                        if (simulationInsightsId.isNotEmpty()) {
                                            viewModel.postSimulationInsights(simulationInsightsId)
                                        }
                                    }
                                } else {

//                                    getModelStartPracticing.message?.let { it1 ->
//                                        showErrorToast(
//                                            it1
//                                        )
//                                    }
                                    return@observe
                                }
                            } catch (e: Exception) {
                                showErrorToast(e.toString())
                            }
                        }


                    }
                }

                Status.ERROR -> {
                    runOnUiThread(Runnable {
                        binding.mainLayoutLL.visibility = View.GONE
                    })
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
                R.id.start_journalingLL -> {
                    startActivity(Intent(this, TodayJournal::class.java))

                }

                R.id.button -> {
//                    val intent = Intent(this, TodayJournal::class.java)
//                    intent.putExtra("journalId", 123) // Integer data
//                    intent.putExtra("journalTitle", "My Daily Thoughts") // String data
//                    startActivity(intent)
                    startActivity(Intent(this, StartPracticing::class.java))
                }

                R.id.llGoToHome -> {
                    val intent =Intent(this, DashboardActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                }

                R.id.back_button -> {
                    finish()
                }

            }
        }
    }
}