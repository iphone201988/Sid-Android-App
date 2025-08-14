package com.tech.sid.ui.dashboard.dashboard_with_fragment.notification

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.tech.sid.R
import com.tech.sid.base.BaseActivity
import com.tech.sid.base.BaseViewModel
import com.tech.sid.base.utils.BindingUtils
import com.tech.sid.databinding.ActivityChangePasswordBinding
import com.tech.sid.databinding.ActivityNotificationBinding
import com.tech.sid.ui.dashboard.dashboard_with_fragment.change_password.ChangePasswordVm
import com.tech.sid.ui.dashboard.journal_folder.TodayJournal
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationActivity : BaseActivity<ActivityNotificationBinding>() {

    private val viewModel: NotificationActivityVm by viewModels()
    override fun getLayoutResource(): Int {
        return R.layout.activity_notification
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView() {
        BindingUtils.screenFillView(this)
        initOnClick()

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
}