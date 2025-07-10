package com.tech.sid.ui.dashboard.dashboard_with_fragment.profile_edit

import com.tech.sid.base.BaseViewModel
import com.tech.sid.data.api.ApiHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class EditProfileVm @Inject constructor(
    private val apiHelper: ApiHelper,
) : BaseViewModel() {
}