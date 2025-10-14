package com.tech.sid.ui.dashboard.dashboard_with_fragment.journal_fragment

import android.app.Activity
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import com.tech.sid.R
import com.tech.sid.base.BaseFragment
import com.tech.sid.base.BaseViewModel
import com.tech.sid.base.utils.BindingUtils
import com.tech.sid.base.utils.Status
import com.tech.sid.base.utils.showErrorToast
import com.tech.sid.data.api.Constants
import com.tech.sid.databinding.FragmentJournalBinding
import com.tech.sid.ui.auth.AuthModelLogin
import com.tech.sid.ui.dashboard.dashboard_with_fragment.home_fragment.HomeGraphModel
import com.tech.sid.ui.dashboard.dashboard_with_fragment.home_fragment.HomeModel
import com.tech.sid.ui.dashboard.dashboard_with_fragment.notification.NotificationActivity
import com.tech.sid.ui.dashboard.journal_folder.AudioListening
import com.tech.sid.ui.dashboard.journal_folder.TodayJournal
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


@AndroidEntryPoint
class JournalFragment : BaseFragment<FragmentJournalBinding>(), DataListener {
    private val viewModel: JournalFragmentVm by viewModels()
    var valueProfile: AuthModelLogin? = null
    override fun onCreateView(view: View) {
        binding.interfaceForDelete = this
        apiObserver()
        initOnClick()
        viewModel.journalFunction()
        valueProfile = sharedPrefManager.getProfileData()
        if (valueProfile != null) {
            binding.apply {
                textView.text =
                    "Hi ,${valueProfile?.user?.firstName} ${valueProfile?.user?.lastName}"
                timeDate.text = getCurrentFormattedDate()
            }
        }
    }

    private fun initOnClick() {
        viewModel.onClick.observe(viewLifecycleOwner) {
            when (it?.id) {
                R.id.bellNotification -> {

                    startActivity(Intent(requireActivity(), NotificationActivity::class.java))

                }
                R.id.button -> {
                    TodayJournal.isEdited = false
                    TodayJournal.data = null
                    launcher.launch(Intent(requireActivity(), TodayJournal::class.java))
                }
            }
        }
    }

    private fun getCurrentFormattedDate(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("EEE, dd MMMM yyyy", Locale.ENGLISH)
        return dateFormat.format(calendar.time)
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
                        Constants.DELETE_JOURNAL_VALUE -> {
                            viewModel.journalFunction()
                        }

                        Constants.GET_JOURNAL -> {
                            try {
                                val signUpModel: JournalModel? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (signUpModel != null) {
                                    binding.bean = signUpModel
//                                    binding.executePendingBindings()
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

    override fun getLayoutResource(): Int {
        return R.layout.fragment_journal
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onDataReceived(data: String) {
        viewModel.journalDeleteFunction(data)
    }

    /** launch activity**/
    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val isJournalUpdated =
                    result.data?.getBooleanExtra("isJournalUpdated", false) ?: false
                if (isJournalUpdated) {
                    viewModel.journalFunction()
                }
            }
        }
}

interface DataListener {
    fun onDataReceived(data: String)
}