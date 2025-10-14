package com.tech.sid.ui.onboarding_ques.describe

import android.annotation.SuppressLint
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
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
        initEventListeners()

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

    @SuppressLint("ClickableViewAccessibility")
    private fun initEventListeners() {
//        binding.note.addTextChangedListener(object : TextWatcher {
//            override fun afterTextChanged(s: Editable?) {
//                val words =
//                    s?.toString()?.trim()?.split("\\s+".toRegex())?.filter { it.isNotEmpty() }
//                        ?: listOf()
//                val wordCount = words.size
//                val remainingWords = 500 - wordCount
//                binding.tvWordCount.text = "$remainingWords/500"
//                if (wordCount > 500) {
//                    showToast("Maximum word limit reached")
//                    val limitedText = words.take(500).joinToString(" ")
//                    binding.note.setText(limitedText)
//                    binding.note.setSelection(limitedText.length)
//                }
//            }
//
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
//        })

        binding.note.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val text = s?.toString() ?: ""
                val charCount = text.length
                val maxChars = 500
                val remainingChars = maxChars - charCount

                // Update counter
                binding.tvWordCount.text = "$remainingChars/$maxChars"

                // Limit input to maxChars
                if (charCount > maxChars) {
                    showToast("Maximum character limit reached")
                    val limitedText = text.take(maxChars)
                    binding.note.setText(limitedText)
                    binding.note.setSelection(limitedText.length)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.note.setOnTouchListener { v, _ ->
            v.parent.requestDisallowInterceptTouchEvent(true)
            false
        }
    }

}