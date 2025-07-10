package com.tech.sid.ui.dashboard.dashboard_with_fragment.insights_fragment

import com.google.gson.JsonObject
import com.tech.sid.base.BaseViewModel
import com.tech.sid.base.utils.event.SingleRequestEvent
import com.tech.sid.data.api.ApiHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class InsightsFragmentVm @Inject constructor(private val apiHelper: ApiHelper) : BaseViewModel() {
    val observeCommon = SingleRequestEvent<JsonObject>()
}