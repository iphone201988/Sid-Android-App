package com.tech.sid.ui.dashboard.dashboard_with_fragment.notification

import android.util.Log
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
class NotificationActivityVm @Inject constructor(
    private val apiHelper: ApiHelper,
) : BaseViewModel() {
    val notificationObserver = SingleRequestEvent<JsonObject>()

    fun getNotifications() {
        CoroutineScope(Dispatchers.IO).launch {
            notificationObserver.postValue(Resource.loading(null))
            try {
                val response = apiHelper.apiGetOnlyAuthToken( Constants.NOTIFICATIONS)

                if (response.isSuccessful && response.body() != null) {
                    Log.e("notifications", "getNotifications: ${response.body()}", )
                    notificationObserver.postValue(Resource.success(Constants.NOTIFICATIONS, response.body()))
                } else if (response.code() == Constants.UN_AUTHORISED_CODE || Constants.UN_AUTHORISED_STRING == CommonFunctionClass.jsonMessage(
                        response.errorBody()
                    )
                ) {
                    notificationObserver.postValue(
                        Resource.un_authorize(
                            handleErrorResponse(response.errorBody(), response.code()),
                            null
                        )
                    )
                } else {
                    notificationObserver.postValue(
                        Resource.error(
                            handleErrorResponse(response.errorBody(), response.code()),
                            null
                        )
                    )
                }

            } catch (e: java.lang.Exception) {
                notificationObserver.postValue(Resource.error(e.message.toString(), null))
            }
        }
    }

    fun addMoodFunction(data: HashMap<String, Any>) {
        CoroutineScope(Dispatchers.IO).launch {
            notificationObserver.postValue(Resource.loading(null))
            try {

                val response = apiHelper.apiPostForRawBody(request = data, url=Constants.ADD_MOOD)
                if (response.isSuccessful && response.body() != null) {
                    notificationObserver.postValue(
                        Resource.success(
                            Constants.ADD_MOOD,
                            response.body()
                        )
                    )
                } else if (response.code() == Constants.UN_AUTHORISED_CODE || Constants.UN_AUTHORISED_STRING == CommonFunctionClass.jsonMessage(
                        response.errorBody()
                    )
                ) {
                    notificationObserver.postValue(
                        Resource.un_authorize(
                            handleErrorResponse(response.errorBody(), response.code()),
                            null
                        )
                    )
                } else {
                    notificationObserver.postValue(
                        Resource.error(
                            handleErrorResponse(response.errorBody(), response.code()),
                            null
                        )
                    )
                }

            } catch (e: java.lang.Exception) {
                notificationObserver.postValue(Resource.error(e.message.toString(), null))
            }
        }
    }
}