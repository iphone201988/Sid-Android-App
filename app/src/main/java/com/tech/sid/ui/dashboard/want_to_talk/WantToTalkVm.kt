package com.tech.sid.ui.dashboard.want_to_talk

import com.tech.sid.base.BaseViewModel
import com.tech.sid.data.api.ApiHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class WantToTalkVm @Inject constructor(
    private val apiHelper: ApiHelper,
) : BaseViewModel() {

}