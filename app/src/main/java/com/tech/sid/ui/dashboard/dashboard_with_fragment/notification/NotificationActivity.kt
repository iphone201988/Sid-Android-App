package com.tech.sid.ui.dashboard.dashboard_with_fragment.notification

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.tech.sid.BR
import com.tech.sid.R
import com.tech.sid.base.BaseActivity
import com.tech.sid.base.BaseViewModel
import com.tech.sid.base.SimpleRecyclerViewAdapter
import com.tech.sid.base.utils.BindingUtils
import com.tech.sid.base.utils.Status
import com.tech.sid.base.utils.showErrorToast
import com.tech.sid.data.api.Constants
import com.tech.sid.databinding.ActivityChangePasswordBinding
import com.tech.sid.databinding.ActivityNotificationBinding
import com.tech.sid.databinding.HolderNotificationBinding
import com.tech.sid.ui.auth.AuthModelLogin
import com.tech.sid.ui.dashboard.dashboard_with_fragment.DashboardActivity
import com.tech.sid.ui.dashboard.dashboard_with_fragment.change_password.ChangePasswordVm
import com.tech.sid.ui.dashboard.dashboard_with_fragment.home_fragment.MoodPostModel
import com.tech.sid.ui.dashboard.journal_folder.TodayJournal
import com.tech.sid.ui.onboarding_ques.OnboardingStart
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationActivity : BaseActivity<ActivityNotificationBinding>(), NotificationAdapter.MoodListener {
    private val viewModel: NotificationActivityVm by viewModels()
    private var notificationList= ArrayList<NotificationData>()
    private lateinit var notificationAdapter : NotificationAdapter
    override fun getLayoutResource(): Int {
        return R.layout.activity_notification
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView() {
        BindingUtils.screenFillView(this)
        viewModel.getNotifications()
        notificationAdapter= NotificationAdapter(this,notificationList,this)
        binding.rvNotification.adapter=notificationAdapter
        initOnClick()
        initObserver()

    }

    private fun initOnClick() {
        viewModel.onClick.observe(this) {
            when (it?.id) {
                R.id.createJournal -> {
                    TodayJournal.isEdited = false
                    TodayJournal.data = null
                    startActivity(Intent(this, TodayJournal::class.java))
                }
                R.id.back_button -> {
                    finish()
                }
            }
        }

    }

    private fun initObserver(){
        viewModel.notificationObserver.observe(this){
            when(it?.status){
                Status.LOADING ->  showLoading("Loading")
                Status.SUCCESS -> {
                    hideLoading()
                    when (it.message) {
                        Constants.NOTIFICATIONS -> {
                            try {
                                val dataModel: NotificationModel? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (dataModel?.success == true) {
                                    if (!dataModel.notifications.isNullOrEmpty()){
                                        notificationList = dataModel.notifications.filter { it.isRead==false } as ArrayList<NotificationData>
                                        Log.e("ddsd", "initObserver: $notificationList", )
                                        notificationAdapter.list=notificationList
                                        notificationAdapter.notifyDataSetChanged()
                                    }

                                } else {
                                    dataModel?.message?.let { it1 -> showErrorToast(it1) }
                                }
                                handleUi()
                            } catch (e: Exception) {
                                showErrorToast(e.toString())
                            }
                        }
                        Constants.ADD_MOOD -> {
                            val signUpModel: MoodPostModel? =
                                BindingUtils.parseJson(it.data.toString())
                            if (signUpModel?.success == true) {
                                val resultIntent = Intent()
                                resultIntent.putExtra("isMoodUpdated", true)
                                setResult(RESULT_OK, resultIntent)
                                finish()
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

    fun updateMoodUI(context: Context, selectedMood: String) {
        val moods = listOf(
            "Thriving" to R.id.ivThriving,
            "Grateful" to R.id.ivGrateful,
            "Drifting" to R.id.ivDrifting,
            "Low" to R.id.ivLow,
            "Overwhelmed" to R.id.ivOverwhelmed
            // "Log My mood" to R.id.LogMymoodLL // optional
        )

        // Gradient background
        val gradientDrawable = GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT, intArrayOf(
                Color.parseColor("#CAB8FF"),
                Color.parseColor("#B5EAEA")
            )
        ).apply {
            cornerRadius = 0f
        }

        for ((mood, viewId) in moods) {
            val imageView = findViewById<ImageView>(viewId)
            if (mood.equals(selectedMood, ignoreCase = true)) {
                imageView?.background = gradientDrawable
            } else {
                imageView?.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
            }
        }
    }

    private fun handleUi(){
        if (!notificationList.isNullOrEmpty()){
            binding.rvNotification.visibility=View.VISIBLE
            notificationAdapter.notifyDataSetChanged()
            binding.tvNoDataView.visibility=View.GONE
        }
        else{
            binding.rvNotification.visibility=View.GONE
            binding.tvNoDataView.visibility=View.VISIBLE
        }
    }

    override fun moodUpdate(context: Context, mood: String) {
        updateMoodUI(context,mood)
    }

    override fun updateTodayMood(mood: String) {
        val data = HashMap<String, Any>().apply {
            put("mood", mood)
        }
        viewModel.addMoodFunction(data)
    }
}