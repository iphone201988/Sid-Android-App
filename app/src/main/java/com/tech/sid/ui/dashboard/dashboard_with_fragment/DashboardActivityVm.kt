package com.tech.sid.ui.dashboard.dashboard_with_fragment

import com.tech.sid.base.BaseViewModel
import com.tech.sid.data.api.ApiHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject



@HiltViewModel
class DashboardActivityVm @Inject constructor(
    private val apiHelper: ApiHelper,
) : BaseViewModel() {


}