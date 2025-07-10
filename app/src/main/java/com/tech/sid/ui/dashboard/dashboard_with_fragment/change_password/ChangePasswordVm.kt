package com.tech.sid.ui.dashboard.dashboard_with_fragment.change_password

import com.tech.sid.base.BaseViewModel
import com.tech.sid.data.api.ApiHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject



@HiltViewModel
class ChangePasswordVm @Inject constructor(
    private val apiHelper: ApiHelper,
) : BaseViewModel() {
}