package com.tech.sid.ui.dashboard.simulation_insights

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.gson.Gson
import com.tech.sid.CommonFunctionClass
import com.tech.sid.R
import com.tech.sid.base.BaseActivity
import com.tech.sid.base.BaseViewModel
import com.tech.sid.base.utils.BindingUtils
import com.tech.sid.base.utils.Status
import com.tech.sid.base.utils.showErrorToast
import com.tech.sid.data.api.Constants
import com.tech.sid.databinding.ActivityChatBinding
import com.tech.sid.databinding.ActivitySimulationInsightsBinding
import com.tech.sid.ui.dashboard.chat_screen.ChatActivityVm
import com.tech.sid.ui.dashboard.chat_screen.ChatAdapter
import com.tech.sid.ui.dashboard.chat_screen.ChatMessage
import com.tech.sid.ui.dashboard.dashboard_with_fragment.DashboardActivity
import com.tech.sid.ui.dashboard.journal_folder.TodayJournal
import com.tech.sid.ui.dashboard.result_screen.ResultActivity
import com.tech.sid.ui.dashboard.start_practicing.ModelStartPracticing
import com.tech.sid.ui.dashboard.subscription_package.SubscriptionActivity
import com.tech.sid.ui.onboarding_ques.OnboardingQuestion
import com.tech.sid.ui.onboarding_ques.StartPracticingModel
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
        var simulationInsightsModel: SimulationInsightsModel ?= null
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView() {
        if(!isChatRoute){
            binding.startJournalingLL.visibility= View.GONE
            binding.button.visibility= View.GONE
        }
        BindingUtils.screenFillView(this)
        initOnClick()
        apiObserver()
        if(simulationInsightsId.isNotEmpty()){

            viewModel.getSimulationInsights(simulationInsightsId)
        }
        else{
            if(simulationInsightsModel!=null){
                binding.bean = simulationInsightsModel
                simulationInsightsModel=null
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
                                val getModelStartPracticing: SimulationInsightsModel? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (getModelStartPracticing != null) {
                                    binding.bean = getModelStartPracticing
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
                        binding.mainLayoutLL.visibility=View.GONE
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
                R.id.button   -> {
//                    val intent = Intent(this, TodayJournal::class.java)
//                    intent.putExtra("journalId", 123) // Integer data
//                    intent.putExtra("journalTitle", "My Daily Thoughts") // String data
//                    startActivity(intent)
                    startActivity(Intent(this, DashboardActivity::class.java))
                }

                R.id.back_button -> {
                    finish()
                }

            }
        }
    }
}