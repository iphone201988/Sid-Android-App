package com.tech.sid.ui.dashboard.chat_screen

import com.tech.sid.base.BaseViewModel
import com.tech.sid.data.api.ApiHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class ChatActivityVm @Inject constructor(
    private val apiHelper: ApiHelper,
) : BaseViewModel() {

}