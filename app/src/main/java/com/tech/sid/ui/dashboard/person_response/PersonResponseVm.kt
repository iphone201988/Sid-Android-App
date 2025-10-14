package com.tech.sid.ui.dashboard.person_response

import com.google.gson.JsonObject
import com.tech.sid.CommonFunctionClass
import com.tech.sid.base.BaseViewModel
import com.tech.sid.base.utils.BindingUtils
import com.tech.sid.base.utils.Resource
import com.tech.sid.base.utils.event.SingleRequestEvent
import com.tech.sid.data.api.ApiHelper
import com.tech.sid.data.api.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject



@HiltViewModel
class PersonResponseVm @Inject constructor(
    private val apiHelper: ApiHelper,
) : BaseViewModel() {

    val observeCommon = SingleRequestEvent<JsonObject>()
    fun getResponseStylesFunction() {
        CoroutineScope(Dispatchers.IO).launch {
            observeCommon.postValue(Resource.loading(null))
            try {

                val response = apiHelper.apiGetOnlyAuthToken( Constants.GET_EMPATHY_OPTIONS_RESPONSE_STYLES_API+"/${BindingUtils.interactionModelPost?.momentId?:""}")

                if (response.isSuccessful && response.body() != null) {
                    observeCommon.postValue(Resource.success(Constants.GET_EMPATHY_OPTIONS_RESPONSE_STYLES_API, response.body()))
                } else if (response.code() == Constants.UN_AUTHORISED_CODE || Constants.UN_AUTHORISED_STRING == CommonFunctionClass.jsonMessage(
                        response.errorBody()
                    )
                ) {
                    observeCommon.postValue(
                        Resource.un_authorize(
                            handleErrorResponse(response.errorBody(), response.code()),
                            null
                        )
                    )
                } else {
                    observeCommon.postValue(
                        Resource.error(
                            handleErrorResponse(response.errorBody(), response.code()),
                            null
                        )
                    )
                }

            } catch (e: java.lang.Exception) {
                observeCommon.postValue(Resource.error(e.message.toString(), null))
            }
        }
    }

}