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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.tech.sid.BR
import com.tech.sid.CommonFunctionClass
import com.tech.sid.R
import com.tech.sid.base.BaseActivity
import com.tech.sid.base.BaseViewModel
import com.tech.sid.base.SimpleRecyclerViewAdapter
import com.tech.sid.base.utils.BindingUtils
import com.tech.sid.base.utils.Status
import com.tech.sid.base.utils.showErrorToast
import com.tech.sid.data.api.Constants
import com.tech.sid.databinding.ActivityTodayJournalBinding
import com.tech.sid.databinding.SuggestionItemCardBinding
import com.tech.sid.ui.dashboard.chat_screen.ChatApiResposeModel
import com.tech.sid.ui.dashboard.chat_screen.ChatMessage
import com.tech.sid.ui.dashboard.dashboard_with_fragment.DashboardActivity
import com.tech.sid.ui.onboarding_ques.JournalModel
import com.tech.sid.ui.onboarding_ques.JournalModel2
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

    companion object {
        var isEdited = false
        var data: JournalModel2? = null
    }

    override fun onCreateView() {
        BindingUtils.screenFillView(this)
        initOnClick()
        val itemListData = ArrayList<SuggestionModel>()
        itemListData.add(SuggestionModel("Anxious", "#CAB8FF",""))
        itemListData.add(SuggestionModel("Overwhelmed", "#B5EAEA",""))
        itemListData.add(SuggestionModel("Grateful", "#FFD9DA",""))
        itemListData.add(SuggestionModel("Conflicted", "#B5EAEA",""))
        itemListData.add(SuggestionModel("Calm", "#CAB8FF",""))

        apiObserver()
        if (isEdited) {
            if (data != null) {
                binding.editNoteSuggestion.setText(data!!.titleSubHeading)
                binding.editTitleSuggestion.setText(data!!.titleMainHeading)
                for(i in itemListData.indices){
                    if(data!!.tags.contains(itemListData[i].titleValue)){
                        itemListData[i].iselected=true
                    }
                }
            }
        }
        rvRecyclerviewSuggest(binding.recyclerviewSuggestion,itemListData)
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

                        Constants.ADD_JOURNAL -> {
                            try {
                                val chatApiResposeModelModel: AddJournalModel? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (chatApiResposeModelModel?.success == true) {
                                    val resultIntent = Intent()
                                    resultIntent.putExtra("isJournalUpdated", true)
                                    setResult(RESULT_OK, resultIntent)
                                    finish()
                                } else {
                                    chatApiResposeModelModel?.message?.let { it1 ->
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

    lateinit var adapter: SimpleRecyclerViewAdapter<SuggestionModel, SuggestionItemCardBinding>
    private fun rvRecyclerviewSuggest(view: RecyclerView, list: ArrayList<SuggestionModel>) {

        adapter =
            SimpleRecyclerViewAdapter(
                R.layout.suggestion_item_card, BR.bean
            ) { v, m, pos ->
                when (v.id) {
                    R.id.mainLayout -> {
                        adapter.list[pos].iselected = adapter.list[pos].iselected != true
                        adapter.notifyItemChanged(pos)
                    }
                }
            }
        view.adapter = adapter
        adapter.list = list
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
            isGranted =
                grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
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
//                    val selectedItems = adapter.list.filter { it.iselected == true }
                    val selectedTitles = adapter.list
                        .filter { it.iselected == true }
                        .map { it.titleValue }

                    if (binding.editTitleSuggestion.text.toString().trim().isEmpty()) {
                        showErrorToast("Please enter title")
                        return@observe
                    }
                    if (binding.editNoteSuggestion.text.toString().trim().isEmpty()) {
                        showErrorToast("Please enter content")
                        return@observe
                    }
                    if (binding.editNoteSuggestion.text.toString().trim().isEmpty()) {
                        showErrorToast("Please enter content")
                        return@observe
                    }
//                    if (selectedTitles.isEmpty()) {
//                        showErrorToast("Please select tag")
//                        return@observe
//                    }
                    val data= HashMap<String, Any>()
                    data["title"]=binding.editTitleSuggestion.text.toString().trim()
                    data["content"]=binding.editNoteSuggestion.text.toString().trim()
                    if (selectedTitles.isNotEmpty()){
                        data["tags"] to selectedTitles
                    }

//                    var isEdited = false
//                    var data: JournalModel? = null
                    if(isEdited){
                        viewModel.editJournalFunction(TodayJournal.data?.id?:"",data)
                    }else{
                        viewModel.addJournalFunction(data)
                    }

                }

                R.id.back_button -> {
                    finish()
                }


                R.id.micId -> {
                    if (checkAudioPermission()) {
//                        startActivityForResult(Intent(this, AudioListening::class.java))
                        val intent = Intent(this, AudioListening::class.java)
                        audioListeningLauncher.launch(intent)
                    }

                }

            }
        }
    }

    private val audioListeningLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            val resultText = data?.getStringExtra("result_text")
            binding.editNoteSuggestion.setText(resultText.toString())

        }
    }

}