package com.tech.sid.ui.dashboard.result_screen


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
import com.tech.sid.databinding.ActivityResultBinding
import com.tech.sid.ui.dashboard.next_best_step.NextBestStep
import com.tech.sid.ui.onboarding_ques.PersonalityAnalysisData
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ResultActivity : BaseActivity<ActivityResultBinding>() {
    private val viewModel: ResultActivityVm by viewModels()
    override fun getLayoutResource(): Int {
        return R.layout.activity_result
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    companion object {
        var onboardingResultModel: PersonalityAnalysisData? = null
    }


    override fun onCreateView() {
        BindingUtils.screenFillView(this)
        initOnClick()
        binding.bean = onboardingResultModel
        binding.executePendingBindings()
        apiObserver()
        val data = intent.getStringExtra("from")
        if (data != null && data == "profile") {
            viewModel.getProfileApi()
            binding.button.visibility = View.GONE
        } else {
            loadChatGraph()
            binding.button.visibility = View.VISIBLE
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
                        Constants.GET_PROFILE -> {
                            try {
                                val loginModel: GetProfileResponse? =
                                    BindingUtils.parseJson(it.data.toString())
                                binding.bean = loginModel?.personalityAnalysis
                                onboardingResultModel = loginModel?.personalityAnalysis
                                loadChatGraph()

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
                R.id.button -> {
                    startActivity(Intent(this, NextBestStep::class.java))
                }

                R.id.back_button -> {
                    finish()
                }

            }
        }
    }

    private fun loadChatGraph() {
        /** THIS IS IS A CUSTOM CLASS RadarChart*/
        val labels = listOf(
            "Openness",
            "Agreeableness",
            "Extraversion",
            "Conscientiousness",
            "Neuroticism",
            "Cognitive Style"
        )
        val traits = onboardingResultModel?.traits ?: return
        val maxScore = 10.5f
        val openness = ((traits.openness ?: 0.0).toFloat() + 0.5f)
        val agreeableness = ((traits.agreeableness ?: 0.0).toFloat() + 0.5f)
        val gratitude = ((traits.gratitude ?: 0.0).toFloat() + 0.5f)
        val conscientiousness = ((traits.conscientiousness ?: 0.0).toFloat() + 0.5f)
        val neuroticism = ((traits.neuroticism ?: 0.0).toFloat() + 0.5f)
        val cognitiveStyle = ((traits.cognitiveStyle ?: 0.0).toFloat() + 0.5f)
        val values: List<Float> = listOf(
            openness, agreeableness, gratitude, conscientiousness, neuroticism, cognitiveStyle
        )

        binding.classificationChart.setChartData(labels, values, "Usage", maxScore)


        /**
         * THIS IS CODE FOR MP CHAT
         *  val labels = listOf("Openness", "Agreeableness", "Gratitude", "Conscientiousness", "Neuroticism","Cognitive Style")
        val values = listOf(10f, 10f, 10f, 10f, 10f, 10f) // Max still 10f

        val entries = values.map { RadarEntry(it) }

        val maxValue = values.maxOrNull() ?: 5f
        // Create dataset
        val dataSet = RadarDataSet(entries, "Usage").apply {
        //              color = Color.CYAN
        //            fillColor = Color.CYAN
        setDrawFilled(true)
        // fillDrawable = ContextCompat.getDrawable(this@PerfumeInfoActivity, R.drawable.radar_gradient)
        fillAlpha = 180
        lineWidth = 2f
        valueTextColor = Color.WHITE
        valueTextSize = 12f
        setDrawValues(false)
        }

        // Prepare data
        val data = RadarData(dataSet)
        binding.classificationChart.data = data

        val typeface = ResourcesCompat.getFont(this, R.font.dm_sans_medium)
        binding.classificationChart.xAxis?.typeface = typeface
        binding.classificationChart.isRotationEnabled = false

        binding.classificationChart.setExtraOffsets(0f, 0f, 0f, 0f) // Removes internal chart offsets

        binding.classificationChart.setPadding(0, 0, 0, 0)
        // Configure axis labels
        binding.classificationChart.xAxis?.apply {
        valueFormatter = IndexAxisValueFormatter(labels)
        textSize = 10f
        textColor = ContextCompat.getColor(this@ResultActivity, R.color.text_color_splash)
        }

        // Y-Axis (optional: hide labels)
        binding.classificationChart.yAxis?.apply {
        setDrawLabels(false)
        axisMinimum = 0f
        axisMaximum = maxValue // Set to highest value
        labelCount = 3
        }

        // Chart styling
        binding.classificationChart.description?.isEnabled = false
        binding.classificationChart.legend?.isEnabled = false
        binding.classificationChart.webColor = ContextCompat.getColor(this, R.color.graph_boarder_width_line)
        binding.classificationChart.webLineWidth = 1f
        binding.classificationChart.webColorInner = ContextCompat.getColor(this, android.R.color.holo_blue_dark)
        binding.classificationChart.webLineWidthInner = 1f
        binding.classificationChart.setBackgroundColor(
        ContextCompat.getColor(
        this,
        R.color.transparent
        )
        )

        binding.classificationChart.renderer = CircularWebRadarChartRenderer(
        binding.classificationChart,
        binding.classificationChart.animator!!,
        binding.classificationChart.viewPortHandler!!
        )

        binding.classificationChart.invalidate()*/
    }

}