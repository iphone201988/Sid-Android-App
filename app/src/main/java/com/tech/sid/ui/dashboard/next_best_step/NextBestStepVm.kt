package com.tech.sid.ui.dashboard.next_best_step

import com.tech.sid.base.BaseViewModel
import com.tech.sid.data.api.ApiHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NextBestStepVm @Inject constructor(
    private val apiHelper: ApiHelper,
) : BaseViewModel() {

}