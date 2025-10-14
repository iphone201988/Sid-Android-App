package com.tech.sid.ui.dashboard.dashboard_with_fragment.home_fragment

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
class HomeFragmentVm @Inject constructor(private val apiHelper: ApiHelper) : BaseViewModel() {
    val observeCommon = SingleRequestEvent<JsonObject>()

    fun addMoodFunction(data: HashMap<String, Any>) {
        CoroutineScope(Dispatchers.IO).launch {
            observeCommon.postValue(Resource.loading(null))
            try {

                val response = apiHelper.apiPostForRawBody(request = data, url=Constants.ADD_MOOD)
                if (response.isSuccessful && response.body() != null) {
                    observeCommon.postValue(
                        Resource.success(
                            Constants.ADD_MOOD,
                            response.body()
                        )
                    )
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

    fun homeDashBoardFunction( ) {
        CoroutineScope(Dispatchers.IO).launch {
            observeCommon.postValue(Resource.loading(null))
            try {
                val response = apiHelper.apiGetOnlyAuthToken( url = Constants.HOME_ACCOUNT)
                if (response.isSuccessful && response.body() != null) {
                    observeCommon.postValue(
                        Resource.success(
                            Constants.HOME_ACCOUNT,
                            response.body()
                        )
                    )
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
    fun homeDashBoardGraphFunction( ) {
        CoroutineScope(Dispatchers.IO).launch {
//            observeCommon.postValue(Resource.loading(null))
            try {
                val response = apiHelper.apiGetOnlyAuthToken( url = Constants.HOME_GRAPH_ACCOUNT)
                if (response.isSuccessful && response.body() != null) {
                    observeCommon.postValue(
                        Resource.success(
                            Constants.HOME_GRAPH_ACCOUNT,
                            response.body()
                        )
                    )
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