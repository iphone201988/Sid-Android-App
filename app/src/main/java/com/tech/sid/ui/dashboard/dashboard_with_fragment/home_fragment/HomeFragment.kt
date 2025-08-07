package com.tech.sid.ui.dashboard.dashboard_with_fragment.home_fragment

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import com.tech.sid.R
import com.tech.sid.base.BaseFragment
import com.tech.sid.base.BaseViewModel
import com.tech.sid.base.utils.BindingUtils
import com.tech.sid.base.utils.Status
import com.tech.sid.base.utils.showErrorToast
import com.tech.sid.data.api.Constants
import com.tech.sid.databinding.FragmentHomeBinding
import com.tech.sid.ui.auth.AuthModelLogin
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>() {
    private val viewModel: HomeFragmentVm by viewModels()
    var valueProfile: AuthModelLogin? = null
    private var viewRoot: View? = null
    override fun onCreateView(view: View) {
        apiObserver()
        viewRoot = view
        valueProfile = sharedPrefManager.getProfileData()
        if (valueProfile != null) {
            binding.apply {
                textView.text =
                    "Hi ,${valueProfile?.user?.firstName} ${valueProfile?.user?.lastName}"
                timeDate.text = getCurrentFormattedDate()
            }
        }

        viewModel.homeDashBoardFunction()

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
                                    if (signUpModel.data?.mostUsedMood != null) {
                                        binding.overwhelmedTv.text = signUpModel.data.mostUsedMood
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
                                    if (signUpModel.reflection != null) {
                                        binding.refrexedTileTv.text = signUpModel.reflection.message
                                        binding.refrexedTv.text = signUpModel.reflection.suggestion
                                    }
                                    if (signUpModel.lastSimulation != null) {
                                        binding.boundaryWithAFriend.text =
                                            signUpModel.lastSimulation.momentTitle
                                    }
                                    if (signUpModel.lastJournal != null) {
                                        binding.feelingUnseen.text = signUpModel.lastJournal.title
                                    }
                                } else {
                                    signUpModel?.message?.let { it1 -> showErrorToast(it1) }
                                }
                            } catch (e: Exception) {
                                showErrorToast(e.toString())
                            }
                            viewModel.homeDashBoardGraphFunction()
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

    override fun getLayoutResource(): Int {
        return R.layout.fragment_home
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
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
}
