package com.tech.sid.ui.dashboard.subscription_package

import com.google.gson.JsonObject
import com.tech.sid.CommonFunctionClass
import com.tech.sid.base.BaseViewModel
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
class SubscriptionVm @Inject constructor(
    private val apiHelper: ApiHelper,
) : BaseViewModel() {
    val subsObserver = SingleRequestEvent<JsonObject>()

    fun buySubscription(data: HashMap<String, Any>) {
        CoroutineScope(Dispatchers.IO).launch {
            subsObserver.postValue(Resource.loading(null))
            try {
                val response = apiHelper.apiPostForRawBody( Constants.CREATE_SUBSCRIPTION,data)

                if (response.isSuccessful && response.body() != null) {
                    subsObserver.postValue(Resource.success(Constants.CREATE_SUBSCRIPTION, response.body()))
                } else if (response.code() == Constants.UN_AUTHORISED_CODE || Constants.UN_AUTHORISED_STRING == CommonFunctionClass.jsonMessage(
                        response.errorBody()
                    )
                ) {
                    subsObserver.postValue(
                        Resource.un_authorize(
                            handleErrorResponse(response.errorBody(), response.code()),
                            null
                        )
                    )
                } else {
                    subsObserver.postValue(
                        Resource.error(
                            handleErrorResponse(response.errorBody(), response.code()),
                            null
                        )
                    )
                }

            } catch (e: java.lang.Exception) {
                subsObserver.postValue(Resource.error(e.message.toString(), null))
            }
        }
    }


    fun buySimulationSubscription(data: HashMap<String, Any>) {
        CoroutineScope(Dispatchers.IO).launch {
            subsObserver.postValue(Resource.loading(null))
            try {
                val response = apiHelper.apiPostForRawBody( Constants.PURCHASE_BUNDLE,data)

                if (response.isSuccessful && response.body() != null) {
                    subsObserver.postValue(Resource.success(Constants.PURCHASE_BUNDLE, response.body()))
                } else if (response.code() == Constants.UN_AUTHORISED_CODE || Constants.UN_AUTHORISED_STRING == CommonFunctionClass.jsonMessage(
                        response.errorBody()
                    )
                ) {
                    subsObserver.postValue(
                        Resource.un_authorize(
                            handleErrorResponse(response.errorBody(), response.code()),
                            null
                        )
                    )
                } else {
                    subsObserver.postValue(
                        Resource.error(
                            handleErrorResponse(response.errorBody(), response.code()),
                            null
                        )
                    )
                }

            } catch (e: java.lang.Exception) {
                subsObserver.postValue(Resource.error(e.message.toString(), null))
            }
        }
    }



}