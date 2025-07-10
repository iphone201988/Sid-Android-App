package com.tech.sid.ui.dashboard.result_screen


import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.github.mikephil.charting.data.RadarData
import com.github.mikephil.charting.data.RadarDataSet
import com.github.mikephil.charting.data.RadarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.tech.sid.R
import com.tech.sid.base.BaseActivity
import com.tech.sid.base.BaseViewModel
import com.tech.sid.base.utils.BindingUtils

import com.tech.sid.databinding.ActivityResultBinding
import com.tech.sid.ui.dashboard.CreatingBaseLineVm
import com.tech.sid.ui.dashboard.next_best_step.NextBestStep
import com.tech.sid.ui.onboarding_ques.OnboardingQuestion
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
    override fun onCreateView() {
        BindingUtils.screenFillView(this)
        initOnClick()
        loadChatGraph()
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
        val labels = listOf("Openness", "Agreeableness", "Gratitude", "Conscientiousness", "Neuroticism", "Cognitive Style", "Cognitive Style")
        val values = listOf(2f, 5f, 2f, 6f, 7f, 10f,5f)
        binding.classificationChart.setChartData(labels, values)

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