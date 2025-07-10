package com.tech.sid.ui.dashboard.journal_folder


import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.tech.sid.BR
import com.tech.sid.R
import com.tech.sid.base.BaseActivity
import com.tech.sid.base.BaseViewModel
import com.tech.sid.base.SimpleRecyclerViewAdapter
import com.tech.sid.base.utils.BindingUtils
import com.tech.sid.databinding.ActivityTodayJournalBinding
import com.tech.sid.databinding.SuggestionItemCardBinding
import com.tech.sid.ui.dashboard.dashboard_with_fragment.DashboardActivity
import com.tech.sid.ui.onboarding_ques.OnboardingQuestion
import com.tech.sid.ui.onboarding_ques.SuggestionModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException
import kotlin.math.pow


@AndroidEntryPoint
class TodayJournal : BaseActivity<ActivityTodayJournalBinding>() {
    private val viewModel: TodayJournalVm by viewModels()
    override fun getLayoutResource(): Int {
        return R.layout.activity_today_journal
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView() {
        BindingUtils.screenFillView(this)
        initOnClick()
        rvRecyclerviewSuggest(binding.recyclerviewSuggestion, binding.editNoteSuggestion)

    }

    private fun rvRecyclerviewSuggest(view: RecyclerView, isSelected: AppCompatEditText) {
        val itemListData = ArrayList<SuggestionModel>()
        itemListData.add(SuggestionModel("Anxious", "#CAB8FF"))
        itemListData.add(SuggestionModel("Overwhelmed", "#B5EAEA"))
        itemListData.add(SuggestionModel("Grateful", "#FFD9DA"))
        itemListData.add(SuggestionModel("Conflicted", "#B5EAEA"))
        itemListData.add(SuggestionModel("Calm", "#CAB8FF"))
        val adapter: SimpleRecyclerViewAdapter<SuggestionModel, SuggestionItemCardBinding> =
            SimpleRecyclerViewAdapter(
                R.layout.suggestion_item_card, BR.bean
            ) { v, m, pos ->
                when (v.id) {
                    R.id.mainLayout -> {
                        val temp = StringBuilder(isSelected.text.toString())
                        temp.append(" ")
                        temp.append(itemListData[pos].titleValue)
                        val result: String = temp.toString()
                        isSelected.setText(result)
                    }
                }

            }
        view.adapter = adapter
        adapter.list = itemListData
        view.isNestedScrollingEnabled = true
    }


    private var isGranted = false
    private val REQUEST_RECORD_AUDIO_PERMISSION = 200
    private fun checkAudioPermission(): Boolean {
        return if (ContextCompat.checkSelfPermission(
                this@TodayJournal, Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Check if user denied permission twice (Don't ask again)
            if (!ActivityCompat.shouldShowRequestPermissionRationale(
                    this@TodayJournal,
                    Manifest.permission.RECORD_AUDIO
                )
            ) {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            } else {
                // Show permission request dialog
                ActivityCompat.requestPermissions(
                    this@TodayJournal,
                    arrayOf(Manifest.permission.RECORD_AUDIO),
                    REQUEST_RECORD_AUDIO_PERMISSION
                )
            }
            false
        } else {
            isGranted = true
            true
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            isGranted = grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
            if (!isGranted) {
                // Check again if rationale should be shown or not
                if (!ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.RECORD_AUDIO
                    )
                ) {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                }
            }
        }
    }
    private fun initOnClick() {
        viewModel.onClick.observe(this) {
            when (it?.id) {
                R.id.button -> {
                    startActivity(Intent(this, DashboardActivity::class.java))
                }

                R.id.back_button -> {
                    finish()
                }

                R.id.spinnerSelection -> {

                }

                R.id.micId -> {
                    if (checkAudioPermission()) {
                        startActivity(Intent(this, AudioListening::class.java))
                    }

                }

            }
        }
    }
}