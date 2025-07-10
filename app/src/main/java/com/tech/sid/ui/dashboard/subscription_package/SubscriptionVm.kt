package com.tech.sid.ui.dashboard.subscription_package

import com.tech.sid.base.BaseViewModel
import com.tech.sid.data.api.ApiHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class SubscriptionVm @Inject constructor(
    private val apiHelper: ApiHelper,
) : BaseViewModel() {


}