package com.tech.sid.ui.onboarding_ques.describe

import android.content.Intent
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.tech.sid.R
import com.tech.sid.base.BaseActivity
import com.tech.sid.base.BaseViewModel
import com.tech.sid.base.utils.BindingUtils
import com.tech.sid.base.utils.Status
import com.tech.sid.base.utils.showErrorToast
import com.tech.sid.data.api.Constants
import com.tech.sid.databinding.ActivityDescribeYourScenarioBinding
import com.tech.sid.ui.dashboard.choose_situation.ModelGetScenariousOption
import com.tech.sid.ui.dashboard.person_response.PersonResponse
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DescribeYourScenarioActivity : BaseActivity<ActivityDescribeYourScenarioBinding>() {
    private val viewModel: DescribeScenarioVm by viewModels()

    override fun getLayoutResource(): Int {
        return R.layout.activity_describe_your_scenario
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView() {
        initOnClick()
        initView()
    }

    private fun initView() {
        apiObserver()
        val data = intent.getStringExtra("momentId")
        if (data != null) {
            BindingUtils.interactionModelPost?.momentId = data
            viewModel.getAIEmpathyCoachFunction(data)

        } else {
            viewModel.getAIEmpathyCoachFunction(BindingUtils.interactionModelPost?.momentId.toString())
        }

    }

    private fun initOnClick() {
        viewModel.onClick.observe(this, Observer {
            when (it?.id) {
                R.id.button -> {
                    if (binding.note.text.toString().trim().isNotEmpty()) {
                        BindingUtils.choosenSatuation = binding.note.text.toString().trim()
                        if (intent.getStringExtra("momentId") != null) {
                            BindingUtils.interactionModelPost?.momentId =
                                intent.getStringExtra("momentId").toString()
                            BindingUtils.interactionModelPost?.customMomentText =
                                binding.note.text.toString().trim()
                            startActivity(Intent(this, PersonResponse::class.java))

                        } else {
                            BindingUtils.interactionModelPost?.customScenarioText =
                                binding.note.text.toString().trim()
                            startActivity(Intent(this, PersonResponse::class.java))
                        }
                    } else {
                        showToast("Please enter your scenario")
                    }
                }

                R.id.back_button -> {
                    finish()
                }
            }
        })

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
                        Constants.GET_EMPATHY_OPTIONS_SCENARIOS_API -> {
                            try {
                                val getModelScenarious: ModelGetScenariousOption? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (getModelScenarious?.success == true) {
                                    BindingUtils.interactionModelPost?.scenarioId =
                                        getModelScenarious.data[0]._id

                                } else {
                                    getModelScenarious?.message?.let { it1 ->
                                        showErrorToast(
                                            it1
                                        )
                                    }
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

}