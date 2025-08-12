package com.tech.sid.ui.dashboard.dashboard_with_fragment.insights_fragment

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat

import com.tech.sid.R

import androidx.fragment.app.viewModels

import com.tech.sid.base.BaseFragment
import com.tech.sid.base.BaseViewModel
import com.tech.sid.base.utils.BindingUtils
import com.tech.sid.base.utils.Status
import com.tech.sid.base.utils.showErrorToast
import com.tech.sid.data.api.Constants
import com.tech.sid.databinding.FragmentHomeBinding
import com.tech.sid.databinding.FragmentInsightsBinding
import com.tech.sid.ui.auth.AuthModelLogin
import com.tech.sid.ui.dashboard.dashboard_with_fragment.home_fragment.HomeFragmentVm
import com.tech.sid.ui.dashboard.dashboard_with_fragment.home_fragment.HomeGraphModel
import com.tech.sid.ui.dashboard.dashboard_with_fragment.home_fragment.HomeModel
import com.tech.sid.ui.dashboard.dashboard_with_fragment.journal_fragment.JournalModel
import com.tech.sid.ui.dashboard.dashboard_with_fragment.notification.NotificationActivity
import com.tech.sid.ui.dashboard.journal_folder.TodayJournal
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class InsightsFragment : BaseFragment<FragmentInsightsBinding>() {
    private val viewModel: InsightsFragmentVm by viewModels()
    private var viewRoot: View? = null
    var valueProfile: AuthModelLogin? = null
    override fun onCreateView(view: View) {
        viewRoot = view
//        val data = listOf(0, 5, 10, 10, 6, 8, 0)
//        val maxY = 10
//        binding.graphView.setGraphData(data, maxY)
        valueProfile = sharedPrefManager.getProfileData()
        if (valueProfile != null) {
            binding.apply {
                textView.text =
                    "Hi ,${valueProfile?.user?.firstName} ${valueProfile?.user?.lastName}"
                timeDate.text = getCurrentFormattedDate()
            }
        }

        apiObserver()
        initOnClick()
        viewModel.insightsFunction()
    }
    private fun initOnClick() {
        viewModel.onClick.observe(viewLifecycleOwner) {
            when (it?.id) {
                R.id.bellNotification -> {

                    startActivity(Intent(requireActivity(), NotificationActivity::class.java))

                }

            }
        }
    }
    fun updateMoodUI(context: Context, selectedMood: String) {
        val moods = listOf(
            "Thriving" to Pair(R.id.ThrivingLL, R.id.Thrivingtv),
            "Grateful" to Pair(R.id.GratefulLL, R.id.GratefulTv),
            "Drifting" to Pair(R.id.DriftingLL, R.id.DriftingTv),
            "Low" to Pair(R.id.LowLL, R.id.LowTv),
            "Overwhelmed" to Pair(R.id.OverwhelmedLL, R.id.OverwhelmedTV),
            "Log My mood" to Pair(R.id.LogMymoodLL, R.id.LogMyMoodTv)
        )

        // Gradient background
        val gradientDrawable = GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT,
            intArrayOf(
                Color.parseColor("#CAB8FF"),
                Color.parseColor("#B5EAEA")
            )
        )
        gradientDrawable.cornerRadius = 0f

        for ((mood, ids) in moods) {
            val linearLayout = viewRoot?.findViewById<LinearLayout>(ids.first)
            val textView = viewRoot?.findViewById<TextView>(ids.second)

            if (mood.equals(selectedMood, ignoreCase = true)) {
                linearLayout?.background = gradientDrawable
                textView?.setTextColor(ContextCompat.getColor(context, R.color.white))

                val typeface = ResourcesCompat.getFont(context, R.font.inter_semi_bold)
                textView?.typeface = typeface
            } else {
                linearLayout?.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
                textView?.setTextColor(ContextCompat.getColor(context, R.color.text_color_splash))

                val typeface = ResourcesCompat.getFont(context, R.font.inter_medium)
                textView?.typeface = typeface

            }
        }
    }

    private fun getCurrentFormattedDate(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("EEE, dd MMMM yyyy", Locale.ENGLISH)
        return dateFormat.format(calendar.time)
    }
    private fun moodValue(mood: String?): Int {
        return when (mood?.lowercase()) {
            "overwhelmed" -> 1
            "low" -> 2
            "drifting" -> 3
            "grateful" -> 4
            "thriving" -> 5
            else -> 0// For null or unknown mood
        }
    }
    private fun apiObserver() {
        viewModel.observeCommon.observe(viewLifecycleOwner) {
            when (it?.status) {
                Status.LOADING -> {
                    showLoading("Loading")
                }

                Status.SUCCESS -> {
                    hideLoading()
                    when (it.message) {
                        Constants.HOME_GRAPH_ACCOUNT -> {
                            try {
                                val signUpModel: HomeGraphModel? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (signUpModel?.success == true) {
                                    /** Add Graph Data*/
                                    if (signUpModel.data?.moodTrend != null) {
                                        Handler(Looper.getMainLooper()).post {
                                            val rawData: List<Int> =
                                                signUpModel.data.moodTrend.map { moodValue(it.mood) }
                                            val paddedData =
                                                List(7 - rawData.size) { 0 } + rawData
                                            val maxY =
                                                paddedData.maxOrNull()?.takeIf { it > 0 } ?: 10
                                            Log.d(
                                                "GraphData",
                                                "Padded Values: $paddedData, maxY: $maxY"
                                            )
                                            binding.graphView.setGraphData(paddedData, maxY)
                                        }

                                    }

                                    if (signUpModel.data?.streak != null) {
                                        binding.daysInARow.text =   "${ signUpModel.data.streak} days in a row"
                                    }
                                    if (signUpModel.data?.moodLabel != null) {
                                        binding.mostlyBalanced.text = signUpModel.data.moodLabel
                                    }


                                } else {
//                                    signUpModel.m?.let { it1 -> showErrorToast(it1) }
                                }
                            } catch (e: Exception) {
                                showErrorToast(e.toString())
                            }
                        }

                        Constants.HOME_ACCOUNT -> {
                            try {
                                val signUpModel: HomeModel? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (signUpModel?.success == true) {

                                    if (signUpModel.mostUsedEmotion != null) {
                                        updateMoodUI(requireActivity(), signUpModel.mostUsedEmotion)
                                    }


                                } else {
                                    signUpModel?.message?.let { it1 -> showErrorToast(it1) }
                                }
                            } catch (e: Exception) {
                                showErrorToast(e.toString())
                            }
                            viewModel.homeDashBoardGraphFunction()
                        }
                        Constants.INSIGHTS_ACCOUNT -> {
                            try {
                                val signUpModel: InsightsModel? =
                                    BindingUtils.parseJson(it.data.toString())
                                if(signUpModel!=null){
                                    binding.bean = signUpModel

                                }

                            } catch (e: Exception) {
                                showErrorToast(e.toString())
                            }
                            viewModel.homeDashBoardFunction()
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

    override fun getLayoutResource(): Int {
        return R.layout.fragment_insights
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }
}

